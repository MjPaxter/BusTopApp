package com.example.davideandrea.bustopapp;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Search extends FragmentActivity implements OnMapReadyCallback {
    Dialog mydialog;
    Dialog dialogOptions;
    Animation animTranslate;
    Animation animTranslateClose;
    Animation rotation;
    Animation rotation2;
    private GoogleMap mMap;
    private ImageView slider;
    private boolean runConnThread=false;
    private Socket socket;
    private String host="";
    private int portnumber=1;
    private boolean optionsShown=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mydialog = new Dialog(this, android.R.style.Theme_Light);
        mydialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialogOptions = new Dialog(this, android.R.style.Theme_Light);
        dialogOptions.requestWindowFeature(Window.FEATURE_NO_TITLE);

        animTranslate = AnimationUtils.loadAnimation(this, R.anim.transition_to_left);
        animTranslateClose = AnimationUtils.loadAnimation(this, R.anim.transition_to_right);
        rotation= AnimationUtils.loadAnimation(this, R.anim.rotation);
        rotation2=AnimationUtils.loadAnimation(this, R.anim.opposite_rotation);
        slider = (ImageView) findViewById(R.id.slider);
        slider.setOnTouchListener(new MotionGesture(Search.this) {
            public void onSwipeLeft() {
                slider.setVisibility(View.GONE);
                ShowPopup();
            }
        });
        try {
            ColorDrawable dialogColor = new ColorDrawable(Color.BLACK);
            dialogColor.setAlpha(100); //(0-255) 0 means fully transparent, and 255 means fully opaque
            mydialog.getWindow().setBackgroundDrawable(dialogColor);
            //mydialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        } catch (NullPointerException ex) {

        }
        try {
            ColorDrawable dialogColor = new ColorDrawable(Color.BLACK);
            dialogColor.setAlpha(100); //(0-255) 0 means fully transparent, and 255 means fully opaque
            dialogOptions.getWindow().setBackgroundDrawable(dialogColor);
            //mydialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        } catch (NullPointerException ex) {

        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);    //find my location (no battery-save mode)
    }


    private void ShowPopup() {
        EditText partenza;
        EditText arrivo;
        ImageView cerca;
        ImageView dialog;
        final ImageView options;
        final LinearLayout d;
        mydialog.setContentView(R.layout.custompopup);
        cerca = (ImageView) mydialog.findViewById(R.id.buttoncerca);
        cerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mydialog.dismiss();
            }
        });
        d=(LinearLayout)mydialog.findViewById(R.id.popup);
        d.startAnimation(animTranslate);
        d.setOnTouchListener(new MotionGesture(this) {
            public void onSwipeRight() {
                d.startAnimation(animTranslateClose);
                animTranslateClose.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mydialog.dismiss();
                        slider.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        });
        options=(ImageView)mydialog.findViewById(R.id.options);
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(optionsShown==false) {
                    options.startAnimation(rotation);
                    optionsShown=true;
                    ShowPopup2();
                }
                else{
                    optionsShown=false;
                    options.startAnimation(rotation2);
                }
            }
        });
        mydialog.show();

    }
    private void ShowPopup2() {
        dialogOptions.setContentView(R.layout.options_popup);
        dialogOptions.show();

    }
    //THREAD PER LA CONNESSIONE -> Background thread che non blocca la UI
    private void Runthread() {
        new Thread() {
            @Override
            public void run() {
                while (runConnThread) {
                            try {
                                socket = new Socket(host, portnumber);
                                while (!socket.isConnected()) {
                                    socket = new Socket(host, portnumber);
                                }
                                BufferedWriter bw = null;
                                try {
                                    bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                    bw.write("rec");
                                    bw.newLine();
                                    bw.flush();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    Thread.sleep(5000);
                                    socket.close();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } catch (
                                    IOException e) {
                                e.printStackTrace();
                            }
                        }
            }
        }.start();
    }
}
