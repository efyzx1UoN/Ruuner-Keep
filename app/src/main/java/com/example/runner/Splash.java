package com.example.runner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;



public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);
        Thread myThread=new Thread(){//创建子线程
            @Override
            public void run() {
                try{
                    sleep(3000);
                    Intent it=new Intent(getApplicationContext(),MainActivity.class);//启动MainActivity
                    startActivity(it);
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}