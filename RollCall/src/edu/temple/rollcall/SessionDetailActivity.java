package edu.temple.rollcall;

import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.temple.rollcall.util.RollCallUtil;

import android.content.Intent;
import android.os.Bundle;
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
	Button check_in_button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_detail);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
				
		course_name = (TextView) findViewById(R.id.session_detail_course_name);
		check_in_button = (Button) findViewById(R.id.session_detail_check_in_button);
		check_in_button.setOnClickListener(buttonListener);

		Intent intent = getIntent();
		JSONObject sessionInfo = null;
		try {
			sessionInfo = new JSONObject(intent.getExtras().getString("sessionInfo"));
			course_name.setText(sessionInfo.getString("course_name"));
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




