package com.example.runner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;



public class Splash extends AppCompatActivity {
    /**
     * Create a splash screen
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);
        Thread myThread=new Thread(){   // create a thread
            @Override
            public void run() {
                try{
                    sleep(3000);
                    Intent it=new Intent(getApplicationContext(),MainActivity.class);//open main_activity
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