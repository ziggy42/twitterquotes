package com.andreapivetta.tweetbooster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.andreapivetta.tweetbooster.background.StartupService;
import com.andreapivetta.tweetbooster.fragments.CategoriesFirstFragment;
import com.andreapivetta.tweetbooster.fragments.MainFragment;
import com.andreapivetta.tweetbooster.fragments.ProgrammedTweetsFragment;
import com.andreapivetta.tweetbooster.fragments.SettingsFragment;
import com.andreapivetta.tweetbooster.twitter.TwitterKs;
import com.andreapivetta.tweetbooster.twitter.UpdateTwitterStatus;


import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private static SharedPreferences mSharedPreferences;
    private static final String PREF_SELECTED_INDEX = "SELECTED_POSITION";
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = getSharedPreferences("MyPref", 0);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        startService(new Intent(getApplicationContext(), StartupService.class));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isTwitterLoggedInAlready()) {
            mNavigationDrawerFragment = (NavigationDrawerFragment)
                    getFragmentManager().findFragmentById(R.id.navigation_drawer);
            mTitle = getTitle();

            mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

            if (mSharedPreferences.getBoolean("FIRST_LAUNCH", true)) {
                createPayWithAClickDialog();
                mSharedPreferences.edit().putBoolean("FIRST_LAUNCH", false).apply();
            }
        } else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    private void createPayWithAClickDialog() {
        (new AlertDialog.Builder(this)).setTitle(getResources().getString(R.string.pay_with_a_tweet))
                .setMessage(getResources().getString(R.string.promo_message))
                .setPositiveButton(R.string.promo_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new UpdateTwitterStatus(getApplicationContext()).execute("What do you think about this? https://play.google.com/store/apps/details?id=com.andreapivetta.tweetbooster #TwitterQuotesJoinTheEmpire");
                    }
                })
                .setIcon(getResources().getDrawable(R.drawable.ic_storm_trooper))
                .setCancelable(false)
                .create()
                .show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        mSharedPreferences.edit().putInt(PREF_SELECTED_INDEX, position).apply();

        switch (position) {
            case 0:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new MainFragment()).commit();
                setTitle(getString(R.string.app_name));
                break;
            case 1:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new CategoriesFirstFragment()).commit();
                setTitle(getResources().getStringArray(R.array.items_array)[position]);
                break;
            case 2:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new ProgrammedTweetsFragment()).commit();
                setTitle(getResources().getStringArray(R.array.items_array)[position]);
                break;
            case 3:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
                setTitle(getResources().getStringArray(R.array.items_array)[position]);
                break;
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;

        try {
            getSupportActionBar().setTitle(mTitle);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isTwitterLoggedInAlready()) {
            try {
                if (!mNavigationDrawerFragment.isDrawerOpen()) {

                    if (mSharedPreferences.getInt(PREF_SELECTED_INDEX, 0) == 0) {
                        getMenuInflater().inflate(R.menu.wikipedia_menu, menu);
                    }

                    return true;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.wikipedia_redirect_menu) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("http://en.wikipedia.org/wiki/" + mSharedPreferences.getString("SOURCE", "LOL")));
            startActivity(i);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isTwitterLoggedInAlready() {
        return mSharedPreferences.getBoolean(TwitterKs.PREF_KEY_TWITTER_LOGIN, false);
    }
}
