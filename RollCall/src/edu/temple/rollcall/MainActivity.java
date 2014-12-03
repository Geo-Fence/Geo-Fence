package edu.temple.rollcall;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import edu.temple.rollcall.util.RollCallUtil;
import edu.temple.rollcall.util.Session;
import edu.temple.rollcall.util.UserAccount;
import edu.temple.rollcall.util.api.API;
import edu.temple.rollcall.util.sessionlist.EmptyFeedFragment;
import edu.temple.rollcall.util.sessionlist.SessionListFragment;

public class MainActivity extends Activity implements
		ConnectionCallbacks, OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

	static final int LOGIN_REQUEST = 1111;

	ProgressBar refreshSpinner;
	FrameLayout cardListContainer;

	ArrayList<Session> sessionList;

	GoogleApiClient googleApiClient;
	List<Geofence> geofenceList = new ArrayList<Geofence>();
	LocationClient locationClient;

	private String mCourseId;
	private double mGeoFenceLatitude;
	private double mGeoFenceLongitude;
	private float mGeoFenceRadius = 100;
	private long mGeoFenceExpirationDuration;
	private int mTransitionType;
	private double mStartTime;
	private double mEndTime;
	private boolean mInProgress;
	private boolean mBuiltGeofences;
	private boolean mWaitingForJSONParse;

	private String sessionId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle(R.string.title_activity_main);
		getActionBar().setDisplayShowHomeEnabled(false);

		googleApiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).build();

		refreshSpinner = (ProgressBar) findViewById(R.id.refresh_spinner);
		cardListContainer = (FrameLayout) findViewById(R.id.card_layout_container);

		sessionList = new ArrayList<Session>();

		// If the user hasn't signed in, start the login activity.
		if (UserAccount.studentId == null) {
			Intent intent = new Intent(MainActivity.this, LoginActivity.class);
			startActivityForResult(intent, LOGIN_REQUEST);
		}

		// Other activities can start MainActivity with a "refresh" action.
		if (getIntent().getAction().equals("refresh")) {
			refreshFeed();
		}
	}

	@Override
	protected void onStart() {
		googleApiClient.connect();
		super.onStart();
	}

	@Override
	protected void onStop() {
		googleApiClient.disconnect();
		super.onStop();
	}

	// Make sure google play services is working whenever the activity is
	// resumed.
	@Override
	protected void onResume() {
		super.onResume();
		RollCallUtil.checkPlayServices(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.action_refresh:
			refreshFeed();
			break;
		case R.id.action_enroll:
			intent = new Intent(MainActivity.this, EnrollActivity.class);
			startActivity(intent);
			break;
		case R.id.action_view_account:
			intent = new Intent(MainActivity.this, AccountDetailActivity.class);
			startActivity(intent);
			break;
		case R.id.action_log_out:
			logout();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case LOGIN_REQUEST: // Sent back from LoginActivity.
			if (resultCode == RESULT_OK) {
				refreshFeed();
				break;
			}
		case RollCallUtil.REQUEST_CODE_RECOVER_PLAY_SERVICES: // Sent back from
																// RollCallUtil
																// after
																// checking for
																// google play
																// services.
			if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Google Play Services must be installed.",
						Toast.LENGTH_SHORT).show();
				finish();
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// Fetches sessions from API and refreshes card list.
	private void refreshFeed() {
		cardListContainer.removeAllViews(); // Remove existing cards.
		refreshSpinner.setVisibility(View.VISIBLE); // Show a progress spinner.
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					JSONObject response = API.getSessionsForStudent(
							MainActivity.this, UserAccount.studentId);
					Message msg = Message.obtain();
					msg.obj = response;
					refreshFeedHandler.sendMessage(msg); // Send sessions from
															// thread to
															// listener.
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}

	// Receives sessions from API thread in refreshFeed().
	Handler refreshFeedHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			refreshSpinner.setVisibility(View.GONE); // Remove the progress
														// spinner.
			JSONObject response = (JSONObject) msg.obj;
			try {
				String status = response.getString("status");
				switch (status) {
				case "ok":
					// If API call was successful but no sessions were returned,
					// prompt the user to enroll in a course.
					if (response.getJSONArray("sessionArray").isNull(0)) {
						FragmentTransaction ft = getFragmentManager()
								.beginTransaction();
						ft.add(cardListContainer.getId(),
								new EmptyFeedFragment()); // Add an
															// EmptyFeedFragment
															// to the container.
						ft.commit();
					} else {
						SessionListFragment cardListFragment = new SessionListFragment();
						// Send the JSON array of sessions to the
						// CardListFragment.
						Bundle args = new Bundle();
						args.putString("sessionArray",
								response.getJSONArray("sessionArray")
										.toString());
						cardListFragment.setArguments(args);

						// Add the cardListFragment to the container view.
						FragmentTransaction ft = getFragmentManager()
								.beginTransaction();
						ft.add(cardListContainer.getId(), cardListFragment);
						ft.commit();

						JSONArray sessionArray = new JSONArray(response
								.getJSONArray("sessionArray").toString());
						for (int i = 0; i < sessionArray.length(); i++) {
							sessionList.add(new Session(sessionArray
									.getJSONObject(i)));
						}
						try {
							// gets only the next session info to create the Geofence
							JSONObject jsonGeoFenceRes = sessionArray.getJSONObject(0);
							System.out.println(sessionArray.getJSONObject(0).getString("session_id"));
							sessionId = jsonGeoFenceRes.getString("session_id");
							mCourseId = jsonGeoFenceRes.getString("course_name");
							mGeoFenceLatitude = jsonGeoFenceRes.getDouble("lat");
							mGeoFenceLongitude = jsonGeoFenceRes.getDouble("lng");
							// This is a placeholder value
							// Probably will set the radius to something else later
							mGeoFenceRadius = 10;

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mBuiltGeofences = false;

					}
					break;
				case "error": // MySQL error in API call.
					Log.d("MainActivity",
							"MySQL error "
									+ response.getString("errno").toString());
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return true;
		}

	});
	
	private void logout() {
		cardListContainer.removeAllViews(); // Remove all session cards.
		UserAccount.logout(); // Clear the UserAccount.
		// Start the login activity.
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivityForResult(intent, LOGIN_REQUEST);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		googleApiClient.connect();

	}

	@Override
	public void onConnected(Bundle arg0) {
		
		if (!servicesConnected() || mBuiltGeofences) {
			Log.d("OnConnected Error:", "services not connected or geofences were already built");
			return;
		}
		
		//These are the test variables; Change them to whatever you prefer
		double mTestLatitude = 39.9812170;
		double mTestLongitude = -75.1637760;
		sessionId = "1234567890";
		
		Geofence spareGeofence = new Geofence.Builder()
				.setRequestId(sessionId)
				.setTransitionTypes(
						Geofence.GEOFENCE_TRANSITION_ENTER
								| Geofence.GEOFENCE_TRANSITION_DWELL
								| Geofence.GEOFENCE_TRANSITION_EXIT)
				.setCircularRegion(mTestLatitude, mTestLongitude,
						1000)
				.setLoiteringDelay(5000)
				.setExpirationDuration(Geofence.NEVER_EXPIRE).build();
		
		geofenceList.add(spareGeofence);
		mBuiltGeofences = true;

		Intent intent = new Intent(this,
				GeofenceTransitionService.class);
		intent.putExtra("student_id", UserAccount.studentId);
		intent.putExtra("session_id", sessionId);
		PendingIntent geofenceTransitionPendingIntent = PendingIntent
				.getService(this, 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
		LocationServices.GeofencingApi.addGeofences(googleApiClient, geofenceList, geofenceTransitionPendingIntent);
	}

	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (ConnectionResult.SUCCESS == resultCode) {
			// Handle success
			return true;
		} else {
			// Handle the error
			return false;
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

}
