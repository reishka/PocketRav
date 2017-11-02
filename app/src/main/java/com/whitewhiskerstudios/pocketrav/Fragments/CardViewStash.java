package com.whitewhiskerstudios.pocketrav.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whitewhiskerstudios.pocketrav.API.Models.FiberStash;
import com.whitewhiskerstudios.pocketrav.API.Models.UnifiedStash;
import com.whitewhiskerstudios.pocketrav.API.Models.YarnStash;
import com.whitewhiskerstudios.pocketrav.Activities.StashActivity_;
import com.whitewhiskerstudios.pocketrav.Adapters.RecyclerViewAdapterWithPicasso;
import com.whitewhiskerstudios.pocketrav.R;
import com.whitewhiskerstudios.pocketrav.Services.DownloadIntentService_;
import com.whitewhiskerstudios.pocketrav.Utils.CardData;
import com.whitewhiskerstudios.pocketrav.Utils.Constants;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rachael on 10/29/17.
 */

public class CardViewStash extends CardView{
    private ArrayList<YarnStash> yarnStashList;
    private ArrayList<FiberStash> fiberStashList;
    private ArrayList<UnifiedStash> unifiedStashList;
    private DownloadIntentResultReceiver downloadIntentResultReceiver;
    private final String TAG = "CardViewStash";

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        downloadIntentResultReceiver = new DownloadIntentResultReceiver(new Handler());

        yarnStashList = null;
        fiberStashList = null;
        unifiedStashList = null;

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Stash");
        startDownloadIntentService(Constants.FETCH_UNIFIED_STASH_LIST);
    }

    public void startDownloadIntentService(int type){
        isDownloading(true);
        Intent intent = new Intent(getActivity(), DownloadIntentService_.class);
        intent.putExtra(Constants.RECEIVER, downloadIntentResultReceiver);
        intent.putExtra(Constants.FETCH_TYPE, type);
        getActivity().startService(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.stash_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.stash_alpha:
                Toast.makeText(getActivity(), "Sorting Alphabetically", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.stash_recent:
                Toast.makeText(getActivity(), "Sorting by Recent", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.stash_weight:
                Toast.makeText(getActivity(), "Sorting by Weight", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.stash_yards:
                Toast.makeText(getActivity(), "Sorting by Yardage", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.stash_color_family:
                Toast.makeText(getActivity(), "Sorting by Color Family", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class DownloadIntentResultReceiver extends ResultReceiver {
        private DownloadIntentResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            isDownloading(false);
            yarnStashList = new ArrayList<>();
            fiberStashList = new ArrayList<>();

            if (resultCode == Constants.SUCCESS_RESULT) {
                try {

                    String resultDataString = resultData.getString(Constants.RESULT_DATA_KEY);
                    ObjectMapper mapper = new ObjectMapper();

                    JSONObject jObject = new JSONObject(resultDataString);
                    String s_stashes = jObject.get("unified_stash").toString();
                    unifiedStashList = mapper.readValue(s_stashes, new TypeReference<ArrayList<UnifiedStash>>() {});

                    if (unifiedStashList != null)
                    {
                        final ArrayList<CardData> cardInfoArrayList = new ArrayList<>();

                        for (UnifiedStash stashItem : unifiedStashList){
                            if (stashItem.isYarn())
                                yarnStashList.add(stashItem.getYarnStash());
                            else
                                fiberStashList.add(stashItem.getFiberStash());
                        }


                        for (int i = 0; i < yarnStashList.size() - 1; i++) {

                            String name = "";
                            String colorwayName = "";
                            String photoUrl = "";
                            int id = -1;

                            if (yarnStashList.get(i).hasColorwayName()) {
                                colorwayName = yarnStashList.get(i).getColorwayName();
                            } else if (yarnStashList.get(i).hasYarn()) {
                                colorwayName = yarnStashList.get(i).getYarn().getYarnCompany().getName() + " - " + yarnStashList.get(i).getYarn().getName();
                            } else {
                                colorwayName = yarnStashList.get(i).getName();
                            }

                            if (yarnStashList.get(i).hasYarn())
                                name = yarnStashList.get(i).getYarn().getYarnCompany().getName() + " - " + yarnStashList.get(i).getYarn().getName();
                            else
                                name = yarnStashList.get(i).getName();

                            if (yarnStashList.get(i).hasFirstPhoto())
                                photoUrl = yarnStashList.get(i).getFirstPhoto().getSmallUrl();

                            id = yarnStashList.get(i).getId();

                            cardInfoArrayList.add(new CardData(colorwayName, name, photoUrl, id, Constants.STASH_TYPE_YARN));
                        }


                        for (int i = 0; i < fiberStashList.size() - 1; i++) {

                            String name = "";
                            String colorwayName = "";
                            String photoUrl = "";
                            int id = -1;

                            if (fiberStashList.get(i).hasColorwayName()) {
                                colorwayName = fiberStashList.get(i).getColorwayName();
                            } else
                                colorwayName = fiberStashList.get(i).getName();

                            if (fiberStashList.get(i).hasFirstPhoto())
                                photoUrl = fiberStashList.get(i).getFirstPhoto().getSmallUrl();

                            id = fiberStashList.get(i).getId();

                            cardInfoArrayList.add(new CardData(colorwayName, name, photoUrl, id, Constants.STASH_TYPE_FIBER));
                        }


                        RecyclerViewAdapterWithPicasso recyclerViewAdapter = new RecyclerViewAdapterWithPicasso(cardInfoArrayList);
                        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapterWithPicasso.MyClickListener() {
                            @Override
                            public void onItemClick(int position, View v) {
                                 Log.i(TAG, " clicked on item at position: " + position);

                                int stashId = cardInfoArrayList.get(position).id;
                                int stashType = cardInfoArrayList.get(position).stashType;

                                Intent intent = new Intent(getActivity(), StashActivity_.class);
                                intent.putExtra(Constants.STASH_ID, stashId);
                                intent.putExtra(Constants.STASH_TYPE, stashType);
                                startActivity(intent);
                            }
                        });
                        cardList.setAdapter(recyclerViewAdapter);
                    }


                } catch (Exception e) {

                    Log.e(TAG, e.toString());
                }
            }
        }
    }
}
