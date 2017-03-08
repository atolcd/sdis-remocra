/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.sdis83.remocra;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.HydrantTable;
import fr.sdis83.remocra.dialog.NewHydrant;
import fr.sdis83.remocra.fragment.Accueil;
import fr.sdis83.remocra.fragment.ListHydrant;
import fr.sdis83.remocra.fragment.ListTournee;
import fr.sdis83.remocra.holder.NatureHolder;
import fr.sdis83.remocra.serializer.TourneeSerializer;

/**
 * Activité "principale" gérant le menu de gauche, et les fragments internes
 */
public class MainActivity extends FragmentActivity implements ListTournee.ListTourneeListener, ListHydrant.ListHydrantListener, NewHydrant.NewHydrantListener {
    /**
     * Attribute key for the list item text.
     */
    private static final String LABEL = "LABEL";
    private static final String ICON = "ICON";
    private static final String CURRENT_ITEM_DRAWER = "CurrentItemDrawer";
    private static final String CURRENT_FRAGMENT = "CurrentFragment";
    private static final String CURRENT_BUNDLE = "CurrentBundle";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    public static final int LISTE_TOURNEE = 1;
    public static final int LISTE_PT_EAU = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Si le GPS n'est pas activé, informe l'utilisateur et l'invite à bien paramétrer son terminal
        maybeAlertGPSNotActive();

        setContentView(R.layout.main);
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // set up the drawer's list view with items and click listener
        List<Map<String, Object>> data = buildListForSimpleAdapter();
        SimpleAdapter adapter = new SimpleAdapter(
                getBaseContext(),
                data,
                R.layout.drawer_list_item,
                new String[]{LABEL, ICON},
                new int[]{android.R.id.text1, android.R.id.icon}
        );
        mDrawerList.setAdapter(adapter);


        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public boolean indicator;

            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                mDrawerToggle.setDrawerIndicatorEnabled(this.indicator);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                indicator = mDrawerToggle.isDrawerIndicatorEnabled();
                mDrawerToggle.setDrawerIndicatorEnabled(true);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (savedInstanceState == null) {
            selectItemFromDrawer(0, false);
        }

        checkParametreServeur();
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
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt(CURRENT_ITEM_DRAWER, mDrawerList.getCheckedItemPosition());
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment instanceof ListHydrant) {
            outState.putString(CURRENT_FRAGMENT, "ListHydrant");
            outState.putBundle(CURRENT_BUNDLE, fragment.getArguments());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        String fragmentName = savedInstanceState.getString(CURRENT_FRAGMENT);
        Bundle bundle = savedInstanceState.getBundle(CURRENT_BUNDLE);
        if (fragmentName != null && bundle != null) {
            if ("ListHydrant".equals(fragmentName)) {
                Long idTournee = bundle.getLong(ListHydrant.PARAM_TOURNEE);
                if (idTournee != null && idTournee > 0) {
                    // openTournee(idTournee, false);
                    setTitle(MessageFormat.format("Tournée {0}", idTournee));
                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    return;
                }
            }
        }
        selectItemFromDrawer(savedInstanceState.getInt(CURRENT_ITEM_DRAWER, 0), true);

    }

    private List<Map<String, Object>> buildListForSimpleAdapter() {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        // Renseigner ce "imageId" avec une ressource image pour mettre une icône au menu
        Integer imageId = null; //getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()), "drawable", getPackageName());

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(LABEL, getString(R.string.accueil));
        map.put(ICON, imageId);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put(LABEL, getString(R.string.tournees));
        map.put(ICON, imageId);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put(LABEL, getString(R.string.pteau));
        map.put(ICON, imageId);
        list.add(map);

        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) mDrawerList.getAdapter().getItem(mDrawerList.getCheckedItemPosition());
        setTitle(data.get(LABEL).toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                // si le drawer est désactivé, utiliser le bouton "home" comme un "back"
                if (!mDrawerToggle.isDrawerIndicatorEnabled()) {
                    onBackPressed();
                    return true;
                }
                return super.onOptionsItemSelected(item);
            case R.id.action_add_hydrant:
                NewHydrant dialog = new NewHydrant();
                dialog.show(getSupportFragmentManager(), "newHydrant");
                return true;
            case R.id.action_param:
                ParamActivity.askPasswordBeforeParams(this);
                return true;
            case R.id.action_synchro:
                intent = new Intent(this, SyncActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_help:
                intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_imp_exp:
                intent = new Intent(this, ImportExportActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void selectItemFromDrawer(int position, boolean onlyDrawner) {
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) mDrawerList.getAdapter().getItem(position);
        if (onlyDrawner == false) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = null;
            if (position == LISTE_TOURNEE) { // Liste tournée
                fragment = new ListTournee();
            } else if (position == LISTE_PT_EAU) { // Liste hydrant
                fragment = ListHydrant.newInstance(null);
            } else {
                fragment = new Accueil();
            }
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "init").commit();
        }
        mDrawerList.setItemChecked(position, true);
        setTitle(data.get(LABEL).toString());
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void openTournee(Long idTournee, boolean anim) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ListHydrant fragment = ListHydrant.newInstance(idTournee);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (anim) {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        transaction.replace(R.id.content_frame, fragment);
        if (anim) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
        setTitle(MessageFormat.format("Tournée {0}", idTournee));
    }

    @Override
    public void openHydrant(Long idHydrant) {
        Intent intent = new Intent(this, HydrantActivity.class);
        intent.putExtra(HydrantActivity.ID_HYDRANT, idHydrant);
        startActivity(intent);
    }

    @Override
    public void onNewHydrant(NatureHolder nature, Location location) {
        // une nature est sélectionnée, on ajoute un nouvel hydrant
        if (nature != null) {
            ContentValues values = new ContentValues();
            String natureCode = nature.getCode();
            values.put(HydrantTable.COLUMN_NATURE, nature.getId());
            values.put(HydrantTable.COLUMN_TYPE_HYDRANT, nature.getTypeNature());
            values.put(HydrantTable.COLUMN_NUMERO, (natureCode.equals("PI") ? "PI " : natureCode.equals("BI") ? "BI " :natureCode.equals("PA") ? "PA ":natureCode.equals("RI") ? "RI ": "PN ") + "*");
            values.put(HydrantTable.COLUMN_TYPE_SAISIE, HydrantTable.TYPE_SAISIE.CREA.toString());

            values.put(HydrantTable.COLUMN_COORD_LON, location.getLongitude());
            values.put(HydrantTable.COLUMN_COORD_LAT, location.getLatitude());

            Uri uriNewHydrant = getContentResolver().insert(RemocraProvider.CONTENT_HYDRANT_URI, values);
            String id = uriNewHydrant.getLastPathSegment();
            if (!TextUtils.isEmpty(id) && TextUtils.isDigitsOnly(id) && Long.valueOf(id) != -1) {
                openHydrant(Long.valueOf(id));
            }
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItemFromDrawer(position, false);
        }
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        MenuItem mi = menu.findItem(R.id.action_filter);
        if (mi != null) {
            mi.setVisible(!drawerOpen);
        }

        mi = menu.findItem(R.id.action_add_hydrant);
        if (mi != null) {
            mi.setVisible(GlobalRemocra.getInstance(this).getCanAddHydrant());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void maybeAlertGPSNotActive() {
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.gps_positionning).setMessage(Html.fromHtml(getString(R.string.gps_notactive)))
                    .setPositiveButton(R.string.localisation_param, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).setNegativeButton(R.string.no_thanks, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int id) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }
}
