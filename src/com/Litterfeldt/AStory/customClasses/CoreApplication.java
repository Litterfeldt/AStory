package com.Litterfeldt.AStory.customClasses;

import android.app.Application;
import android.content.Intent;
import com.Litterfeldt.AStory.services.AudioplayerService;


public class CoreApplication extends Application{
    public boolean serviceStarted = false;

    @Override
    public void onCreate() {
        startService(new Intent(getApplicationContext(), AudioplayerService.class));
        super.onCreate();


    }
    @Override
    public void onTerminate() {
        stopService(new Intent(getApplicationContext(), AudioplayerService.class));
        serviceStarted=false;
        super.onTerminate();


    }
}
