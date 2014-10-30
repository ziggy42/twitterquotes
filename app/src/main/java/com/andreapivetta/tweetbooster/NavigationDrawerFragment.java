package com.andreapivetta.tweetbooster;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.andreapivetta.tweetbooster.adapters.DrawerAdapter;
import com.andreapivetta.tweetbooster.adapters.Draweritem;
import com.andreapivetta.tweetbooster.twitter.TwitterKs;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;


public class NavigationDrawerFragment extends Fragment {

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private NavigationDrawerCallbacks mCallbacks;
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private final String PREF_KEY_RECHECK = "recheck_profile_photo";

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout insideDrawer = (RelativeLayout) inflater.inflate(R.layout.fragment_custom_drawer, container, false);
        mDrawerListView = (ListView) insideDrawer.findViewById(R.id.listView);

        ArrayList<Draweritem> drawerItems = new ArrayList<Draweritem>();
        TypedArray navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        drawerItems.add(new Draweritem(getResources().getStringArray(R.array.items_array)[0], navMenuIcons.getResourceId(0, -1)));
        drawerItems.add(new Draweritem(getResources().getStringArray(R.array.items_array)[1], navMenuIcons.getResourceId(1, -1)));
        drawerItems.add(new Draweritem(getResources().getStringArray(R.array.items_array)[2], navMenuIcons.getResourceId(2, -1)));
        drawerItems.add(new Draweritem(getResources().getStringArray(R.array.items_array)[3], navMenuIcons.getResourceId(3, -1)));

        navMenuIcons.recycle();

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mDrawerListView.setAdapter(new DrawerAdapter(getActivity(), drawerItems));

        ImageView pictureImage = (ImageView) insideDrawer.findViewById(R.id.profilePicture);
        new DownloadImageTask(pictureImage).execute("http://images6.fanpop.com/image/photos/34200000/more-dumb-images-of-philip-j-fry-futurama-34257101-1440-900.png");

        pictureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager manager = getActivity().getPackageManager();
                try {
                    Intent i = manager.getLaunchIntentForPackage("com.twitter.android");
                    if (i == null)
                        throw new PackageManager.NameNotFoundException();
                    i.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        return insideDrawer;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                mDrawerLayout,
                ((MainActivity) getActivity()).toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()

                if (getActivity().getSharedPreferences("MyPref", 0).getBoolean(PREF_KEY_RECHECK, false)) {
                    ImageView profilePc = (ImageView) mDrawerLayout.findViewById(R.id.profilePicture);
                    new DownloadImageTask(profilePc).execute("http://images6.fanpop.com/image/photos/34200000/more-dumb-images-of-philip-j-fry-futurama-34257101-1440-900.png");
                }
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public static interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(int position);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];

            SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("MyPref", 0);
            SharedPreferences.Editor e = mSharedPreferences.edit();

            if (!mSharedPreferences.getString(TwitterKs.PREF_KEY_PICTURE_URL, "").equals("")) {
                urlDisplay = mSharedPreferences.getString(TwitterKs.PREF_KEY_PICTURE_URL, "");
                e.putBoolean(PREF_KEY_RECHECK, false).apply();
            } else {
                e.putBoolean(PREF_KEY_RECHECK, true).apply();

                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(TwitterKs.TWITTER_CONSUMER_KEY);
                builder.setOAuthConsumerSecret(TwitterKs.TWITTER_CONSUMER_SECRET);

                // Access Token
                String access_token = mSharedPreferences.getString(
                        TwitterKs.PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = mSharedPreferences.getString(
                        TwitterKs.PREF_KEY_OAUTH_SECRET, "");

                AccessToken accessToken = new AccessToken(access_token,
                        access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build())
                        .getInstance(accessToken);
                try {
                    User user = twitter.showUser(accessToken.getUserId());
                    urlDisplay = user.getOriginalProfileImageURL();
                    e.putString(TwitterKs.PREF_KEY_PICTURE_URL, urlDisplay).apply();
                } catch (TwitterException exc) {
                    Log.d("Twitter Update Error", exc.getMessage());
                }

            }

            return ImageLoader.getInstance().loadImageSync(urlDisplay);
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
