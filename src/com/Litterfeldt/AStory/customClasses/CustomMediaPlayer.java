package com.Litterfeldt.AStory.customClasses;
import android.media.MediaPlayer;

import com.Litterfeldt.AStory.models.Book;
import java.io.IOException;

public class CustomMediaPlayer {
    private MediaPlayer mp;
    private Book currentBook;
    private boolean backgroundToggle;
    public void setBackgroundToggle(boolean b){
        backgroundToggle = b;
    }
    public boolean getBackgroundToggle(){
        return backgroundToggle;
    }

    public CustomMediaPlayer(){
        mp = new MediaPlayer();
    }
    public void playBook(Book book,int chapterIndex){
        try{
            currentBook = book;
            setBackgroundToggle(false);
            stop();
            reset();
            setDataSource(book.getChapter(chapterIndex).Path());
            prepare();
            start();
        }catch(IOException e){
            currentBook = null;
        }
    }

    public boolean nextChapter(){
        if (hasBook() && currentBook.hasNextChapter()) {
            try {
                stop();
                reset();
                setDataSource(currentBook.nextChapter().Path());
                prepare();
                start();
                return true;
            }catch (IOException e){
                currentBook = null;
            }catch (NullPointerException e){
                currentBook = null;
            }
        }
        return false;
    }
    public boolean previousChapter(){
        if (hasBook() && currentBook.hasPreviousChapter()) {
            try {
                stop();
                reset();
                setDataSource(currentBook.prevChapter().Path());
                prepare();
                start();
                return true;
            }catch (IOException e){
                currentBook = null;
            }
        }
        return false;
    }
    public void setOnCompletionListener( MediaPlayer.OnCompletionListener listener ){
        mp.setOnCompletionListener(listener);
    }
    public int getCurrentPosition(){
        return hasBook() ? mp.getCurrentPosition() : 0;
    }
    public int getDuration(){
        return hasBook() ? mp.getDuration() : 0;
    }
    public void seekTo(int i){
        if (hasBook()) mp.seekTo(i);
    }
    public void start(){
        if (hasBook()) mp.start();
    }
    public void pause(){
        if (hasBook()) mp.pause();
    }
    public void reset(){
        if (hasBook()) mp.reset();
    }
    public void setDataSource(String dataSource) throws IOException {
        mp.setDataSource(dataSource);
    }
    public void prepare() throws IOException {
        mp.prepare();
    }
    public void stop(){
        if (hasBook()) mp.stop();
    }
    public void release(){
        mp.release();
    }
    public boolean hasBook(){
        return (currentBook != null);
    }
    public boolean isPlaying(){
        return mp.isPlaying();
    }
    public int currentChapterIndex(){
        return currentBook.currentChapterIndex();
    }
    public Book book(){
        return currentBook;
    }
    public void removeBook(){
        currentBook = null;
    }
}
