package com.unifacs.projetoavaliacao;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class GnssActivity extends AppCompatActivity implements LocationListener {
    private static final int REQUEST_LOCATION = 0;
    private LocationManager locationManager;
    private LocationProvider locationProvider;
    private GnssStatusCallback gnssStatusCallback;
    private int iFormatOption;
    private CircleView circleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnss);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getConfigs();
        startGnss();
    }

    private void getConfigs(){
        circleView = findViewById(R.id.circleView1);
        String configFormat, configVel;
        SharedPreferences sharedPrefs = getSharedPreferences("MyPreferences",
                Context.MODE_PRIVATE);
        configFormat = sharedPrefs.getString("ConfigFormat", "Decimal");
        configVel = sharedPrefs.getString("ConfigVel", "Kmh");

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
    }
    private void startGnss() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
            locationProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);

            locationManager.requestLocationUpdates(locationProvider.getName(), 5*1000,0.1f,this);
            gnssStatusCallback = new GnssStatusCallback();
            locationManager.registerGnssStatusCallback(gnssStatusCallback);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }

    private void updateStatusBar(Location location) {
        TextView tvLat = (TextView) findViewById(R.id.tvLatitudeCV);
        TextView tvLong = (TextView) findViewById(R.id.tvLongitudeCV);
        TextView tvElev = (TextView) findViewById(R.id.tvElevacao);

        String lsLat = getString(R.string.tv_lat);
        String lsLong = getString(R.string.tv_long);
        String lsAlt = getString(R.string.tv_elev);

        lsAlt += " " + location.getAltitude();

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

        tvElev.setText(lsAlt);
        tvLat.setText(lsLat);
        tvLong.setText(lsLong);
    }
    private void AtualizaInfoGnss(GnssStatus status){

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGnss();
            }
            else {
                Toast.makeText(this,R.string.warning_no_permission, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        desativaGNSS();
    }
    private void desativaGNSS(){
        locationManager.unregisterGnssStatusCallback(gnssStatusCallback);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        updateStatusBar(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    private class GnssStatusCallback extends GnssStatus.Callback {
        public GnssStatusCallback() {
            super();
        }
        @Override
        public void onStarted() { }

        @Override
        public void onStopped() { }

        @Override
        public void onFirstFix(int ttffMillis) { }

        @Override
        public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
            circleView.AtualizaStatus(status);
            circleView.invalidate();
            AtualizaInfoGnss(status);

            int countSatsInUse = 0;
            if (status != null) {
                for (int i = 0; i < status.getSatelliteCount(); i++) {
                    if(status.usedInFix(i)){
                        countSatsInUse++;
                    }
                }
            }
            TextView tvSatsInUse = (TextView) findViewById(R.id.tvSatsInUse);
            TextView tvSatsInView = (TextView) findViewById(R.id.tvSatsInView);

            tvSatsInUse.setText(getString(R.string.tv_sats_in_use) + " " + countSatsInUse );
            tvSatsInView.setText(getString(R.string.tv_sats_in_view) + " " + status.getSatelliteCount());
        }

    }

}