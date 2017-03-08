package fr.sdis83.remocra;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import fr.sdis83.remocra.adapter.ViewHydrantAdapter;
import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.AnomalieNatureTable;
import fr.sdis83.remocra.database.AnomalieTable;
import fr.sdis83.remocra.database.HydrantTable;
import fr.sdis83.remocra.fragment.AbstractHydrant;
import fr.sdis83.remocra.util.DbUtils;

public class HydrantActivity extends FragmentActivity implements ActionBar.TabListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static String ID_HYDRANT = "ID_HYDRANT";

    private static final int URL_LOADER = 0;

    private ViewHydrantAdapter mViewHydrantAdapter;
    private ViewPager mViewPager;
    private long idHydrant;
    private String mNumero;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hydrant);
        if (savedInstanceState != null && savedInstanceState.containsKey(ID_HYDRANT)) {
            idHydrant = savedInstanceState.getLong(ID_HYDRANT);
        } else {
            idHydrant = getIntent().getLongExtra(ID_HYDRANT, -1);
        }
        Bundle bundle = new Bundle();
        bundle.putLong(ID_HYDRANT, idHydrant);
        getSupportLoaderManager().initLoader(URL_LOADER, bundle, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                doSave(mViewPager.getCurrentItem());
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        doSave(mViewPager.getCurrentItem());
        getContentResolver().notifyChange(RemocraProvider.CONTENT_TOURNEE_URI, null);
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ID_HYDRANT, idHydrant);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(ID_HYDRANT)) {
            getSupportLoaderManager().restartLoader(URL_LOADER, savedInstanceState, this);
        }
    }

    public void setTypeSaisie(HydrantTable.TYPE_SAISIE newTypeSaisie) {
        mViewHydrantAdapter.setTypeSaisie(newTypeSaisie);
        updateTitle();
    }

    private void doSave(final int position) {
        if (mViewHydrantAdapter.getTypeSaisie() == HydrantTable.TYPE_SAISIE.LECT) {
            // en lecture seule, on ne permet aucune sauvegarde (même si l'interface le permet pour l'instant)
            return;
        }
        Fragment fragment = mViewHydrantAdapter.getCurrentFragment(position);
        if (fragment != null && fragment instanceof AbstractHydrant) {
            final ContentValues values = ((AbstractHydrant) fragment).getDataToSave();
            if (values.size() > 0) {

                if (isAllTabRead()) {
                    switch (mViewHydrantAdapter.getTypeSaisie()) {
                        case LECT:
                        case CREA:
                            break;
                        case RECEP:
                            values.put(HydrantTable.COLUMN_DATE_RECEP, new Date().getTime());
                            break;
                        case RECO:
                            values.put(HydrantTable.COLUMN_DATE_RECO, new Date().getTime());
                            break;
                        case CTRL:
                            values.put(HydrantTable.COLUMN_DATE_CTRL, new Date().getTime());
                            break;
                        case VERIF:
                            values.put(HydrantTable.COLUMN_DATE_VERIF, new Date().getTime());
                            break;
                    }
                }
                values.put(HydrantTable.COLUMN_DATE_MODIF, new Date().getTime());
                values.put(HydrantTable.COLUMN_TYPE_SAISIE, mViewHydrantAdapter.getTypeSaisie().toString());

                boolean anomalieModified = false;
                if (values.containsKey(HydrantTable.COLUMN_ANOMALIES) || values.containsKey(HydrantTable.COLUMN_ANOMALIES_APP)) {
                    anomalieModified = true;
                }
                final boolean needRecalculDisponibilite = anomalieModified;
                final Uri uri = Uri.withAppendedPath(RemocraProvider.CONTENT_HYDRANT_URI, String.valueOf(idHydrant));
                assert (uri != null);
                final ContentResolver resolver = this.getContentResolver();
                // On enregistre les modifications dans une tâche asynchrone.
                AsyncTask<Void, Void, Boolean> backUpdate = new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        resolver.update(uri, values, null, null);
                        if (needRecalculDisponibilite) {
                            recalculeDisponibilite(resolver, uri);
                        }
                        return true;
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        setTabRead(position);
                    }
                };
                backUpdate.execute();
            }
        }
    }

    public void setTabRead(int position) {
        if (getActionBar() != null) {
            ActionBar actionBar = getActionBar();
            if (position < actionBar.getTabCount()) {
                ActionBar.Tab tab = getActionBar().getTabAt(position);
                if (tab != null) {
                    tab.setIcon(R.drawable.icon_read);
                    tab.setTag(Boolean.TRUE);
                }
            }
        }
    }

    public boolean isAllTabRead() {
        if (getActionBar() != null) {
            for (int i = 0; i < getActionBar().getTabCount(); ++i) {
                ActionBar.Tab tab = getActionBar().getTabAt(i);
                if (!Boolean.TRUE.equals(tab.getTag())) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
        setTabRead(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        doSave(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = Uri.withAppendedPath(RemocraProvider.CONTENT_HYDRANT_URI, String.valueOf(bundle.getLong(ID_HYDRANT)));
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            if (mViewHydrantAdapter != null) {
                mViewHydrantAdapter.setHydrant(cursor, mViewPager.getCurrentItem());
            } else {
                // Set up the adapter.
                mViewHydrantAdapter = new ViewHydrantAdapter(cursor,getSupportFragmentManager(), getBaseContext());
                mViewPager = (ViewPager) findViewById(R.id.viewPager);
                mViewPager.setAdapter(mViewHydrantAdapter);
                mViewPager.setOffscreenPageLimit(mViewHydrantAdapter.getCount() - 1);

                final ActionBar actionBar = getActionBar();
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);

                // For each of the sections in the app, add a tab to the action bar.
                for (int i = 0; i < mViewHydrantAdapter.getCount(); i++) {
                    ActionBar.Tab tab = actionBar.newTab().setText(mViewHydrantAdapter.getPageTitle(i)).setTabListener(this);
                    tab.setIcon(R.drawable.icon_unread);
                    actionBar.addTab(tab);
                }

                // Set up the gesture to swipe between tab.
                mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });

                mViewHydrantAdapter.setHydrant(cursor, 0);
                setTabRead(0);
            }
            mNumero = cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_NUMERO));
            updateTitle();
        } else {
            Log.d("REMOCRA", "loadDataFromCursor - nothing");
        }
    }

    private void updateTitle() {
        int id = getResources().getIdentifier(mViewHydrantAdapter.getTypeSaisie().toString(), "string", "fr.sdis83.remocra");
        setTitle(mNumero + " - " + getResources().getString(id));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        //
    }

    private void recalculeDisponibilite(ContentResolver resolver, Uri uri) {
        // Récupération des données actuelles de l'hydrant
        Cursor hydrantCursor = resolver.query(uri, new String[]{
                HydrantTable.COLUMN_ANOMALIES,
                HydrantTable.COLUMN_ANOMALIES_APP,
                HydrantTable.COLUMN_DISPO,
                HydrantTable.COLUMN_DISPO_HBE,
                HydrantTable.COLUMN_HBE,
                HydrantTable.COLUMN_NATURE}
                , null, null, null);
        Cursor anomalies = null;
        try {
            if (hydrantCursor.moveToFirst()) {
                ContentValues hydrantValue = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(hydrantCursor, hydrantValue);

                // Récupération des anomalies de l'hydrant et de leurs valeurs
                String ano = hydrantValue.getAsString(HydrantTable.COLUMN_ANOMALIES);
                String anoApp = hydrantValue.getAsString(HydrantTable.COLUMN_ANOMALIES_APP);
                if (TextUtils.isEmpty(ano)) {
                    ano = anoApp;
                } else if (!TextUtils.isEmpty(anoApp)) {
                    ano += "," + anoApp;
                }
                if (ano == null) {
                    ano = "";
                }
                String[] lstAnomalie = ano.split(",");
                List<String> selectArg = new ArrayList<String>();
                selectArg.addAll(Arrays.asList(lstAnomalie));
                selectArg.add(hydrantValue.getAsString(HydrantTable.COLUMN_NATURE));
                // TODO : récupérer le type de saisie courant
                String typeSaisie = "CTRL";
                selectArg.add(typeSaisie);
                selectArg.add(typeSaisie);

                anomalies = resolver.query(
                        RemocraProvider.CONTENT_ANOMALIE_NATURE_URI,
                        new String[]{
                                AnomalieTable.TABLE_NAME + "." + AnomalieTable.COLUMN_CODE,
                                AnomalieNatureTable.TABLE_NAME + "." + AnomalieNatureTable.COLUMN_VALEUR,
                                AnomalieNatureTable.TABLE_NAME + "." + AnomalieNatureTable.COLUMN_VALEUR_HBE
                        },
                        AnomalieTable.TABLE_NAME + "." + AnomalieTable._ID + " IN " + DbUtils.makePlaceHolders(lstAnomalie) +
                                " AND " + AnomalieNatureTable.TABLE_NAME + "." + AnomalieNatureTable.COLUMN_NATURE + "=?" +
                                " AND coalesce(" + AnomalieNatureTable.TABLE_NAME + "." + AnomalieNatureTable.COLUMN_TYPE_SAISIE + ", ?) = ?",
                        selectArg.toArray(new String[selectArg.size()]),
                        null);
                assert (anomalies != null);
                // Calcul des points
                Long sommeValeur = 0L;
                Long sommeValeurHbe = 0L;
                Long valeur;
                boolean hasNC = false;
                while (anomalies.moveToNext()) {
                    String code = anomalies.getString(anomalies.getColumnIndex(AnomalieTable.COLUMN_CODE));
                    if (code.endsWith("_NC")) {
                        hasNC = true;
                    }
                    valeur = anomalies.getLong(anomalies.getColumnIndex(AnomalieNatureTable.COLUMN_VALEUR));
                    if (valeur != null) {
                        sommeValeur += valeur;
                    }
                    valeur = anomalies.getLong(anomalies.getColumnIndex(AnomalieNatureTable.COLUMN_VALEUR_HBE));
                    if (valeur != null) {
                        sommeValeurHbe += valeur;
                    }
                }

                String dispo = sommeValeur >= 5 ? "INDISPO" : (hasNC ? "NON_CONFORME" : "DISPO");
                String dispoHbe = sommeValeurHbe >= 5 ? "INDISPO" : "DISPO";

                ContentValues value = new ContentValues();
                if (!dispo.equals(hydrantValue.getAsString(HydrantTable.COLUMN_DISPO))) {
                    value.put(HydrantTable.COLUMN_DISPO, dispo);
                }
                if (!dispoHbe.equals(hydrantValue.getAsString(HydrantTable.COLUMN_DISPO_HBE))) {
                    value.put(HydrantTable.COLUMN_DISPO_HBE, dispoHbe);
                }
                if (value.size() > 0) {
                    resolver.update(uri, value, null, null);
                }
            }
        } finally {
            if (anomalies != null) {
                anomalies.close();
            }
            if (hydrantCursor != null) {
                hydrantCursor.close();
            }
        }
    }
}
