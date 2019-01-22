package com.steve.CitySearch;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_MULTIPLE_PERMISSIONS =24;
    public boolean permission_denied = false;
    protected double lat, lng;
    protected String cityAddr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getLocationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            List<String> PermissionList = new ArrayList<>();
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                PermissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                PermissionList.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (PermissionList.size() > 0)
                requestPermissions(PermissionList.toArray(new String[PermissionList.size()]), REQUEST_MULTIPLE_PERMISSIONS);
        }
    }

    private void findPassedCityData() {
        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat", 0);
        lng = intent.getDoubleExtra("lng", 0);
        cityAddr = intent.getStringExtra("cityAddr");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_MULTIPLE_PERMISSIONS: {
                int len = grantResults.length;
                StringBuilder sb = new StringBuilder();
                if (len > 0) {
                    for (int i = 0; i < len; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            sb.append(permissions[i] + "\n");
                        }
                    }

                    if (sb.length() > 0) {
                        Toast.makeText(this, "Permission denided, this app may not work.", Toast.LENGTH_LONG).show();
                        permission_denied = true;
                    } else {
                        Toast.makeText(this, "Permission granted, you are ready to go.", Toast.LENGTH_LONG).show();
                        permission_denied = false;
                    }
                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in city and move the camera
        LatLng cityCoord = new LatLng(lat, lng);
        float zoom = (float) 9.0;
        mMap.addMarker(new MarkerOptions().position(cityCoord).title(cityAddr));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityCoord, zoom));
    }

    @Override
    protected void onResume() {
        super.onResume();
        findPassedCityData();
    }
}
