package com.Litterfeldt.AStory.services;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.Litterfeldt.AStory.R;
import com.Litterfeldt.AStory.dbConnector.dbBook;
import com.Litterfeldt.AStory.dbConnector.dbSave;
import com.Litterfeldt.AStory.models.Book;
import com.Litterfeldt.AStory.customClasses.CoreApplication;
import com.Litterfeldt.AStory.customClasses.CustomMediaPlayer;
import com.Litterfeldt.AStory.models.SaveState;
import com.Litterfeldt.AStory.pagerView;


import java.util.ArrayList;

public class AudioplayerService extends Service implements MediaPlayer.OnCompletionListener{

    private final IBinder mBinder = new AudioplayerBinder();

    private CustomMediaPlayer mp;
    private NotificationManager notificationManager;
    private final String SOME_ACTION = "com.Litterfeldt.AStory.services";
    private PhoneStateListener phoneStateListener;
    private TelephonyManager mgr;


    @Override
    public void onCreate(){
        super.onCreate();
        mp = new CustomMediaPlayer();
        mp.setOnCompletionListener(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        this.registerReceiver(new KillBroadcastReceiver(), new IntentFilter(SOME_ACTION));
        showNotification();
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if(mp.hasBook()) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                        //Incoming call: Pause music
                        if(mp.isPlaying()){
                            mp.pause();
                        }
                    } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                        //Not in call: Play music
                        if(!mp.isPlaying()){
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mp.start();
                        }
                    } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        //A call is dialing, active or on hold
                        if(mp.isPlaying()){
                            mp.pause();
                        }
                    }
                    showNotification();
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
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
        ((CoreApplication)getApplication()).serviceStarted = false;
        this.stopForeground(true);
        this.stopSelf();
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
        if(mp.isPlaying()){
            String bookname = mp.book().name().trim();
            String author = mp.book().author().trim();
            byte[] bitmapdata = mp.book().image();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata.length);

            Notification noti = new Notification.Builder(this)
                    .setContentTitle("Playing " + bookname)
                    .setContentText(author)
                    .setLargeIcon(bitmap)
                    .setSmallIcon(R.drawable.play)
                    .setDeleteIntent(PendingIntent.getBroadcast(this, 0, new Intent(SOME_ACTION), 0))
                    .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, pagerView.class), 0))
                    .build();
            notificationManager.notify(2345,noti);
        }
        else{
            notificationManager.cancel(2345);
        }
    }

    public CustomMediaPlayer getMediaPlayer(){
        Log.e("MEDIAPLAYER", mp.toString());
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
        boolean success = mp.previousChapter();
        if (!success) {
            mp.seekTo(0);
        }
    }
    public void save(){
        if(mp != null){
            if(mp.hasBook()){
                Book book = mp.book();
                SaveState s = new SaveState(book.id(),
                        book.currentChapterIndex(),
                        mp.getCurrentPosition());
                dbSave.setSave(this.getApplicationContext(), s);
            }
        }
    }
    public SaveState getSave(){
        if(mp != null){
            return dbSave.getSave(this.getApplicationContext());
        }return null;
    }

    private class KillBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            save();
        }
    }

}