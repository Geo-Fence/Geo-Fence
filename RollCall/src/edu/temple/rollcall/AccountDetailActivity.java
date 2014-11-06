package edu.temple.rollcall;

import edu.temple.rollcall.util.UserAccount;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class AccountDetailActivity extends Activity {

	TextView name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_detail);
		getActionBar().setDisplayShowHomeEnabled(false);
		
		name = (TextView) findViewById(R.id.name);
		name.setText(UserAccount.firstName + " " + UserAccount.lastName);
	}
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    
	    Drawable editIcon = getResources().getDrawable(R.drawable.ic_action_edit);
	    MenuItem account = menu.findItem(R.id.action_account);
	    account.setIcon(editIcon);
	    account.setEnabled(false);
	    
	    return true;
	  } 
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
	    switch (item.getItemId()) {
	    case R.id.action_home:
	    	intent = new Intent(AccountDetailActivity.this, MainActivity.class);
	        intent.setAction("refresh");
	        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
	        break;
	    case R.id.action_logout:
	    	intent = new Intent(AccountDetailActivity.this, MainActivity.class);
	    	intent.setAction("logout");
	    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	startActivity(intent);
	    	break;
	    case R.id.action_account:
	    	// Edit account info...
	    	break;
	    }
	    return super.onOptionsItemSelected(item);
	}
}
