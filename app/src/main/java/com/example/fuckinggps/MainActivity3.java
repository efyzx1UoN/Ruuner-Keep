package com.example.fuckinggps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainActivity3 extends AppCompatActivity {
    MyAppListViewModel application;
    List<RecordsViewModel> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        application = (MyAppListViewModel) getApplication();
        list= application.getList();
        getRecords();
    }

    private void getRecords(){
      Log.d("list_SIZE----------",list.size()+"");
        for (int i=0;i<list.size();i++) {
            RecordsViewModel recordsViewModel = list.get(i);
            List<Records> allRecords = recordsViewModel.getAllRecords();
            Log.d("list_____Content","i"+","+i);
            for(Records records:allRecords){
             Log.d("records---------","distance"+records.getDistance()+","+"speed"+":"+
                     records.getSpeed()+","+"Seconds:"+records.getDuration()+","+"id"+":"+records.getId());
            }
        }
    }

}