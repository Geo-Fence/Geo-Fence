package edu.temple.rollcall.util;

import android.app.Activity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import edu.temple.rollcall.RollCall;

public class RollCallUtil {
	
	public static boolean checkPlayServices(Activity activity) {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
		if (status != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
				GooglePlayServicesUtil.getErrorDialog(status, activity, RollCall.REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
			} else {
				Toast.makeText(activity, "This device is not supported.", Toast.LENGTH_LONG).show();
				activity.finish();
			}
			return false;
		}
		return true;
	}

}
