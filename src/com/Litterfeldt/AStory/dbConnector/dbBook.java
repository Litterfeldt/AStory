package com.Litterfeldt.AStory.dbConnector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.Litterfeldt.AStory.models.Book;
import com.Litterfeldt.AStory.models.Chapter;
import java.util.ArrayList;

public class dbBook {

    public static void purge(Context c){
        (new dbBook(c)).purgeAllBooks();
    }
    public static ArrayList<Book> getBooks(Context c){
        return (new dbBook(c)).getAllBooks();
    }
    public static void addBook(Context c, Book b){
        (new dbBook(c)).addBook(b);
    }
    public static Book bookById(Context c, int id){
        return (new dbBook(c)).getBookById(id);
    }

    public static int bookIdByName(Context c, String name){
        return (new dbBook(c)).getBookId(name);
    }


    private static final String ALL_BOOKS = "SELECT * FROM "+ dbConnector.TABLE_NAME_BOOK_LIST + " ;";
    private static final String ALL_BOOKS_BY_ID = "SELECT * FROM " + dbConnector.TABLE_NAME_BOOK_LIST + " WHERE "+dbConnector.COLUMN_BOOK_LIST_ID+" = ? ;";
    private static final String ALL_CHAPTERS_BY_BOOK = "SELECT * FROM " + dbConnector.TABLE_NAME_CHAPTER_LIST + " WHERE BOOK_ID = ? ;";
    private static final String IMAGE = "Select IMG_BLOB from " + dbConnector.TABLE_NAME_BOOK_IMG +  " where BOOK_ID = ?;";
    private static final String PURGE = "DELETE FROM ";

    private dbConnector connector;

    public dbBook(Context c) {
        connector = dbConnector.getInstance(c);
    }
    public void addImg (int bookId, byte[] img){
        ContentValues initialValues = new ContentValues();
        initialValues.put(dbConnector.COLUMN_BOOK_IMG_BOOK_IMG,img);
        initialValues.put(dbConnector.COLUMN_BOOK_IMG_BOOK_ID,bookId);
        connector.write().insert(dbConnector.TABLE_NAME_BOOK_IMG, null, initialValues);
    }
    public void addChapter(Chapter chapter, int bookID){
        ContentValues initialValues = new ContentValues();
        initialValues.put(dbConnector.COLUMN_CHAPTER_LIST_CHAPTER_PATH,chapter.Path());
        initialValues.put(dbConnector.COLUMN_CHAPTER_LIST_CHAPTER_NR,chapter.Nr());
        initialValues.put(dbConnector.COLUMN_CHAPTER_LIST_CHAPTER_DURATION,chapter.Duration());
        initialValues.put(dbConnector.COLUMN_CHAPTER_LIST_BOOK_ID,bookID);
        connector.write().insert(dbConnector.TABLE_NAME_CHAPTER_LIST, null, initialValues);

    }
    public void addBook(Book book) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(dbConnector.COLUMN_BOOK_LIST_NAME,book.name());
        initialValues.put(dbConnector.COLUMN_BOOK_LIST_AUTHOR,book.author());
        connector.write().insert(dbConnector.TABLE_NAME_BOOK_LIST, null, initialValues);
        int bookID = getBookId(book.name());
        Log.e("addBook",Integer.toString(bookID));

        for (Chapter chapter : book.getChapters()){
            addChapter(chapter, bookID);
        }
        addImg(bookID,book.image());
    }
    public int getBookId(String bookName){
        Cursor c = connector.read().query(dbConnector.TABLE_NAME_BOOK_LIST,
                new String[]{dbConnector.COLUMN_BOOK_LIST_ID},
                dbConnector.COLUMN_BOOK_LIST_NAME+"="+"\""+bookName+"\"",
                null,null,null,null);
        c.moveToFirst();
        return c.getInt(0);
    }
    private Book getBookById(int id){
        Cursor bookCursor = connector.read().rawQuery(ALL_BOOKS_BY_ID.replace("?",Integer.toString(id)), null);
        boolean notEmpty = bookCursor.moveToFirst();
        if (notEmpty) {
            String name = bookCursor.getString(1);
            String author = bookCursor.getString(2);
            ArrayList<Chapter> chapters = getAllChapters(id);
            byte[] img = getImageById(id);
            Book book = new Book(id,name, author, chapters, img);
            return book;
        } else {
            return null;
        }


    }
    public void purgeAllBooks(){
        connector.write().execSQL(PURGE + dbConnector.TABLE_NAME_BOOK_LIST +";" );
        connector.write().execSQL(PURGE + dbConnector.TABLE_NAME_CHAPTER_LIST+";" );
        connector.write().execSQL(PURGE + dbConnector.TABLE_NAME_BOOK_IMG+";" );
    }
    public ArrayList<Book> getAllBooks() {
        Cursor bookCursor = connector.read().rawQuery(ALL_BOOKS, null);
        boolean notEmpty = bookCursor.moveToFirst();
        if (notEmpty) {
            ArrayList<Book> books = new ArrayList<Book>();
            while(!bookCursor.isAfterLast()){
                int id = bookCursor.getInt(0);
                String name = bookCursor.getString(1);
                String author = bookCursor.getString(2);
                ArrayList<Chapter> chapters = getAllChapters(id);
                byte[] img = getImageById(id);
                Book book = new Book(id,name, author, chapters, img);
                books.add(book);
                bookCursor.moveToNext();
            }
            return books;
        } else {
            return new ArrayList<Book>();
        }
    }
    public byte[] getImageById(int bookId) {
        Cursor imageCursor = connector.read().rawQuery(IMAGE.replace("?", Integer.toString(bookId)), null);
        boolean notEmpty = imageCursor.moveToFirst();
        if (notEmpty) {
            return imageCursor.getBlob(0);
        } else {
            return null;
        }
    }
    public ArrayList<Chapter> getAllChapters(int bookId) {
        Cursor chapterCursor = connector.read()
                .rawQuery(ALL_CHAPTERS_BY_BOOK.replace("?", Integer.toString(bookId)), null);
        boolean notEmpty = chapterCursor.moveToFirst();
        if (notEmpty) {
            ArrayList<Chapter> chapters = new ArrayList<Chapter>();
            while(!chapterCursor.isAfterLast()){
                int chapterID = chapterCursor.getInt(0);
                int chapterNr = chapterCursor.getInt(2);
                String chapterPath = chapterCursor.getString(3);
                int chapterDuration = chapterCursor.getInt(4);
                Chapter chapter = new Chapter(chapterID, chapterPath, chapterNr, chapterDuration);
                chapters.add(chapter);
                chapterCursor.moveToNext();
            }

            return chapters;
        } else {
            return null;
        }
    }
}
