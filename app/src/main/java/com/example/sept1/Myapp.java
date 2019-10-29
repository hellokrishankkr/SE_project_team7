package com.example.sept1;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

public class Myapp extends Application {
    public void onCreate()
    {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
    }
}
