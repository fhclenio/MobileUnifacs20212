package com.unifacs.projetoavaliacao;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.room.Room;

public class SettingsActivity extends Activity implements View.OnClickListener{
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEditor;

    private RoomDB db;
    private RadioButton btnDecimal;
    private RadioButton btnDecimalMin;
    private RadioButton btnDecimalSegMin;
    private RadioButton btnKm;
    private RadioButton btnMph;
    private RadioButton btnOrienNone;
    private RadioButton btnOrienNorth;
    private RadioButton btnOrienCourse;
    private RadioButton btnTypeVet;
    private RadioButton btnTypeSat;
    private Switch switchTraffic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.config_str);
        sharedPrefs=getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        sharedPrefsEditor=sharedPrefs.edit();

        btnDecimal = findViewById(R.id.rbtnGrauDecimal);
        btnDecimalMin = findViewById(R.id.rbtnGrauMinDecimal);
        btnDecimalSegMin = findViewById(R.id.rbtnGrauMinSegDecimal);

        btnKm = findViewById(R.id.rbtnConfigKm);
        btnMph = findViewById(R.id.rbtnConfigMph);

        btnOrienNone = findViewById(R.id.rbtnConfigOrienNone);
        btnOrienNorth = findViewById(R.id.rbtnConfigOrienNorth);
        btnOrienCourse = findViewById(R.id.rbtnConfigOrienCourse);
        btnTypeVet = findViewById(R.id.rbtnConfigVet);
        btnTypeSat = findViewById(R.id.rbtnConfigSat);

        switchTraffic = findViewById(R.id.switchTraffic);

        findViewById(R.id.btnDeleteHistory).setOnClickListener(this);
        Button btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(this);

        loadFields();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSalvar:
                saveConfigs();
                Toast.makeText(this, R.string.config_salvo ,Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnDeleteHistory:
                db = Room.databaseBuilder(getApplicationContext(), RoomDB.class, "history_database").allowMainThreadQueries().build();
                db.clearAllTables();
                db.close();
                break;
        }
    }

    public void saveConfigs(){
        if(btnDecimal.isChecked()){
            sharedPrefsEditor.putString("ConfigFormat", "Decimal");
        }
        else if(btnDecimalMin.isChecked()){
            sharedPrefsEditor.putString("ConfigFormat", "DecimalMinute");
        }
        else{
            sharedPrefsEditor.putString("ConfigFormat", "DecimalMinuteSecond");
        }
        sharedPrefsEditor.commit();

        if(btnKm.isChecked()){
            sharedPrefsEditor.putString("ConfigVel", "Kmh");
        }
        else{
            sharedPrefsEditor.putString("ConfigVel", "Mph");
        }
        sharedPrefsEditor.commit();

        if(btnOrienNone.isChecked()){
            sharedPrefsEditor.putString("ConfigOrientation", "None");
        }
        else if(btnOrienNorth.isChecked()){
            sharedPrefsEditor.putString("ConfigOrientation", "North");
        }
        else{
            sharedPrefsEditor.putString("ConfigOrientation", "Course");
        }
        sharedPrefsEditor.commit();

        if(btnTypeVet.isChecked()){
            sharedPrefsEditor.putString("ConfigMapType", "Vector");
        }
        else{
            sharedPrefsEditor.putString("ConfigMapType", "Satellite");
        }
        sharedPrefsEditor.commit();

        if(switchTraffic.isChecked()){
            sharedPrefsEditor.putString("ConfigTraffic", "On");
        }
        else{
            sharedPrefsEditor.putString("ConfigTraffic", "Off");
        }
        sharedPrefsEditor.commit();
    }

    public void loadFields(){
        String  configFormat, configVel, configOrientation, configMapType, configTraffic;

        configFormat = sharedPrefs.getString("ConfigFormat", "Decimal");
        configVel = sharedPrefs.getString("ConfigVel", "Kmh");
        configOrientation = sharedPrefs.getString("ConfigOrientation", "None");
        configMapType = sharedPrefs.getString("ConfigMapType", "Vector");
        configTraffic = sharedPrefs.getString("ConfigTraffic", "On");

        switch(configFormat){
            case "Decimal":
                btnDecimal.toggle();
                break;
            case "DecimalMinute":
                btnDecimalMin.toggle();
                break;
            case "DecimalMinuteSecond":
                btnDecimalSegMin.toggle();
                break;
        }

        switch(configVel){
            case "Kmh":
                btnKm.toggle();
                break;
            case "Mph":
                btnMph.toggle();
                break;
        }

        switch(configOrientation){
            case "None":
                btnOrienNone.toggle();
                break;
            case "North":
                btnOrienNorth.toggle();
                break;
            case "Course":
                btnOrienCourse.toggle();
                break;
        }

        switch(configMapType){
            case "Vector":
                btnTypeVet.toggle();
                break;
            case "Satellite":
                btnTypeSat.toggle();
                break;
        }

        switch(configTraffic){
            case "On":
                switchTraffic.setChecked(true);
                break;
            case "Off":
                switchTraffic.setChecked(false);
                break;
        }
    }

}