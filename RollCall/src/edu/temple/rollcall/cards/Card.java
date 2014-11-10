package edu.temple.rollcall.cards;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.json.JSONObject;

import android.os.CountDownTimer;

public class Card {
	public static final String thumbURLPrefix = "https://maps.googleapis.com/maps/api/staticmap?&size=200x200&zoom=17&center=";

	public JSONObject sessionInfo;
	public long start_time_millis;
	public long end_time_millis;
	public String thumbURL;
    public String course_name;
    public String teacher_name;
    public String day_of_week;
    public String start_time;
    public String end_time;
    
    public CardCountDownTimer timer;
        
    public Card(JSONObject sessionInfo) {
    	this.sessionInfo = sessionInfo;
    	try {
    		this.thumbURL = thumbURLPrefix + sessionInfo.getString("lat") + "," + sessionInfo.getString("lng");
    		this.course_name = sessionInfo.getString("course_name");
    		this.teacher_name = sessionInfo.getString("teacher_first_name") + " " + sessionInfo.getString("teacher_last_name");
    		
    		DateTime dateTime = new DateTime(Long.parseLong(sessionInfo.getString("start_time")) * 1000);
    		this.day_of_week = dateTime.toString("E");
    		this.start_time = dateTime.toString("h:mm a");
    		
    		dateTime = dateTime.withMillis(Long.parseLong(sessionInfo.getString("end_time")) * 1000);
    		this.end_time = dateTime.toString("h:mm a");
    		    		
    		this.start_time_millis = Long.parseLong(sessionInfo.getString("start_time")) * 1000;
    		this.end_time_millis = Long.parseLong(sessionInfo.getString("end_time")) * 1000;
    		
    		this.timer = new CardCountDownTimer(this.start_time_millis - new Date().getTime(), 1000);
      	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    // Each card has a CardCountDownTimer which provides a message containing the time remaining
    // until the session begins. Only provides output for sessions beginning in less than one day.
    // The CardListAdapter references the public "output" string to display a countdown on the card
    // in the list.
    public class CardCountDownTimer extends CountDownTimer {

    	public String output;

    	public CardCountDownTimer(long millisInFuture, long countDownInterval) {
    		super(millisInFuture, countDownInterval);
    		this.start();
    	}

    	@Override
    	public void onTick(long millisUntilFinished) {
    		// Generates a grammatically correct phrase...
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
    			else if(minutes == 0) message += ".";
    			if(hours == 0 && minutes == 0) message += " less than a minute.";
    			this.output = message;
    		} else {
    			this.output = "";
    		}
    	}

    	@Override
    	public void onFinish() {
    		this.output = "In progress...";
    	}
    }
}