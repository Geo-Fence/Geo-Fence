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
	
	public static JSONArray getSessionsForStudent(Context context, String student_id) throws Exception {
		
		String response = makeAPICall(context, "getsessionsforstudent.php?student_id=" + student_id);
		
		try {
            JSONObject responseObject = new JSONObject(response);
            return responseObject.getJSONArray("sessions");
        } catch (JSONException e) {
            Log.i("JSON Error in: ", response);
            e.printStackTrace();
        }
		
		return null;
	}
	
	public static boolean checkIn(Context context, String student_id, String session_id) throws Exception {
		String response = makeAPICall(context, "checkin.php?student_id=" + student_id + "&session_id=" + session_id);
		if(response.equals("success")) {
			return true;
		}
		return false;
	}
	
	public static boolean checkOut(Context context, String student_id, String session_id) throws Exception {
		String response = makeAPICall(context, "checkout.php?student_id=" + student_id + "&session_id=" + session_id);
		if(response.equals("success")) {
			return true;
		}
		return false;
	}
}
