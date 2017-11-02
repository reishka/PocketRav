package com.whitewhiskerstudios.pocketrav.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.whitewhiskerstudios.pocketrav.API.Models.FiberStash;
import com.whitewhiskerstudios.pocketrav.API.Models.YarnStash;
import com.whitewhiskerstudios.pocketrav.Adapters.RecyclerViewAdapterWithFontAwesome;
import com.whitewhiskerstudios.pocketrav.R;
import com.whitewhiskerstudios.pocketrav.Services.UploadIntentService_;
import com.whitewhiskerstudios.pocketrav.Utils.CardData;
import com.whitewhiskerstudios.pocketrav.Utils.Constants;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rachael on 10/31/17.
 */

public class StashInfo extends Fragment {

    private static final String TAG = "Project Info Fragment";

    private View rootView;
    private Bundle bundle;
    private FiberStash fiberStash = null;
    private YarnStash yarnStash = null;
    int stashType = -1;

    private RatingBar ratingBar;
    private DiscreteSeekBar seekBar;
    private RecyclerView recyclerView;
    RecyclerViewAdapterWithFontAwesome recyclerViewAdapter;
    ArrayList<CardData> stashItems;

    private UploadIntentResultReceiver uploadIntentResultReceiver;

    private static final int POSITION_YARNSTASH_COLOR_FAMILY = 0;
    private static final int POSITION_YARNSTASH_STATUS= 1;
    private static final int POSITION_YARNSTASH_HANDSPUN = 2;
    private static final int POSITION_YARNSTASH_LOCATION = 3;
    private static final int POSITION_YARNSTASH_HOOKSIZES = 4;
    private static final int POSITION_YARNSTASH_NEEDLESIZES = 5;
    private static final int POSITION_YARNSTASH_WEIGHTNAME = 6;
    private static final int POSITION_QUANTITY = 7;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        bundle = getArguments();
        rootView = inflater.inflate(R.layout.fragment_project_stash_info, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle acBundle) {
        super.onActivityCreated(acBundle);

        Iconify.with(new FontAwesomeModule());

        uploadIntentResultReceiver = new UploadIntentResultReceiver(new Handler());

        if (bundle != null) {

            stashType = bundle.getInt(Constants.STASH_TYPE, -1);

            if (stashType != -1)
            {
                if (stashType == Constants.STASH_TYPE_YARN)
                    yarnStash = (YarnStash) bundle.getSerializable(Constants.STASH_BUNDLE);
                else
                    fiberStash = (FiberStash) bundle.getSerializable(Constants.STASH_BUNDLE);
            }

            initViews();
            loadData();

            try {

                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
                    @Override
                    public int getSpanSize(int position){

                        if (position == POSITION_YARNSTASH_COLOR_FAMILY ||
                                position == POSITION_YARNSTASH_STATUS       ||
                                position == POSITION_YARNSTASH_HANDSPUN     ||
                                position == POSITION_YARNSTASH_LOCATION     ||
                                position == POSITION_YARNSTASH_HOOKSIZES    ||
                                position == POSITION_YARNSTASH_NEEDLESIZES  ||
                                position == POSITION_YARNSTASH_WEIGHTNAME   ||
                                position == POSITION_QUANTITY)
                            return 1;
                        else
                            return 2;
                    }
                });

                recyclerView.setLayoutManager(gridLayoutManager);

                recyclerViewAdapter = new RecyclerViewAdapterWithFontAwesome(stashItems);
                recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapterWithFontAwesome.MyClickListener(){
                    @Override
                    public void onItemClick(int position, View v){
                        Log.i(TAG, " clicked on item at position: " + position);
                        //buildDialog(position);
                    }
                });

