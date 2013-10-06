package com.Litterfeldt.AStory.dbConnector;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class dbConnector extends SQLiteOpenHelper {
    private static dbConnector mInstance = null;

    public static dbConnector getInstance(Context c){
        if (mInstance == null) {
            mInstance = new dbConnector(c.getApplicationContext(),
                    DATABASE_NAME,
                    null,
                    DATABASE_VERSION,
                    new DefaultDatabaseErrorHandler());
        }
        return mInstance;
    }

    private static final String DATABASE_NAME = "astory_cache.db";
    private static final int DATABASE_VERSION = 1;

    //Tables
    public static final String TABLE_NAME_SAVED_STATE = "S_SAVEDSTATE";
    public static final String TABLE_NAME_CHAPTER_LIST ="S_CHAPTERLIST";
    public static final String TABLE_NAME_BOOK_LIST ="S_BOOKLIST";
    public static final String TABLE_NAME_BOOK_IMG = "S_IMAGECACHE";

    //image cache columns
    public static final String COLUMN_BOOK_IMG_BOOK_ID ="BOOK_ID";
    public static final String COLUMN_BOOK_IMG_BOOK_IMG ="IMG_BLOB";

    //saved_state columns
    public static final String COLUMN_SAVED_STATE_STATE_ID ="ID";
    public static final String COLUMN_SAVED_STATE_BOOK_ID ="BOOK_ID";
    public static final String COLUMN_SAVED_STATE_CHAPTER_ID ="CHAPTER_ID";
    public static final String COLUMN_SAVED_STATE_CURRENT_TIME_POS="TIME_POS";
    public static final String COLUMN_SAVED_STATE_BOOKMARK_TAG="MADE_BY";

    //chapter_list columns
    public static final String COLUMN_CHAPTER_LIST_CHAPTER_ID ="ID";
    public static final String COLUMN_CHAPTER_LIST_BOOK_ID ="BOOK_ID";
    public static final String COLUMN_CHAPTER_LIST_CHAPTER_NR="CHAPTER_NR";
    public static final String COLUMN_CHAPTER_LIST_CHAPTER_PATH="FILEPATH";
    public static final String COLUMN_CHAPTER_LIST_CHAPTER_DURATION ="DURATION";


    public static final String COLUMN_BOOK_LIST_ID="ID";
    public static final String COLUMN_BOOK_LIST_NAME="BOOK_TITLE";
    public static final String COLUMN_BOOK_LIST_AUTHOR="BOOK_AUTHOR";



    public dbConnector(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public SQLiteDatabase write(){
        return getWritableDatabase();
    }
    public SQLiteDatabase read(){
        return getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_NAME_CHAPTER_LIST+ " ("
                + COLUMN_CHAPTER_LIST_CHAPTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CHAPTER_LIST_BOOK_ID + " INTEGER,"
                + COLUMN_CHAPTER_LIST_CHAPTER_NR +" INTEGER,"
                + COLUMN_CHAPTER_LIST_CHAPTER_PATH +" TEXT,"
                + COLUMN_CHAPTER_LIST_CHAPTER_DURATION +" INTEGER"
                + ");");
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_NAME_BOOK_LIST+ " ("
                + COLUMN_BOOK_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_BOOK_LIST_NAME +" TEXT,"
                + COLUMN_BOOK_LIST_AUTHOR +" TEXT"
                + ");");
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_NAME_BOOK_IMG+ " ("
                + COLUMN_BOOK_IMG_BOOK_ID +" INTEGER,"
                + COLUMN_BOOK_IMG_BOOK_IMG +" BLOB"
                + ");");

        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_NAME_SAVED_STATE+ " ("
                + COLUMN_SAVED_STATE_STATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SAVED_STATE_BOOKMARK_TAG +" TEXT,"
                + COLUMN_SAVED_STATE_CURRENT_TIME_POS +" INTEGER,"
                + COLUMN_SAVED_STATE_BOOK_ID +" INTEGER,"
                + COLUMN_SAVED_STATE_CHAPTER_ID +" INTEGER"
                + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        //WARN
        Log.w("DATABASE_UPGRADE", "Warning, upgrading database from version " + i + "to " + i2 + ", prepaire to drop all tables.");

        //DROP ALL TABLES
        try{
            sqLiteDatabase.execSQL("DROP TABLE "+ TABLE_NAME_BOOK_IMG +";");

            sqLiteDatabase.execSQL("DROP TABLE "+ TABLE_NAME_SAVED_STATE +";");

            sqLiteDatabase.execSQL("DROP TABLE "+ TABLE_NAME_CHAPTER_LIST +";");

            sqLiteDatabase.execSQL("DROP TABLE "+ TABLE_NAME_BOOK_LIST +";");
        }
        catch (Exception e){
            Log.w("SQL/DATABASEUPGRADE", "Failed to drop all databases");
        }

        //RECREATE DATABASE FROM NEW VERSION
        onCreate(sqLiteDatabase);

    }
}
