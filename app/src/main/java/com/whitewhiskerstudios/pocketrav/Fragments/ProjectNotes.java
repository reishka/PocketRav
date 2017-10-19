package com.whitewhiskerstudios.pocketrav.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whitewhiskerstudios.pocketrav.R;
import com.whitewhiskerstudios.pocketrav.Utils.Constants;

/**
 * Created by rachael on 10/14/17.
 */

public class ProjectNotes extends Fragment {

    View rootView;
    Bundle argsBundle = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        argsBundle = getArguments();
        rootView = inflater.inflate(R.layout.fragment_project_notes, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);

        TextView tv_notes = (TextView)rootView.findViewById((R.id.fragment_project_notes_notes));
        String notes = "No notes to display";

        if (argsBundle != null) {
            if (!argsBundle.getString(Constants.NOTES_BUNDLE).equals(""))
                notes = argsBundle.getString(Constants.NOTES_BUNDLE);
        }

        tv_notes.setText(notes);
    }
}
