package com.andreapivetta.tweetbooster.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andreapivetta.tweetbooster.R;
import com.andreapivetta.tweetbooster.database.TweetsDatabaseManager;
import com.andreapivetta.tweetbooster.twitter.Tweet;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class ProgrammedTweetsFragment extends Fragment {

    private CaldroidFragment caldroidFragment;
    private ViewPager tweetsPager;
    private Calendar c;
    private ArrayList<Tweet> tweets = new ArrayList<Tweet>();
    private ArrayList<Tweet> todayTweets = new ArrayList<Tweet>();
    private ArrayList<String> bluDates = new ArrayList<String>();
    private Date previousSelectedDate;
    private DateFormat formatter = new SimpleDateFormat("yyyyMMdd");

    public ProgrammedTweetsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_programmed_tweets, container,
                false);

        caldroidFragment = new CaldroidFragment();

        c = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        previousSelectedDate = cal.getTime();

        String month = (cal.get(Calendar.MONTH) + 1 < 10) ? ("0" + (cal.get(Calendar.MONTH) + 1)) : ("" + (cal.get(Calendar.MONTH) + 1));
        caldroidFragment.setMinDateFromString(cal.get(Calendar.YEAR) + month + "01", "yyyyMMdd");

        Bundle args = new Bundle();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.calendar_container_frame_layout, caldroidFragment).commit();

        tweetsPager = (ViewPager) rootView.findViewById(R.id.tweetsPager);

        setUpTweets();
        setListeners();
        setUpTodayTweets();

        caldroidFragment.setBackgroundResourceForDate(R.color.barbie_pink, c.getTime());
        return rootView;
    }

    private void setListeners() {
        caldroidFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                c.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

                bluDates.clear();
                setUpTweets();

                if (bluDates.indexOf(formatter.format(previousSelectedDate)) < 0)
                    caldroidFragment.setBackgroundResourceForDate(R.color.white, previousSelectedDate);
                else
                    caldroidFragment.setBackgroundResourceForDate(R.color.colorPrimary, previousSelectedDate);

                caldroidFragment.setBackgroundResourceForDate(R.color.barbie_pink, c.getTime());

                previousSelectedDate = c.getTime();
                setUpTodayTweets();

                caldroidFragment.refreshView();
            }
        });
    }

    private void setUpTodayTweets() {
        todayTweets.clear();

        for (Tweet tweet : tweets) {
            if (c.get(Calendar.DAY_OF_MONTH) == tweet.getDay()) {
                if ((c.get(Calendar.MONTH)) == tweet.getMonth()) {
                    if ((c.get(Calendar.YEAR)) == tweet.getYear())
                        todayTweets.add(tweet);
                }
            }
        }

        tweetsPager.setAdapter(new TweetsSlidePagerAdapter(getActivity().getSupportFragmentManager()));
    }

    private void setUpTweets() {
        tweets.clear();
        TweetsDatabaseManager utDB = new TweetsDatabaseManager(getActivity());
        utDB.open();
        SQLiteDatabase myDB = utDB.getMyDB();

        Cursor cursor = myDB.rawQuery("SELECT tweet,day,month,year,hour,minute FROM tosend", null);

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();

            Tweet temp = new Tweet(cursor.getString(0),
                    cursor.getInt(5), cursor.getInt(4), cursor.getInt(1),
                    cursor.getInt(2), cursor.getInt(3));

            try {
                String date = "" + cursor.getInt(3);

                date = ((cursor.getInt(2) + 1) < 10) ? (date + "0" + (cursor.getInt(2) + 1)) : (date + (cursor.getInt(2) + 1));
                date = (cursor.getInt(1) < 10) ? (date + "0" + cursor.getInt(1)) : (date + cursor.getInt(1));

                bluDates.add(date);
                caldroidFragment.setBackgroundResourceForDate(R.color.caldroid_holo_blue_dark, formatter.parse(date));
            } catch (ParseException e) {
                Log.i("EXCEPTION", "invalid date format");
            }

            tweets.add(temp);
        }

        utDB.close();
        Collections.sort(tweets);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class TweetsSlidePagerAdapter extends FragmentStatePagerAdapter {

        public TweetsSlidePagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return SingleTweetFragment.create(getCount(), position, todayTweets.get(position));
        }

        @Override
        public int getCount() {
            return todayTweets.size();
        }
    }
}
