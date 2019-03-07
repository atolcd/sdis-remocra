package fr.sdis83.remocra;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.HydrantTable;
import fr.sdis83.remocra.fragment.components.HydrantItem;

/**
 * Created by vde on 17/02/2017.
 */
public class MapHydrantActivity extends FragmentActivity implements LocationListener {

    private ImageButton btCenterMap;
    private ImageButton btCompassMapOn;
    private ImageButton btCompassMapOff;

    private OverlayItem currentItem;

    private BoundingBox globalBBox;
    private MapView mapView;
    private Boolean automaticNavigation;
    private ItemizedOverlayWithFocus<OverlayItem> hydrantOverlay;
    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carte_hydrant);

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // Récupération des hydrants existants
        Cursor hydrantCursor = getHydrants();

        List<GeoPoint> toBoundingZoom = new ArrayList<GeoPoint>();
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

        globalBBox = null;
        if(hydrantCursor != null && hydrantCursor.moveToFirst()) {
            int count = 0;
            while (!hydrantCursor.isAfterLast()) {

                ContentValues hydrantValue = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(hydrantCursor, hydrantValue);

                // Récupération des informations de l'hydrant
                Double lat = hydrantValue.getAsDouble(HydrantTable.COLUMN_COORD_LAT);
                Double lon = hydrantValue.getAsDouble(HydrantTable.COLUMN_COORD_LON);

                String title = hydrantValue.getAsString(HydrantTable.COLUMN_NUMERO);

                GeoPoint geoPoint = new GeoPoint(lat, lon);
                Drawable currentMarker = getResources().getDrawable(R.drawable.ic_hydrant);

                HydrantItem item = new HydrantItem(title, "", geoPoint);

                item.setMarker(currentMarker);
                item.setIdHydrant(hydrantValue.getAsLong(HydrantTable._ID));

                items.add(item); // Lat/Lon decimal degrees

                toBoundingZoom.add(geoPoint);
                hydrantCursor.moveToNext();
            }
            globalBBox = BoundingBox.fromGeoPoints(toBoundingZoom);
        }


        mapView = (MapView) findViewById(R.id.carte_hydrant);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setTilesScaledToDpi(true);

        // Overlay des items de la carte.
        hydrantOverlay = buildMapOverlay(items);
        mapView.getOverlays().add(hydrantOverlay);
        Configuration.getInstance().setUserAgentValue(getUrlServeur());
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.invalidate();

        // Ajout du bouton qui centrer sur sa position gps
        addCenterMapBnt(hydrantOverlay);
        addNavigationMapBntOn(hydrantOverlay);
        addNavigationMapBntOff(hydrantOverlay);
        automaticNavigation = false;
        initialiseLocationManager();

        // Utilisation d'un delay pour laisser le temps à la carte de se charger
        // dans l'activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(globalBBox == null) {
                    // Réglage pour centrer sur la france s'il n'y a pas d'hydrant pour construire le bbox
                    mapView.getController().setCenter(new GeoPoint(46.567925, 1.602881));
                    mapView.getController().setZoom(6);
               } else {
                    mapView.zoomToBoundingBox(globalBBox, false);
               }
            }
        }, 200);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        killlocation();
    }

    /**
     * Construction de l'overlay de la carte
     *
     * @param items
     * @return
     */
    private ItemizedOverlayWithFocus<OverlayItem> buildMapOverlay(List<OverlayItem> items) {

        final ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(
                this, items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        // Sur un appuis cours => retour à la fiche de l'hydrant
                        if(item instanceof HydrantItem) {
                            Intent intent = new Intent(MapHydrantActivity.this, HydrantActivity.class);
                            intent.putExtra(HydrantActivity.ID_HYDRANT, ((HydrantItem) item).getIdHydrant());
                            startActivity(intent);
                        }
                        return false;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                });
        mOverlay.setFocusItemsOnTap(true);

        return mOverlay;
    }

    /**
     * Récupération des hydrants stockés depuis la base SQLite
     *
     * @return
     */
    private Cursor getHydrants() {

        // Récupération des hydrants stockés
        Cursor hydrantCursor = getContentResolver().query(RemocraProvider.CONTENT_HYDRANT_URI,
                new String[]{
                        HydrantTable._ID,
                        HydrantTable.COLUMN_NUMERO,
                        HydrantTable.COLUMN_COORD_LAT,
                        HydrantTable.COLUMN_COORD_LON}
                , null, null, null);

        return hydrantCursor;
    }

    /**
     * Initialisation / ajout du bouton qui centre la carte sur sa localisation
     *
     * @param mOverlay overlay qui gère les items de la carte
     */
    private void addCenterMapBnt(final ItemizedOverlayWithFocus mOverlay) {
        btCenterMap = (ImageButton) findViewById(R.id.ic_menu_mylocation);
        btCenterMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((RemocraApp) getApplicationContext()).getCurrentLocation() != null) {
                    GeoPoint currentLocation = new GeoPoint(((RemocraApp) getApplicationContext()).getCurrentLocation());
                    mapView.getController().animateTo(currentLocation);
                }else {
                    Toast.makeText(getApplicationContext(), "Localisation GPS inactive ou inconnue", Toast.LENGTH_SHORT).show();
                }

                automaticNavigation = false;
            }
        });
    }

    private void addNavigationMapBntOff(final ItemizedOverlayWithFocus mOverlay) {
        btCompassMapOff = (ImageButton) findViewById(R.id.ic_menu_compass_off);
        btCompassMapOn = (ImageButton) findViewById(R.id.ic_menu_compass_on);
        btCompassMapOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                automaticNavigation = true;
                if (((RemocraApp) getApplicationContext()).getCurrentLocation() != null) {
                    onLocationChanged(((RemocraApp) getApplicationContext()).getCurrentLocation());
                    btCompassMapOn.setVisibility(View.VISIBLE);
                    btCompassMapOff.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getApplicationContext(), "Localisation GPS inactive ou inconnue", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void addNavigationMapBntOn(final ItemizedOverlayWithFocus mOverlay) {
        btCompassMapOff = (ImageButton) findViewById(R.id.ic_menu_compass_off);
        btCompassMapOn = (ImageButton) findViewById(R.id.ic_menu_compass_on);
        btCompassMapOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btCompassMapOff.setVisibility(View.VISIBLE);
                btCompassMapOn.setVisibility(View.GONE);
                automaticNavigation = false;
            }
        });
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

    @Override
    public void onLocationChanged(Location location) {
        GeoPoint currentLocation = new GeoPoint(location);
        Drawable currentMarker = getResources().getDrawable(R.drawable.position);
        if (automaticNavigation != null && automaticNavigation == true) {
            mapView.getController().animateTo(currentLocation);
        }
        if (currentItem != null) {
            hydrantOverlay.removeItem(currentItem);
        }
        currentItem = new OverlayItem("", "", currentLocation);
        currentItem.setMarker(currentMarker);
        mapView.getOverlays().clear();
        mapView.getOverlays().add(hydrantOverlay);
        if(currentItem != null){
            hydrantOverlay.addItem(currentItem);
        }
        hydrantOverlay.setFocusItemsOnTap(false);
        mapView.invalidate();
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void initialiseLocationManager() {
        if(((RemocraApp) getApplicationContext()).getCurrentLocation() != null) {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Localisation GPS inactive ou inconnue", Toast.LENGTH_SHORT).show();
        }
    }else {
            Toast.makeText(getApplicationContext(), "Localisation GPS inactive ou inconnue", Toast.LENGTH_SHORT).show();
        }
    }

    public void killlocation()
    {
        try
        {
            locationManager.removeUpdates(this);
            locationManager=null;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private String getUrlServeur() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString(ParamActivity.SERVEUR_URL, "");
    }

}
