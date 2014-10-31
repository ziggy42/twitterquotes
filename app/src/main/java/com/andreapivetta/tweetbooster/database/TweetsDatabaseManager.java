package com.andreapivetta.tweetbooster.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class TweetsDatabaseManager {

    static final class SetsMetaData {
        static final String UP_TABLE = "tosend";
        static final String UP_TWEET_KEY = "tweet";
        static final String UP_DAY_KEY = "day";
        static final String UP_MONTH_KEY = "month";
        static final String UP_YEAR_KEY = "year";
        static final String UP_HOUR_KEY = "hour";
        static final String UP_MINUTE_KEY = "minute";
    }

    private static final String TOSEND_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + SetsMetaData.UP_TABLE + " (" + SetsMetaData.UP_TWEET_KEY
            + " text not null," + SetsMetaData.UP_MINUTE_KEY
            + " integer not null," + SetsMetaData.UP_HOUR_KEY
            + " integer not null," + SetsMetaData.UP_DAY_KEY
            + " integer not null," + SetsMetaData.UP_MONTH_KEY
            + " integer not null," + SetsMetaData.UP_YEAR_KEY
            + " integer not null);";

    private DatabaseHelper myDBhelper;
    private SQLiteDatabase myDB;

    private static final String DB_NAME = "user_tweets_db";
    private static final int DB_VERSION = 1;

    public TweetsDatabaseManager(Context context) {
        this.myDBhelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
    }

    public void open() {
        this.myDB = myDBhelper.getWritableDatabase();
    }

    public void close() {
        this.myDB.close();
    }

    public void insertUp(String tweet, int minute, int hour, int day,
                         int month, int year) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SetsMetaData.UP_TWEET_KEY, tweet);
        contentValues.put(SetsMetaData.UP_MINUTE_KEY, minute);
        contentValues.put(SetsMetaData.UP_HOUR_KEY, hour);
        contentValues.put(SetsMetaData.UP_DAY_KEY, day);
        contentValues.put(SetsMetaData.UP_MONTH_KEY, month);
        contentValues.put(SetsMetaData.UP_YEAR_KEY, year);
        myDB.insert(SetsMetaData.UP_TABLE, null, contentValues);
    }

    public void deleteUp(int minute, int hour, int day, int month, int year) {
        String sqlDelete = "DELETE FROM " + SetsMetaData.UP_TABLE + " WHERE "
                + SetsMetaData.UP_DAY_KEY + " = " + day + " AND "
                + SetsMetaData.UP_MONTH_KEY + " = " + month + " AND "
                + SetsMetaData.UP_YEAR_KEY + " = " + year + " AND "
                + SetsMetaData.UP_HOUR_KEY + " = " + hour + " AND "
                + SetsMetaData.UP_MINUTE_KEY + " = " + minute;
        myDB.execSQL(sqlDelete);
    }

    public void deleteUp(String tweet, int minute, int hour, int day,
                         int month, int year) {
        myDB.delete(SetsMetaData.UP_TABLE, SetsMetaData.UP_TWEET_KEY
                        + "=? AND " + SetsMetaData.UP_MINUTE_KEY + " = " + minute
                        + " AND " + SetsMetaData.UP_HOUR_KEY + " = " + hour + " AND "
                        + SetsMetaData.UP_DAY_KEY + " = " + day + " AND "
                        + SetsMetaData.UP_MONTH_KEY + " = " + month + " AND "
                        + SetsMetaData.UP_YEAR_KEY + " = " + year,
                new String[]{tweet});
    }

    public void clearUP() {
        myDB.execSQL("DROP TABLE IF EXISTS " + SetsMetaData.UP_TABLE);
        myDB.execSQL(TOSEND_TABLE_CREATE);
    }

    public ArrayList<String> getAllProgrammedTweets() {
        ArrayList<String> tweets = new ArrayList<String>();
        String sqlQuery = "SELECT " + SetsMetaData.UP_TWEET_KEY + " FROM "
                + SetsMetaData.UP_TABLE;
        Cursor cursor = myDB.rawQuery(sqlQuery, null);

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            tweets.add(cursor.getString(0));
        }

        cursor.close();
        return tweets;
    }

    public SQLiteDatabase getMyDB() {
        return this.myDB;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name,
                              CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TOSEND_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }
}
