package com.example.davideandrea.bustopapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class Menu extends AppCompatActivity {
    private ImageView cerca;
    private ImageView indica;
    private ImageView impostazioni;
    private ImageView esci;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        auth = FirebaseAuth.getInstance();
        //DEFINIRE I PULSANTI COLLEGANDOLI ALLA GRAFICA -> cerca=(ImageView)findViewByID(R.id."nome nell'xml"
        cerca=(ImageView)findViewById(R.id.buttonCerca);
        esci=(ImageView)findViewById(R.id.buttonExit);
        impostazioni=(ImageView)findViewById(R.id.buttonImpostazioni);
        indica=(ImageView)findViewById(R.id.buttonIndica);

        cerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Menu.this, Search.class);
                startActivity(intent);
            }
        });
        indica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Menu.this,Signal.class);
                startActivity(intent);
            }
        });
        esci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                finish();
            }
        });
        impostazioni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Menu.this,SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}
