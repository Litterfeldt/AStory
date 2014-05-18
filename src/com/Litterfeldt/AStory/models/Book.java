package com.Litterfeldt.AStory.models;

import java.util.*;

public class Book {

    private String bookName;
    private int bookId;
    private String bookAuthor;
    private ArrayList<Chapter> bookChapters;
    private int currentChapterIndex;
    private byte[] coverImage;
    private int duration;

    public Book(int id, String name, String author, ArrayList<Chapter> chapterList, byte[] image){
        currentChapterIndex = 0;
        bookId = id;
        bookName = name;
        bookAuthor = author;
        bookChapters = chapterList;
        coverImage = image;
        duration = 0;
    }
    public int id() {
        return bookId;
    }
    public String name() {
        return bookName;
    }
    public String author() {
        return bookAuthor;
    }
    public ArrayList<Chapter> getChapters() {
        return bookChapters;
    }
    public int chapterCount(){
        return bookChapters.size();
    }
    public int getDuration(){
        if (duration > 0) {
            return duration;
        }
        for(Chapter c : bookChapters){
            duration +=  c.Duration() ;
        }
        return duration;
    }
    public int getCurrentChapterDuration() {
        int currentDuration = 0;
        for (int i = 0; i<currentChapterIndex; i++){
            currentDuration += bookChapters.get(i).Duration();
        }
        return currentDuration;
    }
    public boolean hasNextChapter(){
        return (currentChapterIndex<chapterCount());
    }
    public boolean hasPreviousChapter(){
        return (currentChapterIndex>0);
    }
    public int currentChapterIndex(){
        return currentChapterIndex;
    }
    public Chapter nextChapter(){
        currentChapterIndex++;
        return getChapter(currentChapterIndex);
    }
    public Chapter prevChapter(){
        if (hasPreviousChapter()) {
            currentChapterIndex--;
            return getChapter(currentChapterIndex);
        } else {
            return null;
        }
    }
    public Chapter getChapter(int chapterIndex){
        if (hasNextChapter()) {
            currentChapterIndex = chapterIndex;
            return bookChapters.get(chapterIndex);
        }else {
            return null;
        }
    }
    public byte[] image(){
        return coverImage;
    }
}
