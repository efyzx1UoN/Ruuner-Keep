package com.example.fuckinggps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity2 extends AppCompatActivity {
    private TextView show;
    private Chronometer chronometer;
    private Button startButton;
    private Button finishButton;
    private long recordingTime = 0;
    private static Location last_location;
    private static Location cur_location;
    private  float distance =0;
    private float speed=0;
    private RecordsViewModel viewModel;
    private boolean started;
    private LocationManager locationManager;
    private  String provider;
    MyAppListViewModel application;
    private  LocationListener locationListener;
    private  int i;
    private int last_time=0;
    private int cur_time=0;
    private long timeMillis = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        show=findViewById(R.id.main_et_show);
        application = (MyAppListViewModel) getApplication();

//        viewModel=new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
//                .getInstance(this.getApplication())).get(RecordsViewModel.class);
        viewModel = new ViewModelProvider(this).get(RecordsViewModel.class);

         locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
         provider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        initial();
        i=0;
        chronometer =(Chronometer) findViewById(R.id.chronometer);
        SetListeners();

    }

    private void initial(){
        StringBuffer sb = new StringBuffer();
        sb.append("Current location info：");
        sb.append("\nDistance：");
        sb.append(0);
        sb.append("\nSpeed：");
        sb.append(0);
        show.setText(sb.toString());
    }

    private void SetListeners(){

        startButton=findViewById(R.id.startButtonId);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Ready?");
                builder.setMessage("Go After 3 Seconds");
                builder.setCancelable(true);
                final AlertDialog dlg = builder.create();
                dlg.show();
                final Timer t = new Timer();
                t.schedule(new TimerTask() {
                    public void run() {
                        dlg.cancel();
                        chronometer.setBase(SystemClock.elapsedRealtime()-recordingTime);
                        chronometer.start();
                    }
                }, 3000);

                if(locationListener==null){
                     locationListener= new LocationListener() {
                         @Override
                         public void onLocationChanged(@NonNull Location location) {
                             float v ;
                             float m_distance = 0;
                             float m_speed=0;
                             int chronometerSeconds = getChronometerSeconds(chronometer);
                             if (!started) {
                                 last_location = cur_location = location;
                                 started=true;
                                 v = last_location.distanceTo(cur_location);
                                 distance += v;
                                 last_time=cur_time=0;
                             } else {
                                 cur_location=location;
                                 v = last_location.distanceTo(cur_location);
                                 distance += v;
//                                 String d = new DecimalFormat("###,###,###.##").format(distance);
//                                 m_distance = Float.parseFloat(d);
                                 cur_time=chronometerSeconds;
                                 speed=v/(cur_time-last_time);
//                                 String a = new DecimalFormat("###,###,###.#").format(speed);
//                                 m_speed=Float.parseFloat(a);
                                 Log.d("speed",m_speed+"");
                             }
                             StringBuffer sb = new StringBuffer();
                             sb.append("Current location info：");
                             double longitude = location.getLongitude();
                             double latitude = location.getLatitude();
                             sb.append("\nDistance：");
                             last_location=cur_location;
                             last_time=cur_time;
                             sb.append(distance+"Miter");
                             sb.append("\nSpeed：");
                             sb.append(speed+"M/S");
                             viewModel.insert(new Records(longitude, latitude, distance,
                                     speed, chronometerSeconds, timeMillis));
                             Log.d("Seconds",chronometerSeconds+"");
                             show.setText(sb.toString());
                             Log.d("movement", new String(sb));
                             i++;
                             Log.d("Thread---------non_static_i",Thread.currentThread().toString()+","+"i:"+i);
                         }
                     };
                 }
                try {
                    locationManager.requestLocationUpdates(provider,
                            1000, // minimum time interval between updates
                            5, // minimum distance between updates, in metres
                            locationListener);
                } catch (SecurityException e) {
                    Log.d("comp3018", e.toString());
                }
            }
        });
        finishButton=findViewById(R.id.finishButtonId);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.stop();
                Toast.makeText(MainActivity2.this,R.string.toastMessage,
                        Toast.LENGTH_LONG).show();
                try {
                    toFinish();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity2---Stop","Stop");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity2---Start","Start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity2---Resume","Resume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity2---Destroy","destroy");
        if(locationListener!=null){
            locationManager.removeUpdates(locationListener);
            locationListener=null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity2---onPause","onPause");
    }

    private void toFinish(){
        finish();
        if(locationListener!=null){
            locationManager.removeUpdates(locationListener);
            locationListener=null;
        }

    }


    public  static int getChronometerSeconds(Chronometer cmt) {
        int totalss = 0;
        String string = cmt.getText().toString();
        if(string.length()==7){
            String[] split = string.split(":");
            String string2 = split[0];
            int hour = Integer.parseInt(string2);
            int Hours =hour*3600;
            String string3 = split[1];
            int min = Integer.parseInt(string3);
            int Mins =min*60;
            int  SS =Integer.parseInt(split[2]);
            totalss = Hours+Mins+SS;
            return totalss;
        }
        else if(string.length()==5){
            String[] split = string.split(":");
            String string3 = split[0];
            int min = Integer.parseInt(string3);
            int Mins =min*60;
            int  SS =Integer.parseInt(split[1]);
            totalss =Mins+SS;
            return totalss;
        }
        return totalss;
    }
}