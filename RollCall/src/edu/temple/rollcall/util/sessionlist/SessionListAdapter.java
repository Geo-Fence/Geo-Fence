package edu.temple.rollcall.util.sessionlist;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;

import edu.temple.rollcall.MainActivity;
import edu.temple.rollcall.R;
import edu.temple.rollcall.util.GeofenceTransitionService;
import edu.temple.rollcall.util.Session;
import edu.temple.rollcall.util.UserAccount;
import edu.temple.rollcall.util.api.API;
import edu.temple.rollcall.util.api.GoogleMapsImageAPI;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SessionListAdapter extends ArrayAdapter<Session> {
	 
    public SessionListAdapter(Context context, List<Session> items) {
        super(context, R.layout.card_item, items);
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        ViewHolder viewHolder;
        
        if(convertView == null) {
            // inflate the layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.card_item, parent, false);
            
            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.card_icon);
            viewHolder.course_name = (TextView) convertView.findViewById(R.id.card_course_name);
            viewHolder.teacher_name = (TextView) convertView.findViewById(R.id.card_teacher_name);
            viewHolder.session_time = (TextView) convertView.findViewById(R.id.card_session_time);
            viewHolder.countdown = (TextView) convertView.findViewById(R.id.card_countdown);
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        // update the item view
        Session session = getItem(position);
        ImageLoader.getInstance().displayImage(GoogleMapsImageAPI.getURLString(200, 200, 17, session.latitude, session.longitude), viewHolder.icon);
        viewHolder.course_name.setText(session.courseName);
        viewHolder.teacher_name.setText(session.teacherName);
        String sessionTime = session.dayOfWeek + ", " + session.startTimeString + " - " + session.endTimeString;
        viewHolder.session_time.setText(sessionTime);
        viewHolder.countdown.setText(session.timer.output);
        if(viewHolder.timer != null) viewHolder.timer.cancel();
        viewHolder.timer = new ViewHolderUpdateTimer(session.endTimeMillis - new Date().getTime(), 1000, viewHolder.countdown, session, this);

        return convertView;
    }
    
    public void removeTopSession() {
    	this.remove(getItem(0));
    }
    
    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     * 
     * @see http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
     */
    private static class ViewHolder {
        ImageView icon;
        TextView course_name;
        TextView teacher_name;
        TextView session_time;
        TextView countdown;
        
        ViewHolderUpdateTimer timer;
    }
    
    // Each ViewHolder has a ViewHolderUpdateTimer to periodically update the card view.
    // Current implementation uses the timer to display an updated count down message on
    // sessions that begin in less than a day. Can also be used to display an updated 
    // distance message, i.e. "Two miles away". Countdown expires when session is over.
    private class ViewHolderUpdateTimer extends CountDownTimer {

    	private TextView textView;
    	private Session session;
    	private SessionListAdapter adapter;

    	public ViewHolderUpdateTimer(long millisInFuture, long countDownInterval, TextView textView, Session session, SessionListAdapter adapter) {
    		super(millisInFuture, countDownInterval);
    		this.textView = textView;
    		this.session = session;
    		this.adapter = adapter;
    		this.start();
    	}

    	@Override
    	public void onTick(long millisUntilFinished) {
    		this.textView.setText(session.timer.output);
    	}

    	@Override
    	public void onFinish() {
    		// When session is over, remove the card.
    		adapter.removeTopSession();
    		checkOut(session.sessionId);
    	}
    	
    	private void checkOut(final String sessionId) {
    		Thread t = new Thread() {
    			@Override
    			public void run() {
    				try {
    					JSONObject response = API.checkOut(getContext(), UserAccount.studentId, sessionId); 
    					if(response.getString("status").equals("ok")) {
    						Toast.makeText(getContext(), "Automatically checked out of session based on your location.", Toast.LENGTH_SHORT).show();
    					}
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    			}
    		};
    		t.start();
    	}
    }
}
