package edu.temple.rollcall.cards;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.temple.rollcall.SessionDetailActivity;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class CardListFragment extends ListFragment {

	private List<Card> cardItems;

	public static final CardListFragment newInstance(String sessionArray)
	{
		CardListFragment fragment = new CardListFragment();
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

		cardItems = new ArrayList<Card>();

		for(int i = 0 ; i < sessionArray.length() ; i++) {
			JSONObject sessionInfo;
			try {
				sessionInfo = sessionArray.getJSONObject(i);
				
				cardItems.add(new Card(sessionInfo));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		setListAdapter(new CardListAdapter(getActivity(), cardItems));
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getListView().setDivider(null);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Card card = cardItems.get(position);

		Intent intent = new Intent(getActivity(), SessionDetailActivity.class);
		intent.putExtra("sessionInfo", card.sessionInfo.toString());
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}
