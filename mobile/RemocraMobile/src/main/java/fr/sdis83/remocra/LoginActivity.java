package fr.sdis83.remocra;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.UserTable;
import fr.sdis83.remocra.parser.RemocraParser;
import fr.sdis83.remocra.widget.InfoView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

    /**
     * The default identifier to populate the identifier field with.
     */
    public static final String EXTRA_IDENTIFIER = "fr.sdis83.remocra.extra.IDENTIFIER";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // Values for identifier and password at the time of the login attempt.
    private String mIdentifier;
    private String mPassword;

    // UI references.
    private EditText mIdentifierView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        // Message d'information
        InfoView infoView = (InfoView) findViewById(R.id.textViewInfo);
        infoView.setHtmlContent(Html.fromHtml(getResources().getString(R.string.loginInfo)));

        // Set up the login form.
        mIdentifier = getIntent().getStringExtra(EXTRA_IDENTIFIER);
        if (TextUtils.isEmpty(mIdentifier)) {
            mIdentifier = GlobalRemocra.getInstance(getApplicationContext()).getLogin();
        }

        mIdentifierView = (EditText) findViewById(R.id.identifier);
        mPasswordView = (EditText) findViewById(R.id.password);
        String mIdentiFromBdd = getCurentUser();
        if (mIdentiFromBdd != null) {
            mIdentifier = mIdentiFromBdd;
            mIdentifierView.setEnabled(false);
            mPasswordView.requestFocus();
            findViewById(R.id.change_user_button).setVisibility(View.VISIBLE);
        } else {
            if (BuildConfig.DEBUG) {
                if (TextUtils.isEmpty(mIdentifier)) {
                    mIdentifier = "sdis-adm-app";
                }
            }
        }
        if (BuildConfig.DEBUG) {
            mPasswordView.setText("remocra");
        }
        mIdentifierView.setText(mIdentifier);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form_and_btn);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        findViewById(R.id.change_user_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUser();
            }
        });

        checkParametreServeur();
    }

    private void changeUser() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.change_user_title))
                .setMessage(getString(R.string.change_user_message))
                .setPositiveButton(getString(R.string.continuer), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getContentResolver().delete(RemocraProvider.CONTENT_USER_URI, null, null);
                        getContentResolver().delete(RemocraProvider.CONTENT_TOURNEE_URI, null, null);
                        getContentResolver().delete(RemocraProvider.CONTENT_HYDRANT_URI, null, null);
                        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }


    private String getCurentUser() {
        Cursor cursor = null;
        String username = null;
        try {
            cursor = getContentResolver().query(RemocraProvider.CONTENT_USER_URI, new String[]{UserTable.COLUMN_USERNAME}, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                username = cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_USERNAME));
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return username;
    }

    private String getUrlServeur() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString(ParamActivity.SERVEUR_URL, "");
    }

    private void checkParametreServeur() {
        // Si le mot de passe n'est pas renseigné, on définit la valeur par défaut
        if (TextUtils.isEmpty(getUrlServeur())) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(ParamActivity.SERVEUR_URL, ParamActivity.SERVEUR_URL_DEFAULT_VALUE);
            editor.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ParamActivity.PARAMETRAGE_INITIAL) {
            checkParametreServeur();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.help:
                showHelp();
                return true;
            case R.id.action_param:
                ParamActivity.askPasswordBeforeParams(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showHelp() {
        startActivity(new Intent(this, HelpActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid identifier, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mIdentifierView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mIdentifier = mIdentifierView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 2) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid identifier address.
        if (TextUtils.isEmpty(mIdentifier)) {
            mIdentifierView.setError(getString(R.string.error_field_required));
            focusView = mIdentifierView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            mAuthTask = new UserLoginTask();
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int longAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);
            mLoginStatusView.animate()
                    .setDuration(longAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });
            mLoginFormView.animate()
                    .setDuration(longAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            Cursor cursor = null;
            ContentResolver resolver = getApplicationContext().getContentResolver();
            try {
                // on vérifie qu'il y a des données en BDD
                cursor = resolver.query(RemocraProvider.CONTENT_USER_URI, new String[]{"count(*) NB_USER"}, null, null, null);
                if (cursor != null && cursor.moveToNext()) {
                    if (cursor.getLong(0) == 0) {
                        // Aucune donnée en BDD, on va tenter une authentification sur le réseaux et insérer l'utilisateur en BDD
                        Log.d("REMOCRA", "Base de données vide, tentative de connexion au réseau");

                        ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
                        boolean isConnected = netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED;
                        if (!isConnected) {
                            Log.d("REMOCRA", "Pas de connexion réseau");
                            return "Aucune connexion n'est disponible";
                        }

                        String urlServeur = getUrlServeur();
                        if (!TextUtils.isEmpty(urlServeur)) {
                            String message = tryLogin(Uri.parse(urlServeur), mIdentifier, mPassword);
                            if (!"OK".equals(message)) {
                                return message;
                            }
                        }
                    }
                }
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
            try {
                cursor = resolver.query(RemocraProvider.CONTENT_USER_URI,
                        null,
                        UserTable.COLUMN_USERNAME + "=?",
                        new String[]{mIdentifier},
                        null);

                if (cursor != null && cursor.moveToNext()) {
                    if (mPassword.equals(cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_PASSWORD)))) {
                        Context context = getApplicationContext();

                        GlobalRemocra.getInstance(context).setLogin(mIdentifier);
                        GlobalRemocra.getInstance(context).setPassword(mPassword);

                        String data = cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_HYDRANT));
                        GlobalRemocra.getInstance(context).setCanAddHydrant(data.indexOf("C") != -1);

                        data = cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_RECONNAISSANCE));
                        GlobalRemocra.getInstance(context).setCanReconnaissance(data.indexOf("C") != -1);

                        data = cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_CONTROLE));
                        GlobalRemocra.getInstance(context).setCanControl(data.indexOf("C") != -1);

                        data = cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_RECEPTION));
                        GlobalRemocra.getInstance(context).setCanReception(data.indexOf("C") != -1);

                        data = cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_MCO));
                        GlobalRemocra.getInstance(context).setCanSetMco(data.indexOf("C") != -1);

                        return "";
                    }
                }
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
            return getString(R.string.error_incorrect_password);
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected void onPostExecute(final String message) {
            mAuthTask = null;
            showProgress(false);

            if (TextUtils.isEmpty(message)) {
                // On stocke l'identifiant
                getIntent().putExtra(EXTRA_IDENTIFIER, mIdentifier);
                // On informe
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, getString(R.string.login_signed_in, mIdentifier), Toast.LENGTH_SHORT);

                toast.show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));

                // Arrêt de l'activité
                finish();
            } else {
                mPasswordView.setError(message);
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    // TODO : mettre des messages plus explicites que les "e.getMessages()"
    private String tryLogin(Uri uriServeur, String mLogin, String mPassword) {
        String message = null;
        DefaultHttpClient httpClient = new DefaultHttpClient(SyncActivity.prepareHttpParameters(SyncActivity.AUTH_CONNECTION_TIMEOUT_SEC, SyncActivity.AUTH_SOCKET_TIMEOUT_SEC));
        HttpPost request = new HttpPost(uriServeur.buildUpon().appendEncodedPath("auth/login/xml").build().toString());
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("username", mLogin));
        nameValuePairs.add(new BasicNameValuePair("password", mPassword));
        try {
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                XmlPullParser xmlPullParser = Xml.newPullParser();
                try {
                    xmlPullParser.setInput(response.getEntity().getContent(), null);
                    RemocraParser parser = new RemocraParser(getApplicationContext());
                    // Pour que les parseurs y aient accès, on les réinsère dans le GlobalRemocra.
                    GlobalRemocra.getInstance(getApplicationContext()).setLogin(mLogin);
                    GlobalRemocra.getInstance(getApplicationContext()).setPassword(mPassword);
                    message = parser.parse(xmlPullParser);
                    if (message == null) {
                        if (parser.insertIntoDatabase() > 0) {
                            // Parsage réussi
                            message = "OK";
                        }
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
            message = e.getMessage()==null?"Impossible de s'authentifier":e.getMessage();
        }

        return message;
    }
}
