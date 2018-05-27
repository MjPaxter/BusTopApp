package com.example.davideandrea.bustopapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private ImageView guest;
    private ImageView log;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(Login.this,Menu.class));
            finish();
        }
        //DEFINIRE I PULSANTI COLLEGANDOLI ALLA GRAFICA -> log=(ImageView)findViewByID(R.id."nome nell'xml")
        guest = (ImageView) findViewById(R.id.button_Guest);
        log = (ImageView)  findViewById(R.id.button_Login);

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,LoginOrCreate.class);
                startActivity(intent);
            }
        });
        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,Menu.class);
                startActivity(intent);
            }
        });




    }
}
