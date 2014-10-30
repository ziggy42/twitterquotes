package com.andreapivetta.tweetbooster;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.andreapivetta.tweetbooster.adapters.TweetCardsAdapter;
import com.andreapivetta.tweetbooster.database.Repository;
import com.andreapivetta.tweetbooster.database.TweetsDatabaseManager;
import com.andreapivetta.tweetbooster.twitter.UpdateTwitterStatus;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Calendar;


public class QuotesActivity extends ActionBarActivity {

    private static final String MY_PREFERENCES = "MyPref";
    private static final String TEXT_DATA_KEY = "quote";

    private String category;
    private String author;
    private ArrayList<String> quotesArrayList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.quotesToolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        Bundle extras = getIntent().getExtras();
        category = extras.getString("CATEGORY");
        author = extras.getString("AUTHOR");

        quotesArrayList.clear();
        getSupportActionBar().setTitle(author);
        getSupportActionBar().setSubtitle(category);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setUpQuotes();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.quotesRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new TweetCardsAdapter(quotesArrayList, this));

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).
                        addTestDevice("EB8CB71D9FE394E0DCCBF26188BED5D7").
                        addTestDevice("F466F2402B12B6EE39AEEB527D829E2A").
                        addTestDevice("A272A918ED2BBA9EC2138C622D7212D0").
                        addTestDevice("C9F505E68A8DADEB86EF831BD769444D").
                        build();
        adView.loadAd(adRequest);
    }

    private void setUpQuotes() {
        Repository repo = Repository.getInstance(this);
        SQLiteDatabase db = repo.getWritableDatabase();

        String sqlQuery = "SELECT quote FROM " + category + " WHERE source = '"
                + author + "'";
        Cursor cursor = db.rawQuery(sqlQuery, null);

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            quotesArrayList.add(cursor.getString(0));
        }

        cursor.close();
        repo.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!category.equals("Funny"))
            getMenuInflater().inflate(R.menu.wikipedia_menu, menu);
        getMenuInflater().inflate(R.menu.random_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.wikipedia_redirect_menu) {
            startActivity((new Intent(Intent.ACTION_VIEW)).setData(Uri.parse("http://en.wikipedia.org/wiki/" + author)));
            return true;
        } else {
            if (id == R.id.random_send_menu)
                new UpdateTwitterStatus(QuotesActivity.this).execute(quotesArrayList.get((int) (Math.random() * quotesArrayList.size())));
        }
        return super.onOptionsItemSelected(item);
    }

    public static class ProgramTimeDialogFragment extends DialogFragment {

        private DatePicker datePicker;
        private TimePicker timePicker;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.set_time);

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogView = inflater
                    .inflate(R.layout.dialog_time_picker, null);

            builder.setView(dialogView);

            datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker1);
            timePicker = (TimePicker) dialogView.findViewById(R.id.timePicker1);

            SharedPreferences prefs = getActivity().getSharedPreferences(
                    MY_PREFERENCES, Context.MODE_PRIVATE);
            final String currentTweet = prefs.getString(TEXT_DATA_KEY,
                    "Tweet Tweet!");

            builder.setPositiveButton(R.string.done,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            int day = datePicker.getDayOfMonth();
                            int month = datePicker.getMonth();
                            int year = datePicker.getYear();
                            int hour = timePicker.getCurrentHour();
                            int minute = timePicker.getCurrentMinute();

                            if (checkIfDateAvailable(year, month, day, hour,
                                    minute)) {

                                TweetsDatabaseManager utDB = new TweetsDatabaseManager(
                                        getActivity());
                                utDB.open();
                                utDB.insertUp(currentTweet, minute, hour, day,
                                        month, year);
                                utDB.close();
                            } else {
                                Toast.makeText(
                                        getActivity(),
                                        "Nice try :/. you can't send tweets in the past",
                                        Toast.LENGTH_SHORT).show();
                            }

                            ProgramTimeDialogFragment.this.getDialog().cancel();


                        }

                    }).setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            ProgramTimeDialogFragment.this.getDialog().cancel();
                        }
                    });

            builder.setNeutralButton(getResources().getString(R.string.send_now),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new UpdateTwitterStatus(getActivity())
                                    .execute(currentTweet);

                            ProgramTimeDialogFragment.this.getDialog().cancel();
                        }
                    });

            return builder.create();
        }

    }

    private static boolean checkIfDateAvailable(int year, int month, int day,
                                                int hour, int minute) {
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTimeInMillis(System.currentTimeMillis());

        if (year > currentTime.get(Calendar.YEAR))
            return true;
        else {
            if (year == currentTime.get(Calendar.YEAR)) {
                if (month > currentTime.get(Calendar.MONTH))
                    return true;
                else {
                    if (month == currentTime.get(Calendar.MONTH)) {
                        if (day > currentTime.get(Calendar.DAY_OF_MONTH))
                            return true;
                        else {
                            if (day == currentTime.get(Calendar.DAY_OF_MONTH)) {
                                if (hour > currentTime
                                        .get(Calendar.HOUR_OF_DAY))
                                    return true;
                                else {
                                    if (hour == currentTime
                                            .get(Calendar.HOUR_OF_DAY)) {
                                        if (minute > currentTime
                                                .get(Calendar.MINUTE))
                                            return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

}
