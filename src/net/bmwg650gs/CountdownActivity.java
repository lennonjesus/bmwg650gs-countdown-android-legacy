package net.bmwg650gs;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.Time;
import android.widget.TextView;

import static java.lang.String.format;

/**
 * @author Lennon Jesus - lennon.jesus@gmail.com
 */
public class CountdownActivity extends Activity {

    private static final int SEGUNDOS_POR_DIA = 86400;

    private static final int SEGUNDOS_POR_HORA = 3600;

    private static final int SEGUNDOS_POR_MINUTO = 60;

    private static final int MIL = 1000;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                LocationManager locationManager = (LocationManager) CountdownActivity.this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

                Criteria criteria = new Criteria();

                criteria.setAccuracy(Criteria.ACCURACY_FINE);

                String provider = locationManager.getBestProvider(criteria, true);

                // Última posição obtida do usuário
                Location localizacaoAtual = locationManager.getLastKnownLocation(provider);

                TextView txtDistancia = (TextView) findViewById(R.id.txtDistancia);

                if (localizacaoAtual != null) {

                    // R. das Rosas, 544, Itatiaia - Rio de Janeiro
                    Location localizacaoDestino = new Location(Context.LOCATION_SERVICE);
                    localizacaoDestino.setLatitude(-22.44163);
                    localizacaoDestino.setLongitude(-44.53769);


                    float kilometers = localizacaoAtual.distanceTo(localizacaoDestino) / MIL;

                    txtDistancia.setText("e aproximadamente " + Math.round(kilometers) + " Kms (em linha reta)");

                } else {
                    txtDistancia.setText("");
                }

                return null;
            }
        }.execute();


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
