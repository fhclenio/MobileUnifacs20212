package com.unifacs.projetoavaliacao;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class History {
    @PrimaryKey(autoGenerate = true)
    public int id_history;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "location_time")
    public String locationTime;

    public History(double latitude, double longitude, String locationTime){
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationTime = locationTime;
    }
}
