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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

        String origin = editOrigin.getText().toString();
        String dest = editDestnation.getText().toString();
        if(origin.isEmpty()){
            Toast.makeText(getBaseContext(),"Enter origin address.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(dest.isEmpty()){
            Toast.makeText(getBaseContext(),"Enter destination address.",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Vui lòng chờ, đang tìm đường đi giữa hai điểm.");
        progressDialog.setCancelable(false);
        progressDialog.show();



        DirectionsUrl directionsUrl = new DirectionsUrl();
        String url = directionsUrl.url(origin,dest);

        Log.d("url", url + "");
        DownloadTask downloadTask = new DownloadTask();
        // Downlaod du lieu JSON tu Google Directions API
        downloadTask.execute(url);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng currentLocation = new LatLng(20.963081, 105.822766);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));
        mMap.addMarker(new MarkerOptions()
                .title("Xôi Gà Vinh Hoa")
                .position(currentLocation));

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
       // mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getMyLocation();
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

            if(result.size() < 1){
                Toast.makeText(getBaseContext(),"Không tìm thấy đường đi.",Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);
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
                  //  mMap.clear();

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
                    if(points.size() > 1){
                        mMap.clear();
                    }
                    points.add(position);
                    mMap.addMarker(new MarkerOptions()
                            .position(latLngStartLocation)
                            .title("From")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue)));

                    mMap.addMarker(new MarkerOptions()
                            .position(latLngEndLocation).title("To").icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngStartLocation, 15));
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
           // mMap.clear();
            mMap.addPolyline(lineOptions);
        }
    }
}
