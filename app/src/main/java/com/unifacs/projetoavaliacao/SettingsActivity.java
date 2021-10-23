package com.unifacs.projetoavaliacao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEditor;

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
    private RadioButton btnTrafficON;
    private RadioButton btnTrafficOFF;

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

        btnTrafficOFF = findViewById(R.id.rbtnConfigTrafficOFF);
        btnTrafficON = findViewById(R.id.rbtnConfigTrafficON);

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

        if(btnTrafficON.isChecked()){
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
                btnTrafficON.toggle();
                break;
            case "Off":
                btnTrafficOFF.toggle();
                break;
        }
    }





    public SharedPreferences getSharedPrefs() {
        return sharedPrefs;
    }

    public void setSharedPrefs(SharedPreferences sharedPrefs) {
        this.sharedPrefs = sharedPrefs;
    }

    public SharedPreferences.Editor getSharedPrefsEditor() {
        return sharedPrefsEditor;
    }

    public void setSharedPrefsEditor(SharedPreferences.Editor sharedPrefsEditor) {
        this.sharedPrefsEditor = sharedPrefsEditor;
    }

    public RadioButton getBtnDecimal() {
        return btnDecimal;
    }

    public void setBtnDecimal(RadioButton btnDecimal) {
        this.btnDecimal = btnDecimal;
    }

    public RadioButton getBtnDecimalMin() {
        return btnDecimalMin;
    }

    public void setBtnDecimalMin(RadioButton btnDecimalMin) {
        this.btnDecimalMin = btnDecimalMin;
    }

    public RadioButton getBtnDecimalSegMin() {
        return btnDecimalSegMin;
    }

    public void setBtnDecimalSegMin(RadioButton btnDecimalSegMin) {
        this.btnDecimalSegMin = btnDecimalSegMin;
    }

    public RadioButton getBtnKm() {
        return btnKm;
    }

    public void setBtnKm(RadioButton btnKm) {
        this.btnKm = btnKm;
    }

    public RadioButton getBtnMph() {
        return btnMph;
    }

    public void setBtnMph(RadioButton btnMph) {
        this.btnMph = btnMph;
    }

    public RadioButton getBtnOrienNone() {
        return btnOrienNone;
    }

    public void setBtnOrienNone(RadioButton btnOrienNone) {
        this.btnOrienNone = btnOrienNone;
    }

    public RadioButton getBtnOrienNorth() {
        return btnOrienNorth;
    }

    public void setBtnOrienNorth(RadioButton btnOrienNorth) {
        this.btnOrienNorth = btnOrienNorth;
    }

    public RadioButton getBtnOrienCourse() {
        return btnOrienCourse;
    }

    public void setBtnOrienCourse(RadioButton btnOrienCourse) {
        this.btnOrienCourse = btnOrienCourse;
    }

    public RadioButton getBtnTypeVet() {
        return btnTypeVet;
    }

    public void setBtnTypeVet(RadioButton btnTypeVet) {
        this.btnTypeVet = btnTypeVet;
    }

    public RadioButton getBtnTypeSat() {
        return btnTypeSat;
    }

    public void setBtnTypeSat(RadioButton btnTypeSat) {
        this.btnTypeSat = btnTypeSat;
    }

    public RadioButton getBtnTrafficON() {
        return btnTrafficON;
    }

    public void setBtnTrafficON(RadioButton btnTrafficON) {
        this.btnTrafficON = btnTrafficON;
    }

    public RadioButton getBtnTrafficOFF() {
        return btnTrafficOFF;
    }

    public void setBtnTrafficOFF(RadioButton btnTrafficOFF) {
        this.btnTrafficOFF = btnTrafficOFF;
    }
}