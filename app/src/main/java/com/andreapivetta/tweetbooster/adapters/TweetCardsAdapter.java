package com.andreapivetta.tweetbooster.adapters;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.andreapivetta.tweetbooster.QuotesActivity;
import com.andreapivetta.tweetbooster.R;
import com.andreapivetta.tweetbooster.database.TweetsDatabaseManager;
import com.andreapivetta.tweetbooster.twitter.UpdateTwitterStatus;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

        final Calendar c = Calendar.getInstance();
        int day, month, yar, hh, mm;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogView = View.inflate(getActivity(), R.layout.dialog_time_picker, null);

            final LinearLayout dateLinearLayout = (LinearLayout) dialogView.findViewById(R.id.selectDateLL);
            final LinearLayout timeLinearLayout = (LinearLayout) dialogView.findViewById(R.id.selectHourLL);
            final TextView dateTextView = (TextView) dialogView.findViewById(R.id.dateTextView);
            final TextView timeTextView = (TextView) dialogView.findViewById(R.id.timeTextView);
            final String currentTweet = currentQuote;

            timeLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog tpd = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            hh = hourOfDay;
                            mm = minute;

                            timeTextView.setText(hh + ":" + mm);
                        }
                    }, 8, 0, false);
                    tpd.show();
                }
            });

            dateLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            day = dayOfMonth;
                            yar = year;
                            month = monthOfYear;

                            dateTextView.setText((month+1) + "/" + day + "/" + yar);
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                    dpd.show();
                }
            });

            builder.setView(dialogView)
                    .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            if (dateTextView.getText().length() == 0 || timeTextView.getText().length() == 0) {
                                Toast.makeText(
                                        getActivity(),
                                        "All fields are required",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                if (checkIfDateAvailable(yar, month, day, hh,
                                        mm)) {

                                    TweetsDatabaseManager utDB = new TweetsDatabaseManager(
                                            getActivity());
                                    utDB.open();
                                    utDB.insertUp(currentTweet, mm, hh, day,
                                            month, yar);
                                    utDB.close();
                                } else {
                                    Toast.makeText(
                                            getActivity(),
                                            "Nice try :/. you can't send tweets in the past",
                                            Toast.LENGTH_SHORT).show();
                                }
                        }
                    }
        })
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(getResources().getString(R.string.send_now),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new UpdateTwitterStatus(getActivity())
                                            .execute(currentTweet);
                                }
                            });

            return builder.create();
        }

    }
}
