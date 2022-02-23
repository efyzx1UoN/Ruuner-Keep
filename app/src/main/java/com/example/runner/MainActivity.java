package com.example.runner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;



public class MainActivity extends AppCompatActivity {
    private Button button;
    private Button button1;
    private Button button2;
    private boolean started;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity---onCreate","onCreate");
        button=findViewById(R.id.button);
        button1=findViewById(R.id.button2);
        button2=findViewById(R.id.button3);
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

    /**
     * jump to different activities
     */
    public void SetListeners(){
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity_Running.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity_Records.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity_Statistic.class);
                startActivity(intent);
            }
        });
    }

    /**
     * create a alert dialog if is the first time open  main_activity
     */
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