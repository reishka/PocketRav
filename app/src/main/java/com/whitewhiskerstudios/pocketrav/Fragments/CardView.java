package com.whitewhiskerstudios.pocketrav.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whitewhiskerstudios.pocketrav.Adapters.RecyclerViewAdapter;
import com.whitewhiskerstudios.pocketrav.R;
import com.whitewhiskerstudios.pocketrav.Utils.CardData;

import java.util.ArrayList;

/**
 * Created by rachael on 10/6/17.
 */

public class CardView extends Fragment{

    View rootView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.recycler_view, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);

        RecyclerView cardList = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cardList.setLayoutManager(llm);


        ArrayList<CardData> cardInfoArrayList = new ArrayList<>();
        cardInfoArrayList.add(new CardData("texta", "text1", "photo"));
        cardInfoArrayList.add(new CardData("texdb", "text2", "photo"));
        cardInfoArrayList.add(new CardData("textc", "text3", "photo"));
        cardInfoArrayList.add(new CardData("textd", "text4", "photo"));

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(cardInfoArrayList);
        cardList.setAdapter(recyclerViewAdapter);
    }



}
