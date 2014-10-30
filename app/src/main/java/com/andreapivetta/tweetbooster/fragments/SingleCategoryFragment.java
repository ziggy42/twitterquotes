package com.andreapivetta.tweetbooster.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.andreapivetta.tweetbooster.R;
import com.andreapivetta.tweetbooster.adapters.CategoriesAdapter;
import com.andreapivetta.tweetbooster.adapters.TweetCardsAdapter;
import com.andreapivetta.tweetbooster.database.Repository;

import java.util.ArrayList;


public class SingleCategoryFragment extends Fragment {

    public static final String ARG_PAGE = "page";
    private int mPageNumber;

    private static String categoryName, currentCategory;
    private ArrayList<String> arrayList = new ArrayList<String>();

    public static SingleCategoryFragment create(int pageNumber) {
        SingleCategoryFragment fragment = new SingleCategoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SingleCategoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_ce_category, container, false);

        categoryName = getResources().getStringArray(R.array.categories_query)[mPageNumber];
        getArrayList();

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.categoryRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new CategoriesAdapter(getActivity(), arrayList, currentCategory, mPageNumber));

        return rootView;
    }

    private void getArrayList() {
        arrayList.clear();
        Repository repo = Repository.getInstance(getActivity());
        SQLiteDatabase db = repo.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT source FROM " + categoryName, null);

        currentCategory = categoryName;

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            arrayList.add(cursor.getString(0));
        }

        cursor.close();
        repo.close();
    }
}