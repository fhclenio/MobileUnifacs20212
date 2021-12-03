package com.unifacs.projetoavaliacao;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.unifacs.projetoavaliacao.databinding.ActivityHistoryBinding;

import java.util.List;

public class HistoryActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener {

    private GoogleMap mMap;
    private ActivityHistoryBinding binding;
    private RoomDB db;

    private Marker lastLocationMarker;
    private Marker firstLocationMarker;
    private int iFormatOption;
    private UiSettings mapUI;
    private LatLng firstLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapHistory);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        firstLocation = new LatLng(0,0);
        db = Room.databaseBuilder(getApplicationContext(), RoomDB.class, "history_database").allowMainThreadQueries().build();

        findViewById(R.id.imgMyLocationHis).setOnClickListener(this);

        findViewById(R.id.buttonLog).setOnClickListener(this);
        findViewById(R.id.buttonMap).setOnClickListener(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setConfigs();
        setTrailAndLog();
    }

    private void setTrailAndLog(){
        PolylineOptions polyOpt = new PolylineOptions().width(10).color(Color.rgb(147,0,47));
        boolean isFirst = true;
        TextView tvDados = (TextView) findViewById(R.id.tvDados);
        String dados = "";
        List<History> listHistory = db.hisDAO().getAll();
        for (History his : listHistory) {
            LatLng point = new LatLng(his.latitude, his.longitude);
            polyOpt.add(point);
            dados += updateTextViewDados(his);
            if(isFirst){
                firstLocation = point;
                isFirst = false;
                updateMarkerPosition();
            }
        }
        tvDados.setText(dados);
        mMap.addPolyline(polyOpt);
    }
    private void setConfigs() {
        this.mapUI = mMap.getUiSettings();
        mapUI.setAllGesturesEnabled(true);
        mapUI.setCompassEnabled(true);
        mapUI.setZoomControlsEnabled(true);

        firstLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title(getString(R.string.my_location))
                .zIndex(30)
                .draggable(false));

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

        switch (configOrientation) {
            case "None":
                mapUI.setRotateGesturesEnabled(true);
                mapUI.setCompassEnabled(true);
                mapUI.setTiltGesturesEnabled(true);
                break;
            case "North":
                CameraPosition builder;
                mapUI.setRotateGesturesEnabled(false);
                mapUI.setCompassEnabled(false);
                mapUI.setTiltGesturesEnabled(false);
                builder = new CameraPosition.Builder(mMap.getCameraPosition())
                        .bearing(0)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder));
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

    private String updateTextViewDados(History his) {
        String lsLat = getString(R.string.tv_lat);
        String lsLong = getString(R.string.tv_long);
        String lsVel = getString(R.string.tv_vel);

        char lonChar = (his.longitude > 0) ? 'E' : 'W';
        char latChar = (his.latitude > 0) ? 'N' : 'S';

        int liLatSeconds = (int) Math.round(his.latitude  * 3600);
        int liLatDegrees = liLatSeconds / 3600;
        liLatSeconds = Math.abs(liLatSeconds % 3600);
        int liLatMinutes = liLatSeconds / 60;
        liLatSeconds %= 60;

        int liLongSeconds = (int) Math.round(his.longitude * 3600);
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
        StringBuilder strbd = new StringBuilder();
        strbd.append("ID: ")
                .append(his.id_history)
                .append(" ")
                .append(lsLat)
                .append(" ")
                .append(lsLong)
                .append(" ")
                .append(his.locationTime).append("\n");

        return strbd.toString();
    }


    private void updateMarkerPosition() {
        firstLocationMarker.setPosition(firstLocation);
    }

    private void setMinhaLocalizacao() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 14));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgMyLocationHis:
                setMinhaLocalizacao();
                break;
            case R.id.buttonMap:
                findViewById(R.id.layoutMap).setVisibility(View.VISIBLE);
                findViewById(R.id.layoutScroll).setVisibility(View.GONE);
                findViewById(R.id.backgroundHis).setVisibility(View.GONE);
                break;
            case R.id.buttonLog:
                findViewById(R.id.layoutMap).setVisibility(View.GONE);
                findViewById(R.id.layoutScroll).setVisibility(View.VISIBLE);
                findViewById(R.id.backgroundHis).setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}