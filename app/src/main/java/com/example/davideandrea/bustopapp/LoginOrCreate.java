package com.example.davideandrea.bustopapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
public class LoginOrCreate extends AppCompatActivity {
    private EditText username;
    private EditText password;      //nell'xml per avere i caratteri nascosti -> android:inputType="textPassword"
    private EditText codiceCarta;
    private ImageView log;      //pulsante login
    private ImageView rec;      //pulsante registrazione
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_create);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginOrCreate.this,Menu.class));
            finish();
        }
        //DEFINIRE I PULSANTI COLLEGANDOLI ALLA GRAFICA -> log=(ImageView)findViewByID(R.id."nome nell'xml")
        log = (ImageView) findViewById(R.id.button_Login);
        rec = (ImageView) findViewById(R.id.button_Registrati);
        username = (EditText) findViewById(R.id.text_Username);
        password = (EditText) findViewById(R.id.text_Password);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= username.getText().toString();
                final String pass = password.getText().toString();
                if (email.length()==0) {
                    Toast.makeText(getApplicationContext(), "Inserisci la mail", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length()==0) {
                    Toast.makeText(getApplicationContext(), "Inserisci la password", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(LoginOrCreate.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    if (password.length() < 6) {
                                        password.setError("Password troppo corta");
                                    } else {
                                        Toast.makeText(LoginOrCreate.this, "Autenticazione fallita", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Intent intent = new Intent(LoginOrCreate.this, Menu.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginOrCreate.this,Register.class));
                finish();
            }
        });
    }
}
