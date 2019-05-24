package com.org.PRM391x_GoogleMap_khoidtFX01411;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lib.DirectionsJSONParser;
import lib.DirectionsUrl;
import lib.JsonDataFromURL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    EditText editOrigin;
    EditText editDestnation;
    Button btnFindPath;
    ProgressDialog progressDialog;
    TextView tvdistance;
    TextView tvduration;


    private static final int LOCATION_REQUEST = 10;

    private String[] LOCATION_PERMS = {android.Manifest.permission.ACCESS_FINE_LOCATION };

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
        if (origin.isEmpty()) {
            Toast.makeText(getBaseContext(), "Enter origin address.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dest.isEmpty()) {
            Toast.makeText(getBaseContext(), "Enter destination address.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Waiting...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        DirectionsUrl directionsUrl = new DirectionsUrl();
        String url = directionsUrl.url(origin, dest);

        Log.d("url", url + "");
        DownloadTask downloadTask = new DownloadTask();
        // Downlaod du lieu JSON tu Google Directions API
        downloadTask.execute(url);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showCurrentLocation();
    }
    public void showCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, LOCATION_PERMS, LOCATION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(this, LOCATION_PERMS, LOCATION_REQUEST);
            }
        } else {
            mMap.setMyLocationEnabled(true);
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {

                                LatLng userCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                CameraPosition cp = CameraPosition.builder().target(userCurrentLocation).zoom(25).build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp), 500, null);
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case LOCATION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onMapReady(mMap);
                } else {
                    //Toast.makeText(this, R.string.location_permission_required_message, Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
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

            String distance ="";
            String duration ="";
            Double latStartlocation = null;
            Double lngStartlocation = null;
            Double latEndlocation = null;
            Double lngEndlocation = null;
            String endAddress ="";
            String startAddress ="";

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
                    else if(j==1){
                        lngStartlocation = Double.parseDouble(point.get("lng_start"));
                        continue;
                    }
                    LatLng latLngStartLocation = new LatLng(latStartlocation,lngStartlocation);

                    if(j==2){
                        latEndlocation = Double.parseDouble(point.get("lat_end"));
                        continue;
                    }
                    else if(j==3) {
                        lngEndlocation = Double.parseDouble(point.get("lng_end"));
                        continue;
                    }
                    LatLng latLngEndLocation = new LatLng(latEndlocation,lngEndlocation);


                    // lay endAddress
                    if(j==4){
                        endAddress = (String)point.get("end_address");
                        continue;
                    }
                    else if(j==5){ // lay startAddress
                        startAddress = (String)point.get("start_address");
                        continue;
                    }

                    if(j==6){ // lay distance tu list HashMap
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==7){ // lay duration tu list HashMap
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
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngStartLocation, 15));
                    mMap.addMarker(new MarkerOptions()
                            .position(latLngStartLocation)
                            .title(startAddress)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue)));

                    mMap.addMarker(new MarkerOptions()
                            .position(latLngEndLocation).title(endAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green)));
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLUE);
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
