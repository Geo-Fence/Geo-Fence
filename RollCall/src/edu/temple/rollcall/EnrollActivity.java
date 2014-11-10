package edu.temple.rollcall;

import org.json.JSONObject;

import edu.temple.rollcall.util.API;
import edu.temple.rollcall.util.RollCallUtil;
import edu.temple.rollcall.util.UserAccount;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EnrollActivity extends Activity {

	TextView title;
	EditText enrollmentCode;
	Button enroll_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enroll);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		title = (TextView) findViewById(R.id.enroll_title);
		enrollmentCode = (EditText) findViewById(R.id.enrollment_code_field);
		enroll_button = (Button) findViewById(R.id.enroll_button);

		enroll_button.setOnClickListener(enrollButtonListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
		RollCallUtil.checkPlayServices(this);
	}

	// Listener for the "Enroll" button.
	View.OnClickListener enrollButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Thread t = new Thread() {
				@Override
				public void run() {
					JSONObject result = API.enroll(EnrollActivity.this, UserAccount.studentId, enrollmentCode.getText().toString());
					Message msg = Message.obtain();
					msg.obj = result;
					enrollAttemptHandler.sendMessage(msg); // Send API response back to EnrollActivity.
				}
			};
			t.start();
		}
	};

	// Receives API response from thread in enrollButtonListener.
	Handler enrollAttemptHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			JSONObject response = (JSONObject) msg.obj;
			String status = "";
			Toast toast;

			try{
				status = response.getString("status");
				switch(status) {
				case "ok":
					Intent intent = new Intent(EnrollActivity.this, MainActivity.class);
					intent.setAction("refresh"); // Start MainActivity with "refresh" action.
					// Clear back stack so that clicking the back button wont return the user to this activity.
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					break;
				case "error":
					if(response.getInt("errno") == 0) { // If there was no MySQL error, then the enrollment code was invalid.
						toast = Toast.makeText(EnrollActivity.this, "Invalid enrollment code.", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						toast.show();
					} else if(response.getInt("errno") == 1062) { // MySQL error number 1062 means the roster entry already exists.
						toast = Toast.makeText(EnrollActivity.this, "You're already enrolled in that course.", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						toast.show();
					} else {
						Log.d("LoginActivity", "MySQL error " + response.getString("errno").toString());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.d("EnrollActivity", "Error in enrollAttemptHandler");
			}
			return false;
		}
	});

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: // Action bar back button.
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
