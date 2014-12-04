package edu.temple.rollcall.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class SessionList {
	
	private static ArrayList<Session> sessions = new ArrayList<Session>();
	
	public static void update(JSONArray jsonSessionArray) {
		sessions.clear();
		for(int i = 0 ; i < jsonSessionArray.length() ; i++) {
			try {
				sessions.add(new Session(jsonSessionArray.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Session getSessionWithId(String sessionId) {
		for(Session session : sessions) {
			if(session.sessionId.equals(sessionId)) {
				return session;
			}
		}
		return null;
	}
	
	public static Session getSession(int index) {
		return sessions.get(index);
	}
	
	public static void clear() {
		sessions.clear();
	}
	
	public static int length() {
		return sessions.size();
	}
	
	public static List<Session> getSessionList() {
		return sessions;
	}
}
