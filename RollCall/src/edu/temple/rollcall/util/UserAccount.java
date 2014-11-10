package edu.temple.rollcall.util;

import org.json.JSONException;
import org.json.JSONObject;

// Static class contains user credentials. No instance required, all variables are static.
public class UserAccount {
	
	public static String studentId = null;
	public static String email;
	public static String firstName;
	public static String lastName;
	
	// Update credentials with values from a JSON object.
	public static void update(JSONObject account) {
		try {
			studentId = account.getString("id");
			email = account.getString("email");
			firstName = account.getString("first_name");
			lastName = account.getString("last_name");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	// Clear user credentials.
	public static void logout() {
		studentId = null;
		email = null;
		firstName = null;
		lastName = null;
	}

}
