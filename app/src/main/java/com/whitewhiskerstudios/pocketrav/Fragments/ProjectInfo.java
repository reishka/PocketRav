package com.whitewhiskerstudios.pocketrav.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.whitewhiskerstudios.pocketrav.API.Models.Project;
import com.whitewhiskerstudios.pocketrav.R;
import com.whitewhiskerstudios.pocketrav.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by rachael on 10/14/17.
 */

public class ProjectInfo extends Fragment {

    private View rootView;
    private Bundle bundle;
    private Project project = null;

    private RatingBar ratingBar;
    private ProgressBar progressBar;
    private TextView dayStarted, dayCompleted, madeFor, status, tags;

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
                dayStarted.setText(project.getStarted());
                dayCompleted.setText(project.getCompleted());
                madeFor.setText(project.getMadeFor());
                status.setText(project.getStatusName());

                ArrayList<String> a_tags = new ArrayList<>();
                a_tags = project.getTagNames();
                String s_tags = "";

                for (int i = 0; i < a_tags.size(); i++) {
                 if (i == a_tags.size() -1)
                    s_tags += a_tags.get(i);
                 else
                     s_tags += a_tags.get(i) + ", ";
                }

                tags.setText(s_tags);



            } catch (Exception e) {
            }
        }
    }

    private void initViews(){

        ratingBar = (RatingBar)rootView.findViewById(R.id.ratingBar);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        dayStarted = (TextView)rootView.findViewById(R.id.dateStartedValue);
        dayCompleted = (TextView)rootView.findViewById(R.id.dateCompletedValue);
        madeFor = (TextView)rootView.findViewById(R.id.madeForValue);
        status = (TextView)rootView.findViewById(R.id.statusValue);
        tags = (TextView)rootView.findViewById(R.id.tagValue);


    }
}
