//package edu.temple.rollcall.util;
//
//import java.util.concurrent.TimeUnit;
//
//import android.os.CountDownTimer;
//import android.view.View;
//import android.widget.TextView;
//
//public class CardCountDownTimer extends CountDownTimer {
//
//	TextView view;
//	
//	public CardCountDownTimer(long millisInFuture, long countDownInterval, TextView view) {
//		super(millisInFuture, countDownInterval);
//		this.view = view;
//	}
//
//	@Override
//	public void onTick(long millisUntilFinished) {
//		Long millis = millisUntilFinished;
//		long days = TimeUnit.MILLISECONDS.toDays(millis);
//        millis -= TimeUnit.DAYS.toMillis(days);
//        long hours = TimeUnit.MILLISECONDS.toHours(millis);
//        millis -= TimeUnit.HOURS.toMillis(hours);
//        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
//        if(days == 0) {
//        	String message = "Begins in";
//        	if(hours > 1) message += " " + hours + " hours";
//        	else if(hours == 1) message += " " + hours + " hour";
//        	if(minutes > 1) message += " " + minutes + " minutes.";
//        	else if(minutes == 1) message += " " + minutes + " minute.";
//        	if(hours == 0 && minutes == 0) message += " less than a minute.";
//        	this.view.setText(message);
//        } else {
//        	this.view.setVisibility(View.GONE);
//        }
//	}
//
//	@Override
//	public void onFinish() {
//		this.view.setVisibility(View.GONE);
//	}
//	
//}