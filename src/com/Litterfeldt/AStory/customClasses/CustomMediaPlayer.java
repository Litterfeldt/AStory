package com.Litterfeldt.AStory.customClasses;

import android.media.MediaPlayer;
import com.Litterfeldt.AStory.R;
import com.Litterfeldt.AStory.pagerView;

import java.io.IOException;

public class CustomMediaPlayer {
    private MediaPlayer mp;

    public String datasource;
    public String currentBookname;
    public int currentChapterIndex;
    public boolean isInit = true;
    public boolean isPlaying = false;
    public boolean IMGPAUSE= false;
    public boolean startedPlaying = false;
    public boolean backgroundIsSet = false;
    public boolean currentbook = false;


    public CustomMediaPlayer(){
        mp = new MediaPlayer();
    }
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener){
        mp.setOnCompletionListener(listener);

    }
    public int getCurrentPosition(){
        return mp.getCurrentPosition();
    }
    public int getDuration(){
        return mp.getDuration();
    }
    public void seekTo(int i){
        mp.seekTo(i);
    }
    public void start(){
        mp.start();
        isPlaying=true;
        isInit = true;
        currentbook = true;
    }
    public void pause(){
        mp.pause();
        isPlaying = false;
    }
    public void reset(){
        mp.reset();
        isPlaying=false;
        currentbook = false;
    }
    public void setDataSource(String dataSource) throws IOException {
        try{
        mp.setDataSource(dataSource);}catch (Exception e){}
    }
    public void prepare() throws IOException {
        try{
        mp.prepare();
        }catch (Exception e){}
    }
    public void stop(){
        mp.stop();
        isPlaying=false;
        isInit=false;
        currentbook = false;
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
            startedPlaying =true;
            currentbook = true;
            isPlaying=true;
            backgroundIsSet = false;
            IMGPAUSE = true;

        } catch (IOException e) {e.printStackTrace();}
    }

    public void release(){
        mp.release();
    }



}
