package com.example.runner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;



import java.util.Timer;
import java.util.TimerTask;

public class MainActivity2 extends AppCompatActivity {
    private TextView show;
    private Chronometer chronometer;
    private Button startButton;
    private Button finishButton;
    private Button stopButton;
    private long recordingTime = 0;
    private float distance =0;
    private float speed=0;
    private boolean started;
    private LocationManager locationManager;
    private  String provider;
    private LocationService.ServiceBinder locationService;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            locationService = (LocationService.ServiceBinder) iBinder;
            // if currently tracking then enable stopButton and disable startButton
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            locationService = null;
        }
    };
    private MyReceiver receiver;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        show=findViewById(R.id.main_et_show);
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
            String[] strings =
                    {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, strings, 1);
        }
        initial();
        Intent intent = new Intent(this, LocationService.class);
        MainActivity2.this.startForegroundService(intent);
        bindService(
                new Intent(this, LocationService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        receiver =new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.example.locationService");
        MainActivity2.this.registerReceiver(receiver,filter);
        chronometer =(Chronometer) findViewById(R.id.chronometer);
        SetListeners();
    }
    public  class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            distance= bundle.getFloat("distance");
            speed = bundle.getFloat("speed");
            StringBuffer sb = new StringBuffer();
            sb.append("Current location info：");
            sb.append("\nDistance：");
            sb.append(distance+"Meters");
            sb.append("\nSpeed：");
            sb.append(speed+"KM/H");
            show.setText(sb.toString());
            Log.e("TAG", "onReceive: " + distance + "     " + speed);
        }
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                   if(!started){
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
                       locationService.StartTiming();
                   }else{
                       chronometer.setBase(SystemClock.elapsedRealtime()-recordingTime);
                       chronometer.start();
                   }
                   locationService.StartedRunning();
                   startButton.setEnabled(false);
                   stopButton.setEnabled(true);
            }
        });

        stopButton=findViewById(R.id.stopButtonId);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.stop();
                recordingTime = SystemClock.elapsedRealtime()- chronometer.getBase();
                started=true;
                StringBuffer sb = new StringBuffer();
                sb.append("Current location info：");
                sb.append("\nDistance：");
                sb.append(distance+"Meters");
                sb.append("\nSpeed：");
                sb.append(0+"KM/H");
                show.setText(sb.toString());
                startButton.setEnabled(true);
            }
        });

        finishButton=findViewById(R.id.finishButtonId);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.stop();
                int chronometerSeconds = getChronometerSeconds(chronometer);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
                builder.setTitle("Running Info");
                builder.setMessage("Are you sure to finish recording then exit ?");

                // add the buttons
                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        locationService.duration=chronometerSeconds;
                        locationService.FinishRunning();
                        if(receiver!=null){
                            unregisterReceiver(receiver);
                            receiver=null;
                        }
                        Toast.makeText(MainActivity2.this,R.string.toastMessage,
                                Toast.LENGTH_LONG).show();
                        toFinish();
                    }
                });
                builder.setNegativeButton("Cancel", null);

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity2---onPause","onPause");
    }

    private void toFinish(){
        finish();
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