package edu.temple.rollcall;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.temple.rollcall.util.RollCallUtil;
import edu.temple.rollcall.util.Session;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SessionDetailActivity extends FragmentActivity {

	private Session session;
	private GoogleMap gMap;
	private SessionDetailCountDownTimer timer;
	
	TextView courseName;
	TextView teacherName;
	TextView distance;
	TextView time;
	TextView countdown;
	
	Button checkInButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_detail);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
				
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
		
		displayMap(session.latitude, session.longitude);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		RollCallUtil.checkPlayServices(this);
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
    		this.start();
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
}




