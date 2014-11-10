package edu.temple.rollcall.cards;

import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.json.JSONObject;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;


public class Card {
	public static final String thumbURLPrefix = "https://maps.googleapis.com/maps/api/staticmap?&size=200x200&zoom=17&center=";

	public JSONObject sessionInfo;
	public long start_time_millis;
	public String thumbURL;
    public String course_name;
    public String teacher_name;
    public String day_of_week;
    public String start_time;
    public String end_time;
    
    public Card(JSONObject sessionInfo) {
    	this.sessionInfo = sessionInfo;
    	try {
    		this.start_time_millis = Long.parseLong(sessionInfo.getString("start_time")) * 1000;
    		this.thumbURL = thumbURLPrefix + sessionInfo.getString("lat") + "," + sessionInfo.getString("lng");
    		this.course_name = sessionInfo.getString("course_name");
    		this.teacher_name = sessionInfo.getString("teacher_first_name") + " " + sessionInfo.getString("teacher_last_name");
    		
    		DateTime dateTime = new DateTime(Long.parseLong(sessionInfo.getString("start_time")) * 1000);
    		this.day_of_week = dateTime.toString("E");
    		this.start_time = dateTime.toString("hh:mm a");
    		
    		dateTime = dateTime.withMillis(Long.parseLong(sessionInfo.getString("end_time")) * 1000);
    		this.end_time = dateTime.toString("hh:mm a");
      	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}