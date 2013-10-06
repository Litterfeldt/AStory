package com.Litterfeldt.AStory.services;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;

import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import android.util.Log;
import com.Litterfeldt.AStory.R;
import com.Litterfeldt.AStory.dbConnector.dbBook;
import com.Litterfeldt.AStory.models.Book;
import com.Litterfeldt.AStory.customClasses.CoreApplication;
import com.Litterfeldt.AStory.customClasses.CustomMediaPlayer;
import com.Litterfeldt.AStory.pagerView;


import java.util.ArrayList;

public class AudioplayerService extends Service implements MediaPlayer.OnCompletionListener{

    private final IBinder mBinder = new AudioplayerBinder();

    private CustomMediaPlayer mp;

    private ArrayList<Book> booklist;

    @Override
    public void onCreate(){
        super.onCreate();
        mp = new CustomMediaPlayer();
        mp.setOnCompletionListener(this);
        populateBookList();
        showNotification();
    }

    //--- SERVICE MANAGEMENT ---
    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        Log.w("LocalService", "Received start id " + startID + ": " + intent);
        if(!((CoreApplication) getApplication()).serviceStarted){
            onCreate();
        }
        return START_STICKY;
    }
    public void stopThisServiceNow(){
        mp.release();
        mp=null;
        booklist = null;
        ((CoreApplication)getApplication()).serviceStarted = false;
        this.stopForeground(true);
        this.stopSelf();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    public class AudioplayerBinder extends Binder {
        public AudioplayerService getService(){
            return AudioplayerService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder ;
    }

    //--- SERVICE METHODS ---
    public ArrayList<Book> getBookList(){
        return dbBook.getBooks(((CoreApplication) getApplication()).getApplicationContext());
    }

    //--- HELPER METHODS ---

    @SuppressWarnings("deprecation")
    public void showNotification() {
        CharSequence header;
        CharSequence text;
        int drw ;
        if(mp.isPlaying()){
            text = mp.book().name().trim();
            header = "Playing:";
            drw = R.drawable.play;
            Notification notification = new Notification(drw, text,
                    System.currentTimeMillis());
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, pagerView.class), 0);
            notification.setLatestEventInfo(this, header,
                    text, contentIntent);
            startForeground(2345, notification);
        }
        else{
            text = "";
            header = "Paused";
            drw = R.drawable.pasue;
            Notification notification = new Notification(drw, header,
                    System.currentTimeMillis());
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, pagerView.class), 0);
            notification.setLatestEventInfo(this, header,
                    text, contentIntent);
            startForeground(2345, notification);
        }

    }
    public void populateBookList(){
        booklist =dbBook.getBooks(this.getApplicationContext());
    }
    public CustomMediaPlayer getMediaPlayer(){
        return mp;
    }
    @Override
    public void onCompletion(MediaPlayer cp) {
        nextChapter();
    }
    public void nextChapter(){
        boolean success = mp.nextChapter();
        if (!success) {
            mp.stop();
            mp.reset();
            mp.removeBook();
        }
    }
    public void previousChapter(){
        boolean success = mp.nextChapter();
        if (!success) {
            mp.seekTo(0);
        }
    }

}