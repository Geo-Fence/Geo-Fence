package edu.temple.rollcall.cards;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;

import edu.temple.rollcall.R;

import android.content.Context;
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
            viewHolder.day_of_week = (TextView) convertView.findViewById(R.id.card_day_of_week);
            viewHolder.start_time = (TextView) convertView.findViewById(R.id.card_start_time);
            viewHolder.end_time = (TextView) convertView.findViewById(R.id.card_end_time);
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
        viewHolder.day_of_week.setText(card.day_of_week);
        viewHolder.start_time.setText(card.start_time);
        viewHolder.end_time.setText(card.end_time);
        
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
        TextView day_of_week;
        TextView start_time;
        TextView end_time;
    }
}
