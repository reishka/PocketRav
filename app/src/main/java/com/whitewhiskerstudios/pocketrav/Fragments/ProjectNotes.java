package com.whitewhiskerstudios.pocketrav.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.joanzapata.iconify.IconDrawable;
import com.whitewhiskerstudios.pocketrav.API.Models.NeedleSizes;
import com.whitewhiskerstudios.pocketrav.API.Models.Project;
import com.whitewhiskerstudios.pocketrav.R;
import com.whitewhiskerstudios.pocketrav.Services.DownloadIntentService_;
import com.whitewhiskerstudios.pocketrav.Services.UploadIntentService_;
import com.whitewhiskerstudios.pocketrav.Utils.Constants;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by rachael on 10/14/17.
 */

public class ProjectNotes extends Fragment {

    View rootView;
    Bundle bundle = null;
    int projectId = -1;
    public static final String TAG = "Project Notes";
    public static final String EMPTY_NOTES = "No notes to display.";
    String notes = EMPTY_NOTES;
    TextView tv_notes;

    private UploadIntentResultReceiver uploadIntentResultReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        bundle = getArguments();
        rootView = inflater.inflate(R.layout.fragment_project_notes, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle acBundle){
        super.onActivityCreated(acBundle);

        uploadIntentResultReceiver = new UploadIntentResultReceiver(new Handler());
        tv_notes = (TextView)rootView.findViewById((R.id.fragment_project_notes_notes));


        if (bundle != null) {
            if (!bundle.getString(Constants.NOTES_BUNDLE).equals(""))
                notes = bundle.getString(Constants.NOTES_BUNDLE);
            projectId = bundle.getInt(Constants.PROJECT_ID, -1);
        }

        tv_notes.setMovementMethod(new ScrollingMovementMethod());
        tv_notes.setText(notes);

        Button addNotes = (Button) rootView.findViewById(R.id.add_notes);
        addNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
                final EditText et_input = new EditText(getActivity());

                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
                String date = sdf.format(new Date());
                String ravDelim = "\n===================================\n";
                et_input.append(date + ravDelim);
                builder.setTitle("Add Notes");
                builder.setView(et_input);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        JsonObject jsonObject = new JsonObject();
                        String notes_update = "";

                        if (notes.equals(EMPTY_NOTES)){
                            notes_update = et_input.getText().toString();
                        }else {
                            notes_update = notes + "\n\n" + et_input.getText().toString();
                        }

                        jsonObject.addProperty("notes", notes_update);
                        startUploadIntentService(Constants.POST_PROJECT, projectId, jsonObject.toString());

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        Button editNotes = (Button) rootView.findViewById(R.id.edit_notes);
        editNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
                final EditText et_input = new EditText(getActivity());

                if (notes.equals(EMPTY_NOTES))
                {
                    builder.setTitle("No notes to edit");
                    TextView textView = new TextView(getActivity());
                    textView.setPadding(30, 10, 30, 10 );  // Left, top, right, bottom
                    textView.setText("You do not have any notes to edit. Please add a note instead.");
                    builder.setView(textView);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });

                }else {
                    et_input.append(notes);
                    builder.setTitle("Edit Notes");
                    builder.setView(et_input);

                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("notes", et_input.getText().toString());
                            startUploadIntentService(Constants.POST_PROJECT, projectId, jsonObject.toString());

                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });
                }

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
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
                try {
                    JSONObject jObject = new JSONObject(resultDataString);
                    String s_project = jObject.get("project").toString();
                    Project project = mapper.readValue(s_project, Project.class);

                    if (project != null) {

                        notes = project.getNotes();
                        if (notes.equals("") || notes == null){
                            notes = EMPTY_NOTES;
                        }

                        tv_notes.setText(notes);

                        Snackbar.make(rootView, "Notes updated successfully.", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(rootView, "Whoops, something went wrong somewhere. We couldn't update your notes.", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }

                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        }
    }
}
