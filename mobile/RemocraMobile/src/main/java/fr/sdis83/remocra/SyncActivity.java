package fr.sdis83.remocra;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.HydrantTable;
import fr.sdis83.remocra.database.TourneeTable;
import fr.sdis83.remocra.dialog.ChoiceTournee;
import fr.sdis83.remocra.parser.RemocraParser;
import fr.sdis83.remocra.parser.ServerInfoParser;
import fr.sdis83.remocra.parser.XmlErrorParser;
import fr.sdis83.remocra.serializer.HydrantSerializer;
import fr.sdis83.remocra.serializer.TourneeSerializer;
import fr.sdis83.remocra.widget.InfoView;

/**
 * Created by jpt on 24/09/13.
 */
public class SyncActivity extends FragmentActivity implements ChoiceTournee.ChoiceTourneeListener {

    public static final int AUTH_CONNECTION_TIMEOUT_SEC = 10;
    public static final int AUTH_SOCKET_TIMEOUT_SEC = 60;

    public static final int DEFAULT_CONNECTION_TIMEOUT_SEC = 30;
    public static final int DEFAULT_SOCKET_TIMEOUT_SEC = 300;

    private TextView mMessage3;
    private Button mBtnSynchro;
    private RemocraParser parsers;
    private TextView mMessageNbHydrant;
    private List<Cookie> cookies;
    private ProgressBar mProgressBar;
    private int[] idNewTournees;
    private int[] idLocalTournees;
    private List<Integer> tourneeSelectionnees;
    private TextView mMessage2;

    enum SYNC_ACTION {
        GET_LOCAL_INFO,
        CHECK_CONNEXION,
        GET_SERVER_INFO,
        UPLOAD_HYDRANT,
        DOWNLOAD_REFERENTIEL,
        DOWNLOAD_TOURNEE,
        INTEGRE_DATA
    }

    private class ActionResult {
        public final boolean success;
        public final InputStream inputStream;
        public final String message;

        public ActionResult(boolean success, String message) {
            this.success = success;
            this.message = message;
            this.inputStream = null;
        }

        public ActionResult(boolean success, InputStream inputStream) {
            this.success = success;
            this.inputStream = inputStream;
            this.message = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync);

        parsers = new RemocraParser(this);

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // Message d'information
        InfoView infoView = (InfoView)findViewById(R.id.textViewInfo);
        infoView.setHtmlContent(Html.fromHtml(getResources().getString(R.string.syncInfo)));

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageNbHydrant = (TextView) findViewById(R.id.message1);
        mMessageNbHydrant.setText(getResources().getQuantityString(R.plurals.syncNbHydrant, 6, 6));

        mMessage2 = (TextView) findViewById(R.id.message2);

        mMessage3 = (TextView) findViewById(R.id.message3);
        mBtnSynchro = (Button) findViewById(R.id.synchro);

        mBtnSynchro.setEnabled(false);
        if (checkConnection()) {
            doAction(SYNC_ACTION.GET_LOCAL_INFO);
        } else {
            mMessage3.setText("Aucune connexion n'est disponible");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void doSynchro(View view) {
        Bundle bundle = new Bundle();
        bundle.putIntArray("idNewTournee", idNewTournees);
        bundle.putIntArray("idLocalTournee", idLocalTournees);
        ChoiceTournee dialog = new ChoiceTournee();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "choiceTournee");
    }

    @Override
    public void onSelectTournee(List<Integer> idTournees) {
        mBtnSynchro.setEnabled(true);
        tourneeSelectionnees = idTournees;
        doAction(SYNC_ACTION.UPLOAD_HYDRANT);
    }

    private void setNbHydrant(int nb) {
        if (nb == 0) {
            mMessageNbHydrant.setText(getResources().getString(R.string.syncNbHydrantEmpty));
        } else {
            mMessageNbHydrant.setText(getResources().getQuantityString(R.plurals.syncNbHydrant, nb, nb));
        }
    }

    private void doAction(SYNC_ACTION action) {
        Synchronisation synchroAction = new Synchronisation();
        synchroAction.execute(action);
    }

    private boolean checkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        return activeInfo != null && activeInfo.isConnected();
    }

