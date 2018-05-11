package com.example.davideandrea.bustopapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

public class LoginOrCreate extends AppCompatActivity {
    private EditText username;
    private EditText password;      //nell'xml per avere i caratteri nascosti -> android:inputType="textPassword"
    private EditText codiceCarta;
    private ImageView log;      //pulsante login
    private ImageView rec;      //pulsante registrazione
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_create);
        //DEFINIRE I PULSANTI COLLEGANDOLI ALLA GRAFICA -> log=(ImageView)findViewByID(R.id."nome nell'xml")




    }
}
