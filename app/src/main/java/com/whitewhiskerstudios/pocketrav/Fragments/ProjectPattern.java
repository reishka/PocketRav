package com.whitewhiskerstudios.pocketrav.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whitewhiskerstudios.pocketrav.R;
import com.whitewhiskerstudios.pocketrav.Utils.Constants;

/**
 * Created by rachael on 10/14/17.
 */

public class ProjectPattern extends Fragment {

    View rootView;
    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        bundle = getArguments();
        rootView = inflater.inflate(R.layout.fragment_project_pattern, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle acBundle){
        super.onActivityCreated(acBundle);









    }
}
