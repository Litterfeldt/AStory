package com.Litterfeldt.AStory;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import com.Litterfeldt.AStory.fragments.LibraryFragment;
import com.Litterfeldt.AStory.fragments.PlayerFragment;
import com.Litterfeldt.AStory.services.AudioplayerService;

public class pagerView extends FragmentActivity {
    public MyFragmentPagerAdapter mAdapter = null;
    public ViewPager mPager = null;
    public AudioplayerService apService = null;
    public boolean serviceConnected = false;

    private void startup(){
        setContentView(R.layout.main);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPager.setAdapter(mAdapter);
        Log.e("custom","startup method");
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        doBindService();
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void onPause() {
        doUnbindService();
        finish();
        Log.e("custom", "pause");
        super.onPause();
    }
    @Override
    protected void onResume(){
        if(serviceConnected){
            startup();
        }else{
            doBindService();
        }
        Log.e("custom", "resume");
        super.onResume();
    }
    @Override
    protected void onStart(){
        doBindService();
        Log.e("custom", "start");
        super.onStart();
     }
    @Override
    protected void onStop() {
        Log.e("custom", "stop");
        doUnbindService();
        super.onStop();
    }

    //################# SERVICE GLUE AND CONNECTIONS #################
    public AudioplayerService getApService(){
        return apService;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if(!serviceConnected){
                apService = ((AudioplayerService.AudioplayerBinder)service).getService();
                serviceConnected = true;
                startup();
            }
            Log.e("custom", "service connected");
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceConnected = false;
            apService = null;
            Log.e("custom", "service disconected");
        }
    };
    void doBindService(){
        if(!serviceConnected){
            Intent serviceIntent = new Intent(pagerView.this, AudioplayerService.class);
            bindService(serviceIntent,serviceConnection,Context.BIND_ABOVE_CLIENT);
        }
    }
    void doUnbindService() {
        if (serviceConnected) {
            serviceConnected = false;
            unbindService(serviceConnection);
        }
    }

    //################# PRIVATE STATIC CLASSES #################
    public static class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        public static final int NUMBER_OF_PAGES = 2;

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new PlayerFragment();
                case 1:
                    return new LibraryFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUMBER_OF_PAGES;
        }
        @SuppressWarnings("deprecation")
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2){
            ((ViewPager) arg0).removeView((View) arg2);
        }
    }
}

