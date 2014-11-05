package edu.temple.rollcall;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class SessionDetailActivity extends Activity {

	TextView course_name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_detail);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		course_name = (TextView) findViewById(R.id.session_detail_course_name);

		Intent intent = getIntent();
		JSONObject sessionInfo = null;
		try {
			sessionInfo = new JSONObject(intent.getExtras().getString("sessionInfo"));
			course_name.setText(sessionInfo.getString("course_name"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	        finish();
	    }
	    return super.onOptionsItemSelected(item);
	}
}