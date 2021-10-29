package com.unifacs.projetoavaliacao;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.unifacs.projetoavaliacao.databinding.ActivityNavigationBinding;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NavigationActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener,
        View.OnClickListener, SensorEventListener {

    private static final int REQUEST_LOCATION = 0;
    private static final int REQUEST_LAST_LOCATION = 1;
    private static final int REQUEST_LOCATION_UPDATES = 2;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Marker myLocation;
    private int iVelOption;
    private int iFormatOption;
    private int iOrientOption;
    private UiSettings mapUI;
    private Circle myLocationCircle;
    private boolean isStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        com.unifacs.projetoavaliacao.databinding.ActivityNavigationBinding binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isStart = true;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        findViewById(R.id.imgMyLocation).setOnClickListener(this);
        setConfigs();
        startLocationUpdates();
    }

    private void setConfigs() {
        this.mapUI = mMap.getUiSettings();
        mapUI.setAllGesturesEnabled(true);
        mapUI.setCompassEnabled(true);
        mapUI.setZoomControlsEnabled(true);
        myLocation = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(-12.98155, -38.45121))
                .title(getString(R.string.my_location))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow_up))
                .zIndex(30)
                .draggable(false)
                .anchor(0.5f, 0.6f));

        myLocationCircle = mMap.addCircle(new CircleOptions()
                .radius(200)
                .center(new LatLng(-12.98155, -38.45121))
                .zIndex(20)
                .strokeColor(Color.rgb(46, 125, 50)));


        String configFormat, configVel, configOrientation, configMapType, configTraffic;
        SharedPreferences sharedPrefs = getSharedPreferences("MyPreferences",
                Context.MODE_PRIVATE);
        configFormat = sharedPrefs.getString("ConfigFormat", "Decimal");
        configVel = sharedPrefs.getString("ConfigVel", "Kmh");
        configOrientation = sharedPrefs.getString("ConfigOrientation", "None");
        configMapType = sharedPrefs.getString("ConfigMapType", "Vector");
        configTraffic = sharedPrefs.getString("ConfigTraffic", "On");

        switch (configFormat) {
            case "Decimal":
                this.iFormatOption = 1;
                break;
            case "DecimalMinute":
                this.iFormatOption = 2;
                break;
            case "DecimalMinuteSecond":
                this.iFormatOption = 3;
                break;
        }

        switch (configVel) {
            case "Kmh":
                this.iVelOption = 1;
                break;
            case "Mph":
                this.iVelOption = 2;
                break;
        }

        switch (configOrientation) {
            case "None":
                this.iOrientOption = 1;
                break;
            case "North":
                this.iOrientOption = 2;
                break;
            case "Course":
                this.iOrientOption = 3;
                break;
        }

        switch (configMapType) {
            case "Vector":
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "Satellite":
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
        }

        switch (configTraffic) {
            case "On":
                mMap.setTrafficEnabled(true);
                break;
            case "Off":
                mMap.setTrafficEnabled(false);
                break;
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();
                    if (isStart) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 11));
                        isStart = false;
                    }
                    updateMarkerPosition(location);
                    updateMapRotation(location);
                    updateStatusBar(location);
                }
            };
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, null);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_UPDATES);
        }
    }

    private void updateMarkerPosition(Location location) {
        myLocation.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        myLocation.setRotation(location.getBearing());
        myLocationCircle.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
        myLocationCircle.setRadius(location.getAccuracy());
    }

    private void updateMapRotation(Location location) {
        CameraPosition builder;
        switch (iOrientOption) {
            case 1:
                mapUI.setRotateGesturesEnabled(true);
                mapUI.setCompassEnabled(true);
                mapUI.setTiltGesturesEnabled(true);
                break;
            case 2:
                mapUI.setRotateGesturesEnabled(false);
                mapUI.setCompassEnabled(false);
                mapUI.setTiltGesturesEnabled(false);
                builder = new CameraPosition.Builder(mMap.getCameraPosition())
                        .bearing(0)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder));
                break;
            case 3:
                mapUI.setRotateGesturesEnabled(false);
                mapUI.setCompassEnabled(false);
                mapUI.setTiltGesturesEnabled(false);
                builder = new CameraPosition.Builder(mMap.getCameraPosition())
                        .bearing(location.getBearing())
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder));
                break;
        }
    }

    private void updateStatusBar(Location location) {
        TextView tvLat = (TextView) findViewById(R.id.tvLatitude);
        TextView tvLong = (TextView) findViewById(R.id.tvLongitude);
        TextView tvVel = (TextView) findViewById(R.id.tvVelocidade);

        String lsLat = getString(R.string.tv_lat);
        String lsLong = getString(R.string.tv_long);
        String lsVel = getString(R.string.tv_vel);

        char lonChar = (location.getLongitude() > 0) ? 'E' : 'W';
        char latChar = (location.getLatitude() > 0) ? 'N' : 'S';

        int liLatSeconds = (int) Math.round(location.getLatitude() * 3600);
        int liLatDegrees = liLatSeconds / 3600;
        liLatSeconds = Math.abs(liLatSeconds % 3600);
        int liLatMinutes = liLatSeconds / 60;
        liLatSeconds %= 60;

        int liLongSeconds = (int) Math.round(location.getLongitude() * 3600);
        int liLongDegrees = liLongSeconds / 3600;
        liLongSeconds = Math.abs(liLongSeconds % 3600);
        int liLongMinutes = liLongSeconds / 60;
        liLongSeconds %= 60;

        switch (iFormatOption) {
            case 1:
                lsLat += " " + Math.abs(liLatDegrees) + "º " + latChar;
                lsLong += " " + Math.abs(liLongDegrees) + "° " + lonChar;
                break;
            case 2:
                lsLat += " " + Math.abs(liLatDegrees) + "º " + liLatMinutes + "' " + latChar;
                lsLong += " " + Math.abs(liLongDegrees) + "° " + liLongMinutes + "' " + lonChar;
                break;
            case 3:
                lsLat += " " + Math.abs(liLatDegrees) + "º " + liLatMinutes + "' " + liLatSeconds + "\" " + latChar;
                lsLong += " " + Math.abs(liLongDegrees) + "° " + liLongMinutes + "' " + liLongSeconds + "\" " + lonChar;
                break;
        }
        DecimalFormat df = new DecimalFormat("###.##");
        df.setRoundingMode(RoundingMode.UP);
        switch (iVelOption) {
            case 1:
                lsVel += " " + df.format((location.getSpeed() * 3.6)) + " kmh";
                break;
            case 2:
                lsVel += " " + df.format((location.getSpeed() * 2.237)) + " mph";
                break;
        }

        tvLat.setText(lsLat);
        tvLong.setText(lsLong);
        tvVel.setText(lsVel);
    }

    private void setMinhaLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this,
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LAST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_LOCATION:
                    break;
                case REQUEST_LAST_LOCATION:
                    setMinhaLocalizacao();
                    break;
                case REQUEST_LOCATION_UPDATES:
                    startLocationUpdates();
                    break;
                default:
                    makeText(this, R.string.warning_no_permission, LENGTH_LONG).show();
                    this.finish();
                    break;
            }

        }

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(Location location) {
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imgMyLocation) {
            setMinhaLocalizacao();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
