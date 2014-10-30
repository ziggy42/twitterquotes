package com.andreapivetta.tweetbooster.adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andreapivetta.tweetbooster.QuotesActivity;
import com.andreapivetta.tweetbooster.R;

import java.util.ArrayList;

public class TweetCardsAdapter extends RecyclerView.Adapter<TweetCardsAdapter.ViewHolder>  {

    private ArrayList<String> mDataset;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tweetRowTextView;
        public ImageButton tweetRowImageButton;

        public ViewHolder(View container) {
            super(container);

            this.tweetRowTextView = (TextView) container.findViewById(R.id.tweetRowTextView);
            this.tweetRowImageButton = (ImageButton) container.findViewById(R.id.tweetRowImageButton);
        }
    }

    public TweetCardsAdapter(ArrayList<String> tweets, Context context) {
        this.mDataset = tweets;
        this.context = context;
    }

    @Override
    public TweetCardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tweet_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tweetRowTextView.setText(mDataset.get(position));
        holder.tweetRowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mSharedPreferences = context
                        .getSharedPreferences("MyPref", Context.MODE_PRIVATE);

                mSharedPreferences.edit()
                        .putString("quote", mDataset.get(position))
                        .apply();

                DialogFragment dialog = new QuotesActivity.ProgramTimeDialogFragment();
                dialog.show(((QuotesActivity) context).getSupportFragmentManager(),
                        "AddTweetDialogFragment");
            }
        });

        holder.tweetRowImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .setAction(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_TEXT, mDataset.get(position))
                        .setType("text/plain");

                context.startActivity(
                        Intent.createChooser(sendIntent, "Share this quote"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
