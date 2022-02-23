package com.example.runner;

import android.net.Uri;

public class RecordsProviderContract {
    public static final String AUTHORITY = "com.example.runner.RecordsContentProvider";
    public static final Uri Records_URI = Uri.parse("content://"+AUTHORITY+"/records");
}
