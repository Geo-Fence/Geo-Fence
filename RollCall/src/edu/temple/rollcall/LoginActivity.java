package edu.temple.rollcall;

import org.json.JSONException;
import org.json.JSONObject;

import edu.temple.rollcall.util.API;
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
	Button login_button; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.password);
		login_button = (Button) findViewById(R.id.login);
		
		login_button.setOnClickListener(login_button_listener);
	}
	
	View.OnClickListener login_button_listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Thread t = new Thread() {
				@Override
				public void run() {
					JSONObject result = API.login(LoginActivity.this, email.getText().toString(), password.getText().toString());
					Message msg = Message.obtain();
					msg.obj = result;
					loginAttemptHandler.sendMessage(msg);
				}
			};
			t.start();
		}
	};
	
	Handler loginAttemptHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			JSONObject response = (JSONObject) msg.obj;
			String status = "";
			Toast toast;
			
			try{
				status = response.getString("status");
			} catch (Exception e) {
				e.printStackTrace();
				Log.d("LoginActivity", "Error in loginAttemptHandler");
			}
			
			switch(status) {
			case "ok":
				try {
					UserAccount.update(response.getJSONObject("account"));
					setResult(RESULT_OK, getIntent());
					finish();
					return true;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			case "incorrect_email":
				toast = Toast.makeText(LoginActivity.this, "No account for that email", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.show();
				break;
			case "incorrect_password":
				toast = Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.show();
				break;
			default:
				Log.d("LoginActivity", "Error in loginAttemptHandler switch statement.");
			}
			return false;
		}
	});
}
