package edu.temple.rollcall.util.sessionlist;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.temple.rollcall.SessionDetailActivity;
import edu.temple.rollcall.util.Session;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class SessionListFragment extends ListFragment {

	private ArrayList<Session> sessionList;

	public static final SessionListFragment newInstance(String sessionArray)
	{
		SessionListFragment fragment = new SessionListFragment();
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

		sessionList = new ArrayList<Session>();

		for(int i = 0 ; i < sessionArray.length() ; i++) {
			JSONObject sessionInfo;
			try {
				sessionInfo = sessionArray.getJSONObject(i);
				sessionList.add(new Session(sessionInfo));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		setListAdapter(new SessionListAdapter(getActivity(), sessionList));
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getListView().setDivider(null);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Session session = sessionList.get(position);

		Intent intent = new Intent(getActivity(), SessionDetailActivity.class);
		intent.putExtra("sessionInfo", session.toJSONString());
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}
