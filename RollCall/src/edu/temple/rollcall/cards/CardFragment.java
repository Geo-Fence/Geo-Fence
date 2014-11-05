package edu.temple.rollcall.cards;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.temple.rollcall.R;
import edu.temple.rollcall.SessionDetailActivity;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class CardFragment extends ListFragment {
		private List<Card> mItems;        // ListView items list
		 
		public static final CardFragment newInstance(String sessionArray)
		{
			CardFragment fragment = new CardFragment();
		    Bundle bundle = new Bundle(1);
		    bundle.putString("sessionArray", sessionArray);
		    fragment.setArguments(bundle);
		    return fragment;
		}
		
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        String sessionArrayStr = getArguments().getString("sessionArray");
	        JSONArray sessionArray = null;
	        
	        try {
	        	sessionArray = new JSONArray(sessionArrayStr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
	        
	        // initialize the items list
	        mItems = new ArrayList<Card>();

	        for(int i = 0 ; i < sessionArray.length() ; i++) {
	        	JSONObject sessionInfo;
				try {
					sessionInfo = sessionArray.getJSONObject(i);
		        	mItems.add(new Card(getResources().getDrawable(R.drawable.map_sample), sessionInfo));
				} catch (JSONException e) {
					e.printStackTrace();
				}
	        }
	        	
	        // initialize and set the list adapter
	        setListAdapter(new CardAdapter(getActivity(), mItems));
	    }
	    
	    @Override
	    public void onViewCreated(View view, Bundle savedInstanceState) {
	        super.onViewCreated(view, savedInstanceState);
	        getListView().setDivider(null);
	    }
	 
	    @Override
	    public void onListItemClick(ListView l, View v, int position, long id) {
	        Card card = mItems.get(position);
	        
	        Intent intent = new Intent(getActivity(), SessionDetailActivity.class);
	        intent.putExtra("sessionInfo", card.sessionInfo.toString());
	        startActivity(intent);
	    }
	}
