package com.Litterfeldt.AStory.models;

public class Book {

    private String name;
    private String author;
    private List<String> chapters;
    private int currentChapterIndex;

    public Book(String name, String author, List<String> chapters){
        self.name = name;
        self.author = author;
        self.chapters = chapter;
        self.currentChapterIndex = 0;
    }
    public String getName() {
        return name;
    }
    public String getAuthor() {
        return author;
    }
    public List<String> getChapters() {
        return chapters;
    }
    public int getChapterCount(){
        return chapters.size();
    }
    public String nextChapter(){
        self.currentChapterIndex++;
        return getChapter(currentChapterIndex);
    }
    public String prevChapter(){
        self.currentChapterIndex--;
        return getChapter(currentChapterIndex);
    }
    public String getChapter(chapterIndex){
        self.currentChapterIndex = chapterIndex;
        return chapters.get(chapterIndex)
    }
}
