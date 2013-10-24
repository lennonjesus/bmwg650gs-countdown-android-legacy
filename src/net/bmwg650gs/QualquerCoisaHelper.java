package net.bmwg650gs;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class QualquerCoisaHelper {

    private static final java.lang.String TAG = QualquerCoisaHelper.class.getCanonicalName();

    private FragmentActivity context;

    public QualquerCoisaHelper(FragmentActivity context) {
        this.context = context;
    }

    /**
     * Show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    public void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, context, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            errorFragment.setDialog(errorDialog);
            errorFragment.show(context.getSupportFragmentManager(), TAG);
        }
    }


}
