package com.andreapivetta.tweetbooster.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.andreapivetta.tweetbooster.R;
import com.andreapivetta.tweetbooster.adapters.CategoriesAdapter;
import com.andreapivetta.tweetbooster.database.Repository;

import java.util.ArrayList;


public class SingleCategoryFragment extends Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;

    private static String categoryName;
    private ArrayList<String> arrayList = new ArrayList<String>();

    private String currentCategory;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
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
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_ce_category, container, false);

        categoryName = getResources().getStringArray(R.array.categories_query)[mPageNumber];
        ListView categoryListView = (ListView) rootView.findViewById(R.id.categoryListView);

        getArrayList();

        ArrayAdapter<String> arrayAdapter = new CategoriesAdapter(getActivity(), R.layout.source_row, arrayList, currentCategory, mPageNumber);
        categoryListView.setAdapter(arrayAdapter);

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


    public int getPageNumber() {
        return mPageNumber;
    }
}