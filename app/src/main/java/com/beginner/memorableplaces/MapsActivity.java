package com.beginner.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, LocationListener {

    private GoogleMap mMap;
    int location = -1;
    LocationManager locationManager;
    String provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
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
        Location userlocation = locationManager.getLastKnownLocation(provider);
        if (userlocation != null) {
            onLocationChanged(userlocation);
        } else {
            Log.i("location info", "location not found");
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        Intent i = getIntent();
        location = i.getIntExtra("locationinfo", -1);

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);


            // Check if we were successful in obtaining the map.


        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        mMap.setOnMapLongClickListener(this);
        if (location != -1 && location != 0) {
            locationManager.removeUpdates(this);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.locations.get(location), 10));

            mMap.addMarker(new MarkerOptions().position(MainActivity.locations.get(location)).title(MainActivity.places.get(location)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        } else {

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
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                locationManager.removeUpdates(this);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
        String label = new Date().toString();
        try {
            List<Address> addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                label = addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainActivity.places.add(label);
        MainActivity.arrayAdapter.notifyDataSetChanged();
        MainActivity.locations.add(latLng);
        mMap.addMarker(new MarkerOptions().position(latLng).title(label).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

    }

    @Override
    public void onLocationChanged(Location userlocation) {
//        Toast.makeText(this,"Latitude "+String.valueOf(userlocation.getLatitude())+"Longitude "+String.valueOf(userlocation.getLongitude()),Toast.LENGTH_LONG).show();
        Double lat = userlocation.getLatitude();
        Double lang = userlocation.getLongitude();
//        Toast.makeText(this,"location found"+String.valueOf(lat)+String.valueOf(lang),Toast.LENGTH_LONG).show();
if(mMap!=null) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lang), 10));
}

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


    @Override
    protected void onResume() {
        super.onResume();
        if (location == -1 || location == 0) {

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
            locationManager.requestLocationUpdates(provider, 400, 1, this);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }
}
