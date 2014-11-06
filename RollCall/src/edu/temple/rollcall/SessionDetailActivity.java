package edu.temple.rollcall;

import org.json.JSONException;
import android.graphics.PorterDuff;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class SessionDetailActivity extends Activity {

	TextView course_name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_detail);
				
		course_name = (TextView) findViewById(R.id.session_detail_course_name);

		Intent intent = getIntent();
		JSONObject sessionInfo = null;
		try {
			sessionInfo = new JSONObject(intent.getExtras().getString("sessionInfo"));
			course_name.setText(sessionInfo.getString("course_name"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
	}
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    
	    Drawable refreshIcon = getResources().getDrawable(R.drawable.ic_action_refresh);
	    refreshIcon.mutate().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
	    MenuItem refresh = menu.findItem(R.id.action_refresh);
	    refresh.setIcon(refreshIcon);
	    refresh.setEnabled(false);
	    
	    return true;
	  } 
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
	    switch (item.getItemId()) {
	    case android.R.id.home:
	        finish();
	        break;
	    case R.id.action_logout:
	    	intent = new Intent(SessionDetailActivity.this, MainActivity.class);
	    	intent.setAction("logout");
	    	startActivity(intent);
	    	break;
	    case R.id.action_account:
//	    	intent = new Intent(SessionDetailActivity.this, AccountDetailActivity.class);
//	    	startActivity(intent);
	    	break;
	    }
	    return super.onOptionsItemSelected(item);
	}
}