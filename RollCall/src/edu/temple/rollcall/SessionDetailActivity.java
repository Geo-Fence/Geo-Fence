package edu.temple.rollcall;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.temple.rollcall.util.RollCallUtil;
import edu.temple.rollcall.util.Session;
import edu.temple.rollcall.util.api.API;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SessionDetailActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

	private GoogleApiClient googleApiClient;
	private LocationRequest locationRequest;
	
	private Session session;
	private GoogleMap gMap;
	private SessionDetailCountDownTimer timer;
	
	private TextView courseName;
	private TextView teacherName;
	private TextView distance;
	private TextView time;
	private TextView countdown;
	
	Button checkInButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_detail);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		googleApiClient = new GoogleApiClient.Builder(this)
		.addApi(LocationServices.API)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .build();
				
		courseName = (TextView) findViewById(R.id.session_detail_course_name);
		teacherName = (TextView) findViewById(R.id.session_detail_teacher_name);
		distance = (TextView) findViewById(R.id.session_detail_distance);
		time = (TextView) findViewById(R.id.session_detail_time);
		countdown = (TextView) findViewById(R.id.session_detail_countdown);
		checkInButton = (Button) findViewById(R.id.session_detail_check_in_button);
		checkInButton.setOnClickListener(buttonListener);

		session = null;
		try {
			session = new Session(new JSONObject(getIntent().getExtras().getString("sessionInfo")));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		courseName.setText(session.courseName);
		teacherName.setText(session.teacherName);
		time.setText(session.dayOfWeek + ", " + session.startTimeString + " - " + session.endTimeString);
		countdown.setText(session.timer.output);
		
		timer = new SessionDetailCountDownTimer(session, countdown);
		timer.start();
		
		displayMap(session.latitude, session.longitude);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		RollCallUtil.checkPlayServices(this);
		googleApiClient.connect();
	}
	
	@Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }
	
	@Override
	public void onLocationChanged(Location location) {
		float[] meters = new float[1];
		Location.distanceBetween(session.latitude, session.longitude, location.getLatitude(), location.getLongitude(), meters);
		DecimalFormat df = new DecimalFormat("###.#");
		distance.setText(df.format(meters[0] * 0.000621371) + " miles away");
	}

	@Override
	public void onConnected(Bundle arg0) {
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		locationRequest.setInterval(1000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, SessionDetailActivity.this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home: // Back button in Action Bar.
	        finish();
	        break;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	private OnClickListener buttonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try{
				//testing Check-In Feature of the API 
				//TODO: add logic to see if class start time is close enough to current time 
				// to allow user to checkIn
				//TODO: change UI of Button session to reflect user sign-in
				if(GeofenceTransitionService.canCheckIn()){
					/* Should make an call to the server to add the current/logged-in student to 
					 * the list of students currently attending the class
					 * (Not Complete yet)
					 */
					checkInButton.setVisibility(Button.VISIBLE);
					String studentId = GeofenceTransitionService.getStudentId();
					String sessionId = GeofenceTransitionService.getSessionId();
					API.checkIn(getBaseContext(), studentId, sessionId);
					
				}
				
				
			} catch (Exception e) {
				Log.e("Error: ", e.getMessage());
			}
			
		}
	};
	
	private void displayMap(double lat, double lng) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (gMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            gMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.session_detail_map)).getMap();
            // Check if we were successful in obtaining the map.
            if (gMap != null) {
            	gMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(session.courseName));
            	gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 17));
            }
        }
    }
	
	private class SessionDetailCountDownTimer extends CountDownTimer {

    	private TextView textView;
    	private Session session;

    	public SessionDetailCountDownTimer(Session session, TextView textView) {
    		super(session.startTimeMillis, 1000);
    		this.textView = textView;
    		this.session = session;
    	}

    	@Override
    	public void onTick(long millisUntilFinished) {
    		this.textView.setText(session.timer.output);
    	}

    	@Override
    	public void onFinish() {
    		this.textView.setText("In progress...");
    	}
    }

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}
}




