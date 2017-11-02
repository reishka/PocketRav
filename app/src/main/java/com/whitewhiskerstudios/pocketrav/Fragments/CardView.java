package com.whitewhiskerstudios.pocketrav.Fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import com.whitewhiskerstudios.pocketrav.R;

/**
 * Created by rachael on 10/6/17.
 */

public class CardView extends Fragment {

    private final String TAG = "Card View";

    View rootView = null;

    public RecyclerView cardList;
    public ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.recycler_view, container, false);
        setHasOptionsMenu(true);

        progressBar = rootView.findViewById(R.id.progressBar);

        cardList = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cardList.setLayoutManager(llm);

        return rootView;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void isDownloading(boolean isDownloading) {

        if (isDownloading){
            cardList.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
            ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", 0, 500);
            animation.setDuration (5000); //in milliseconds
            animation.setInterpolator (new DecelerateInterpolator());
            animation.start ();
        }else{
            progressBar.setVisibility(View.GONE);
            cardList.setVisibility(View.VISIBLE);
        }

    }
}
