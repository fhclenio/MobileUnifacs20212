package com.unifacs.projetoavaliacao;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;

public abstract class MapsUtils {
    public void updateMapRotation(Location location, int orientOption, UiSettings mapUI, GoogleMap mMap) {
        CameraPosition builder;
        switch (orientOption) {
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
}
