package edu.temple.rollcall;

import org.json.JSONObject;

import edu.temple.rollcall.util.API;
import edu.temple.rollcall.util.RollCallUtil;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateAccountActivity extends Activity {
	
	EditText firstName;
	EditText lastName;
	EditText email;
	EditText password;
	
	Button createAccountButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		firstName = (EditText) findViewById(R.id.create_account_first_name);
		lastName = (EditText) findViewById(R.id.create_account_last_name);
		email = (EditText) findViewById(R.id.create_account_email);
		password = (EditText) findViewById(R.id.create_account_password);
		
		createAccountButton = (Button) findViewById(R.id.create_account_button);
		createAccountButton.setOnClickListener(createAccountButtonListener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		RollCallUtil.checkPlayServices(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home: // Action bar back button.
	    	finish();
	        break;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	View.OnClickListener createAccountButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Thread t = new Thread() {
				@Override
				public void run() {
					JSONObject result = API.createAccount(CreateAccountActivity.this, firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(), password.getText().toString());
					Message msg = Message.obtain();
					msg.obj = result;
					createAccountAttemptHandler.sendMessage(msg);
				}
			};
			t.start();
		}
	};
	
	Handler createAccountAttemptHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			JSONObject response = (JSONObject) msg.obj;
			String status = "";
			Toast toast;
			
			try{
				status = response.getString("status");
				switch(status) {
				case "ok":
					getIntent().putExtra("email", email.getText().toString());
					getIntent().putExtra("password", password.getText().toString());
					setResult(RESULT_OK, getIntent());
					finish();
					break;
				case "error":
					if(response.getInt("errno") == 1062) {
						toast = Toast.makeText(CreateAccountActivity.this, "There is already an account associated with that email address.", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						toast.show();
					} else {
						Log.d("CreateAccountActivity", "MySQL error " + response.getString("errno").toString());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.d("CreateAccountActivity", "Error in createAccountAttemptHandler");
			}
			return false;
		}
	});
}
