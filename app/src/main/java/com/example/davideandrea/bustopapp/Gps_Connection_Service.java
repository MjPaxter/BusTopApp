package com.example.davideandrea.bustopapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationParams;

/**
 * Created by andrea on 12/05/2018.
 */

public class Gps_Connection_Service extends Service {
    Handler handler = new Handler();
    Context context = null;
    String lat="1";
    String lon="1";
    double latNumb=0f;
    double lonNumb=0f;
    int k;
    private List<String> luoghi = new ArrayList<String>();
    private static final String host = "andreapasin99.hopto.org";
    private static final int portnumber = 1080;
    private Socket socket = null;
    private int startconnection = 0;
    private String value = "";
    String filename = "user.txt";
    int stop=0;
    public static Boolean started=false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Lasciare la CPU del cellulare accesa anche quando lo schermo è spento
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        if(k!=1) {
            wakeLock.acquire();
        }
        Bundle extras = intent.getExtras(); //In Signal viene avviata questa classe e gli vengono passati dei dati tramite extras
        if(extras == null)
            Log.d("Service","null");
        else
        {
            String from = (String) extras.get("From");
            if(from.equals("STOP")){       //Se in Signal viene passato il valore STOP si arresta la classe
                SmartLocation.with(context).location().stop();
                k=2;
                stop=1;
                startconnection=0;
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
            }
            else {  //Se non viene trovato STOP allora prende i luoghi da Signal
                started=true;
                String[] from1 = from.split(",");
                for (int i = 0; i < from1.length; i++) {
                    luoghi.add(from1[i]);
                }
                if(k!=1) {
                    k = 1;
                    Runthread();
                }
            }//
        }
        if(stop==0) {
            value = readfile(filename);
            context = getApplicationContext();
            SmartLocation.with(context).location().config(LocationParams.NAVIGATION).start(locationListener);
        }
        else{
            handler.removeCallbacks(locationRunnable);
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SmartLocation.with(context).location().stop();
    }

    @Override
    public void onCreate() {
        //Mostrare una icona di notifica quando si invia la posizione

        Intent notificationIntent = new Intent(this, com.example.davideandrea.bustopapp.Signal.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                //.setSmallIcon(R.drawable.toplogobig)
                .setContentTitle("Dov'è il mio bus? (VI)")
                .setContentText("Inviando la tua posizione...")
                .setContentIntent(pendingIntent).build();

        startForeground(1337, notification);

    }

    public String readfile(String file){
        //Void per leggere un file e metterlo in una stringa
        String text="";
        try {
            FileInputStream fis= openFileInput(file);
            int size= fis.available();
            byte[] buffer= new byte[size];
            fis.read(buffer);
            fis.close();
            text= new String(buffer);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return text;
    }
    OnLocationUpdatedListener locationListener = new OnLocationUpdatedListener(){
        @Override
        public void onLocationUpdated(Location location) {
            latNumb = location.getLatitude();
            lonNumb = location.getLongitude();
            lat=Double.toString(latNumb);
            lon=Double.toString(lonNumb);
            if(startconnection==1) {
                Toast.makeText(getApplicationContext(), "Connesso! Grazie del tuo aiuto!", Toast.LENGTH_SHORT).show();
                startconnection++;
            }
            /*else{
                Toast.makeText(getApplicationContext(), lat+" "+lon, Toast.LENGTH_SHORT).show();
            }*/
            if(stop==0) {
                handler.postDelayed(locationRunnable, 5000);
            }
        }
    };
    Runnable locationRunnable = new Runnable(){
        @Override
        public void run () {
            SmartLocation.with(context).location().config(LocationParams.NAVIGATION).start(locationListener);
        }
    };

    //Thread per effettuare la connessione al server
    private void Runthread() {
        new Thread() {
            @Override
            public void run() {
                while (k == 1) {
                    if (!lat.equals("1")) {
                        if (!lon.equals("1")) {
                            try {
                                socket = new Socket(host, portnumber);
                                while (socket.isConnected() == false) {
                                    socket = new Socket(host, portnumber);
                                }
                                BufferedWriter bw = null;
                                try {
                                    bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                    bw.write("rec");
                                    bw.newLine();
                                    bw.write(value);
                                    bw.newLine();
                                    for (int i = 0; i < luoghi.size(); i++) {
                                        bw.write(luoghi.get(i).toString());
                                        bw.newLine();
                                    }
                                    bw.write(lat);
                                    bw.newLine();
                                    bw.write(lon);
                                    bw.newLine();
                                    bw.write("0");
                                    bw.flush();
                                    if (startconnection == 0) {
                                        startconnection = 1;
                                    }
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
                }

            }
        }.start();
    }
}
