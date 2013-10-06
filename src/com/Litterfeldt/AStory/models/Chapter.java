package com.Litterfeldt.AStory.models;

public class Chapter {
    private int chapterId;
    private String chapterPath;
    private int chapterNr;
    private int chapterDuration;

    public Chapter (int id, String path, int nr, int duration ){
        chapterId = id;
        chapterPath = path;
        chapterNr = nr;
        chapterDuration = duration;
    }

    public int Id() {
        return chapterId;
    }

    public String Path() {
        return chapterPath;
    }

    public int Nr() {
        return chapterNr;
    }

    public int Duration() {
        return chapterDuration;
    }
}
