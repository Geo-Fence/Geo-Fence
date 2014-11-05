package edu.temple.rollcall;

import org.json.JSONException;
import org.json.JSONObject;

import edu.temple.rollcall.R;
import edu.temple.rollcall.cards.CardFragment;
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
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	static final int LOGIN_REQUEST = 1;

	TextView feedMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//		cardListView = (CardListView) findViewById(R.id.card_list);
		feedMessage = (TextView) findViewById(R.id.feedMessage);

		if(UserAccount.studentId() == null) {
			Intent intent = new Intent(MainActivity.this, LoginActivity.class);
			startActivityForResult(intent, LOGIN_REQUEST);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LOGIN_REQUEST) {
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

			Log.d("JSON", response.toString());

			try {
				String status = response.getString("status");

				switch(status) {
				case "ok":

					CardFragment listViewDemoFragment = new CardFragment();

					Bundle args = new Bundle();
					args.putString("sessions", response.getJSONArray("sessions").toString());
					listViewDemoFragment.setArguments(args);

					FragmentManager fm = getFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					ft.add(R.id.card_container, listViewDemoFragment);
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

			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			return false;
		}
	});

}