                recyclerView.setAdapter(recyclerViewAdapter);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    private void initViews(){

        // Disable the following, since they only apply to projects and not to stashes
        ratingBar = (RatingBar)rootView.findViewById(R.id.ratingBar);
        ratingBar.setVisibility(View.GONE);

        seekBar = (DiscreteSeekBar)rootView.findViewById(R.id.seekBar);
        seekBar.setVisibility(View.GONE);

        LinearLayout project_bar = (LinearLayout)rootView.findViewById(R.id.project_bar);
        project_bar.setVisibility(View.GONE);


        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        LinearLayout recyclerViewParent = (LinearLayout)recyclerView.getParent();
        recyclerViewParent.setBackgroundColor(Color.TRANSPARENT); // RecyclerView has a gradient, but our activity also has a gradient, so remove the rv gradient


    }

    private void startUploadIntentService(int type, int id, String string){

        Intent intent = new Intent(getActivity(), UploadIntentService_.class);
        intent.putExtra(Constants.RECEIVER, uploadIntentResultReceiver);
        intent.putExtra(Constants.POST_TYPE, type);
        intent.putExtra(Constants.PROJECT_ID, id);
        intent.putExtra(Constants.POST_JSON_STRING, string);
        getActivity().startService(intent);
    }


    private class UploadIntentResultReceiver extends ResultReceiver {
        private UploadIntentResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == Constants.SUCCESS_RESULT) {

                Log.d(TAG, "success result");

                String resultDataString = resultData.getString(Constants.RESULT_DATA_KEY);
                ObjectMapper mapper = new ObjectMapper();

                if (stashType == Constants.STASH_TYPE_YARN) {
                    try {
                        JSONObject jObject = new JSONObject(resultDataString);
                        String s_yarnStash = jObject.get("stash").toString();
                        yarnStash = mapper.readValue(s_yarnStash, YarnStash.class);

                        if (yarnStash != null) {
                            reloadData();

                        } else {
                            Snackbar.make(rootView, "Whoops, something went wrong somewhere. We couldn't update your yarn stash.", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                }else{
                    try {
                        JSONObject jObject = new JSONObject(resultDataString);
                        String s_fiberStash = jObject.get("fiber_stash").toString();
                        fiberStash = mapper.readValue(s_fiberStash, FiberStash.class);

                        if (fiberStash != null) {
                            reloadData();

                        } else {
                            Snackbar.make(rootView, "Whoops, something went wrong somewhere. We couldn't update your yarn stash.", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                }
            }
        }
    }

    private void loadData(){


        stashItems = new ArrayList<>();

        switch (stashType){
            case Constants.STASH_TYPE_YARN:
                stashItems.add(POSITION_YARNSTASH_COLOR_FAMILY, new CardData(getString(R.string.project_date_started), yarnStash.getColorwayName(), "{fa-calendar}"));
                //stashItems.add(POSITION_DATE_FINISHED, new CardData(getString(R.string.project_date_completed), project.getCompleted(), "{fa-calendar-check-o}"));
                //stashItems.add(POSITION_MADE_FOR, new CardData(getString(R.string.project_made_for), project.getMadeFor(), "{fa-user}"));
                //stashItems.add(POSITION_STATUS, new CardData(getString(R.string.project_status), project.getStatusName(), "{fa-list-alt}"));
                //stashItems.add(POSITION_TAGS, new CardData(getString(R.string.project_tags), s_tags, "{fa-tags}"));
                //stashItems.add(POSITION_SIZE, new CardData(getString(R.string.project_size), project.getSize(), "{fa-asterisk}"));
                //stashItems.add(POSITION_NEEDLE_SIZES, new CardData(getString(R.string.project_needles_hooks), s_needleSizes, "{fa-wrench}"));

        }




        //for (int i = 0; i < a_packs.size(); i++) {
          //  String yarnString = "";
           // yarnString += a_packs.get(i).getYarnName() + "\nColorway: ";
            //yarnString += a_packs.get(i).getColorway();

//            if (a_packs.get(i).getQuantityDescription() != null)
  //              yarnString += "\nAmount used: " + a_packs.get(i).getQuantityDescription();

            //stashItems.add(new CardData("Yarn: ", yarnString, "{fa-bookmark}"));

    }


    private void reloadData() {

        loadData();
        recyclerViewAdapter.updateList(stashItems);
        Snackbar.make(rootView, "Project updated successfully.", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }


}
