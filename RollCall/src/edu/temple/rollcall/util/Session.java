package edu.temple.rollcall.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.json.JSONObject;

import android.os.CountDownTimer;

public class Session {
	
	private JSONObject jsonSession;
	
	public String sessionId;
	public String courseName;
    public String teacherName;
    public String dayOfWeek;
    public String startTimeString;
    public String endTimeString;
    public double latitude;
    public double longitude;
	public long startTimeMillis;
	public long endTimeMillis;
	public SessionCountDownTimer timer;
    
    public Session(JSONObject jsonSession) {
    	this.jsonSession = jsonSession;
    	
    	try {
    		this.sessionId = jsonSession.getString("session_id");
    		this.courseName = jsonSession.getString("course_name");
    		this.teacherName = jsonSession.getString("teacher_first_name") + " " + jsonSession.getString("teacher_last_name");
    		
    		this.latitude = Double.parseDouble(jsonSession.getString("lat"));
    		this.longitude = Double.parseDouble(jsonSession.getString("lng"));
    		this.startTimeMillis = Long.parseLong(jsonSession.getString("start_time")) * 1000;
    		this.endTimeMillis = Long.parseLong(jsonSession.getString("end_time")) * 1000;
    		
    		DateTime dateTime = new DateTime(this.startTimeMillis);
    		this.dayOfWeek = dateTime.toString("EEEE");
    		this.startTimeString = dateTime.toString("h:mm a");
    		
    		dateTime = dateTime.withMillis(this.endTimeMillis);
    		this.endTimeString = dateTime.toString("h:mm a");
    		
    		this.timer = new SessionCountDownTimer(this.startTimeMillis - new Date().getTime(), 1000);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public String toJSONString() {
    	return this.jsonSession.toString();
    }
    
    public class SessionCountDownTimer extends CountDownTimer {

    	public String output;

    	public SessionCountDownTimer(long millisInFuture, long countDownInterval) {
    		super(millisInFuture, countDownInterval);
    		this.start();
    	}

    	@Override
    	public void onTick(long millisUntilFinished) {
    		Long millis = millisUntilFinished;
    		long days = TimeUnit.MILLISECONDS.toDays(millis);
    		millis -= TimeUnit.DAYS.toMillis(days);
    		long hours = TimeUnit.MILLISECONDS.toHours(millis);
    		millis -= TimeUnit.HOURS.toMillis(hours);
    		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
    		
    		days++;
    		hours++;
    		minutes++;
    	
    		// Generates a grammatically correct phrase...
    		String message = "Begins in ";
    		if(days < 1) {
    			if(hours > 0) {
    				message += hours;
    				message += (hours == 1) ? " hour" : " hours";
    				message += (minutes >= 0) ? ", " : "";
    			} else if(minutes == 0) {
    				message += "less than a minute";
    			}
    			if(minutes > 0) {
    				message += minutes;
    				message += (minutes == 1) ? " minute" : " minutes";
    			}
    		} else {
    			message += days;
    			message += (days > 1) ? " days" : " day";
    		}
    		this.output = message;
    	}

    	@Override
    	public void onFinish() {
    		this.output = "In progress...";
    	}
    }

}
