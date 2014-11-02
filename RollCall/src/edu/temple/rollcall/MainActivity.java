package edu.temple.rollcall;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.temple.rollcall.R;
import edu.temple.rollcall.util.API;
import edu.temple.rollcall.util.UserAccount;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	static final int LOGIN_REQUEST = 1;
		
	CardListView cardListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		cardListView = (CardListView) findViewById(R.id.card_list);
		
		if(UserAccount.student_id() == null) {
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
					JSONArray sessions = API.getSessionsForStudent(MainActivity.this, UserAccount.student_id());
					Message msg = Message.obtain();
					msg.obj = sessions;
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
			
			JSONArray sessions_array = (JSONArray) msg.obj;
			ArrayList<Card> cards = new ArrayList<Card>();
			
			for(int i = 0 ; i < sessions_array.length() ; i++) {
				try {
					JSONObject session = sessions_array.getJSONObject(i);
					Card card = new Card(MainActivity.this, R.layout.row_card);
					card.setTitle(session.getString("course_name"));
					cards.add(card);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			CardArrayAdapter cardArrayAdapter = new CardArrayAdapter(MainActivity.this, cards);
			cardListView.setAdapter(cardArrayAdapter);
			
			return true;
		}
	});

}
