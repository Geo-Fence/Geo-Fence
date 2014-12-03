package edu.temple.rollcall;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class GeofenceTransitionService extends IntentService {

	private static boolean checkIn, checkOut;
	private static String studentId, sessionId;

	public GeofenceTransitionService() {
		super("GeofenceTransitionService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// print to console to check if a Geofence transition was crossed
		System.out
				.println("GeofenceTransitionService.onHandleIntent(...) launched");
		if (intent != null) 
		{
			studentId = intent.getStringExtra("student_id");
			sessionId = intent.getStringExtra("session_id");
			GeofencingEvent geofenceEvent = GeofencingEvent.fromIntent(intent);
			
			// Logic to decide what to do for each transition type
			switch (geofenceEvent.getGeofenceTransition()) 
			{
			case Geofence.GEOFENCE_TRANSITION_DWELL:
				Log.d("GEOFENCE_TRANSITION",
						"Geofence Transition was of type: Geofence.GEOFENCE_TRANSITION_DWELL");
				// This 'if' statement may be unnecessary: check if user is
				// inside geofence area
				if (canCheckIn() == true) 
				{
					// TODO: some code to run when to signify the user is still
					// within the geofence area
					// TODO: create a notification to send to user when this
					// transition happens
					break;
				}
				break;
			case Geofence.GEOFENCE_TRANSITION_ENTER:
				Log.d("GEOFENCE_TRANSITION",
						"Geofence Transition was of type: Geofence.GEOFENCE_TRANSITION_ENTER");
				// set checkIn = true which will allow the 'Check In' Button in
				// sessionDeatilActivity to do an action
				// TODO: create a notification to send to user when this
				// transition happens
				checkIn = true;
				break;
			case Geofence.GEOFENCE_TRANSITION_EXIT:
				Log.d("GEOFENCE_TRANSITION",
						"Geofence Transition was of type: Geofence.GEOFENCE_TRANSITION_EXIT");
				// TODO: create a notification to send to user when this
				// transition happens
				checkOut = true;
				checkIn = false;
				break;

			default:
				Log.d("GEOFENCE_TRANSITION",
						"Geofence Transition was of type: This service was not launched by a geofence transition");
				// TODO: create code to determine the error that sent this
				// intent and launch a proper response
				checkIn = false;
				break;
			}

		}
		
		// intent == null;
		else {
			Log.e("ERROR", "Error in GeofenceTransition:  Intent is null");
		}
	}

	public static boolean canCheckIn() {
		return checkIn;
	}
	public static boolean canCheckOut(){
		return checkOut;
	}

	public static String getSessionId() {
		return sessionId;
	}

	public static String getStudentId() {

		return studentId;
	}

}
