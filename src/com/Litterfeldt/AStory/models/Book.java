package com.Litterfeldt.AStory.models;

import java.util.*;

public class Book {

    private String bookName;
    private int bookId;
    private String bookAuthor;
    private ArrayList<Chapter> bookChapters;
    private int currentChapterIndex;
    private byte[] coverImage;

    public Book(int id, String name, String author, ArrayList<Chapter> chapterList, byte[] image){
        currentChapterIndex = 0;
        bookId = id;
        bookName = name;
        bookAuthor = author;
        bookChapters = chapterList;
        coverImage = image;
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
        currentChapterIndex--;
        return getChapter(currentChapterIndex);
    }
    public Chapter getChapter(int chapterIndex){
        currentChapterIndex = chapterIndex;
        return bookChapters.get(chapterIndex);
    }
    public byte[] image(){
        return coverImage;
    }
}
