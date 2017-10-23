package com.whitewhiskerstudios.pocketrav.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import com.whitewhiskerstudios.pocketrav.API.Models.NeedleSize;
import com.whitewhiskerstudios.pocketrav.API.Models.Pack;
import com.whitewhiskerstudios.pocketrav.API.Models.Project;
import com.whitewhiskerstudios.pocketrav.Adapters.RecyclerViewAdapterWithFontAwesome;
import com.whitewhiskerstudios.pocketrav.R;
import com.whitewhiskerstudios.pocketrav.Utils.CardData;
import com.whitewhiskerstudios.pocketrav.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by rachael on 10/14/17.
 */

public class ProjectInfo extends Fragment {

    private static final String TAG = "Project Info Fragment";

    private View rootView;
    private Bundle bundle;
    private Project project = null;

    private RatingBar ratingBar;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private static final int POSITION_DATE_STARTED = 0;
    private static final int POSITION_DATE_FINISHED = 1;
    private static final int POSITION_MADE_FOR = 2;
    private static final int POSITION_STATUS = 3;
    private static final int POSITION_TAGS = 4;
    private static final int POSITION_SIZE = 5;
    private static final int POSITION_NEEDLE_SIZES = 6;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        bundle = getArguments();
        rootView = inflater.inflate(R.layout.fragment_project_info, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle acBundle) {
        super.onActivityCreated(acBundle);

        if (bundle != null) {
            project = (Project) bundle.getSerializable(Constants.PROJECT_BUNDLE);

            initViews();

            try {
                ratingBar.setRating(project.getRating());
                progressBar.setProgress(project.getProgress());

                final ArrayList<CardData> projectItems = new ArrayList<>();

                ArrayList<String> a_tags = new ArrayList<>();
                a_tags = project.getTagNames();
                String s_tags = "";

                for (int i = 0; i < a_tags.size(); i++) {
                    if (i == a_tags.size() -1)
                        s_tags += a_tags.get(i);
                    else
                        s_tags += a_tags.get(i) + ", ";
                }

                ArrayList<NeedleSize> a_needleSizes = new ArrayList<>();
                a_needleSizes = project.getNeedleSizes();
                String s_needleSizes = "";

                for (int i = 0; i < a_needleSizes.size(); i++){
                    if (i == a_needleSizes.size() - 1)
                        s_needleSizes += a_needleSizes.get(i).getName();
                    else
                        s_needleSizes += a_needleSizes.get(i).getName() + ", \n";
                }

                ArrayList<Pack> a_packs = new ArrayList<>();
                a_packs = project.getPack();
                ArrayList<CardData> a_packCards;

                projectItems.add(POSITION_DATE_STARTED, new CardData("Date Started:", project.getStarted(), "{fa-calendar}"));
                projectItems.add(POSITION_DATE_FINISHED, new CardData("Date Completed:", project.getCompleted(), "{fa-calendar-check-o}"));
                projectItems.add(POSITION_MADE_FOR, new CardData("Made for: ", project.getMadeFor(), "{fa-user}"));
                projectItems.add(POSITION_STATUS, new CardData("Status: ", project.getStatusName(), "{fa-list-alt}"));
                projectItems.add(POSITION_TAGS, new CardData("Tags: ", s_tags, "{fa-tags}"));
                projectItems.add(POSITION_SIZE, new CardData("Size: ", project.getSize(), "{fa-wrench}"));
                projectItems.add(POSITION_NEEDLE_SIZES, new CardData("Needles/Hooks: ", s_needleSizes, "{fa-asterisk}"));


                for (int i = 0; i < a_packs.size(); i++){
                    String yarnString = "";
                    yarnString += a_packs.get(i).getYarnName() + "\nColorway: ";
                    yarnString += a_packs.get(i).getColorway();

                    if ( a_packs.get(i).getQuantityDescription() != null)
                        yarnString += "\nAmount used: " + a_packs.get(i).getQuantityDescription();

                    projectItems.add(new CardData("Yarn: ", yarnString, "{fa-bookmark}"));
                }

                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
                    @Override
                    public int getSpanSize(int position){

                        if (position == POSITION_DATE_STARTED ||
                                position == POSITION_DATE_FINISHED ||
                                position == POSITION_MADE_FOR ||
                                position == POSITION_STATUS ||
                                position == POSITION_SIZE ||
                                position == POSITION_NEEDLE_SIZES ){
                            return 1;
                        }else
                            return 2;
                    }
                });


                recyclerView.setLayoutManager(gridLayoutManager);

                RecyclerViewAdapterWithFontAwesome recyclerViewAdapter = new RecyclerViewAdapterWithFontAwesome(projectItems);
                recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapterWithFontAwesome.MyClickListener(){
                    @Override
                    public void onItemClick(int position, View v){
                        Log.i(TAG, " clicked on item at position: " + position);

                        switch(position){
                            case POSITION_DATE_STARTED:
                                break;
                            case POSITION_DATE_FINISHED:
                                break;
                            case POSITION_MADE_FOR:
                                break;
                            case POSITION_STATUS:
                                break;
                            case POSITION_TAGS:
                                break;


                        }

                    }
                });

                recyclerView.setAdapter(recyclerViewAdapter);


            } catch (Exception e) {
            }
        }
    }

    private void initViews(){

        ratingBar = (RatingBar)rootView.findViewById(R.id.ratingBar);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);

    }
}
