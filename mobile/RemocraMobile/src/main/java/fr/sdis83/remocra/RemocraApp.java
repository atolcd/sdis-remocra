package fr.sdis83.remocra;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by jpt on 23/10/13.
 */
public class RemocraApp extends Application implements LocationListener {

    public static final String LOCATION_CHANGE = "fr.sdis83.remocra.RemocraApp.locationChange";
    private LocationManager locationManager;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int DELAY_REFRESH = 1000 * 30 * 1; // 30 sec
    private static final int DISTANCE_REFRESH = 10; // 10 m
    private Location currentLocation = null;

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location lastNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (isBetterLocation(currentLocation, lastNetwork)) {
            currentLocation = lastNetwork;
        }

        // Ecoute de l'activité de l'application : application au premier plan / arrière plan
        // pour activer / désactiver la géolocalisation
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            private int resumed = 0;
            private int stopped = 0;
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }
            @Override
            public void onActivityStarted(Activity activity) {
                if (resumed == stopped) {
                    Log.d("REMOCRA", "RemocraApp au premier plan");
                    startGeoLocalisation();
                }
            }
            @Override
            public void onActivityResumed(Activity activity) {
                ++resumed;
            }
            @Override
            public void onActivityPaused(Activity activity) {
            }
            @Override
            public void onActivityStopped(Activity activity) {
                ++stopped;
                if (resumed == stopped) {
                    Log.d("REMOCRA", "RemocraApp en arrière plan");
                    stopGeoLocalisation();
                }
            }
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }
            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    @Override
    public void onTerminate() {
        // Par sécurité, on arrête la géolocalisation
        stopGeoLocalisation();
        super.onTerminate();
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isBetterLocation(location, currentLocation)) {
            currentLocation = location;
            Intent intent = new Intent();
            intent.setAction(LOCATION_CHANGE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        locationManager.requestLocationUpdates(provider, DELAY_REFRESH, DISTANCE_REFRESH, this);
    }

    @Override
    public void onProviderDisabled(String provider) {
        stopGeoLocalisation();
        startGeoLocalisation();
    }

    public void stopGeoLocalisation() {
        locationManager.removeUpdates(this);
    }

    public void startGeoLocalisation() {
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, DELAY_REFRESH, DISTANCE_REFRESH, this);
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, DELAY_REFRESH, DISTANCE_REFRESH, this);
        }
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (location == null) {
            // A empty new location is never better
            return false;
        }
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
