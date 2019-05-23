package lib;

import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionsJSONParser  {
    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        JSONObject jDistance = null;
        JSONObject jDuration = null;
        JSONObject jStartLocation = null;
        JSONObject jEndlocation = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            // duyet tat ca routers
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                // duyet tat ca legs
                for(int j=0;j<jLegs.length();j++){

                    // location start legs

                    jStartLocation = ((JSONObject) jLegs.get(j)).getJSONObject("start_location");
                    Double latStart = jStartLocation.getDouble("lat");
                    HashMap<String,String> hmLatStartLocationLat = new HashMap<String, String>();
                    hmLatStartLocationLat.put("lat_start",Double.toString(latStart));
//
                    Double lngStart = jStartLocation.getDouble("lng");
                    HashMap<String,String> hmLngStartLocation = new HashMap<String, String>();
                    hmLngStartLocation.put("lng_start",Double.toString(lngStart));
//
//                    // location end legs
//
                    jEndlocation = ((JSONObject) jLegs.get(j)).getJSONObject("end_location");
                    Double latEnd = jEndlocation.getDouble("lat");
                    HashMap<String,String> hmLatEndLocation = new HashMap<String, String>();
                    hmLatEndLocation.put("lat_end",Double.toString(latEnd));

                    Double lngEnd = jEndlocation.getDouble("lng");
                    HashMap<String,String> hmLngEndLocation = new HashMap<String, String>();
                    hmLngEndLocation.put("lng_end",Double.toString(lngEnd));


                    // Lay distance tu json data */
                    jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
                    HashMap<String, String> hmDistance = new HashMap<String, String>();
                    hmDistance.put("distance", jDistance.getString("text"));

                    // Lay duration tu json data */
                    jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                    HashMap<String, String> hmDuration = new HashMap<String, String>();
                    hmDuration.put("duration", jDuration.getString("text"));

                    // add lat and lng cua diem bat dau
                     path.add(hmLatStartLocationLat);
                     path.add(hmLngStartLocation);

                    // add lat and lng cua diem ket thuc
                    path.add(hmLatEndLocation);
                    path.add(hmLngEndLocation);


                    // Adding distance object vao path
                    path.add(hmDistance);

                    // Adding duration object vao path

                    path.add(hmDuration);

                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List list = decodePolyLine(polyline);

                        /** Traversing all points */
                        for(int l=0;l <list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );


                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

        return routes;
    }
    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }
        return decoded;
    }
}
