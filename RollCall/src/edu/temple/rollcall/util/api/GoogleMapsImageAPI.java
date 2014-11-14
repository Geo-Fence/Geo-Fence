package edu.temple.rollcall.util.api;

public class GoogleMapsImageAPI {
	
	public static final String urlPrefix = "https://maps.googleapis.com/maps/api/staticmap?";

	public static String getURLString(int width, int height, int zoom, double lat, double lng) {
		String url = urlPrefix;
		url += "size=" + width + "x" + height;
		url += "&zoom=" + zoom;
		url += "&center=" + lat + "," + lng;
		return url;
	}
}
