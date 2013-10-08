package net.bmwg650gs;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.Time;
import android.widget.TextView;

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

        Time time = new Time();
        time.set(15, 10, 2013);

        new CountDownTimer(time.toMillis(true), 1000) {

            public void onTick(long millisUntilFinished) {
                long left = (millisUntilFinished - System.currentTimeMillis()) / (24 * 60 * 60 * 10 * 10 * 10);

                txtDaysLeft.setText("" + left);
            }

            public void onFinish() {
                txtFaltam.setText("");
                txtDaysLeft.setText("É hoje!");
                txtDias.setText("");
            }
        }.start();

    }
}
