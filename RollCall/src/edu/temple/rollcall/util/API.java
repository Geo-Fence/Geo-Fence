package edu.temple.rollcall.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;

public class API  {
	static final String APIBaseURL = "http://cis-linux2.temple.edu/~tud04734/api/";
	
	private static String makeAPICall(Context context, String api) throws ClientProtocolException, IOException {
    	AndroidHttpClient client = AndroidHttpClient.newInstance("Android", context);
    	HttpGet method = new HttpGet(APIBaseURL + api);
    	method.addHeader("Accept-Encoding", "gzip");
    	HttpResponse httpResponse = client.execute(method);
    	String response = extractHttpResponse(httpResponse);
    	client.close();
        return response.toString();
	}
	
	private static String extractHttpResponse(HttpResponse httpResponse) throws IllegalStateException, IOException{
    	InputStream instream = httpResponse.getEntity().getContent();
        Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding"); 
        if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
            instream = new GZIPInputStream(instream);
        }      
        BufferedReader r = new BufferedReader(new InputStreamReader(instream));      
        StringBuilder response = new StringBuilder();
        String line = "";       
        while ((line = r.readLine()) != null) {
            response.append(line);
        }
        return response.toString();
    }
	
	/**
	 * Get all upcoming sessions for the specified student.
	 * @param context
	 * @param student_id
	 * @return JSON Array of all upcoming sessions
	 * @throws Exception
	 */
	public static JSONObject getSessionsForStudent(Context context, String student_id) {
		String responseStr;
		try {
			responseStr = makeAPICall(context, "getsessionsforstudent.php?student_id=" + student_id);
			return new JSONObject(responseStr);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Check in the specified student to the specified session
	 * @param context
	 * @param student_id
	 * @param session_id
	 * @return TRUE if the check in was successful, FALSE if the student has already checked in.
	 * @throws Exception
	 */
	public static boolean checkIn(Context context, String student_id, String session_id) throws Exception {
		String response = makeAPICall(context, "checkin.php?student_id=" + student_id + "&session_id=" + session_id);
		if(response.equals("success")) {
			return true;
		}
		return false;
	}
	
	/**
	 * Check out the specified student from the specified session
	 * @param context
	 * @param student_id
	 * @param session_id
	 * @return TRUE if the check out was successful, FALSE if the student never checked in.
	 * @throws Exception
	 */
	public static boolean checkOut(Context context, String student_id, String session_id) throws Exception {
		String response = makeAPICall(context, "checkout.php?student_id=" + student_id + "&session_id=" + session_id);
		if(response.equals("success")) {
			return true;
		}
		return false;
	}
	
	public static JSONObject login(Context context, String email, String password) {
		try {
			String response = makeAPICall(context, "login.php?email=" + email + "&password=" + password);
			return new JSONObject(response);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
