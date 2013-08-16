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
import com.Litterfeldt.AStory.customClasses.CoreApplication;
import com.Litterfeldt.AStory.fragments.LibraryFragment;
import com.Litterfeldt.AStory.fragments.PlayerFragment;
import com.Litterfeldt.AStory.fragments.TestActivity;
import com.Litterfeldt.AStory.services.AudioplayerService;

public class pagerView extends FragmentActivity {
    public MyFragmentPagerAdapter mAdapter;
    public ViewPager mPager;
    public AudioplayerService apService;
    public boolean serviceBound = false;
    private Intent serviceIntent;
    public boolean updatePicture = false;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("SERVICE","########audioservice started");
            apService = ((AudioplayerService.AudioplayerBinder)service).getService();
            if(!((CoreApplication)getApplication()).serviceStarted){
            ((CoreApplication)getApplication()).serviceStarted=true;
            startup();}
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            apService = null;
            Log.e("SERVICE","########audioservice stopped");
        }
    };
    private void startup(){
        setContentView(R.layout.main);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPager.setAdapter(mAdapter);
    }

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(((CoreApplication)getApplication()).serviceStarted){
           startup();
        }
        else {
            doBindService();
        }
        super.onCreate(savedInstanceState);

    }



    @Override
    protected void onPause() {
        if(!apService.mp.isPlaying){
            doUnbindService();
            apService.stopThisServiceNow();
            super.onStop();}
        else {
        doUnbindService();
        super.onStop();}
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
            serviceIntent = new Intent(pagerView.this, AudioplayerService.class);
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

    //########################################################## PRIVATE STATIC CLASSES ##############################################
    public static class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        public static final int NUMBER_OF_PAGES = 3;

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
                case 2:
                    return new TestActivity();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUMBER_OF_PAGES;
        }
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2){
            ((ViewPager) arg0).removeView((View) arg2);
        }
    }
}

