package com.andreapivetta.tweetbooster.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.andreapivetta.tweetbooster.QuotesActivity;
import com.andreapivetta.tweetbooster.R;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private ArrayList<String> authorsArrayList;
    private Context context;
    private String category;
    private int mPageNumber;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Button categoryButton;

        public ViewHolder(View container) {
            super(container);

            this.categoryButton = (Button) container.findViewById(R.id.categoryButton);
        }
    }

    public  CategoriesAdapter(Context context, ArrayList<String> authorsArrayList, String category, int mPageNumber) {
        this.context = context;
        this.authorsArrayList = authorsArrayList;
        this.category = category;
        this.mPageNumber = mPageNumber;
    }

    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.source_row, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.categoryButton.setText(authorsArrayList.get(position));
        holder.categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSharedPreferences("MyPref", 0).edit()
                        .putBoolean("IS_THIS_A_RETURN", true)
                        .putInt("LAST_CATEGORY", mPageNumber)
                        .apply();

                Intent intent = new Intent(context, QuotesActivity.class);
                intent.putExtra("CATEGORY", category)
                        .putExtra("AUTHOR", authorsArrayList.get(position));
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return authorsArrayList.size();
    }

}
