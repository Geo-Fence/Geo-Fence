package edu.temple.rollcall.cards;

import java.util.Date;
import java.util.List;
import com.nostra13.universalimageloader.core.ImageLoader;

import edu.temple.rollcall.R;

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
            viewHolder.countdown = (TextView) convertView.findViewById(R.id.card_countdown);
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
        viewHolder.countdown.setText(card.timer.output);
        if(viewHolder.timer != null) viewHolder.timer.cancel();
        viewHolder.timer = new ViewHolderUpdateTimer(card.end_time_millis - new Date().getTime(), 1000, viewHolder.countdown, card, this);

        return convertView;
    }
    
    public void removeTopCard() {
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
    	private Card card;
    	private CardListAdapter adapter;

    	public ViewHolderUpdateTimer(long millisInFuture, long countDownInterval, TextView textView, Card card, CardListAdapter adapter) {
    		super(millisInFuture, countDownInterval);
    		this.textView = textView;
    		this.card = card;
    		this.adapter = adapter;
    		this.start();
    	}

    	@Override
    	public void onTick(long millisUntilFinished) {
    		this.textView.setText(card.timer.output);
    	}

    	@Override
    	public void onFinish() {
    		// When session is over, remove the card.
    		adapter.removeTopCard();
    	}
    }
}
