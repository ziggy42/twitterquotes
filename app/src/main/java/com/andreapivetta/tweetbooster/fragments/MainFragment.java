package com.andreapivetta.tweetbooster.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.andreapivetta.tweetbooster.R;
import com.andreapivetta.tweetbooster.database.Repository;
import com.andreapivetta.tweetbooster.twitter.Tweet;
import com.andreapivetta.tweetbooster.twitter.TwitterKs;
import com.andreapivetta.tweetbooster.twitter.UpdateTwitterStatus;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;


public class MainFragment extends Fragment implements SensorEventListener {

    private Boolean bool = false;
    private Boolean bool1 = false;
    private InterstitialAd interstitial;
    private long lastUpdate;
    private Vibrator v;

    private TextView tweetTextView, sourceTextView;
    private ImageButton sendThisImageButton, changeThisImageButton2;

    private ArrayList<Tweet> tweetList = new ArrayList<Tweet>();
    private SharedPreferences mypref;

    private boolean enabled = false;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container,
                false);

        interstitial = new InterstitialAd(getActivity());
        interstitial.setAdUnitId(TwitterKs.INTESTINAL_AD);
        AdRequest adRequest = new AdRequest.Builder().
                addTestDevice(AdRequest.DEVICE_ID_EMULATOR).
                addTestDevice("EB8CB71D9FE394E0DCCBF26188BED5D7").
                addTestDevice("A272A918ED2BBA9EC2138C622D7212D0").
                addTestDevice("C9F505E68A8DADEB86EF831BD769444D").
                addTestDevice("EEC1F897DA0F5D96B97DD79FA09522C6").
                build();
        interstitial.loadAd(adRequest);

        mypref = getActivity().getSharedPreferences("MyPref", 0);

        v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdate = System.currentTimeMillis();

        tweetTextView = (TextView) rootView.findViewById(R.id.tweetTextView);
        sourceTextView = (TextView) rootView.findViewById(R.id.sourceTextView);
        sendThisImageButton = (ImageButton) rootView.findViewById(R.id.sendThisImageButton);
        changeThisImageButton2 = (ImageButton) rootView.findViewById(R.id.changeThisImageButton2);

        getAllTweetsInDatabase();

        if(savedInstanceState != null) {
            tweetTextView.setText(savedInstanceState.getString("Tweet"));
            sourceTextView.setText(savedInstanceState.getString("SOURCE"));
        } else {
            setUpDisplayedTweet();
        }

        setupOnClickListeners();

        return rootView;
    }


    private void setupOnClickListeners() {
        sendThisImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mypref.getBoolean("pref_key_dialog_show", true)) {
                    sendTweet(tweetTextView.getText().toString());
                    setUpDisplayedTweet();
                } else {

                    (new AlertDialog.Builder(getActivity())).setTitle(getResources().getString(R.string.send_now))
                            .setMultiChoiceItems(R.array.send_tweet_without_ask, null, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                    if (b)
                                        mypref.edit().putBoolean("pref_key_dialog_show", false).apply();
                                }
                            })
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    sendTweet(tweetTextView.getText().toString());
                                    setUpDisplayedTweet();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            })
                            .create()
                            .show();
                }

            }
        });

        changeThisImageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpDisplayedTweet();
            }
        });
    }

    void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }

    void sendTweet(String status) {

        if (status.trim().length() > 0) {
            new UpdateTwitterStatus(getActivity()).execute(status);

            Toast.makeText(getActivity().getApplicationContext(),
                    "Tweet Sent", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Please enter status message", Toast.LENGTH_SHORT).show();
        }


        int count = mypref.getInt("SEND_COUNT", 0);
        if (count == 30) {
            (new AlertDialog.Builder(getActivity())).setTitle(getResources().getString(R.string.rate_this_app))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=com.andreapivetta.tweetbooster"));
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton(R.string.not_now, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    })
                    .create().show();

            mypref.edit().putInt("SEND_COUNT", 0).apply();
        } else {
            mypref.edit().putInt("SEND_COUNT", count + 1).apply();
        }
    }

    void getAllTweetsInDatabase() {
        Repository repo = Repository.getInstance(getActivity());
        SQLiteDatabase db = repo.getWritableDatabase();

        for (String category : getResources().getStringArray(R.array.categories_query)) {
            Cursor cursor = db.rawQuery("SELECT quote,source FROM " + category, null);

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();

                tweetList.add(new Tweet(cursor.getString(0),
                        cursor.getString(1)));
            }

            cursor.close();
        }

        repo.close();
    }

    private void setUpDisplayedTweet() {
        Tweet tmp = tweetList.get((int) (Math.random() * tweetList.size()));
        tweetTextView.setText(tmp.tweet);
        sourceTextView.setText(tmp.source);

        int count = mypref.getInt("show_ad", 0);
        if (count == 10) {
            displayInterstitial();
            mypref.edit().putInt("show_ad", 0).apply();

            AdRequest adRequest = new AdRequest.Builder().
                    addTestDevice(AdRequest.DEVICE_ID_EMULATOR).
                    addTestDevice("EB8CB71D9FE394E0DCCBF26188BED5D7").
                    addTestDevice("A272A918ED2BBA9EC2138C622D7212D0").
                    addTestDevice("C9F505E68A8DADEB86EF831BD769444D").
                    addTestDevice("EEC1F897DA0F5D96B97DD79FA09522C6").
                    build();
            interstitial.loadAd(adRequest);
        } else {
            mypref.edit().putInt("show_ad", count + 1).apply();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (enabled) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float[] values = event.values;
                // Movement
                float x = values[0];
                float y = values[1];
                float z = values[2];

                float accelationSquareRoot = (x * x + y * y + z * z)
                        / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);

                long actualTime = System.currentTimeMillis();
                if (accelationSquareRoot >= 5.5) {
                    bool1 = false;
                    if (!bool) {
                        if (actualTime - lastUpdate < 1000)
                            return;

                        lastUpdate = actualTime;

                        v.vibrate(500);
                        setUpDisplayedTweet();

                        bool = true;
                    }
                } else {
                    bool = false;

                    if (!bool1) {
                        if (actualTime - lastUpdate < 4000) {
                            return;
                        }
                        lastUpdate = actualTime;
                        bool1 = true;
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        enabled = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        enabled = false;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("SOURCE", sourceTextView.getText().toString());
        savedInstanceState.putString("Tweet", tweetTextView.getText().toString());
    }

}
