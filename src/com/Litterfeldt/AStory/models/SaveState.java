package com.Litterfeldt.AStory.models;

public class SaveState {
    private int bookId;
    private int chapterId;
    private int time_pos;

    public SaveState (int bookID, int chapterID, int timePos){
        bookId = bookID;
        chapterId = chapterID;
        time_pos = timePos;
    }

    public int bookId() {
        return bookId;
    }

    public int chapterId() {
        return chapterId;
    }

    public int time_pos() {
        return time_pos;
    }
}
