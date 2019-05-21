package com.org.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lib.DirectionsJSONParser;
import lib.DirectionsUrl;
import lib.JsonDataFromURL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    GoogleMap mMap;
    EditText editOrigin;
    EditText editDestnation;
    Button btnFindPath;
    ProgressDialog progressDialog;
    LocationManager lm;

    List<Marker> markerList = new ArrayList<>();


    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";

    String keyAPI = "AIzaSyAGqOwbGtnXAIlQ3hoYvgYwDMRHKBgYYHo";

    LatLng origin = new LatLng(20.963669, 105.823021);
    LatLng dest = new LatLng(21.005645, 105.824216);

    LatLng latLngOrigin;
    LatLng latLngDest;
    String provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

       // lm = (LocationManager) getSystemService(LOCATION_SERVICE);
       // provider = lm.getBestProvider(new Criteria(), false);

        editOrigin = (EditText) findViewById(R.id.editOrigin);
        editDestnation = (EditText) findViewById(R.id.editDest);
        btnFindPath = (Button) findViewById(R.id.btnFindPath);


         btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  seachLoction();
//                try {
//                    drawPolylines();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
            }
        });

        try {
            drawPolylines();
        } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
       }
    }


    private void drawPolylines() throws UnsupportedEncodingException {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait, Polyline between two locations is building.");
        progressDialog.setCancelable(false);
        progressDialog.show();

//        final Geocoder geocoder = new Geocoder(this);
//        List<Address> addressListOrigin  = null;
//
//        try {
//            String origin = editOrigin.getText().toString();
//            addressListOrigin = geocoder.getFromLocationName(origin, 1);
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//        Address address = addressListOrigin.get(0);
//        latLngOrigin = new LatLng(address.getLatitude(),address.getLongitude());
//
//        List<Address> addressListDest  = null;
//
//        try {
//            String dest = editDestnation.getText().toString();
//            addressListDest = geocoder.getFromLocationName(dest, 1);
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//        Address addressDest = addressListDest.get(0);
//        latLngDest = new LatLng(address.getLatitude(),address.getLongitude());


        DirectionsUrl directionsUrl = new DirectionsUrl();

        String url = directionsUrl.url(origin,dest);


        //String url = directionsUrl.url();

        Log.d("url", url + "");
        DownloadTask downloadTask = new DownloadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions()
                .position(origin)
                .title("LinkedIn")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        googleMap.addMarker(new MarkerOptions()
                .position(dest));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));

//        mMap = googleMap;
//        LatLng hcmus = new LatLng(20.963081, 105.822766);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 18));
//        markerList.add(mMap.addMarker(new MarkerOptions()
//                .title("Xôi Gà Vinh Hoa")
//                .position(hcmus)));
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {

//        try {
//            drawPolylines();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        lm.requestLocationUpdates(provider, 400, 1, this);
//        lat = location.getLatitude();
//        lng = location.getLongitude();
//        marker.setPosition(new LatLng(lat,lng));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lng)));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        JsonDataFromURL jsonDataFromURL = new JsonDataFromURL();
        @Override
        protected String doInBackground(String... url) {

            String data = "";
            try {
                data = jsonDataFromURL.downloadUrl(url[0]);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            progressDialog.dismiss();
            Log.d("result", result.toString());
            ArrayList points = null;
            PolylineOptions lineOptions = null;

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }
}
