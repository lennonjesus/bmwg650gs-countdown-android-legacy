package net.bmwg650gs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import static java.lang.String.format;

/**
 * @author Lennon Jesus - lennon.jesus@gmail.com
 */
public class CountdownActivity extends FragmentActivity implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getCanonicalName();

    private static final int SEGUNDOS_POR_DIA = 86400;

    private static final int SEGUNDOS_POR_HORA = 3600;

    private static final int SEGUNDOS_POR_MINUTO = 60;

    private static final int MIL = 1000;

    private LocationRequest locationRequest;

    private LocationClient locationClient;

    private Location localizacaoDestino;

    private TextView txtDistancia;

    private CheckBox ckbAutoUpdate;


    // . . . 8

    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor editor;

    boolean areUpdatesRequested = false;

    private boolean isClientConnected = false;

    private boolean isPeriodicUpdatesEnabled = false;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        txtDistancia = (TextView) findViewById(R.id.txtDistancia);

        ckbAutoUpdate = (CheckBox) findViewById(R.id.ckbAutoUpdate);

        locationRequest = LocationRequest.create();

        /*
         * Set the update interval
         */
        locationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        locationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        // Note that location updates are off until the user turns them on
        areUpdatesRequested = false;

        // Open Shared Preferences
        sharedPreferences = getSharedPreferences(LocationUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        // Get an editor
        editor = sharedPreferences.edit();

        locationClient = new LocationClient(this, this, this);

        localizacaoDestino = new Location(Context.LOCATION_SERVICE);

        localizacaoDestino.setLatitude(-25.42378);
        localizacaoDestino.setLongitude(-48.882769);

        showCountdown();

    }

    public void showLocation() {
        Location localizacaoAtual = getLocation();

//        if (localizacaoAtual != null) {
//            txtDistancia.setText("e aproximadamente " + Math.round(localizacaoAtual.distanceTo(localizacaoDestino) / MIL) + " Kms (em linha reta)");
//        }
        txtDistancia.setText("O local do encontro será definido em breve. \nFique atento aos tópicos do fórum!");
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart()");
        super.onStart();

        locationClient.connect();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop()");

        if (locationClient.isConnected()) {
            stopPeriodicUpdates();
        }

        locationClient.disconnect();

        super.onStop();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause()");

        editor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, areUpdatesRequested);
        editor.commit();

        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");

        super.onResume();

        if (sharedPreferences.contains(LocationUtils.KEY_UPDATES_REQUESTED)) {
            areUpdatesRequested = sharedPreferences.getBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
            toggleAutoUpdate(null);

        } else {
            editor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
            editor.commit();
        }

        Log.d(TAG, "areUpdatesRequested: " + areUpdatesRequested);
        ckbAutoUpdate.setChecked(areUpdatesRequested);

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected()");

        isClientConnected = true;

        if (areUpdatesRequested) {
            startPeriodicUpdates();
        }

        showLocation();
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "onDisconnected()");

        isClientConnected = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged()");

        showLocation();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed()");

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            // If no resolution is available, display a dialog to the user with the error.
            new QualquerCoisaHelper(this).showErrorDialog(connectionResult.getErrorCode());
        }
    }

    /*
     * Handle results returned to this Activity by other Activities started with
     * startActivityForResult(). In particular, the method onConnectionFailed() in
     * LocationUpdateRemover and LocationUpdateRequester may call startResolutionForResult() to
     * start an Activity that handles Google Play services problems. The result of this
     * call returns here, to onActivityResult.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        Log.d(TAG, "onActivityResult()");


        // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST:

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:
                        Log.d(TAG, getString(R.string.resolved));
                        break;

                    // If any other result was returned by Google Play services
                    default:
                        Log.d(TAG, getString(R.string.no_resolution));
                        break;
                }
            default:
                Log.d(TAG, getString(R.string.unknown_activity_request_code, requestCode));
                break;
        }
    }

    public Location getLocation() {

        if (isGooglePlayServicesConnected()) {
            return locationClient.getLastLocation();
        }

        return null;
    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean isGooglePlayServicesConnected() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d(TAG, getString(R.string.play_services_available));
            return true;
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), TAG);
            }
            return false;
        }
    }

    public void toggleAutoUpdate(View view) {

        if (ckbAutoUpdate.isChecked()) {
            startUpdates();
        } else {
            stopUpdates();
        }

    }

    public void startUpdates() {
        areUpdatesRequested = true;

        if (isGooglePlayServicesConnected()) {
            startPeriodicUpdates();
        }
    }

    public void stopUpdates() {
        areUpdatesRequested = false;

        if (isGooglePlayServicesConnected()) {
            stopPeriodicUpdates();
        }
    }

    /**
     * In response to a request to start updates, send a request
     * to Location Services
     */
    private void startPeriodicUpdates() {
        if (areUpdatesRequested && locationClient.isConnected()) {
            locationClient.requestLocationUpdates(locationRequest, this);
        }

        isPeriodicUpdatesEnabled = true;
    }

    /**
     * In response to a request to stop updates, send a request to
     * Location Services
     */
    private void stopPeriodicUpdates() {
        if (isPeriodicUpdatesEnabled && locationClient.isConnected()) {
            locationClient.removeLocationUpdates(this);
        }
        isPeriodicUpdatesEnabled = false;
    }


    public void showCountdown() {
        final TextView txtFaltam = (TextView) findViewById(R.id.txtFaltam);
        final TextView txtDaysLeft = (TextView) findViewById(R.id.txtDaysLeft);
        final TextView txtDias = (TextView) findViewById(R.id.txtDias);

        Time dataEvento = new Time();
        dataEvento.set(18, 3, 2015);
        dataEvento.normalize(true);

        Time today = new Time();
        today.setToNow();
        today.normalize(true);

        long dataEventoMillis = dataEvento.toMillis(true);
        long todayMillis = today.toMillis(true);

        long diffMillis = dataEventoMillis - todayMillis;

        new CountDownTimer(diffMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

//                int weeks = (int) ((millisUntilFinished / 1000) / 604800);
                int dias = (int) ((millisUntilFinished / MIL) / SEGUNDOS_POR_DIA);
                int horas = (int) (((millisUntilFinished / MIL) - (dias * SEGUNDOS_POR_DIA)) / SEGUNDOS_POR_HORA);
                int minutos = (int) (((millisUntilFinished / MIL) - ((dias * SEGUNDOS_POR_DIA) + (horas * SEGUNDOS_POR_HORA))) / SEGUNDOS_POR_MINUTO);
                int segundos = (int) ((millisUntilFinished / MIL) % SEGUNDOS_POR_MINUTO);

                txtDaysLeft.setText("" + dias + " dias, " + format("%02d", horas) + ":" + format("%02d", minutos) + ":" + format("%02d", segundos) + " horas");

            }

            @Override
            public void onFinish() {
                txtFaltam.setText("");
                txtDaysLeft.setText("É hoje!");
                txtDias.setText("");
            }
        }.start();
    }

}