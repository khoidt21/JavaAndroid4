package lib;
import com.google.android.gms.maps.model.LatLng;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DirectionsUrl {

    private static final String URLAPI = "https://maps.google.com/maps/api/directions/json";
    private static final String KEYAPI = "AIzaSyAGqOwbGtnXAIlQ3hoYvgYwDMRHKBgYYHo";

   // https://maps.google.com/maps/api/directions/json?origin=20.963669,105.823021&destination=21.005645,105.824216&key=AIzaSyAGqOwbGtnXAIlQ3hoYvgYwDMRHKBgYYHo

    public String url(LatLng origin,LatLng dest) {

        //String origin = URLEncoder.encode(urlOrigin,"utf-8");
        //String destination = URLEncoder.encode(urlDestination,"utf-8");

        String str_origin = origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = dest.latitude + "," + dest.longitude;

       //String url = URLAPI + "?origin="+str_origin+ "&destination="+str_dest+ "&key="+KEYAPI+"";

        String url = "https://maps.google.com/maps/api/directions/json?origin="+str_origin+"&destination="+str_dest+"+&key=AIzaSyAGqOwbGtnXAIlQ3hoYvgYwDMRHKBgYYHo";
        return url;
    }



}
