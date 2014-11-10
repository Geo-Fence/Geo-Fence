package edu.temple.rollcall.cards;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.nostra13.universalimageloader.core.ImageLoader;

import edu.temple.rollcall.R;
//import edu.temple.rollcall.util.CardCountDownTimer;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CardListAdapter extends ArrayAdapter<Card> {
	 
    public CardListAdapter(Context context, List<Card> items) {
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
            viewHolder.timerText = (TextView) convertView.findViewById(R.id.card_countdown);
            viewHolder.timer = new CardCountDownTimer(getItem(position).start_time_millis - new Date().getTime(), 1000, viewHolder.timerText);
            viewHolder.timer.start();
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view 
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        // update the item view
        Card card = getItem(position);
        ImageLoader.getInstance().displayImage(card.thumbURL, viewHolder.icon);
        viewHolder.course_name.setText(card.course_name);
        viewHolder.teacher_name.setText(card.teacher_name);
        String sessionTime = card.day_of_week + ", " + card.start_time + " - " + card.end_time;
        viewHolder.session_time.setText(sessionTime);
        if(new Date().getTime() >= card.start_time_millis) viewHolder.timerText.setText("In progress...");
        
        return convertView;
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
        TextView timerText;
        
        CardCountDownTimer timer;
    }
    
    public class CardCountDownTimer extends CountDownTimer {

    	TextView view;
    	
    	public CardCountDownTimer(long millisInFuture, long countDownInterval, TextView view) {
    		super(millisInFuture, countDownInterval);
    		this.view = view;
    	}

    	@Override
    	public void onTick(long millisUntilFinished) {
    		Long millis = millisUntilFinished;
    		long days = TimeUnit.MILLISECONDS.toDays(millis);
            millis -= TimeUnit.DAYS.toMillis(days);
            long hours = TimeUnit.MILLISECONDS.toHours(millis);
            millis -= TimeUnit.HOURS.toMillis(hours);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            if(days == 0) {
            	String message = "Begins in";
            	if(hours > 1) message += " " + hours + " hours";
            	else if(hours == 1) message += " " + hours + " hour";
            	if(minutes > 1) message += " " + minutes + " minutes.";
            	else if(minutes == 1) message += " " + minutes + " minute.";
            	if(hours == 0 && minutes == 0) message += " less than a minute.";
            	this.view.setText(message);
            } else {
            	this.view.setVisibility(View.GONE);
            }
    	}

    	@Override
    	public void onFinish() {
    		this.view.setText("In progress...");
    	}
    	
    }
    
    
}
