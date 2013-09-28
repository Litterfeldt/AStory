package com.Litterfeldt.AStory.customClasses;

import android.media.MediaPlayer;
import com.Litterfeldt.AStory.pagerView;

import java.io.IOException;

public class CustomMediaPlayer {
    private MediaPlayer mp;

    public String datasource;
    public String currentBookname;
    public int currentChapterIndex;
    public boolean mpHasInitialized = true;
    public boolean isPlaying = false;
    public boolean pauseIMGisSet = false;
    public boolean playerStartedPlayingABook = false;
    public boolean backgroundIsSet = false;
    public boolean hasCurrentBook = false;


    public CustomMediaPlayer(){
        mp = new MediaPlayer();
    }
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener){
        mp.setOnCompletionListener(listener);

    }
    public int getCurrentPosition(){
        if (hasCurrentBook){
            return mp.getCurrentPosition();
        }
        else {
            return 0;
        }
    }
    public int getDuration(){
        if(hasCurrentBook){
            return mp.getDuration();
        }
        else{
            return 0;
        }

    }
    public void seekTo(int i){
        if (hasCurrentBook){
            mp.seekTo(i);
        }
    }
    public void start(){
        mp.start();
        isPlaying=true;
        mpHasInitialized = true;
        hasCurrentBook = true;
    }
    public void pause(){
        if (hasCurrentBook){
            mp.pause();
            isPlaying = false;
        }

    }
    public void reset(){
        if (hasCurrentBook){
            mp.reset();
            isPlaying=false;
            hasCurrentBook = false;
        }
    }
    public void setDataSource(String dataSource) throws IOException {
        try{
        mp.setDataSource(dataSource);}catch (Exception ignored){}
    }
    public void prepare() throws IOException {
        try{
        mp.prepare();
        }catch (Exception ignored){}
    }
    public void stop(){
        if (hasCurrentBook){
            mp.stop();
            isPlaying=false;
            mpHasInitialized =false;
            hasCurrentBook = false;
        }
    }
    public void playBook(String Bookname,int Chapterindex, pagerView activity){
        try{
            stop();
            reset();
            activity.apService.getChapters(Bookname);
            datasource = activity.apService.currentBookChapterList.get(Chapterindex).get(2);
            currentBookname = activity.apService.currentBookChapterList.get(Chapterindex).get(0);
            currentChapterIndex = Chapterindex;
            setDataSource(datasource);
            prepare();
            start();
            playerStartedPlayingABook =true;
            hasCurrentBook = true;
            isPlaying=true;
            backgroundIsSet = false;
            pauseIMGisSet = true;

        } catch (IOException e) {e.printStackTrace();}
    }

    public void release(){
        mp.release();
    }



}
