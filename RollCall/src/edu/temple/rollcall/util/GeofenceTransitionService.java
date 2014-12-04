package edu.temple.rollcall.util;

import java.util.List;

import org.json.JSONObject;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationClient;

import edu.temple.rollcall.util.api.API;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class GeofenceTransitionService extends IntentService {

	public GeofenceTransitionService() {
		super("GeofenceTransitionService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// print to console to check if a Geofence transition was crossed
		System.out.println("GeofenceTransitionService.onHandleIntent() called");
		
		if (intent != null) {
			
			List<Geofence> triggeredGeofences = LocationClient.getTriggeringGeofences(intent);
			GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
			
			for(Geofence geofence : triggeredGeofences) {
				
				Session session = SessionList.getSessionWithId(geofence.getRequestId());
				
				switch(geofencingEvent.getGeofenceTransition()) {
				
				case Geofence.GEOFENCE_TRANSITION_ENTER:
					Log.d("GEOFENCE_TRANSITION", "ENTER");
					if(session != null) {
						session.canCheckIn = true;
					}
					break;
					
				case Geofence.GEOFENCE_TRANSITION_EXIT:
					Log.d("GEOFENCE_TRANSITION", "EXIT");
					if(session != null) {
						if (session.isCheckedIn) {
							checkOut(session.sessionId);
							session.isCheckedIn = false;
						}
						session.canCheckIn = false;
					}
					break;
					
				default:
					Log.d("GEOFENCE_TRANSITION", "Error, handler called with invalid intent");
					break; 
				}
				
			}
		}
		
		// Else, intent == null
		else {
			Log.e("GEOFENCE_TRANSITION", "Error: intent is null");
		}
	}

	private void checkOut(final String sessionId) {
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					JSONObject response = API.checkOut(getApplicationContext(), UserAccount.studentId, sessionId); 
					if(response.getString("status").equals("ok")) {
						Toast.makeText(getApplicationContext(), "Automatically checked out of session based on your location.", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}

}
