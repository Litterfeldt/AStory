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
import com.Litterfeldt.AStory.customClasses.CoreApplication;
import com.Litterfeldt.AStory.customClasses.CustomMediaPlayer;
import com.Litterfeldt.AStory.customClasses.SqlConnector;
import com.Litterfeldt.AStory.pagerView;


import java.io.IOException;
import java.util.ArrayList;

public class AudioplayerService extends Service implements MediaPlayer.OnCompletionListener{

    private final IBinder mBinder = new AudioplayerBinder();

    public CustomMediaPlayer mp;
    public SqlConnector sqlConnector;
    public ArrayList<ArrayList<String>> booklist;
    public ArrayList<ArrayList<String>> currentBookChapterList;

    @Override
    public void onCreate(){
        super.onCreate();
        mp = new CustomMediaPlayer();
        sqlConnector = new SqlConnector(this);
        mp.setOnCompletionListener(this);
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

        if(mp.hasCurrentBook){
            try{
                sqlConnector.clearCach();
                sqlConnector.newSave(sqlConnector.getBookIDFromName(mp.currentBookname),mp.currentChapterIndex,mp.getCurrentPosition());
                Log.w("SQL/SAVE","Made save!");
            }
            catch (Exception e){
                Log.w("SQL/SAVE","Couldnt make save");
            }
        }
        mp.release();
        mp=null;
        sqlConnector.close();
        sqlConnector= null;
        booklist = null;
        currentBookChapterList= null;
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
    public void getBookList(){
        booklist = sqlConnector.getBooklist();
    }
    public void getChapters(String Bookname){
        currentBookChapterList = sqlConnector.getChapters(Bookname);
    }

    //--- HELPER METHODS ---
    public void playBook(String Bookname,int Chapterindex){
        try{
            mp.stop();
            mp.reset();
            getChapters(Bookname);
            mp.datasource = currentBookChapterList.get(Chapterindex).get(2);
            mp.currentBookname = currentBookChapterList.get(Chapterindex).get(0);
            mp.currentChapterIndex = Chapterindex;
            mp.setDataSource(mp.datasource);
            mp.prepare();
            mp.start();
            mp.playerStartedPlayingABook =true;
            mp.hasCurrentBook = true;
            mp.isPlaying=true;
            mp.backgroundIsSet = false;
            mp.pauseIMGisSet = true;

        } catch (IOException e) {e.printStackTrace();}
    }
    @SuppressWarnings("deprecation")
    public void showNotification() {
        CharSequence header;
        CharSequence text;
        int drw ;
        if(mp.isPlaying){
            text = mp.currentBookname.trim();
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
    @Override
    public void onCompletion(MediaPlayer cp) {
        try{
        if(mp.currentChapterIndex < currentBookChapterList.size()){
            playBook(mp.currentBookname, mp.currentChapterIndex + 1);
            mp.playerStartedPlayingABook = true;
            mp.hasCurrentBook = true;
        }
        else{
            mp.stop();
            mp.playerStartedPlayingABook = false;
            mp.hasCurrentBook =false;
        }}catch (Exception e){}
    }
}
