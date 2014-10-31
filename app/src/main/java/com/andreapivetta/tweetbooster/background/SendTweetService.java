package com.andreapivetta.tweetbooster.background;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.andreapivetta.tweetbooster.database.TweetsDatabaseManager;
import com.andreapivetta.tweetbooster.twitter.UpdateTwitterStatus;

import java.util.Calendar;


public class SendTweetService extends IntentService {

    public SendTweetService() {
        super("SendTweetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTimeInMillis(System.currentTimeMillis());

        TweetsDatabaseManager utDB = new TweetsDatabaseManager(
                getApplicationContext());
        utDB.open();
        SQLiteDatabase myDB = utDB.getMyDB();
        String sqlQuery = "SELECT tweet,day,month,year,hour,minute FROM tosend WHERE day = "
                + currentTime.get(Calendar.DAY_OF_MONTH)
                + " AND month = "
                + currentTime.get(Calendar.MONTH)
                + " AND year = "
                + currentTime.get(Calendar.YEAR)
                + " AND hour = "
                + currentTime.get(Calendar.HOUR_OF_DAY)
                + " AND minute = "
                + currentTime.get(Calendar.MINUTE);
        Cursor cursor = myDB.rawQuery(sqlQuery, null);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            new UpdateTwitterStatus(getApplicationContext()).execute(cursor.getString(0));
        }

        myDB.execSQL("DELETE FROM tosend WHERE day = "
                + currentTime.get(Calendar.DAY_OF_MONTH)
                + " AND month = "
                + currentTime.get(Calendar.MONTH)
                + " AND year = "
                + currentTime.get(Calendar.YEAR)
                + " AND hour = "
                + currentTime.get(Calendar.HOUR_OF_DAY)
                + " AND minute = "
                + currentTime.get(Calendar.MINUTE));

        utDB.close();
    }
}
