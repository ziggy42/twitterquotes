package com.andreapivetta.tweetbooster.adapters;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.andreapivetta.tweetbooster.QuotesActivity;
import com.andreapivetta.tweetbooster.R;
import com.andreapivetta.tweetbooster.database.TweetsDatabaseManager;
import com.andreapivetta.tweetbooster.twitter.UpdateTwitterStatus;

import java.util.ArrayList;
import java.util.Calendar;

public class TweetCardsAdapter extends RecyclerView.Adapter<TweetCardsAdapter.ViewHolder> {

    static String currentQuote;
    private ArrayList<String> mDataset;
    private Context context;

    public TweetCardsAdapter(ArrayList<String> tweets, Context context) {
        this.mDataset = tweets;
        this.context = context;
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

    @Override
    public TweetCardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tweet_row, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tweetRowTextView.setText(mDataset.get(position));
        holder.tweetRowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentQuote = mDataset.get(position);
                DialogFragment dialog = new ProgramTimeDialogFragment();
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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tweetRowTextView;
        public ImageButton tweetRowImageButton;

        public ViewHolder(View container) {
            super(container);

            this.tweetRowTextView = (TextView) container.findViewById(R.id.tweetRowTextView);
            this.tweetRowImageButton = (ImageButton) container.findViewById(R.id.tweetRowImageButton);
        }
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

            datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker1);
            timePicker = (TimePicker) dialogView.findViewById(R.id.timePicker1);
            datePicker.setMinDate(System.currentTimeMillis() - 1000);
            final String currentTweet = currentQuote;

            builder.setView(dialogView)
                    .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
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
                    })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    ProgramTimeDialogFragment.this.getDialog().cancel();
                                }
                            })
                    .setNeutralButton(getResources().getString(R.string.send_now),
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
}
