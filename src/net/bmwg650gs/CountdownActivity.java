package net.bmwg650gs;

import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
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

    private TextView txtDistancia;

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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        txtDistancia = (TextView) findViewById(R.id.txtDistancia);

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


        showCountdown();

    }

    @Override
    public void onStart() {
        super.onStart();

        locationClient.connect();
    }

    @Override
    public void onStop() {

        if (locationClient.isConnected()) {
            stopPeriodicUpdates();
        }

        locationClient.disconnect();

        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        isClientConnected = true;

        Toast.makeText(this, "OK", Toast.LENGTH_SHORT);

        if (areUpdatesRequested) {
//        TODO    startPeriodicUpdates();
        }
    }

    @Override
    public void onDisconnected() {
        isClientConnected = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO atualizar informacao na tela

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
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
        locationClient.requestLocationUpdates(locationRequest, this);
        isPeriodicUpdatesEnabled = true;
    }

    /**
     * In response to a request to stop updates, send a request to
     * Location Services
     */
    private void stopPeriodicUpdates() {
        locationClient.removeLocationUpdates(this);
        isPeriodicUpdatesEnabled = false;
    }


    public void showCountdown() {
        final TextView txtFaltam = (TextView) findViewById(R.id.txtFaltam);
        final TextView txtDaysLeft = (TextView) findViewById(R.id.txtDaysLeft);
        final TextView txtDias = (TextView) findViewById(R.id.txtDias);

        Time dataEvento = new Time();
        dataEvento.set(15, 10, 2013);
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


//
//
//
//new AsyncTask<Void, Void, Void>() {
//@Override
//protected Void doInBackground(Void... voids) {
//
//        LocationManager locationManager = (LocationManager) CountdownActivity.this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//
//Criteria criteria = new Criteria();
//
//criteria.setAccuracy(Criteria.ACCURACY_FINE);
//
//String provider = locationManager.getBestProvider(criteria, true);
//
//// Última posição obtida do usuário
//Location localizacaoAtual = locationManager.getLastKnownLocation(provider);
//
//TextView txtDistancia = (TextView) findViewById(R.id.txtDistancia);
//
//if (localizacaoAtual != null) {
//
//        // R. das Rosas, 544, Itatiaia - Rio de Janeiro
//        Location localizacaoDestino = new Location(Context.LOCATION_SERVICE);
//localizacaoDestino.setLatitude(-22.44163);
//localizacaoDestino.setLongitude(-44.53769);
//
//
//float kilometers = localizacaoAtual.distanceTo(localizacaoDestino) / MIL;
//
//txtDistancia.setText("e aproximadamente " + Math.round(kilometers) + " Kms (em linha reta)");
//
//} else {
//        txtDistancia.setText("---");
//}
//
//        return null;
//}
//        }.execute();