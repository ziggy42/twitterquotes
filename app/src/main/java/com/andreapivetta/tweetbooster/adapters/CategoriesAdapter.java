package com.andreapivetta.tweetbooster.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.andreapivetta.tweetbooster.QuotesActivity;
import com.andreapivetta.tweetbooster.R;

import java.util.ArrayList;


public class CategoriesAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> sourcesArrayList;
    private int resource, mPageNumber;
    private String category;

    public CategoriesAdapter(Context context, int resource,
                             ArrayList<String> authorsArrayList, String category, int mPageNumber) {
        super(context, resource, authorsArrayList);

        this.context = context;
        this.resource = resource;
        this.sourcesArrayList = authorsArrayList;
        this.category = category;
        this.mPageNumber = mPageNumber;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(resource, parent, false);

        Button categoryButton = (Button) rowView.findViewById(R.id.categoryButton);
        categoryButton.setText(sourcesArrayList.get(position));
        categoryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().getSharedPreferences("MyPref", 0).edit()
                        .putBoolean("IS_THIS_A_RETURN", true)
                        .putInt("LAST_CATEGORY", mPageNumber)
                        .apply();

                Intent intent = new Intent(context, QuotesActivity.class);
                intent.putExtra("CATEGORY", category)
                        .putExtra("AUTHOR", sourcesArrayList.get(position));
                context.startActivity(intent);
            }
        });

        return rowView;
    }
}
