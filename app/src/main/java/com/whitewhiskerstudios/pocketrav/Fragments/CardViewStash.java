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
import com.whitewhiskerstudios.pocketrav.API.Models.Stash;
import com.whitewhiskerstudios.pocketrav.API.Models.UnifiedStash;
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
    private ArrayList<Stash> stashList;
    private ArrayList<UnifiedStash> unifiedStashList;
    private DownloadIntentResultReceiver downloadIntentResultReceiver;
    private final String TAG = "CardViewStash";

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        downloadIntentResultReceiver = new DownloadIntentResultReceiver(new Handler());

        stashList = null;
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
            stashList = new ArrayList<>();

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
                                stashList.add(stashItem.getStash());
                        }

                        for (int i = 0; i < stashList.size() - 1; i++) {

                            String name = "";
                            String colorwayName = "";
                            String photoUrl = "";
                            int id = -1;
                            int stashType = -1;

                            if (stashList.get(i).hasColorwayName()) {
                                colorwayName = stashList.get(i).getColorwayName();
                            } else
                                colorwayName = stashList.get(i).getName();

                            if (stashList.get(i).hasFirstPhoto())
                                photoUrl = stashList.get(i).getFirstPhoto().getSmallUrl();

                            id = stashList.get(i).getId();

                            if (stashList.get(i).isYarn() || stashList.get(i).isHandspun())
                                stashType = Constants.STASH_TYPE_YARN;
                            else
                                stashType = Constants.STASH_TYPE_FIBER;

                            cardInfoArrayList.add(new CardData(colorwayName, name, photoUrl, id, stashType));
                        }


                        RecyclerViewAdapterWithPicasso recyclerViewAdapter = new RecyclerViewAdapterWithPicasso(cardInfoArrayList);
                        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapterWithPicasso.MyClickListener() {
                            @Override
                            public void onItemClick(int position, View v) {
                                 Log.i(TAG, " clicked on item at position: " + position);

                                int stashId = cardInfoArrayList.get(position).getMainId();
                                int stashType = cardInfoArrayList.get(position).getStashType();

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
