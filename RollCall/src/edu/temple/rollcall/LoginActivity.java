package edu.temple.rollcall;

import org.json.JSONObject;

import edu.temple.rollcall.util.API;
import edu.temple.rollcall.util.RollCallUtil;
import edu.temple.rollcall.util.UserAccount;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	EditText email;
	EditText password;
	Button loginButton; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.login);
		
		loginButton.setOnClickListener(loginButtonListener);
		
		getActionBar().setDisplayShowHomeEnabled(false);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		RollCallUtil.checkPlayServices(this);
	}
	
	View.OnClickListener loginButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Thread t = new Thread() {
				@Override
				public void run() {
					JSONObject result = API.login(LoginActivity.this, email.getText().toString(), password.getText().toString());
					Message msg = Message.obtain();
					msg.obj = result;
					loginAttemptHandler.sendMessage(msg); // Send API response back to LoginActivity.
				}
			};
			t.start();
		}
	};
	
	// Receives API response from thread in loginButtonListener.
	Handler loginAttemptHandler = new Handler(new Handler.Callback() {	
		@Override
		public boolean handleMessage(Message msg) {
			JSONObject response = (JSONObject) msg.obj;
			String status = "";
			Toast toast;
			
			try{
				status = response.getString("status");
				switch(status) {
				case "ok":
					UserAccount.update(response.getJSONObject("account")); // Update UserAccount credentials.
					setResult(RESULT_OK, getIntent());
					finish(); // Return to MainActivity.
					break;
				case "error":
					if(response.getInt("errno") == 0) { // If there was no MySQL error, then the email/password were unsuccessful.
						toast = Toast.makeText(LoginActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						toast.show();
					} else {
						Log.d("LoginActivity", "MySQL error " + response.getString("errno").toString());
					}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.d("LoginActivity", "Error in loginAttemptHandler");
			}
			return false;
		}
	});
}
