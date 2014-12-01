package edu.temple.rollcall.util.api;

import java.util.List;

import android.app.Activity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;

public class GeoFenceAPI implements Geofence{

	Activity mActivity;
	boolean mInProgress;
	GoogleApiClient googleApiClient;

	public GeoFenceAPI(Activity activity) {
		mActivity = activity;
	}

	public void registerGeofences(List<Geofence> geofenceList) {
		//mCurrentGeofences = (ArrayList<Geofence>) geofences;

		if (!mInProgress) {
			mInProgress = true;
			/*
			 * If a failure occurs, onActivityResult is eventually called, and
			 * it needs to know what type of request was in progress.
			 */
			// REMOVE ?????? mRequestType = REQUEST_TYPE.ADD;
		//	getLocationClient().connect();

		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public String getRequestId() {
		// TODO Auto-generated method stub
		return null;
	}


}