package com.Litterfeldt.AStory.customClasses;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import com.Litterfeldt.AStory.pagerView;

import java.io.File;
import java.util.*;

public class SqlConnector extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "astory_cache.db";
    private static final int DATABASE_VERSION = 3;

    //Tables
    private static final String TABLE_NAME_SAVED_STATE = "S_SAVEDSTATE";
    private static final String TABLE_NAME_CHAPTER_LIST ="S_CHAPTERLIST";
    private static final String TABLE_NAME_BOOK_IMG = "S_IMAGECACHE";

    //image cache collumns
    private static final String COLUMN_BOOK_IMG_BOOK_NAME ="BOOK_TITLE";
    private static final String COLUMN_BOOK_IMG_BOOK_IMG ="IMG_BLOB";

    //saved_state columns
    private static final String COLUMN_SAVED_STATE_STATE_ID ="ID";
    private static final String COLUMN_SAVED_STATE_BOOK_ID ="BOOK_ID";
    private static final String COLUMN_SAVED_STATE_CHAPTER_ID ="CHAPTER_ID";
    private static final String COLUMN_SAVED_STATE_CURRENT_TIME_POS="TIME_POS";
    private static final String COLUMN_SAVED_STATE_BOOKMARK_TAG="MADE_BY";

    //chapter_list columns
    private static final String COLUMN_CHAPTER_LIST_CHAPTER_ID ="ID";
    private static final String COLUMN_CHAPTER_LIST_CHAPTER_NR="CHAPTER_NR";
    private static final String COLUMN_CHAPTER_LIST_CHAPTER_PATH="FILEPATH";
    private static final String COLUMN_CHAPTER_LIST_CHAPTER_DURATION ="DURATION";
    private static final String COLUMN_BOOK_LIST_NAME="BOOK_TITLE";
    private static final String COLUMN_BOOK_LIST_AUTHOR="BOOK_AUTHOR";
    private static final String COLUMN_BOOK_LIST_CHAPTER_COUNT="NUMBER_OF_CHAPTERS";

    //Misc
    private boolean imageIsCached = false;
    private static File defaultDir;
    private static SQLiteDatabase db;
    private static MediaMetadataRetriever mmr;
    private static final Set<String> acceptedFormats = new HashSet<String>(Arrays.asList(
            new String[]{"mp3", "m4a", "aac", "flac"}
    ));
    private static final String selectAllBooks = "Select "+COLUMN_BOOK_LIST_NAME+", "+COLUMN_BOOK_LIST_AUTHOR+", "+COLUMN_CHAPTER_LIST_CHAPTER_PATH+" from S_CHAPTERLIST where CHAPTER_NR = 1 order by BOOK_AUTHOR;";
    private static final String selectBook = "Select BOOK_TITLE, BOOK_AUTHOR, FILEPATH, DURATION, CHAPTER_NR, NUMBER_OF_CHAPTERS from S_CHAPTERLIST where BOOK_TITLE = '?' order by CHAPTER_NR;";
    private static final String selectBookNameFromID = "Select BOOK_TITLE from S_CHAPTERLIST where ID = '?';";
    private static final String selectBookIDFromName = "Select ID from S_CHAPTERLIST where BOOK_TITLE = '?';";
    private static final String getimage = "Select IMG_BLOB from S_IMAGECACHE where BOOK_TITLE = '?';";
    private static final String getLastSave = "Select "+COLUMN_SAVED_STATE_BOOK_ID+", "+COLUMN_SAVED_STATE_CHAPTER_ID+", "+COLUMN_SAVED_STATE_CURRENT_TIME_POS+" from S_SAVEDSTATE;";

    private int chaptercounter;
    private ArrayList<String> bookPathHerarchy;
    private Context context;



    public SqlConnector(Context context){
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
        this.context = context;
        defaultDir = new File(Environment.getExternalStorageDirectory() +"/Audiobooks");
        if(!defaultDir.exists()){
            defaultDir.mkdir();
            defaultDir = new File(Environment.getExternalStorageDirectory() +"/Audiobooks");
        }

        db=getWritableDatabase();
        mmr = new MediaMetadataRetriever();

    }
    public ArrayList<String> allocateBookFolderHerarchy(){
        ArrayList<String> bookPathList = new ArrayList<String>();

        File[] files = defaultDir.listFiles();

        if (files == null){
            return null;
        }else {
            for (File f : files){
                if(f.isDirectory()){
                    bookPathList.add(f.getAbsolutePath());
                }
                else if (f.isFile()){
                    String fullpath = f.getAbsolutePath();
                    String filetype = fullpath.substring(fullpath.lastIndexOf(".")+1);

                    if(acceptedFormats.contains(filetype.toLowerCase())){
                        bookPathList.add(fullpath);
                    }}}
            bookPathHerarchy = bookPathList;
            return bookPathList;
        }
    }
    public void allocateBooks(){

        File[] files = defaultDir.listFiles();
        if(files == null){
            Log.e("ASTORY","The audiobook-folder is empty");
        }
        else {
        for (File f : files){
            imageIsCached = false;
            if(f.isDirectory()){
                makeChapterlist(f);
            }
            else if (f.isFile()){
                String fullpath = f.getAbsolutePath();
                int dot = fullpath.lastIndexOf(".");
                String filetype= fullpath.substring(dot+1);
                if(acceptedFormats.contains(filetype.toLowerCase())){
                    addChapter(fullpath);
                }}}
        }
    }
    private void makeChapterlist(File directory){
        File files[] = directory.listFiles();
        chaptercounter = 0;
        for (File f : files){
            if (f.isFile()){
                String audiopath = getAudioFilePath(f);
                if(audiopath != null){
                  addChapter(audiopath);
                }}}}
    private void addChapter(String path){
        mmr.setDataSource(path);
        String bookName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String author = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String numOfChapters = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS);
        String chapterNum = "";
        if (chapterNum==""){
            chaptercounter++;
            chapterNum = Integer.toString(chaptercounter);
        }
        Log.e("DEV","Chapternumber: "+chapterNum);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String insert = "INSERT INTO "
                +TABLE_NAME_CHAPTER_LIST
                +"("+ COLUMN_BOOK_LIST_NAME
                +","+COLUMN_BOOK_LIST_AUTHOR
                +","+COLUMN_BOOK_LIST_CHAPTER_COUNT
                +","+COLUMN_CHAPTER_LIST_CHAPTER_NR
                +","+COLUMN_CHAPTER_LIST_CHAPTER_DURATION
                +","+COLUMN_CHAPTER_LIST_CHAPTER_PATH+")"
                +" VALUES ("
                +"'"+bookName.replace("'","''")+"'"
                +",'"+author.replace("'","''")+"'"
                +","+numOfChapters
                +","+chapterNum
                +","+duration
                +",'"+path.replace("'","''")+"');";
        db.execSQL(insert);
        if(imageIsCached == false){
            ContentValues initialValues = new ContentValues();
            initialValues.put(COLUMN_BOOK_IMG_BOOK_IMG,(byte[]) mmr.getEmbeddedPicture());
            initialValues.put(COLUMN_BOOK_IMG_BOOK_NAME,bookName);
            db.insert(TABLE_NAME_BOOK_IMG, null, initialValues);
            imageIsCached = true;

        }
    }
    public void newSave(int bookID, int chapterNum, int currentTimePos){
        clearCach();
        String insert = "INSERT INTO "
                +TABLE_NAME_SAVED_STATE
                +"("+ COLUMN_SAVED_STATE_BOOK_ID
                +","+COLUMN_SAVED_STATE_CURRENT_TIME_POS
                +","+COLUMN_SAVED_STATE_CHAPTER_ID
                +","+COLUMN_SAVED_STATE_BOOKMARK_TAG+")"
                +" VALUES ("
                +bookID
                +","+currentTimePos
                +","+chapterNum
                +",'no');";
        db.execSQL(insert);

    }
    public int[] getSave(){
        Cursor cur = db.rawQuery(getLastSave,null);
        cur.moveToFirst();
        int[] i = new int[3];
        i[0]=cur.getInt(0);
        i[1]=cur.getInt(1);
        i[2]=cur.getInt(2);
        return i;


    }
    public byte[] getPicture(String bookname){
        Cursor cur = db.rawQuery(getimage.replace("?",bookname.replace("'","''")),null);
        cur.moveToFirst();
        return cur.getBlob(0);
    }


    private static String getAudioFilePath(File f){
        String fullpath = f.getAbsolutePath();
        int dot = fullpath.lastIndexOf(".");
        String filetype= fullpath.substring(dot+1);
        if(acceptedFormats.contains(filetype.toLowerCase())){
            return fullpath;
        }
        else{
            return null;
        }
    }
    public void emptyBookList(){
        try {
            db.execSQL("DROP TABLE "+ TABLE_NAME_CHAPTER_LIST +";");
        }
        catch (Exception e){

        }
        try{
            db.execSQL("DROP TABLE "+ TABLE_NAME_BOOK_IMG +";");

        }
        catch (Exception e){

        }
        db.execSQL("CREATE TABLE "+TABLE_NAME_CHAPTER_LIST+ " ("
                + COLUMN_CHAPTER_LIST_CHAPTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CHAPTER_LIST_CHAPTER_PATH +" TEXT,"
                + COLUMN_CHAPTER_LIST_CHAPTER_NR +" INTEGER,"
                + COLUMN_CHAPTER_LIST_CHAPTER_DURATION +" INTEGER,"
                + COLUMN_BOOK_LIST_NAME +" TEXT,"
                + COLUMN_BOOK_LIST_AUTHOR +" TEXT,"
                + COLUMN_BOOK_LIST_CHAPTER_COUNT +" INTEGER"
                + ");");
        db.execSQL("CREATE TABLE "+TABLE_NAME_BOOK_IMG+ " ("
                + COLUMN_BOOK_IMG_BOOK_NAME +" TEXT,"
                + COLUMN_BOOK_IMG_BOOK_IMG +" BLOB"
                + ");");
    }
    public void clearCach(){
        try {
            db.execSQL("DROP TABLE "+ TABLE_NAME_SAVED_STATE +";");
        }

        catch (Exception e){

        }
        db.execSQL("CREATE TABLE "+TABLE_NAME_SAVED_STATE+ " ("
                + COLUMN_SAVED_STATE_STATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SAVED_STATE_BOOKMARK_TAG +" TEXT,"
                + COLUMN_SAVED_STATE_CURRENT_TIME_POS +" INTEGER,"
                + COLUMN_SAVED_STATE_BOOK_ID +" INTEGER,"
                + COLUMN_SAVED_STATE_CHAPTER_ID +" INTEGER"
                + ");");
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME_CHAPTER_LIST+ " ("
                + COLUMN_CHAPTER_LIST_CHAPTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CHAPTER_LIST_CHAPTER_PATH +" TEXT,"
                + COLUMN_CHAPTER_LIST_CHAPTER_NR +" INTEGER,"
                + COLUMN_CHAPTER_LIST_CHAPTER_DURATION +" INTEGER,"
                + COLUMN_BOOK_LIST_NAME +" TEXT,"
                + COLUMN_BOOK_LIST_AUTHOR +" TEXT,"
                + COLUMN_BOOK_LIST_CHAPTER_COUNT +" INTEGER"
                + ");");
        db.execSQL("CREATE TABLE "+TABLE_NAME_BOOK_IMG+ " ("
                + COLUMN_BOOK_IMG_BOOK_NAME +" TEXT,"
                + COLUMN_BOOK_IMG_BOOK_IMG +" BLOB"
                + ");");

        db.execSQL("CREATE TABLE "+TABLE_NAME_SAVED_STATE+ " ("
                + COLUMN_SAVED_STATE_STATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SAVED_STATE_BOOKMARK_TAG +" TEXT,"
                + COLUMN_SAVED_STATE_CURRENT_TIME_POS +" INTEGER,"
                + COLUMN_SAVED_STATE_BOOK_ID +" INTEGER,"
                + COLUMN_SAVED_STATE_CHAPTER_ID +" INTEGER"
                + ");");
    }

    public ArrayList<ArrayList<String>> getBooklist(){
        Cursor cur = db.rawQuery(selectAllBooks,null);
        cur.moveToLast();
        int limit = cur.getPosition();
        int i = 0;
        cur.moveToFirst();

        ArrayList<ArrayList<String>> books = new ArrayList<ArrayList<String>>();
        while(i<=limit){
            ArrayList<String> tmp = new ArrayList<String>();
            Log.i("DatabaseQuerry", cur.getString(0));
            tmp.add(cur.getString(0));
            tmp.add(cur.getString(1));
            tmp.add(cur.getString(2));
            books.add(tmp);
            cur.moveToNext();
            i++;
        }
        Log.i("DatabaseQuerry", books.toString());
        return books;
    }
    public ArrayList<ArrayList<String>> getChapters(String BookName){
        Cursor cur = db.rawQuery(selectBook.replace("?",BookName.replace("'","''")),null);
        cur.moveToLast();
        int limit = cur.getPosition();
        int i = 0;
        cur.moveToFirst();
        ArrayList<ArrayList<String>> chapters = new ArrayList<ArrayList<String>>();
        while(i<=limit){
            ArrayList<String> tmp = new ArrayList<String>();
            tmp.add(cur.getString(0));
            tmp.add(cur.getString(1));
            tmp.add(cur.getString(2));
            tmp.add(cur.getString(3));
            tmp.add(cur.getString(4));
            tmp.add(cur.getString(5));
            chapters.add(tmp);
            cur.moveToNext();
            i++;
        }
        return chapters;
    }
    public String getBookNameFromID(int bookID){
        Cursor cur = db.rawQuery(selectBookNameFromID.replace("?",String.valueOf(bookID)),null);
        cur.moveToFirst();
        return (cur.getString(0));

    }

    public int getBookIDFromName(String BookName){
        Cursor cur = db.rawQuery(selectBookIDFromName.replace("?",BookName.replace("'","''")),null);
        cur.moveToFirst();
        return (cur.getInt(0));

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //WARN
        Log.w("DATABASE_UPGRADE", "Warning, upgrading database from version " + i + "to " + i1 + ", prepaire to drop all tables.");

        //DROP ALL TABLES
        try{
            db.execSQL("DROP TABLE "+ TABLE_NAME_BOOK_IMG +";");

            db.execSQL("DROP TABLE "+ TABLE_NAME_SAVED_STATE +";");

            db.execSQL("DROP TABLE "+ TABLE_NAME_CHAPTER_LIST +";");


        }
        catch (Exception e){
            Log.w("SQL/DATABASEUPGRADE", "No tables to drop.");
        }

        //RECREATE DATABASE FROM NEW VERSION
        onCreate(db);
    }
}
