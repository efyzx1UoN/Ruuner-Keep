package com.example.runner;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;



import java.util.ArrayList;
import java.util.List;

public class LocationService extends Service {
    private LocationManager locationManager;
    private String provider;
    private LocationListener locationListener;
    private final IBinder binder = new ServiceBinder();
    private RecordsViewModel viewModel;

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = LocationManager.GPS_PROVIDER;
        viewModel = new RecordsViewModel(this.getApplication());
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

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * release resources when app closed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationListener!=null){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(locationListener);
        }
        locationManager = null;
        stopForeground(true);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        createNotification();
        return super.onStartCommand(intent, flags, startId);
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    /**
     * create Notification
     */
    private void createNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String id = "my_channel_01";

        CharSequence name = getString(R.string.channel_name);

        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);

        mChannel.setDescription(description);

        mNotificationManager.createNotificationChannel(mChannel);

        String CHANNEL_ID = "my_channel_01";
        // Create a notification and set the notification channel.
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Runner Info") .setContentText("Runner is running")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setChannelId(CHANNEL_ID)
                .build();
        startForeground(1,notification);
    }

    public class ServiceBinder extends Binder {
        private  Location last_location;
        private  Location cur_location;
        public float distance =0;
        private float speed=0;
        private float average_speed=0;
        Long longValue = new Long(11);
        public boolean started=false;
        public long timeMill;
        public int duration;
        public void StartTiming(){
            timeMill=System.currentTimeMillis();
        }

        /**
         * Calculates distance and speed then send sendBroadcast to running activity
         * Insert data to database if location changed
         */
        public void StartedRunning() {
            if(locationListener==null){
                locationListener=new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        Log.d("TAG",timeMill+","+location.getLongitude()+" "+
                                location.getLatitude());
                        float v ;

                        if (!started) {
                            last_location = cur_location = location;
                            started=true;
                            v = last_location.distanceTo(cur_location);
                            distance += v;
                            started=true;
                        } else {
                            cur_location=location;
                            v = last_location.distanceTo(cur_location);
                            distance += v;
                            longValue = new Long(cur_location.getTime()-last_location.getTime());
                            speed=3600 * v / ( longValue.floatValue());
                        }
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();
                        last_location=cur_location;
                        viewModel.insert(new Records(longitude,latitude,distance,speed,0,timeMill, 0,""));
                        Intent intent = new Intent();
                        Float b_distance=distance;
                        Float b_speed=speed;
                        intent.putExtra("distance", b_distance);
                        intent.putExtra("speed",b_speed);
                        intent.setAction("com.example.locationService");
                        sendBroadcast(intent);
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

        /**
         * call this method when stop running
         * get the duration from running activity then calculate average_speed
         * Insert data to database
         * release locationlistener
         * stopforeground
         */
        public void FinishRunning(){
            average_speed=distance/duration;
            viewModel.insert(new Records(0.0,0.0,distance,average_speed,duration,timeMill, 0,""));
            distance=0;
            speed=0;
            timeMill=0;
            if(locationListener!=null){
                locationManager.removeUpdates(locationListener);
            }

            stopForeground(true);
        }

    }
}