package com.example.fuckinggps;

import android.app.Application;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyAppListViewModel extends Application {
    private static  List<Float> list;
    @Override
    public void onCreate() {
        super.onCreate();
        list=new ArrayList<>();
    }

    public static List<Float> getList(){
        return list;
    }
}
