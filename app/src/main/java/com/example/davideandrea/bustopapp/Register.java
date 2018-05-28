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

public class Register extends AppCompatActivity {
    private ImageView register;
    private EditText username;
    private EditText password1;
    private EditText password2;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(Register.this,Menu.class));
            finish();
        }
        register = (ImageView) findViewById(R.id.button_Registrati);
        username = (EditText) findViewById(R.id.text_Username);
        password1 = (EditText) findViewById(R.id.text_Password);
        password2 = (EditText) findViewById(R.id.text_Password2);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= username.getText().toString();
                final String pass1 = password1.getText().toString();
                final String pass2 = password2.getText().toString();
                if (email.length()==0) {
                    Toast.makeText(getApplicationContext(), "Inserisci la mail", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass1.length()==0) {
                    Toast.makeText(getApplicationContext(), "Inserisci la password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(pass1.length()<6)
                {
                    Toast.makeText(getApplicationContext(), "Password troppo corta", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!pass1.equals(pass2))
                {
                    Toast.makeText(getApplicationContext(), "Le password non sono uguali", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.createUserWithEmailAndPassword(email, pass1)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Registrazione fallita" + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(Register.this, Menu.class));
                                    finish();
                                }
                            }
                        });
            }
        });
    }
}
