package edu.temple.rollcall;

import org.json.JSONArray;
import org.json.JSONException;

import edu.temple.rollcall.R;
import edu.temple.rollcall.util.API;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView demo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		demo = (TextView) findViewById(R.id.demo);

		Thread t = new Thread() {
			@Override
			public void run(){
				try {
					JSONArray sessions = API.getSessionsForStudent(MainActivity.this, "1");
					Message msg = Message.obtain();
					msg.obj = sessions;
					
					getSessionsHandler.sendMessage(msg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	Handler getSessionsHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			JSONArray sessions = (JSONArray) msg.obj;
			if (sessions != null) {
				demo.setText("Upcoming sessions for student_id=1: \n" + sessions.toString());
			}
			return false;
		}
	});
}
