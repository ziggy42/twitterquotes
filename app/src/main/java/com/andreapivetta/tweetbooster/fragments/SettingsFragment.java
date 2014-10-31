package com.andreapivetta.tweetbooster.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.widget.Toast;

import com.andreapivetta.tweetbooster.R;
import com.andreapivetta.tweetbooster.database.TweetsDatabaseManager;
import com.andreapivetta.tweetbooster.twitter.TwitterKs;

import me.piebridge.android.preference.PreferenceFragment;


public class SettingsFragment extends PreferenceFragment {
    private Preference logOutPreference;
    private Preference clearCalendarPreference;
    private CheckBoxPreference showDialogCheckBox;
    private CheckBoxPreference animationCheckBox;
    private Preference prefKeyRateApp;
    private Preference prefKeyShareApp;
    private SharedPreferences mypref;

    public SettingsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        logOutPreference = findPreference("pref_key_twitter_logout");
        clearCalendarPreference = findPreference("pref_key_calendar_clear");
        showDialogCheckBox = (CheckBoxPreference) findPreference("pref_key_dialog_show");
        prefKeyRateApp = findPreference("pref_key_rate_app");
        prefKeyShareApp = findPreference("pref_key_share_app");
        animationCheckBox = (CheckBoxPreference) findPreference("pref_key_animation");

        mypref = getActivity().getSharedPreferences("MyPref", 0);
        showDialogCheckBox.setChecked(mypref.getBoolean("pref_key_dialog_show", true));
        animationCheckBox.setChecked(mypref.getBoolean("Animation", true));

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        logOutPreference
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference preference) {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                getActivity());
                        alertDialog
                                .setTitle(getResources().getString(R.string.logout_title_dialog));
                        alertDialog.setPositiveButton(getResources().getString(R.string.yes),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        mypref.edit().remove(TwitterKs.PREF_KEY_OAUTH_TOKEN)
                                                .remove(TwitterKs.PREF_KEY_OAUTH_SECRET)
                                                .remove(TwitterKs.PREF_KEY_TWITTER_LOGIN)
                                                .remove(TwitterKs.PREF_KEY_PICTURE_URL)
                                                .apply();

                                        Toast.makeText(
                                                getActivity()
                                                        .getApplicationContext(),
                                                "Log Out Done.",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        getActivity().finish();
                                    }
                                }
                        );

                        alertDialog.setNegativeButton(getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                    }
                                }
                        );

                        alertDialog.show();

                        return false;
                    }
                });

        showDialogCheckBox.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                if (showDialogCheckBox.isChecked()) {
                    mypref.edit().putBoolean("pref_key_dialog_show", true).apply();
                } else {
                    mypref.edit().putBoolean("pref_key_dialog_show", false).apply();
                }
                return true;
            }
        });

        animationCheckBox.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                if (animationCheckBox.isChecked()) {
                    mypref.edit().putBoolean("Animation", true).apply();
                } else {
                    mypref.edit().putBoolean("Animation", false).apply();
                }

                return true;
            }
        });

        clearCalendarPreference
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference preference) {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                getActivity());
                        alertDialog
                                .setTitle(getString(R.string.delete_programmed_tweets));
                        alertDialog.setPositiveButton(getString(R.string.yes),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        TweetsDatabaseManager utDM = new TweetsDatabaseManager(
                                                getActivity());
                                        utDM.open();
                                        utDM.clearUP();
                                        utDM.close();

                                        Toast.makeText(
                                                getActivity()
                                                        .getApplicationContext(),
                                                "Programmed tweets deleted",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                }
                        );

                        alertDialog.setNegativeButton(getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                    }
                                }
                        );

                        alertDialog.show();

                        return false;
                    }
                });

        prefKeyRateApp
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference preference) {

                        Intent i = new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=com.andreapivetta.tweetbooster"));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);

                        return false;
                    }
                });

        prefKeyShareApp
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent
                                .putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Check out this app!! http://play.google.com/store/apps/details?id=com.andreapivetta.tweetbooster");

                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent,
                                "Share this App!"));

                        return false;
                    }
                });

    }
}
