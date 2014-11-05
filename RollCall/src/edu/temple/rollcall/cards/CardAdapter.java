package edu.temple.rollcall.cards;

import java.util.List;

import edu.temple.rollcall.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CardAdapter extends ArrayAdapter<Card> {
	 
    public CardAdapter(Context context, List<Card> items) {
        super(context, R.layout.card_item, items);
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        
        if(convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.card_item, parent, false);
            
            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.card_icon);
            viewHolder.course_name = (TextView) convertView.findViewById(R.id.card_course_name);
            viewHolder.teacher_name = (TextView) convertView.findViewById(R.id.card_teacher_name);
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view 
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        // update the item view
        Card item = getItem(position);
        viewHolder.icon.setImageDrawable(item.icon);
        viewHolder.course_name.setText(item.course_name);
        viewHolder.teacher_name.setText(item.teacher_name);
        
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
    }
}
