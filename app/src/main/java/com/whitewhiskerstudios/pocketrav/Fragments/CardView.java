package com.whitewhiskerstudios.pocketrav.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whitewhiskerstudios.pocketrav.API.Models.Project;
import com.whitewhiskerstudios.pocketrav.Activities.ProjectActivity;
import com.whitewhiskerstudios.pocketrav.Activities.ProjectActivity_;
import com.whitewhiskerstudios.pocketrav.Adapters.RecyclerViewAdapterWithPicasso;
import com.whitewhiskerstudios.pocketrav.R;
import com.whitewhiskerstudios.pocketrav.Services.DownloadIntentService_;
import com.whitewhiskerstudios.pocketrav.Utils.CardData;
import com.whitewhiskerstudios.pocketrav.Utils.Constants;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rachael on 10/6/17.
 */

public class CardView extends Fragment{

    private final String TAG = "Card View";

    private DownloadIntentResultReceiver downloadIntentResultReceiver;
    View rootView = null;
    private ArrayList<Project> projects;
    private RecyclerView cardList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.recycler_view, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);

        downloadIntentResultReceiver = new DownloadIntentResultReceiver(new Handler());

        cardList = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cardList.setLayoutManager(llm);

        int type = getArguments().getInt(Constants.FETCH_TYPE);

        switch (type){

            case Constants.FETCH_PROJECT_LIST:
                projects = null;
                startDownloadIntentService(Constants.FETCH_PROJECT_LIST);
                break;

            default:
                break;
        }
    }


    private void startDownloadIntentService(int type){

        Intent intent = new Intent(getActivity(), DownloadIntentService_.class);
        intent.putExtra(Constants.RECEIVER, downloadIntentResultReceiver);
        intent.putExtra(Constants.FETCH_TYPE, type);
        getActivity().startService(intent);
    }

    private class DownloadIntentResultReceiver extends ResultReceiver {
        private DownloadIntentResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == Constants.SUCCESS_RESULT){

                int type = resultData.getInt(Constants.FETCH_TYPE);
                String resultDataString = resultData.getString(Constants.RESULT_DATA_KEY);
                ObjectMapper mapper = new ObjectMapper();

                switch(type){
                    case Constants.FETCH_PROJECT_LIST:

                        try {
                            JSONObject jObject = new JSONObject(resultDataString);
                            String s_projects = jObject.get("projects").toString();
                            projects = mapper.readValue(s_projects, new TypeReference<ArrayList<Project>>(){});

                            if (projects != null);
                            {
                                final ArrayList<CardData> cardInfoArrayList = new ArrayList<>();

                                for (int i = 0; i < projects.size() - 1; i++)
                                {

                                    String name = "";
                                    String patternName = "";
                                    String photoUrl = "";
                                    int id = -1;

                                    if (projects.get(i).getName() != null)
                                        name = projects.get(i).getName();

                                    if (projects.get(i).getPatternName() != null)
                                        patternName = projects.get(i).getPatternName();

                                    if (projects.get(i).hasFirstPhoto())
                                        photoUrl = projects.get(i).getFirstPhoto().getSmallUrl();

                                    id = projects.get(i).getId();

                                    cardInfoArrayList.add(new CardData(name, patternName, photoUrl, id));
                                }

                                RecyclerViewAdapterWithPicasso recyclerViewAdapter = new RecyclerViewAdapterWithPicasso(cardInfoArrayList);
                                recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapterWithPicasso.MyClickListener(){
                                    @Override
                                    public void onItemClick(int position, View v){
                                        Log.i(TAG, " clicked on item at position: " + position);

                                        int projectId = cardInfoArrayList.get(position).id;

                                        Intent intent = new Intent(getActivity(), ProjectActivity_.class);
                                        intent.putExtra(Constants.PROJECT_ID, projectId);
                                        startActivity(intent);
                                    }
                                });

                                cardList.setAdapter(recyclerViewAdapter);


                            }


                        }catch (Exception e) {

                            projects = null;
                            Log.e(TAG, e.toString());
                        }

                        break;
                    default:
                        Log.e(TAG, "How did we get here....?");
                }
            }
        }
    }



}
