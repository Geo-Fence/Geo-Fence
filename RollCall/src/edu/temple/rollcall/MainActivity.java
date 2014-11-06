package edu.temple.rollcall;

import org.json.JSONException;
import org.json.JSONObject;

import edu.temple.rollcall.R;
import edu.temple.rollcall.cards.CardListFragment;
import edu.temple.rollcall.util.API;
import edu.temple.rollcall.util.UserAccount;

import android.app.Activity;
import android.app.FragmentManager;
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
import android.widget.TextView;

public class MainActivity extends Activity {

	static final int LOGIN_REQUEST = 1;

	TextView feedMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle(R.string.title_activity_main);

		feedMessage = (TextView) findViewById(R.id.feedMessage);

		if(UserAccount.studentId() == null) {
			Intent intent = new Intent(MainActivity.this, LoginActivity.class);
			startActivityForResult(intent, LOGIN_REQUEST);
		}
		
		getActionBar().setDisplayShowHomeEnabled(false);
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
		case LOGIN_REQUEST:
			if (resultCode == RESULT_OK) {
				refreshFeed();
			}
		}
	}

	private void refreshFeed() {
		Thread t = new Thread() {
			@Override
			public void run(){
				try {
					JSONObject response = API.getSessionsForStudent(MainActivity.this, UserAccount.studentId()); 
					Message msg = Message.obtain();
					msg.obj = response;
					refreshFeedHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}

	Handler refreshFeedHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			JSONObject response = (JSONObject) msg.obj;
			
			try {
				String status = response.getString("status");
				
				switch(status) {
				case "ok":
					CardListFragment cardListFragment = new CardListFragment();

					Bundle args = new Bundle();
					args.putString("sessionArray", response.getJSONArray("sessionArray").toString());
					cardListFragment.setArguments(args);

					FragmentManager fm = getFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					ft.add(R.id.card_container, cardListFragment);
					ft.commit();
			
					break;
				case "empty":
					feedMessage.setText("You have no upcoming sessions.");
					feedMessage.setVisibility(View.VISIBLE);
					break;
				case "error":
					Log.d("MainActivity", "API Error " + response.getString("errno").toString());
					break;
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return true;
		}
	});
	
	private void logout() {
		FrameLayout cardContainer = (FrameLayout) findViewById(R.id.card_container);
		cardContainer.removeAllViews();
		feedMessage.setVisibility(View.GONE);
		
		UserAccount.logout();
		
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivityForResult(intent, LOGIN_REQUEST);
	}
	
}
