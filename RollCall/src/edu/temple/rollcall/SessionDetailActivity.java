package edu.temple.rollcall;

import java.text.DecimalFormat;
import java.util.Date;

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
import edu.temple.rollcall.util.SessionList;
import edu.temple.rollcall.util.UserAccount;
import edu.temple.rollcall.util.api.API;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SessionDetailActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

	protected static final int CHECKED_IN = 0;
	protected static final int CHECKED_OUT = 1;
	
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

		session = SessionList.getSessionWithId(getIntent().getExtras().getString("sessionId"));

		courseName.setText(session.courseName);
		teacherName.setText(session.teacherName);
		time.setText(session.dayOfWeek + ", " + session.startTimeString + " - " + session.endTimeString);
		countdown.setText(session.timer.output);

		timer = new SessionDetailCountDownTimer(session, countdown);
		timer.start();

		if(session.startTimeMillis <= new Date().getTime()) {
			checkInButton.setVisibility(View.VISIBLE);
			if(session.isCheckedIn) {
				checkInButton.setText(getString(R.string.checked_in));
				checkInButton.setEnabled(false);
			}
		} else {
			checkInButton.setVisibility(View.GONE);
		}

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
		distance.setText(df.format(meters[0] * 0.000621371) + " " + getString(R.string.miles_away));
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

				if(session.canCheckIn){				
					Thread t = new Thread() {					
						@Override
						public void run() {
							try {
								JSONObject checkInResponse = API.checkIn(SessionDetailActivity.this, UserAccount.studentId, session.sessionId); 
								String status = checkInResponse.getString("status");
								Message msg = Message.obtain();
								if(status.equals("ok")) {
									msg.obj = status;
								} else {
									msg.obj = checkInResponse.getString("errno");
								}
								checkInOutHandler.sendMessage(msg);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					t.start();

					// Else, student is not present and can't check in.
				} else {
					Toast toast = Toast.makeText(SessionDetailActivity.this, "You can't check in to this session until you've arrived!", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}

			} catch (Exception e) {
				Log.e("Error: ", e.getMessage());
			}

		}
	};
	
	private void checkOut(){
		if(session.isCheckedIn){
			Thread t = new Thread() {
				@Override
				public void run() {
					try {
						JSONObject response = API.checkOut(SessionDetailActivity.this, UserAccount.studentId, session.sessionId); 
						if(response.getString("status").equals("ok")) {
							Message msg = Message.obtain();
							msg.what = CHECKED_OUT;
							checkInOutHandler.sendMessage(msg);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			t.start();
		}
	}

	Handler checkInOutHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if(msg.what == CHECKED_IN && msg.obj.equals("ok")) {
				session.isCheckedIn = true;
				checkInButton.setText(getString(R.string.checked_in));
				checkInButton.setEnabled(false);
			} else if (msg.what == CHECKED_OUT) {
				Toast toast = Toast.makeText(SessionDetailActivity.this, "Session is over, successfully checked out.", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				finish();
			}
			return false;
		}
	});


	private void displayMap(double lat, double lng) {
		// Do a null check to confirm that we have not already instantiated the map.
		if (gMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			gMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.session_detail_map)).getMap();
			// Check if we were successful in obtaining the map.
			if (gMap != null) {
				gMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(session.courseName));
				gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 17));
				gMap.setMyLocationEnabled(true);
			}
		}
	}

	private class SessionDetailCountDownTimer extends CountDownTimer {

		private TextView textView;
		private Session session;

		public SessionDetailCountDownTimer(Session session, TextView textView) {
			super(session.endTimeMillis, 1000);
			this.textView = textView;
			this.session = session;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			this.textView.setText(session.timer.output);
		}

		@Override
		public void onFinish() {
			checkOut();
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