    private class Synchronisation extends AsyncTask<SYNC_ACTION, String, ActionResult> {
        private SYNC_ACTION currentAction;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ActionResult doInBackground(SYNC_ACTION... actions) {
            if (checkConnection()) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SyncActivity.this);
                Uri uriServeur = Uri.parse(sharedPref.getString(ParamActivity.SERVEUR_URL, ""));
                currentAction = actions[0];
                Log.d("REMOCRA", "doInBackground : " + currentAction.toString());
                switch (currentAction) {
                    case GET_LOCAL_INFO:
                        int nbHydrant = getNbHydrantSynchro();
                        return new ActionResult(true, String.valueOf(nbHydrant));
                    case CHECK_CONNEXION:
                        publishProgress(getString(R.string.sync_check_connexion));
                        return doLogin(uriServeur);
                    case GET_SERVER_INFO:
                        publishProgress(getString(R.string.sync_get_server_info));
                        return getServerInfo(uriServeur);
                    case UPLOAD_HYDRANT:
                        publishProgress(getString(R.string.sync_send_hydrant));
                        return uploadHydrants(uriServeur);
                    case DOWNLOAD_REFERENTIEL:
                        publishProgress(getString(R.string.sync_loading_referentiel));
                        return downloadAndParse(uriServeur.buildUpon().appendEncodedPath("xml/referentiels").build(), getString(R.string.sync_referentiel_loaded));
                    case DOWNLOAD_TOURNEE:
                        publishProgress(getString(R.string.sync_loading_tournee));
                        return downloadTournee(uriServeur);
                    case INTEGRE_DATA:
                        publishProgress(getString(R.string.sync_integre_data));
                        int nb = parsers.insertIntoDatabase();
                        return new ActionResult(true, getString(R.string.sync_integre_done, nb));
                }
            }
            return new ActionResult(false, "Pas de connexion");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            mMessage3.setText(values[0]);
        }

        @Override
        protected void onPostExecute(ActionResult result) {
            mMessage3.setText(result.message);
            mProgressBar.setVisibility(View.GONE);
            switch (currentAction) {
                case GET_LOCAL_INFO:
                    mMessage3.setText("");
                    int nbHydrant = Integer.valueOf(result.message);
                    setNbHydrant(nbHydrant);
                    doAction(SYNC_ACTION.CHECK_CONNEXION);
                    break;
                case CHECK_CONNEXION:
                    if (result.success) {
                        doAction(SYNC_ACTION.GET_SERVER_INFO);
                    }
                    break;
                case GET_SERVER_INFO:
                    if (result.success) {
                        mMessage2.setText(result.message);
                        mMessage3.setText("");
                        mBtnSynchro.setEnabled(true);
                    }
                    break;
                case UPLOAD_HYDRANT:
                    if (result.success) {
                        doAction(SYNC_ACTION.DOWNLOAD_REFERENTIEL);
                    }
                    break;
                case DOWNLOAD_REFERENTIEL:
                    if (result.success) {
                        doAction(SYNC_ACTION.DOWNLOAD_TOURNEE);
                    }
                    break;
                case DOWNLOAD_TOURNEE:
                    if (result.success) {
                        doAction(SYNC_ACTION.INTEGRE_DATA);
                    }
                    break;
                case INTEGRE_DATA:
                    if (result.success) {
                        mBtnSynchro.setEnabled(true);
                    }
                    break;
            }
        }
    }

    private ActionResult getServerInfo(Uri uriServeur) {
        String message = "";
        boolean success = false;

        DefaultHttpClient httpClient = addCookies(new DefaultHttpClient(prepareHttpParameters()));
        HttpGet request = new HttpGet(uriServeur.buildUpon().appendEncodedPath("tournees").build().toString());
        request.addHeader("Accept", "application/xml");
        try {
            HttpResponse response = httpClient.execute(request);
//            InputStream stream = response.getEntity().getContent();
//            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
//                    StringBuilder total = new StringBuilder();
//                    String line;
//                    while ((line = r.readLine()) != null) {
//                        total.append(line);
//                    }
//                    message = total.toString();
//                    Log.d("REMOCRA","XML reçu: " + message);


            XmlPullParser xmlPullParser = Xml.newPullParser();
            try {
                xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                xmlPullParser.setInput(response.getEntity().getContent(), null);
                ServerInfoParser parser = new ServerInfoParser();
                List<Integer> lstTournee = parser.parse(xmlPullParser);
                idNewTournees = new int[lstTournee.size()];
                for (int idx = 0; idx < lstTournee.size(); ++idx) {
                    idNewTournees[idx] = lstTournee.get(idx);
                }
                success = true;
                message = idNewTournees.length + " nouvelle(s) tournée(s) disponible(s)";
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                message = e.getMessage();
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            int socketTimeoutSec = httpClient.getParams().getIntParameter(CoreConnectionPNames.SO_TIMEOUT, -1);
            message = e.getMessage()==null?
                    "Délai de réponse dépassé" + (socketTimeoutSec>=0?" ("+(socketTimeoutSec/1000)+"s)":"")
                    :e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            message = e.getMessage()==null?"Impossible d'exécuter la requête":e.getMessage();
        }

        return new ActionResult(success, message);
    }

    private DefaultHttpClient addCookies(DefaultHttpClient httpClient) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                httpClient.getCookieStore().addCookie(cookie);
            }
        }
        return httpClient;
    }

    private int getNbHydrantSynchro() {
        Cursor cursor = null;
        int nbHydrant = 0;
        try {
            cursor = getContentResolver().query(RemocraProvider.CONTENT_TOURNEE_URI,
                    new String[]{TourneeTable._ID},
                    null, null, null);
            if (cursor != null) {
                idLocalTournees = new int[cursor.getCount()];
                int idx = 0;
                while (cursor.moveToNext()) {
                    idLocalTournees[idx] = cursor.getInt(0);
                    idx++;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        try {
            cursor = getContentResolver().query(RemocraProvider.CONTENT_HYDRANT_URI,
                    new String[]{"count(" + HydrantTable._ID + ") as countHydrant"},
                    HydrantTable.COLUMN_STATE_H1 + "=1 AND " +
                            HydrantTable.COLUMN_STATE_H2 + "=1 AND " +
                            HydrantTable.COLUMN_STATE_H3 + "=1 AND " +
                            HydrantTable.COLUMN_STATE_H4 + "=1 AND " +
                            HydrantTable.COLUMN_STATE_H5 + "=1 AND " +
                            HydrantTable.COLUMN_STATE_H6 + "=1 AND " +
                            HydrantTable.COLUMN_TYPE_SAISIE + " is not null", null, null);
            if (cursor != null && cursor.moveToFirst()) {
                nbHydrant = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return nbHydrant;
    }

    private ActionResult doLogin(Uri uriServeur) {
        GlobalRemocra global = GlobalRemocra.getInstance(getApplicationContext());
        String mLogin = global.getLogin();
        String mPassword = global.getPassword();

        boolean success = false;
        String message = null;

        DefaultHttpClient httpClient = new DefaultHttpClient(prepareHttpParameters(AUTH_CONNECTION_TIMEOUT_SEC, AUTH_SOCKET_TIMEOUT_SEC));

        HttpPost request = new HttpPost(uriServeur.buildUpon().appendEncodedPath("auth/login/xml").build().toString());
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("username", mLogin));
        nameValuePairs.add(new BasicNameValuePair("password", mPassword));

        try {
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                cookies = httpClient.getCookieStore().getCookies();
                XmlPullParser xmlPullParser = Xml.newPullParser();
                try {
                    xmlPullParser.setInput(response.getEntity().getContent(), null);
                    message = parsers.parse(xmlPullParser);
                    if (message == null) {
                        // parsage réussi
                        success = true;
                        message = "Authentification réussie";
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            } else {
                if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    message = "Erreur d'authentification";
                } else {
                    message = "ERR " + response.getStatusLine().getStatusCode() + "/" + response.getStatusLine().getReasonPhrase();
                }
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            int socketTimeoutSec = httpClient.getParams().getIntParameter(CoreConnectionPNames.SO_TIMEOUT, -1);
            message = e.getMessage()==null?
                    "Délai de réponse dépassé" + (socketTimeoutSec>=0?" ("+(socketTimeoutSec/1000)+"s)":"")
                    :e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            message = e.getMessage()==null?"Impossible d'exécuter la requête":e.getMessage();
        }
        return new ActionResult(success, message);
    }

    private ActionResult uploadHydrants(Uri uriServeur) {
        DefaultHttpClient httpclient = addCookies(new DefaultHttpClient(prepareHttpParameters()));
        HttpPost request = new HttpPost(uriServeur.buildUpon().appendEncodedPath("xml/hydrants").build().toString());
        Cursor cursor = null;
        String sMessage = "";
        boolean result = false;

        try {
            // Avant l'export on sauvegarde tout.
            TourneeSerializer tourneeSerializer = new TourneeSerializer(getApplicationContext());
            tourneeSerializer.serialize();

            // Export
            cursor = getContentResolver().query(RemocraProvider.CONTENT_HYDRANT_URI, null,
                    HydrantTable.COLUMN_STATE_H1 + "=1 AND " +
                            HydrantTable.COLUMN_STATE_H2 + "=1 AND " +
                            HydrantTable.COLUMN_STATE_H3 + "=1 AND " +
                            HydrantTable.COLUMN_STATE_H4 + "=1 AND " +
                            HydrantTable.COLUMN_STATE_H5 + "=1 AND " +
                            HydrantTable.COLUMN_STATE_H6 + "=1 AND " +
                            HydrantTable.COLUMN_TYPE_SAISIE + " is not null", null,
                    HydrantTable.COLUMN_TYPE_HYDRANT);
            if (cursor != null) {
                if (cursor.getCount() == 0) {
                    Log.d("REMOCRA", "Aucun hydrant synchronisable");
                    return new ActionResult(true, "Aucun hydrant à synchronier");
                } else {
                    Log.d("REMOCRA", cursor.getCount() + " hydrant(s) synchronisable(s)");
                }
            } else {
                return new ActionResult(false, "Erreur interne");
            }
            HydrantSerializer serializer = new HydrantSerializer(SyncActivity.this);
            String xml = serializer.serialize(cursor);

            Log.d("REMOCRA", xml);

            StringEntity se = new StringEntity(xml);
            se.setContentType("application/xml");

            request.setEntity(se);
            HttpResponse response = httpclient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                // TODO : implémenter un parseur de réponse XML

                InputStream stream = response.getEntity().getContent();
                BufferedReader r = new BufferedReader(new InputStreamReader(stream));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
                String message = total.toString();
                Log.d("REMOCRA", "reponse reçue : " + message);

                result = true;
            } else {
                InputStream stream = response.getEntity().getContent();
                if (stream != null) {
                    XmlPullParser xmlPullParser = Xml.newPullParser();
                    try {
                        xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                        xmlPullParser.setInput(stream, null);
                        XmlErrorParser parserError = new XmlErrorParser();
                        sMessage = parserError.parse(xmlPullParser);
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                        return new ActionResult(false, e.getMessage());
                    }
                }
                if (sMessage == null) {
                    sMessage = "Err " + response.getStatusLine().getStatusCode() + " / " + response.getStatusLine().getReasonPhrase();
                }
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            int socketTimeoutSec = httpclient.getParams().getIntParameter(CoreConnectionPNames.SO_TIMEOUT, -1);
            sMessage = e.getMessage()==null?
                    "Délai de réponse dépassé" + (socketTimeoutSec>=0?" ("+(socketTimeoutSec/1000)+"s)":"")
                    :e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            sMessage = e.getMessage()==null?"Impossible d'exécuter la requête":e.getMessage();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return new ActionResult(result, sMessage);
    }

    private ActionResult downloadTournee(Uri uriServeur) {
        Uri uri = uriServeur.buildUpon().appendEncodedPath("xml/tournees").appendQueryParameter("tournees", TextUtils.join(",", tourneeSelectionnees)).build();
        ActionResult result = downloadAndParse(uri, getString(R.string.sync_referentiel_loaded));
        return result;
    }

    private ActionResult downloadAndParse(Uri uri, String message) {
        ActionResult result = downloadUrl(uri);
        if (result.success && result.inputStream != null) {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            try {
                xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                xmlPullParser.setInput(result.inputStream, null);
                String sParserMessage = parsers.parse(xmlPullParser);
                return new ActionResult(sParserMessage == null, sParserMessage == null ? message : sParserMessage);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return new ActionResult(false, e.getMessage());
            }
        }
        return result;
    }

    private ActionResult downloadUrl(Uri uri) {
        DefaultHttpClient client = addCookies(new DefaultHttpClient(prepareHttpParameters()));
        HttpGet request = new HttpGet(uri.toString());
        String message;
        try {
            request.addHeader("x-requested-with", "XMLHttpRequest");
            Log.d("REMOCRA", "download uri = " + uri.toString());
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                return new ActionResult(true, response.getEntity().getContent());
            }
            message = "ERR " + response.getStatusLine().getStatusCode() + " / " + response.getStatusLine().getReasonPhrase();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            int socketTimeoutSec = client.getParams().getIntParameter(CoreConnectionPNames.SO_TIMEOUT, -1);
            message = e.getMessage()==null?
                    "Délai de réponse dépassé" + (socketTimeoutSec>=0?" ("+(socketTimeoutSec/1000)+"s)":"")
                    :e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            message = e.getMessage()==null?"Impossible d'exécuter la requête":e.getMessage();
        }
        return new ActionResult(false, message);
    }

    public static HttpParams prepareHttpParameters() {
        return prepareHttpParameters(DEFAULT_CONNECTION_TIMEOUT_SEC, DEFAULT_SOCKET_TIMEOUT_SEC);
    }

    public static HttpParams prepareHttpParameters(int connectionTimeoutSec, int socketTimeoutSec) {
        // Timeout connexion et réception des données
        HttpParams httpParameters = new BasicHttpParams();
        httpParameters.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeoutSec * 1000);
        httpParameters.setParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeoutSec * 1000);
        return httpParameters;
    }
}
