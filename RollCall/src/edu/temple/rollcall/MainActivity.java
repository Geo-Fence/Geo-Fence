package edu.temple.rollcall;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.temple.rollcall.R;
import edu.temple.rollcall.util.RollCallUtil;
import edu.temple.rollcall.util.Session;
import edu.temple.rollcall.util.UserAccount;
import edu.temple.rollcall.util.api.API;
import edu.temple.rollcall.util.sessionlist.EmptyFeedFragment;
import edu.temple.rollcall.util.sessionlist.SessionListFragment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
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

public class MainActivity extends Activity {

	static final int LOGIN_REQUEST = 1111;

	ProgressBar refreshSpinner;
	FrameLayout cardListContainer;
	
	ArrayList<Session> sessionList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle(R.string.title_activity_main);
		getActionBar().setDisplayShowHomeEnabled(false);

		refreshSpinner = (ProgressBar) findViewById(R.id.refresh_spinner);
		cardListContainer = (FrameLayout) findViewById(R.id.card_layout_container);
		
		sessionList = new ArrayList<Session>();
		
		// If the user hasn't signed in, start the login activity.
		if(UserAccount.studentId == null) {
			Intent intent = new Intent(MainActivity.this, LoginActivity.class);
			startActivityForResult(intent, LOGIN_REQUEST);
		}

		// Other activities can start MainActivity with a "refresh" action.
		if(getIntent().getAction().equals("refresh")) {
			refreshFeed();
		}
	}

	// Make sure google play services is working whenever the activity is resumed.
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
		switch(requestCode) {
		case LOGIN_REQUEST: // Sent back from LoginActivity.
			if (resultCode == RESULT_OK) {
				refreshFeed();
				break;
			}
		case RollCallUtil.REQUEST_CODE_RECOVER_PLAY_SERVICES: // Sent back from RollCallUtil after checking for google play services.
			if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Google Play Services must be installed.", Toast.LENGTH_SHORT).show();
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
			public void run(){
				try {
					JSONObject response = API.getSessionsForStudent(MainActivity.this, UserAccount.studentId); 
					Message msg = Message.obtain();
					msg.obj = response;
					refreshFeedHandler.sendMessage(msg); // Send sessions from thread to listener.
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
			refreshSpinner.setVisibility(View.GONE); // Remove the progress spinner.
			JSONObject response = (JSONObject) msg.obj;
			try {
				String status = response.getString("status");	
				switch(status) {
				case "ok":
					// If API call was successful but no sessions were returned, prompt the user to enroll in a course.
					if(response.getJSONArray("sessionArray").isNull(0)) {
						FragmentTransaction ft = getFragmentManager().beginTransaction();
						ft.add(cardListContainer.getId() , new EmptyFeedFragment()); // Add an EmptyFeedFragment to the container.
						ft.commit();
					} else {
						SessionListFragment cardListFragment = new SessionListFragment();
						// Send the JSON array of sessions to the CardListFragment.
						Bundle args = new Bundle();
						args.putString("sessionArray", response.getJSONArray("sessionArray").toString());
						cardListFragment.setArguments(args);
						
						// Add the cardListFragment to the container view.
						FragmentTransaction ft = getFragmentManager().beginTransaction();
						ft.add(cardListContainer.getId(), cardListFragment);
						ft.commit();
						
						JSONArray sessionArray = new JSONArray(response.getJSONArray("sessionArray").toString());
						for(int i = 0 ; i < sessionArray.length() ; i++) {
							sessionList.add(new Session(sessionArray.getJSONObject(i)));
						}
					}
					break;
				case "error": // MySQL error in API call.
					Log.d("MainActivity", "MySQL error " + response.getString("errno").toString());
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

}
