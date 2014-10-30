package com.andreapivetta.tweetbooster;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;


public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        findViewById(R.id.mailTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity((new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://twitter.com/Pivix00"))));
            }
        });

        findViewById(R.id.stefanoMailTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity((new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://twitter.com/stefano_openlab"))));
            }
        });

        findViewById(R.id.myWebsiteTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity((new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://" + getString(R.string.my_website)))));
            }
        });

        findViewById(R.id.twitter4jTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity((new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://" + getString(R.string.twitter4j_link)))));
            }
        });

        findViewById(R.id.CaldroidTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity((new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://" + getString(R.string.Caldroid_link)))));
            }
        });

        findViewById(R.id.roundedImageViewTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity((new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://" + getString(R.string.rounded_image_view_link)))));
            }
        });

        findViewById(R.id.TwitterOAuthViewTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity((new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://" + getString(R.string.twitter_oauth_view_link)))));
            }
        });

        findViewById(R.id.androidUniversalImageLoader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity((new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://" + getString(R.string.android_universal_image_loader_link)))));
            }
        });

        findViewById(R.id.preferenceFragmentCompatTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity((new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://" + getString(R.string.preference_fragment_compat_link)))));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
