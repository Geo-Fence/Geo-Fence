package edu.temple.rollcall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class InfoPage extends Activity {

	TextView tvInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_page);

		tvInfo = (TextView) findViewById(R.id.info_name);

		Intent intent = getIntent();
		tvInfo.setText(intent.getExtras().getString("coursename"));
	}

}
