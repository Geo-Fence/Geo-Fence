package edu.temple.rollcall.util;

import edu.temple.rollcall.EnrollActivity;
import edu.temple.rollcall.R;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

// Fragment to be displayed when a user logs in and has no upcoming sessions.
public class EmptyFeedFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(R.layout.empty_feed, container, false);
		Button enrollButton = (Button) myFragmentView.findViewById(R.id.empty_feed_button);
		enrollButton.setOnClickListener(enrollButtonOnClickListener);
		return myFragmentView;
	}
	
	View.OnClickListener enrollButtonOnClickListener = new View.OnClickListener() {	
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(), EnrollActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	};

}
