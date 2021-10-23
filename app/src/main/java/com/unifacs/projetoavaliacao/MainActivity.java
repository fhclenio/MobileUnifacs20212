package com.unifacs.projetoavaliacao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSettings = (Button) findViewById(R.id.buttonSettn);
        Button btnGnss = (Button) findViewById(R.id.buttonGnss);
        Button btnNav = (Button) findViewById(R.id.buttonNav);
        Button btnHist = (Button) findViewById(R.id.buttonHist);
        Button btnExit = (Button) findViewById(R.id.buttonExit);
        Button btnCredits = (Button) findViewById(R.id.buttonCredits);

        btnSettings.setOnClickListener(this);
        btnGnss.setOnClickListener(this);
        btnNav.setOnClickListener(this);
        btnHist.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnCredits.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSettn:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.buttonCredits:
                startActivity(new Intent(this, CreditsActivity.class));
                break;
            case R.id.buttonExit:
                this.finish();
                break;
            case R.id.buttonGnss:
            case R.id.buttonHist:
            case R.id.buttonNav:
                Toast.makeText(this, R.string.wip_str ,Toast.LENGTH_SHORT).show();
                break;
        }
    }
}