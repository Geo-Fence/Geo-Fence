package edu.temple.rollcall;

import edu.temple.rollcall.util.RollCallUtil;
import edu.temple.rollcall.util.UserAccount;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class AccountDetailActivity extends Activity {

	TextView name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_detail);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		name = (TextView) findViewById(R.id.name);
		name.setText(UserAccount.firstName + " " + UserAccount.lastName);
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
}
