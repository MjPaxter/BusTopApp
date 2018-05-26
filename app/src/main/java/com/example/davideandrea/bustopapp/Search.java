package com.example.davideandrea.bustopapp;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

public class Search extends FragmentActivity implements OnMapReadyCallback {
    Dialog mydialog;
    Animation animTranslate;
    Animation animTranslateClose;
    private GoogleMap mMap;
    private ImageView slider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mydialog = new Dialog(this, android.R.style.Theme_Light);
        mydialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        animTranslate= AnimationUtils.loadAnimation(this, R.anim.swipeleft);
        animTranslateClose= AnimationUtils.loadAnimation(this, R.anim.swiperight);
        slider=(ImageView)findViewById(R.id.slider);
        slider.setOnTouchListener(new MotionGesture(Search.this){
            public void onSwipeLeft() {
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
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }





    private void ShowPopup() {
        EditText partenza;
        EditText arrivo;
        ImageView cerca;
        ImageView dialog;
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
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        });
        mydialog.show();

    }
}
