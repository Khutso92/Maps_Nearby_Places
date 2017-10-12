package com.example.priyanka.Khutso;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.khutso.nearByplaces.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 10000;
    double latitude, longitude;

    Bundle d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ninfo = cm.getActiveNetworkInfo();


        if (ninfo != null && ninfo.isConnected()) {
            Toast.makeText(this, "A network is connected" + ninfo.getTypeName(), Toast.LENGTH_LONG).show();

        } else

        {

            ss();

        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }


    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if (currentLocationmMarker != null) {
            currentLocationmMarker.remove();

        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }
    }

    public void onClick(View v) {
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

        switch (v.getId()) {

            case R.id.B_hopistals:
                mMap.clear();
                String hospital = "hospital";
                String url = getUrl(latitude, longitude, hospital);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();

//location Marker
                LatLng latLng = new LatLng(latitude, longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Location");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationmMarker = mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
                break;


            case R.id.B_schools:
                mMap.clear();
                String school = "school";
                url = getUrl(latitude, longitude, school);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby Schools", Toast.LENGTH_SHORT).show();

                LatLng latLng_schools = new LatLng(latitude, longitude);
                MarkerOptions markerOptions_school = new MarkerOptions();
                markerOptions_school.position(latLng_schools);
                markerOptions_school.title("Current Location");
                markerOptions_school.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationmMarker = mMap.addMarker(markerOptions_school);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_schools));
                mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
                break;

            case R.id.B_restaurants:
                mMap.clear();
                String resturant = "restuarant";
                url = getUrl(latitude, longitude, resturant);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby Restaurants", Toast.LENGTH_SHORT).show();


                LatLng latLng_restaurants = new LatLng(latitude, longitude);
                MarkerOptions markerOptions_restaurants = new MarkerOptions();
                markerOptions_restaurants.position(latLng_restaurants);
                markerOptions_restaurants.title("Current Location");
                markerOptions_restaurants.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationmMarker = mMap.addMarker(markerOptions_restaurants);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_restaurants));
                mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

                break;
            case R.id.B_pharmacy:
                mMap.clear();
                String pharmacy = "pharmacy";
                url = getUrl(latitude, longitude, pharmacy);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby pharmacies", Toast.LENGTH_SHORT).show();

                LatLng latLng_pharmacy = new LatLng(latitude, longitude);
                MarkerOptions markerOptions_pharmacy = new MarkerOptions();
                markerOptions_pharmacy.position(latLng_pharmacy);
                markerOptions_pharmacy.title("Current Location");
                markerOptions_pharmacy.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationmMarker = mMap.addMarker(markerOptions_pharmacy);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_pharmacy));
                mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

                break;

            case R.id.B_church:
                mMap.clear();
                String church = "church";
                url = getUrl(latitude, longitude, church);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby churches", Toast.LENGTH_SHORT).show();


                LatLng latLng_church = new LatLng(latitude, longitude);
                MarkerOptions markerOptions_church = new MarkerOptions();
                markerOptions_church.position(latLng_church);
                markerOptions_church.title("Current Location");
                markerOptions_church.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationmMarker = mMap.addMarker(markerOptions_church);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_church));
                mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
                break;

            case R.id.B_stadium:
                mMap.clear();
                String stadium = "stadium";
                url = getUrl(latitude, longitude, stadium);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby stadiums", Toast.LENGTH_SHORT).show();

                LatLng latLng_stadium = new LatLng(latitude, longitude);
                MarkerOptions markerOptions_stadium = new MarkerOptions();
                markerOptions_stadium.position(latLng_stadium);
                markerOptions_stadium.title("Current location");
                markerOptions_stadium.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationmMarker = mMap.addMarker(markerOptions_stadium);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_stadium));
                mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
                break;

            case R.id.B_gym:

                mMap.clear();
                String gym = "gym";
                url = getUrl(latitude, longitude, gym);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby gyms", Toast.LENGTH_SHORT).show();

                LatLng latLng_gym = new LatLng(latitude, longitude);
                MarkerOptions markerOptions_gym = new MarkerOptions();
                markerOptions_gym.position(latLng_gym);
                markerOptions_gym.title("Current location");
                markerOptions_gym.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationmMarker = mMap.addMarker(markerOptions_gym);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_gym));
                mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

                break;

            case R.id.B_gas_station:
                mMap.clear();
                String gas_station = "gas_station";
                url = getUrl(latitude, longitude, gas_station);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby gas stations", Toast.LENGTH_SHORT).show();

                LatLng latLng_gas = new LatLng(latitude, longitude);
                MarkerOptions markerOptions_gas = new MarkerOptions();
                markerOptions_gas.position(latLng_gas);
                markerOptions_gas.title("Current location");
                markerOptions_gas.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationmMarker = mMap.addMarker(markerOptions_gas);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_gas));
                mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
                break;


            case R.id.B_university:
                mMap.clear();
                String university = "university";
                url = getUrl(latitude, longitude, university);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby gas universities", Toast.LENGTH_SHORT).show();

                LatLng latLng_university = new LatLng(latitude, longitude);
                MarkerOptions markerOptions_university = new MarkerOptions();
                markerOptions_university.position(latLng_university);
                markerOptions_university.title("Current location");
                markerOptions_university.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationmMarker = mMap.addMarker(markerOptions_university);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_university));
                mMap.animateCamera(CameraUpdateFactory.zoomBy(10));


                break;

            case R.id.B_police:
                mMap.clear();
                String police = "police";
                url = getUrl(latitude, longitude, police);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby police station", Toast.LENGTH_SHORT).show();

                LatLng latLng_police = new LatLng(latitude, longitude);
                MarkerOptions markerOptions_police = new MarkerOptions();
                markerOptions_police.position(latLng_police);
                markerOptions_police.title("Current location");
                markerOptions_police.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationmMarker = mMap.addMarker(markerOptions_police);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_police));
                mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
                break;


            case R.id.B_museum:
                mMap.clear();
                String museum = "museum";
                url = getUrl(latitude, longitude, museum);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby gas museums", Toast.LENGTH_SHORT).show();

                LatLng latLng_museum = new LatLng(latitude, longitude);
                MarkerOptions markerOptions_museum = new MarkerOptions();
                markerOptions_museum.position(latLng_museum);
                markerOptions_museum.title("Current location");
                markerOptions_museum.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationmMarker = mMap.addMarker(markerOptions_museum);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_museum));
                mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

                break;

            case R.id.B_cafe:
                mMap.clear();
                String cafe = "cafe";
                url = getUrl(latitude, longitude, cafe);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby  cafes", Toast.LENGTH_SHORT).show();

                LatLng latLng_cafe = new LatLng(latitude, longitude);
                MarkerOptions markerOptions_cafe = new MarkerOptions();
                markerOptions_cafe.position(latLng_cafe);
                markerOptions_cafe.title("Current location");
                markerOptions_cafe.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                currentLocationmMarker = mMap.addMarker(markerOptions_cafe);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_cafe));
                mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
                break;


        }
    }


    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type=" + nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=" + "AIzaSyBLEPBRfw7sMb73Mr88L91Jqh3tuE4mKsE");

        Log.d("MapsActivity", "url = " + googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;

        } else
            return true;
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    public void ss() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Connection failed");
        builder.setMessage("Unable to connect. Please \n review your network settings");
        builder.setPositiveButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                finish();

            }
        });


        // Create the AlertDialog object and return it
        builder.create().show();
    }

}
