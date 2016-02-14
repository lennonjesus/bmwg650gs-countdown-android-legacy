package net.bmwg650gs;

import android.content.Context;
import android.location.Location;

/**
 * Defines app-wide constants and utilities
 */
public final class LocationUtils {

    public static final String SHARED_PREFERENCES = "bmwg650gs.SHARED_PREFERENCES";

    public static final String KEY_UPDATES_REQUESTED = "bmwg650gs.KEY_UPDATES_REQUESTED";

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    /*
     * Constants for location update parameters
     */
    public static final int MILLISECONDS_PER_SECOND = 1000;

    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    // A fast interval ceiling
    public static final int FAST_CEILING_IN_SECONDS = 1;

    // A fast ceiling of update intervals, used when the app is visible
    public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;

    /**
     * Get the latitude and longitude from the Location object returned by
     * Location Services.
     *
     * @param currentLocation A Location object containing the current location
     * @return The latitude and longitude of the current location, or null if no
     *         location is available.
     */
    public static String getLatLng(Context context, Location currentLocation) {
        if (currentLocation != null) {
            return context.getString(net.bmwg650gs.R.string.latitude_longitude, currentLocation.getLatitude(), currentLocation.getLongitude());
        } else {
            return "";
        }
    }
}

