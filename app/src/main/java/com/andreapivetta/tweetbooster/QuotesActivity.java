package com.andreapivetta.tweetbooster;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.andreapivetta.tweetbooster.adapters.TweetCardsAdapter;
import com.andreapivetta.tweetbooster.database.Repository;
import com.andreapivetta.tweetbooster.twitter.UpdateTwitterStatus;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;


public class QuotesActivity extends ActionBarActivity {

    private String category;
    private String author;
    private ArrayList<String> quotesArrayList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        Bundle extras = getIntent().getExtras();
        category = extras.getString("CATEGORY");
        author = extras.getString("AUTHOR");

        quotesArrayList.clear();
        getSupportActionBar().setTitle(author);
        getSupportActionBar().setSubtitle(category);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setUpQuotes();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.quotesRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new TweetCardsAdapter(quotesArrayList, this));

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("EB8CB71D9FE394E0DCCBF26188BED5D7")
                .addTestDevice("A272A918ED2BBA9EC2138C622D7212D0")
                .addTestDevice("C9F505E68A8DADEB86EF831BD769444D")
                .addTestDevice("EEC1F897DA0F5D96B97DD79FA09522C6")
                .addTestDevice("D92FD0DC69AECDDD9D41C63DEF1D68C4")
                .build();
        adView.loadAd(adRequest);
    }

    private void setUpQuotes() {
        Repository repo = Repository.getInstance(this);
        SQLiteDatabase db = repo.getWritableDatabase();

        String sqlQuery = "SELECT quote FROM " + category + " WHERE source = '"
                + author + "'";
        Cursor cursor = db.rawQuery(sqlQuery, null);

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            quotesArrayList.add(cursor.getString(0));
        }

        cursor.close();
        repo.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!category.equals("Funny"))
            getMenuInflater().inflate(R.menu.wikipedia_menu, menu);
        getMenuInflater().inflate(R.menu.random_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.wikipedia_redirect_menu) {
            startActivity((new Intent(Intent.ACTION_VIEW)).setData(Uri.parse("http://en.wikipedia.org/wiki/" + author)));
            return true;
        } else {
            if (id == R.id.random_send_menu)
                new UpdateTwitterStatus(QuotesActivity.this).execute(quotesArrayList.get((int) (Math.random() * quotesArrayList.size())));
        }
        return super.onOptionsItemSelected(item);
    }
}
