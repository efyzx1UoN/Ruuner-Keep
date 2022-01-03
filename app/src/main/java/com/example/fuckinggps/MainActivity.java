package com.example.fuckinggps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private Button button1;
    private boolean started;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity---onCreate","onCreate");
        button=findViewById(R.id.button);
        button1=findViewById(R.id.button2);
        SetListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity---onDestroy","onDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity---onStop","onStop");
    }

    public void SetListeners(){
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart()
    {
        super.onStart();
     if(!started){
         final Dialog dialog = new Dialog(this);
         dialog.setContentView(R.layout.dialog);
         dialog.setTitle("Dialog box");

         Button button = (Button) dialog.findViewById(R.id.Button01);
         button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 dialog.dismiss();
             }
         });

         dialog.show();
         started=true;
     }

    }


}