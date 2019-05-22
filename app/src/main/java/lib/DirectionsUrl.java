package lib;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DirectionsUrl {

    private static final String URLAPI = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String KEYAPI = "AIzaSyAGqOwbGtnXAIlQ3hoYvgYwDMRHKBgYYHo";

   // https://maps.google.com/maps/api/directions/json?origin=20.963669,105.823021&destination=21.005645,105.824216&key=AIzaSyAGqOwbGtnXAIlQ3hoYvgYwDMRHKBgYYHo

    public String url(String origin,String dest) throws UnsupportedEncodingException {

        String urlOrigin = URLEncoder.encode(origin,"utf-8");
        String urlDest = URLEncoder.encode(dest,"utf-8");
        return URLAPI + "origin=" + urlOrigin + "&destination=" + urlDest + "&key=" + KEYAPI;

    }
}
