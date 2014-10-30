package com.andreapivetta.tweetbooster.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andreapivetta.tweetbooster.R;
import com.andreapivetta.tweetbooster.database.TweetsDatabaseManager;
import com.andreapivetta.tweetbooster.twitter.Tweet;

/**
 * Questo è il fragment da usare sotto il calendario
 * per mostrare un tweet
 */
public class SingleTweetFragment extends Fragment {

    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";
    public static final String ARG_TOTAL = "total";
    public static final String ARG_HOUR = "hour";
    public static final String ARG_DAY = "day";
    public static final String ARG_MONTH = "month";
    public static final String ARG_YEAR = "year";
    public static final String ARG_MINUTE = "minute";
    public static final String ARG_TWEET = "text";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;
    private int total;
    private int hour;
    private int minute;
    private int day;
    private int month;
    private int year;
    private String tweet;

    private TextView dateTextView;
    private ImageButton deleteTweetImageButton;
    private Button restoreButton;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static SingleTweetFragment create(int total, int pageNumber, Tweet stodayTweet) {
        SingleTweetFragment fragment = new SingleTweetFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putInt(ARG_TOTAL, total);
        args.putInt(ARG_HOUR, stodayTweet.getHour());
        args.putInt(ARG_MINUTE, stodayTweet.getMinute());
        args.putInt(ARG_DAY, stodayTweet.getDay());
        args.putInt(ARG_MONTH, stodayTweet.getMonth());
        args.putInt(ARG_YEAR, stodayTweet.getYear());
        args.putString(ARG_TWEET, stodayTweet.getTweet());
        fragment.setArguments(args);
        return fragment;
    }

    public SingleTweetFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        total = getArguments().getInt(ARG_TOTAL);
        hour = getArguments().getInt(ARG_HOUR);
        minute = getArguments().getInt(ARG_MINUTE);
        tweet = getArguments().getString(ARG_TWEET);
        day = getArguments().getInt(ARG_DAY);
        month = getArguments().getInt(ARG_MONTH);
        year = getArguments().getInt(ARG_YEAR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        final ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_single_tweet, container, false);

        TextView singleTweetTextView = (TextView) rootView.findViewById(R.id.singleTweetTextView);
        dateTextView = (TextView) rootView.findViewById(R.id.dateTextView);
        TextView countTextView = (TextView) rootView.findViewById(R.id.countTextView);
        deleteTweetImageButton = (ImageButton) rootView.findViewById(R.id.deleteTweetImageButton);

        singleTweetTextView.setText(tweet);

        String hourToSend = (minute < 10) ? "0" + minute : minute + "";

        dateTextView.setText(hour + " : " + hourToSend);
        countTextView.setText((mPageNumber + 1) + "/" + total);

        restoreButton = (Button) rootView.findViewById(R.id.restoreButton);

        deleteTweetImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TweetsDatabaseManager tdbm = new TweetsDatabaseManager(getActivity());
                tdbm.open();
                tdbm.deleteUp(tweet, minute, hour, day, month, year);
                tdbm.close();
                rootView.setBackgroundColor(getResources().getColor(R.color.caldroid_darker_gray));
                dateTextView.setVisibility(View.GONE);
                deleteTweetImageButton.setVisibility(View.GONE);
                restoreButton.setVisibility(View.VISIBLE);
            }
        });


        restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootView.setBackgroundColor(getResources().getColor(R.color.white));
                dateTextView.setVisibility(View.VISIBLE);
                deleteTweetImageButton.setVisibility(View.VISIBLE);
                restoreButton.setVisibility(View.GONE);

                TweetsDatabaseManager utDB = new TweetsDatabaseManager(
                        getActivity());
                utDB.open();
                utDB.insertUp(tweet, minute, hour, day,
                        month, year);
                utDB.close();
            }
        });

        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

}
