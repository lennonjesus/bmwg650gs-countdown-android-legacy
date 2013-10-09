package net.bmwg650gs;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.Time;
import android.widget.TextView;
import net.bmwg650gs.beta.R;

/**
 * @author Lennon Jesus - lennon.jesus@gmail.com
 */
public class CountdownActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final TextView txtFaltam = (TextView) findViewById(R.id.txtFaltam);
        final TextView txtDaysLeft = (TextView) findViewById(R.id.txtDaysLeft);
        final TextView txtDias = (TextView) findViewById(R.id.txtDias);

//        Time time = new Time();
//        time.set(15, 10, 2013);
//
//        new CountDownTimer(time.toMillis(true), 1000) {
//
//            public void onTick(long millisUntilFinished) {
//                long diasRestantes = (millisUntilFinished - System.currentTimeMillis()) / (24 * 60 * 60 * 10 * 10 * 10);
//
//                txtDaysLeft.setText("" + diasRestantes);
//            }
//
//            public void onFinish() {
//                txtFaltam.setText("");
//                txtDaysLeft.setText("É hoje!");
//                txtDias.setText("");
//            }
//        }.start();


        Time timerSet = new Time();
        timerSet.set(15, 10, 2013); //day month year
        timerSet.normalize(true);

        long millis = timerSet.toMillis(true);

        Time timeNow = new Time();
        timeNow.setToNow(); // set the date to Current Time
        timeNow.normalize(true);

        long millis2 = timeNow.toMillis(true);

        long millisset = millis - millis2; //subtract current from future to set the time remaining

        new CountDownTimer(millisset, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                int weeks = (int) ((millisUntilFinished / 1000) / 604800);
                int days = (int) ((millisUntilFinished / 1000) / 86400);
                int hours = (int) (((millisUntilFinished / 1000) - (days * 86400)) / 3600);
                int minutes = (int) (((millisUntilFinished / 1000) - ((days * 86400) + (hours * 3600))) / 60);
                int seconds = (int) ((millisUntilFinished / 1000) % 60);

                txtDaysLeft.setText("" + days + " dias, " + hours + ":" + minutes+ ":" + seconds + "" );

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
