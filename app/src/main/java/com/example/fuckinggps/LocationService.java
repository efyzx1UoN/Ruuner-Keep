package com.example.fuckinggps;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.util.ArrayList;
import java.util.List;

public class LocationService extends Service {
    private LocationManager locationManager;
    private String provider;
    private LocationListener locationListener;
    private final IBinder binder = new ServiceBinder();
    public List<Location> locations = new ArrayList<>();
    private final String CHANNEL_ID = "100";
    private final int NOTIFICATION_ID = 001;
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
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        stopForeground(true);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        int type = intent.getIntExtra("type",1);
        Log.d("LocationService", "the create notification type is " + type + "----" + (type == 1 ? "true" : "false"));
        if(type == 1) {
            createNotificationChannel();
        }else{
            createErrorNotification();
        }
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void createErrorNotification() {
        Notification notification = new Notification.Builder(this).build();
        startForeground(0, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 通知渠道的id
        String id = "my_channel_01";
        // 用户可以看到的通知渠道的名字.
        CharSequence name = getString(R.string.channel_name);
//         用户可以看到的通知渠道的描述
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
//         配置通知渠道的属性
        mChannel.setDescription(description);
//         设置通知出现时的闪灯（如果 android 设备支持的话）
        mChannel.enableLights(true); mChannel.setLightColor(Color.RED);
//         设置通知出现时的震动（如果 android 设备支持的话）
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//         最后在notificationmanager中创建该通知渠道 //
        mNotificationManager.createNotificationChannel(mChannel);

        // 为该通知设置一个id
        int notifyID = 1;
        // 通知渠道的id
        String CHANNEL_ID = "my_channel_01";
        // Create a notification and set the notification channel.
        Notification notification = new Notification.Builder(this)
                .setContentTitle("New Message") .setContentText("You've received new messages.")
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
        public void StartedRunning() {
            if(locationListener==null){
                locationListener=new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        Log.d("TAG",timeMill+","+location.getLongitude()+" "+
                                location.getLatitude());
                        float v ;
                        float m_speed=0;
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
                        viewModel.insert(new Records(longitude,latitude,distance,speed,0,timeMill, 0));
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
                Log.d("list_size",locations.size()+"");
            } catch (SecurityException e) {
                Log.d("comp3018", e.toString());
            }
        }
        public void FinishRunning(){
            average_speed=distance/duration;
            Log.d("a_speed",average_speed+"");
            viewModel.insert(new Records(0.0,0.0,distance,average_speed,duration,timeMill, 0));
            distance=0;
            speed=0;
            timeMill=0;
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_ID);
        }
//       // public void notification(){
//            LocationService.this.addNotification();
//        }
    }
}