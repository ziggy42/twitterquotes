package com.andreapivetta.tweetbooster.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andreapivetta.tweetbooster.QuotesActivity;
import com.andreapivetta.tweetbooster.QuotesActivity.ProgramTimeDialogFragment;
import com.andreapivetta.tweetbooster.R;

import java.util.ArrayList;


public class TweetsAdapter extends ArrayAdapter<String> {

    private static final String MY_PREFERENCES = "MyPref";
    private static final String TEXT_DATA_KEY = "quote";

    private Context context;
    private ArrayList<String> tweetsArrayList;
    private int resource;

    public TweetsAdapter(Context context, int resource,
                         ArrayList<String> tweetsArrayList) {
        super(context, resource, tweetsArrayList);

        this.context = context;
        this.tweetsArrayList = tweetsArrayList;
        this.resource = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(resource, parent, false);

        TextView tweetRowTextView = (TextView) rowView
                .findViewById(R.id.tweetRowTextView);
        ImageButton tweetRowImageButton = (ImageButton) rowView
                .findViewById(R.id.tweetRowImageButton);

        tweetRowTextView.setText(tweetsArrayList.get(position));

        tweetRowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences mSharedPreferences = context
                        .getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

                mSharedPreferences.edit()
                        .putString(TEXT_DATA_KEY, tweetsArrayList.get(position))
                        .apply();

                DialogFragment dialog = new ProgramTimeDialogFragment();
                dialog.show(((QuotesActivity) context).getSupportFragmentManager(),
                        "AddTweetDialogFragment");
            }
        });

        tweetRowImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .setAction(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_TEXT, tweetsArrayList.get(position))
                        .setType("text/plain");

                context.startActivity(
                        Intent.createChooser(sendIntent, "Share this quote"));
            }
        });

        return rowView;
    }
}
