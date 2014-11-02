package edu.temple.rollcall.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class UserAccount {
	
	private static String student_id = null;
	
	public static String student_id() {
		return UserAccount.student_id;
	}
	
	public static boolean login(Context context, String email, String password) {
		JSONObject result = API.login(context, email, password);
		try {
			student_id = result.getString("id");
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void update(JSONObject account) {
		try {
			student_id = account.getString("id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
