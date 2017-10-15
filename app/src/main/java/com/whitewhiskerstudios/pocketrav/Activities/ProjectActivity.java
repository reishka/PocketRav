package com.whitewhiskerstudios.pocketrav.Activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TabHost;

import com.daimajia.slider.library.SliderLayout;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.whitewhiskerstudios.pocketrav.API.Models.Project;
import com.whitewhiskerstudios.pocketrav.API.Models.User;
import com.whitewhiskerstudios.pocketrav.Fragments.ProjectInfo;
import com.whitewhiskerstudios.pocketrav.Fragments.ProjectNotes;
import com.whitewhiskerstudios.pocketrav.Fragments.ProjectPattern;
import com.whitewhiskerstudios.pocketrav.R;
import com.whitewhiskerstudios.pocketrav.Services.DownloadIntentService_;
import com.whitewhiskerstudios.pocketrav.Utils.Constants;

import org.json.JSONObject;

/**
 * Created by rachael on 9/17/17.
 */

public class ProjectActivity extends AppCompatActivity {

    private DownloadIntentResultReceiver downloadIntentResultReceiver;
    private static final String TAG = "ProjectActivity";
    private Project project;
    private SliderLayout sliderLayout;
    private FragmentTabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        sliderLayout = (SliderLayout)findViewById(R.id.slider);

        mRecyclerView.setHasFixedSize(true);
        downloadIntentResultReceiver = new DownloadIntentResultReceiver(new Handler());

        tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        setupTabs();

        Intent intent = getIntent();
        int projectId = intent.getIntExtra(Constants.PROJECT_ID, -1);

        if (projectId != -1){
            startDownloadIntentService(Constants.FETCH_PROJECT, projectId);
        }
    }

    private void setupTabs(){

        tabHost.addTab(tabHost.newTabSpec("Project Info").setIndicator("Simple"),
                ProjectInfo.class, null);
        tabHost.addTab(tabHost.newTabSpec("Pattern Info").setIndicator("Simple"),
                ProjectPattern.class, null);
        tabHost.addTab(tabHost.newTabSpec("Project Notes").setIndicator("Simple"),
                ProjectNotes.class, null);
    }



    private void startDownloadIntentService(int type, int id){

        Intent intent = new Intent(this, DownloadIntentService_.class);
        intent.putExtra(Constants.RECEIVER, downloadIntentResultReceiver);
        intent.putExtra(Constants.FETCH_TYPE, type);
        intent.putExtra(Constants.PROJECT_ID, id);
        this.startService(intent);
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
                    case Constants.FETCH_PROJECT:

                        try {
                            JSONObject jObject = new JSONObject(resultDataString);
                            String s_project = jObject.get("project").toString();
                            project = mapper.readValue(s_project, Project.class);

                            if (project != null) {
                                setupSlideshow();
                                setupProject();
                                setupPattern();
                                setupNotes();
                            }

                        }catch (Exception e) {

                            project = null;
                            Log.e(TAG, "Could not get project info from data returned from Ravelry");
                        }

                        break;

                    case Constants.FETCH_PATTERN:
                        break;

                    case Constants.FETCH_PHOTOS:
                        break;


                    default:
                        Log.e(TAG, "How did we get here....?");
                }
            }
        }
    }

    private void setupSlideshow(){}

    private void setupProject(){

    }

    private void setupPattern(){}
    private void setupNotes(){}

}
