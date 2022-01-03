package com.example.fuckinggps;

import android.app.Application;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyAppListViewModel extends Application {
    private static  List<RecordsViewModel> list;
    @Override
    public void onCreate() {
        super.onCreate();
        list=new ArrayList<>();
    }

    public static void add(RecordsViewModel recordsViewModel){
        list.add(recordsViewModel);
    }
    public static List<RecordsViewModel> getList(){
        return list;
    }
}
