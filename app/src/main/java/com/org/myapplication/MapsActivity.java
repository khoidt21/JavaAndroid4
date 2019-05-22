package com.org.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
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
    TextView tvdistance;
    TextView tvduration;

    List<Marker> markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.\

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        editOrigin = (EditText) findViewById(R.id.editOrigin);
        editDestnation = (EditText) findViewById(R.id.editDest);
        tvdistance = (TextView) findViewById(R.id.tvDistance);
        tvduration = (TextView) findViewById(R.id.tvDuration);
        btnFindPath = (Button) findViewById(R.id.btnFindPath);


        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    searchLocation();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void searchLocation() throws UnsupportedEncodingException {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Vui lòng chờ, đang tìm đường đi giữa hai điểm.");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String origin = editOrigin.getText().toString();
        String dest = editDestnation.getText().toString();

        DirectionsUrl directionsUrl = new DirectionsUrl();
        String url = directionsUrl.url(origin,dest);

        Log.d("url", url + "");
        DownloadTask downloadTask = new DownloadTask();
        // Downlaod du lieu JSON tu Google Directions API
        downloadTask.execute(url);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

//        mMap = googleMap;
//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        googleMap.addMarker(new MarkerOptions()
//                .position(origin)
//                .title("LinkedIn")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//
//        googleMap.addMarker(new MarkerOptions()
//                .position(dest));
//
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));

        mMap = googleMap;
        LatLng currentLocation = new LatLng(20.963081, 105.822766);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));
        markerList.add(mMap.addMarker(new MarkerOptions()
                .title("Xôi Gà Vinh Hoa")
                .position(currentLocation)));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {

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

            String distance="";
            String duration="";
            Double latStartlocation = null;
            Double lngStartlocation = null;

            Double latEndlocation = null;
            Double lngEndlocation = null;


            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

//                lineOptions = new PolylineOptions();

                //PolylineOptions lineOptions = new PolylineOptions();

                for (int j = 0; j < path.size(); j++) {

                    HashMap<String, String> point = path.get(j);

                    if(j==0) {
                        latStartlocation = Double.parseDouble(point.get("lat_start"));
                        continue;
                    }
                    if(j==1){
                        lngStartlocation = Double.parseDouble(point.get("lng_start"));
                        continue;
                    }
                    LatLng latLngStartLocation = new LatLng(latStartlocation,lngStartlocation);
//
                    if(j==2){
                        latEndlocation = Double.parseDouble(point.get("lat_end"));
                        continue;
                    }
                    if(j==3) {
                        lngEndlocation = Double.parseDouble(point.get("lng_end"));
                        continue;
                    }
                    LatLng latLngEndLocation = new LatLng(latEndlocation,lngEndlocation);
//
                    mMap.addMarker(new MarkerOptions()
                   .position(latLngStartLocation)
                   .title("From")
                   .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                    mMap.addMarker(new MarkerOptions()
                        .position(latLngEndLocation));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngStartLocation, 15));


                   if(j==4){ // lay distance tu list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==5){ // lay duration tu list
                        duration = (String)point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                   // points.add(latLngStartLocation);
                   // points.add(latLngEndLocation);

                    // points.add(latLngStartLocation);
                   // points.add(latLngEndLocation);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);
                Log.d("log 1================",lineOptions.toString());


            }
            tvdistance.setText(distance);
            tvduration.setText(duration);
            // Ve tuyen duong di len google map
            mMap.addPolyline(lineOptions);
        }
    }
}
