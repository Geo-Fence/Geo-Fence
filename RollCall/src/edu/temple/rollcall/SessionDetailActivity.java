package edu.temple.rollcall;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.temple.rollcall.cards.Card.CardCountDownTimer;
import edu.temple.rollcall.util.RollCallUtil;

import android.content.Intent;
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

	private GoogleMap gMap;
	
	TextView course_name;
	TextView teacher_name;
	TextView session_time;
	TextView timer;
	Button check_in_button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_detail);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
				
		course_name = (TextView) findViewById(R.id.session_detail_course_name);
		teacher_name = (TextView) findViewById(R.id.session_detail_teacher_name);
		session_time = (TextView) findViewById(R.id.session_detail_session_time);
		check_in_button = (Button) findViewById(R.id.session_detail_check_in_button);
		check_in_button.setOnClickListener(buttonListener);

		Intent intent = getIntent();
		JSONObject sessionInfo = null;
		try {
			sessionInfo = new JSONObject(intent.getExtras().getString("sessionInfo"));
			course_name.setText(sessionInfo.getString("course_name"));
			teacher_name.setText(sessionInfo.getString("teacher_first_name") + " " + sessionInfo.getString("teacher_last_name"));
			
			DateTime dateTime = new DateTime(Long.parseLong(sessionInfo.getString("start_time")) * 1000);
			String sessionTime = dateTime.toString("E") + ", " + dateTime.toString("h:mm a") + " - ";
			dateTime = dateTime.withMillis(Long.parseLong(sessionInfo.getString("end_time")) * 1000);
			sessionTime = sessionTime + dateTime.toString("h:mm a");
			session_time.setText(sessionTime);
			
			setUpMapIfNeeded(sessionInfo.getDouble("lat"), sessionInfo.getDouble("lng"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
	
	private void setUpMapIfNeeded(double lat, double lng) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (gMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            gMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (gMap != null) {
            	gMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Marker"));
            	gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 17));
            }
        }
    }
	
	public class CardCountDownTimer extends CountDownTimer {

    	public String output;

    	public CardCountDownTimer(long millisInFuture, long countDownInterval) {
    		super(millisInFuture, countDownInterval);
    		this.start();
    	}

    	@Override
    	public void onTick(long millisUntilFinished) {
    		Long millis = millisUntilFinished;
    		long days = TimeUnit.MILLISECONDS.toDays(millis);
    		millis -= TimeUnit.DAYS.toMillis(days);
    		long hours = TimeUnit.MILLISECONDS.toHours(millis);
    		millis -= TimeUnit.HOURS.toMillis(hours);
    		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
    	
    		// Generates a grammatically correct phrase...
    		String message = "Begins in ";
    		if(days < 1) {
    			if(hours > 0) {
    				message += hours;
    				message += (hours == 1) ? " hour" : " hours";
    				message += (minutes > 0) ? ", " : ".";
    			} else if(minutes == 0) {
    				message += "less than a minute.";
    			}
    			if(minutes > 0) {
    				message += minutes;
    				message += (minutes == 1) ? " minute." : " minutes.";
    			}
    			this.output = message;
    		} else {
    			this.output = "";
    		}
    	}

    	@Override
    	public void onFinish() {
    		this.output = "In progress...";
    	}
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
	
	
}




