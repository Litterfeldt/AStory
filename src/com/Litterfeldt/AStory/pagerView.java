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
import android.view.View;
import com.Litterfeldt.AStory.customClasses.CoreApplication;
import com.Litterfeldt.AStory.fragments.LibraryFragment;
import com.Litterfeldt.AStory.fragments.PlayerFragment;
import com.Litterfeldt.AStory.services.AudioplayerService;

public class pagerView extends FragmentActivity {
    public MyFragmentPagerAdapter mAdapter;
    public ViewPager mPager;
    public AudioplayerService apService;
    public boolean serviceBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            apService = ((AudioplayerService.AudioplayerBinder)service).getService();
            if(!((CoreApplication)getApplication()).serviceStarted){
            ((CoreApplication)getApplication()).serviceStarted=true;
            startup();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            apService = null;
        }
    };
    private void startup(){
        setContentView(R.layout.main);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPager.setAdapter(mAdapter);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(((CoreApplication)getApplication()).serviceStarted){
           startup();
        }else {
            doBindService();
        }
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void onPause() {
        doUnbindService();
        super.onPause();
    }
    @Override
    protected void onResume(){
        doBindService();
        super.onResume();
    }
    @Override
    protected void onStart(){
        doBindService();
        super.onStart();
     }
    @Override
    protected void onStop() {
        doUnbindService();
        super.onStop();
    }
    void doBindService(){
        if(!serviceBound){
            Intent serviceIntent = new Intent(pagerView.this, AudioplayerService.class);
            bindService(serviceIntent,serviceConnection,Context.BIND_ABOVE_CLIENT);
            serviceBound=true;
        }
    }
    void doUnbindService() {
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
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

