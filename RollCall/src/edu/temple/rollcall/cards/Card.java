package edu.temple.rollcall.cards;

import org.json.JSONObject;

import android.graphics.drawable.Drawable;

public class Card {
	public JSONObject sessionInfo;
    public Drawable icon;
    public String course_name;
    public String teacher_name;
    
    public Card(Drawable icon, JSONObject sessionInfo) {
    	this.sessionInfo = sessionInfo;
    	this.icon = icon;
    	try {
    		this.course_name = sessionInfo.getString("course_name");
    		this.teacher_name = sessionInfo.getString("teacher_first_name") + " " + sessionInfo.getString("teacher_last_name");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}