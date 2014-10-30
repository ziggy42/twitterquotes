package com.andreapivetta.tweetbooster.background;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;


public class StartupService extends IntentService {

    public StartupService() {
        super("StartupService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressWarnings("static-access")
    @Override
    protected void onHandleIntent(Intent intent) {
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent startupIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                startupIntent, 0);
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(System.currentTimeMillis());
        time.add(Calendar.MILLISECOND, 60000);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, time.MILLISECOND,
                60000, pendingIntent);
    }

}