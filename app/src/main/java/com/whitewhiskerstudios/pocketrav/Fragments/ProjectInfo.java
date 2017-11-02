package com.whitewhiskerstudios.pocketrav.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.ResultReceiver;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.whitewhiskerstudios.pocketrav.API.Models.NeedleSizes;
import com.whitewhiskerstudios.pocketrav.API.Models.Pack;
import com.whitewhiskerstudios.pocketrav.API.Models.Project;
import com.whitewhiskerstudios.pocketrav.Adapters.RecyclerViewAdapterWithFontAwesome;
import com.whitewhiskerstudios.pocketrav.R;
import com.whitewhiskerstudios.pocketrav.Services.DownloadIntentService_;
import com.whitewhiskerstudios.pocketrav.Services.UploadIntentService_;
import com.whitewhiskerstudios.pocketrav.Utils.CardData;
import com.whitewhiskerstudios.pocketrav.Utils.Constants;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rachael on 10/14/17.
 */


public class ProjectInfo extends Fragment{

    private static final String TAG = "Project Info Fragment";

    private View rootView;
    private Bundle bundle;
    private Project project = null;

    private RatingBar ratingBar;
    private DiscreteSeekBar seekBar;
    private RecyclerView recyclerView;
    RecyclerViewAdapterWithFontAwesome recyclerViewAdapter;
    ArrayList<CardData> projectItems;

    ArrayList<NeedleSizes> knittingNeedleSizes = new ArrayList<>();
    ArrayList<NeedleSizes> crochetNeedleSizes = new ArrayList<>();

    private UploadIntentResultReceiver uploadIntentResultReceiver;
    private DownloadIntentResultReceiver downloadIntentResultReceiver;

    private static final int POSITION_DATE_STARTED = 0;
    private static final int POSITION_DATE_FINISHED = 1;
    private static final int POSITION_MADE_FOR = 2;
    private static final int POSITION_STATUS = 3;
    private static final int POSITION_TAGS = 4;
    private static final int POSITION_SIZE = 5;
    private static final int POSITION_TOOLS = 6;
    private static final int POSITION_EPI_PPI = 7;

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
        downloadIntentResultReceiver = new DownloadIntentResultReceiver(new Handler());

        if (bundle != null) {
            project = (Project) bundle.getSerializable(Constants.PROJECT_BUNDLE);

            if (project.getCraftId() == Project.CRAFT_KNITTING || project.getCraftId() == Project.CRAFT_CROCHET) {
                startDownloadIntentService(Constants.FETCH_NEEDLES_CROCHET);
                startDownloadIntentService(Constants.FETCH_NEEDLES_KNITTING);
            }

            initViews();
            loadData();

            try {

                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
                    @Override
                    public int getSpanSize(int position){

                        if (position == POSITION_DATE_STARTED ||
                                position == POSITION_DATE_FINISHED ||
                                position == POSITION_MADE_FOR ||
                                position == POSITION_STATUS ||
                                position == POSITION_SIZE ||
                                position == POSITION_TOOLS){
                            return 1;
                        }else
                            return 2;
                    }
                });

                recyclerView.setLayoutManager(gridLayoutManager);

                recyclerViewAdapter = new RecyclerViewAdapterWithFontAwesome(projectItems);
                recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapterWithFontAwesome.MyClickListener(){
                    @Override
                    public void onItemClick(int position, View v){
                        Log.i(TAG, " clicked on item at position: " + position);
                        buildDialog(position);
                        }
                });

                recyclerView.setAdapter(recyclerViewAdapter);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    private void initViews(){

        ratingBar = (RatingBar)rootView.findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if(rating - 1 != project.getRating()) {

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("rating", rating - 1);
                    startUploadIntentService(Constants.POST_PROJECT, project.getId(), jsonObject.toString());
                }
            }
        });

        seekBar = (DiscreteSeekBar)rootView.findViewById(R.id.seekBar);

        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {

            int progressValue = 0;
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int progress, boolean fromUser) {
                progressValue = progress;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

                if (progressValue != project.getProgress()) {

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("progress", progressValue);
                    startUploadIntentService(Constants.POST_PROJECT, project.getId(), jsonObject.toString());
                }
            }
        });

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        LinearLayout recyclerViewParent = (LinearLayout)recyclerView.getParent();
        recyclerViewParent.setBackgroundColor(Color.TRANSPARENT); // RecyclerView has a gradient, but our activity also has a gradient, so remove the rv gradient

        LinearLayout stash_bar = (LinearLayout)rootView.findViewById(R.id.stash_bar);
        stash_bar.setVisibility(View.GONE);
    }

    private String getTags(){

        ArrayList<String> a_tags = project.getTagNames();
        String s_tags = "";

        for (int i = 0; i < a_tags.size(); i++) {
           if (i == a_tags.size() -1)
                s_tags += a_tags.get(i);
           else
                s_tags += a_tags.get(i) + ", ";
        }

        return s_tags;
    }

    private void buildDialog(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final EditText et_input = new EditText(getActivity());
        final DatePicker picker  = new DatePicker(getActivity());
        picker.setCalendarViewShown(false);

        switch (position) {
            case POSITION_DATE_STARTED:

                builder.setTitle("Date Started");
                builder.setView(picker);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        JsonObject jsonObject = new JsonObject();

                        int year = picker.getYear();
                        int month = picker.getMonth();
                        int day = picker.getDayOfMonth();

                        String date = String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(day);

                        jsonObject.addProperty("started", date);
                        startUploadIntentService(Constants.POST_PROJECT, project.getId(), jsonObject.toString());

                        dialog.dismiss();
                    }
                });
                break;

            case POSITION_DATE_FINISHED:

                builder.setTitle("Date Finished");
                builder.setView(picker);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        JsonObject jsonObject = new JsonObject();

                        int year = picker.getYear();
                        int month = picker.getMonth();
                        int day = picker.getDayOfMonth();

                        String date = String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(day);

                        jsonObject.addProperty("completed", date);
                        startUploadIntentService(Constants.POST_PROJECT, project.getId(), jsonObject.toString());

                        dialog.dismiss();
                    }
                });
                break;

            case POSITION_MADE_FOR:

                builder.setView(et_input);
                builder.setTitle("Made for");
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("made_for", et_input.getText().toString());
                        startUploadIntentService(Constants.POST_PROJECT, project.getId(), jsonObject.toString());

                        dialog.dismiss();
                    }
                });
                break;

            case POSITION_STATUS:

                builder.setTitle("Project status");
                String[] status = {"In Progress", "Finished", "Hibernating", "Frogged"};

                builder.setItems(status, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String s_status = "";
                        int i_status = 0;

                        switch (which) {
                            case 0:
                                s_status = "In progress";
                                i_status = 1;
                                break;
                            case 1:
                                s_status = "Finished";
                                i_status = 2;
                                break;
                            case 2:
                                s_status = "Hibernating";
                                i_status = 3;
                                break;
                            case 3:
                                s_status = "Frogged";
                                i_status = 4;
                                break;
                        }

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("project_status_id", String.valueOf(i_status));
                        jsonObject.addProperty("status_name", s_status);
                        startUploadIntentService(Constants.POST_PROJECT, project.getId(), jsonObject.toString());

                        dialog.dismiss();
                    }
                });

                break;

            case POSITION_TAGS:

                builder.setView(et_input);
                builder.setTitle("Tags");
                et_input.setText(getTags());

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        JsonObject jsonObject = new JsonObject();
                        String tags = et_input.getText().toString();
                        String[] a_tags = null;
                        JsonArray jsonArray = new JsonArray();

                            if (tags.contains(","))
                                a_tags = tags.split(",");
                            else if (tags.contains(" "))
                                a_tags = tags.split(" ");

                        if (a_tags!= null && a_tags.length > 0 ) {
                            for (int i = 0; i < a_tags.length; i++) {
                                a_tags[i] = a_tags[i].trim();
                                if (a_tags[i].contains(" ")) {
                                    a_tags[i] = a_tags[i].replace(" ", "");
                                }
                            }
                            for (String tag : a_tags)
                                jsonArray.add(tag);
                        }
                            jsonObject.add("tag_names", jsonArray);
                            startUploadIntentService(Constants.POST_PROJECT, project.getId(), jsonObject.toString());

                        dialog.dismiss();
                    }
                });
                break;

            case POSITION_SIZE:

                builder.setView(et_input);
                builder.setTitle("Size made");
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("size", et_input.getText().toString());
                        startUploadIntentService(Constants.POST_PROJECT, project.getId(), jsonObject.toString());

                        dialog.dismiss();
                    }
                });
                break;

            case POSITION_TOOLS:

                builder.setTitle("Tools");

                if (project.getCraftId() == Project.CRAFT_KNITTING) { // get knitting needle list

                    String[] displayNames = new String[knittingNeedleSizes.size()];
                    boolean[] checkedStatus = new boolean[knittingNeedleSizes.size()];

                    for (int i = 0; i < knittingNeedleSizes.size(); i++) {
                        displayNames[i] = knittingNeedleSizes.get(i).getDisplayName();
                        checkedStatus[i] = knittingNeedleSizes.get(i).getChecked();
                    }

                    builder.setMultiChoiceItems(displayNames, checkedStatus, new DialogInterface.OnMultiChoiceClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                            if (isChecked) {
                                knittingNeedleSizes.get(which).setChecked(true);
                            } else if (knittingNeedleSizes.get(which).getChecked())
                                knittingNeedleSizes.get(which).setChecked(false);
                        }
                    });
                } else if (project.getCraftId() == Project.CRAFT_CROCHET) {

                    String[] displayNames = new String[crochetNeedleSizes.size()];
                    boolean[] checkedStatus = new boolean[crochetNeedleSizes.size()];

                    for (int i = 0; i < crochetNeedleSizes.size(); i++) {
                        displayNames[i] = crochetNeedleSizes.get(i).getDisplayName();
                        checkedStatus[i] = crochetNeedleSizes.get(i).getChecked();
                    }

                    builder.setMultiChoiceItems(displayNames, checkedStatus, new DialogInterface.OnMultiChoiceClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                            if (isChecked) {
                                crochetNeedleSizes.get(which).setChecked(true);
                            } else if (crochetNeedleSizes.get(which).getChecked())
                                crochetNeedleSizes.get(which).setChecked(false);
                        }
                    });
                }else {

                    TextView textView = new TextView(getActivity());
                    textView.setPadding(30, 10, 30, 10 );  // Left, top, right, bottom
                    builder.setView(textView);
                    textView.setText(R.string.project_update_not_supported);

                }

                if (project.getCraftName().equals("Crochet") ||
                        project.getCraftName().equals("Knitting")) {
                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            JsonObject jsonObject = new JsonObject();
                            JsonArray jsonArray = new JsonArray();

                            if (project.getCraftName().equals("Knitting")) {
                                for (int i = 0; i < knittingNeedleSizes.size(); i++) {
                                    if (knittingNeedleSizes.get(i).getChecked())
                                        jsonArray.add(knittingNeedleSizes.get(i).getId());
                                }
                            } else if (project.getCraftName().equals("Crochet")) {
                                for (int i = 0; i < crochetNeedleSizes.size(); i++) {
                                    if (crochetNeedleSizes.get(i).getChecked())
                                        jsonArray.add(crochetNeedleSizes.get(i).getId());
                                }
                            }
                            jsonObject.add("needle_sizes", jsonArray);
                            startUploadIntentService(Constants.POST_PROJECT, project.getId(), jsonObject.toString());

                            dialog.dismiss();
                        }
                    });
                }
                break;

            default:
                break;
        }

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
                    project = mapper.readValue(s_project, Project.class);

                    if (project != null) {
                        reloadData();

                    } else {
                        Snackbar.make(rootView, "Whoops, something went wrong somewhere. We couldn't update your project.", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }

                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        }
    }

    private void loadData(){
        if (project.getRating() != -1)
        {
            ratingBar.setRating(project.getRating() +1);
        }

        seekBar.setProgress(project.getProgress());

        projectItems = new ArrayList<>();

        String s_tags = getTags();

        ArrayList<NeedleSizes> a_needleSizes = new ArrayList<>();
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

        projectItems.add(POSITION_DATE_STARTED, new CardData(getString(R.string.project_date_started), project.getStarted(), "{fa-calendar}"));
        projectItems.add(POSITION_DATE_FINISHED, new CardData(getString(R.string.project_date_completed), project.getCompleted(), "{fa-calendar-check-o}"));
        projectItems.add(POSITION_MADE_FOR, new CardData(getString(R.string.project_made_for), project.getMadeFor(), "{fa-user}"));
        projectItems.add(POSITION_STATUS, new CardData(getString(R.string.project_status), project.getStatusName(), "{fa-list-alt}"));
        projectItems.add(POSITION_TAGS, new CardData(getString(R.string.project_tags), s_tags, "{fa-tags}"));
        projectItems.add(POSITION_SIZE, new CardData(getString(R.string.project_size), project.getSize(), "{fa-asterisk}"));

        if (project.getCraftId() == Project.CRAFT_CROCHET || project.getCraftId() == Project.CRAFT_KNITTING)
            projectItems.add(POSITION_TOOLS, new CardData(getString(R.string.project_needles_hooks), s_needleSizes, "{fa-wrench}"));
        else if (project.getCraftId() == Project.CRAFT_WEAVING)
            projectItems.add(POSITION_TOOLS, new CardData(getString(R.string.project_loom), project.getTools().getMake() +
                    " " + project.getTools().getModel(), "{fa-wrench}" ));


        for (int i = 0; i < a_packs.size(); i++) {
            String yarnString = "";
            yarnString += a_packs.get(i).getYarnName() + "\nColorway: ";
            yarnString += a_packs.get(i).getColorway();

            if (a_packs.get(i).getQuantityDescription() != null)
                yarnString += "\nAmount used: " + a_packs.get(i).getQuantityDescription();

            projectItems.add(new CardData("Yarn: ", yarnString, "{fa-bookmark}"));
        }
    }


    private void reloadData() {

        loadData();
        recyclerViewAdapter.updateList(projectItems);
        Snackbar.make(rootView, "Project updated successfully.", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
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

                switch (type){

                    case Constants.FETCH_NEEDLES_KNITTING:

                        try {
                            JSONObject jObject = new JSONObject(resultDataString);
                            String s_needles = jObject.get("needle_sizes").toString();
                            knittingNeedleSizes = mapper.readValue(s_needles, new  TypeReference<ArrayList<NeedleSizes>>(){});

                            for (NeedleSizes needle : knittingNeedleSizes){
                                String displayName;
                                if (needle.getUs() != null){
                                    String name = needle.getUs();
                                    if (name.equals("8/0"))
                                        name = "00000000";
                                    else if (name.equals("6/0"))
                                        name = "000000";
                                    else if (name.equals("5/0"))
                                        name = "00000";
                                    else if (name.equals("4/0"))
                                        name = "0000";

                                    displayName = "US " + name + " - " + needle.getMetric() + " mm";
                                }else
                                    displayName = needle.getMetric() + " mm";

                                needle.setDisplayName(displayName);
                            }

                            ArrayList<NeedleSizes> projectSizes = project.getNeedleSizes();

                            for (int i = 0; i < projectSizes.size(); i++){
                                for (int j = 0; j < knittingNeedleSizes.size(); j++ ){
                                    if (projectSizes.get(i).getId() == knittingNeedleSizes.get(j).getId())
                                        knittingNeedleSizes.get(j).setChecked(true);
                                }
                            }


                        }catch (Exception e){}

                        break;

                    case Constants.FETCH_NEEDLES_CROCHET:

                        try {
                            JSONObject jObject = new JSONObject(resultDataString);
                            String s_needles = jObject.get("needle_sizes").toString();
                            crochetNeedleSizes = mapper.readValue(s_needles, new  TypeReference<ArrayList<NeedleSizes>>(){});

                            for (NeedleSizes needle : crochetNeedleSizes){
                                String displayName = needle.getMetric() + " mm";
                                if (needle.getHook() != null){
                                    displayName += " (" + needle.getHook() + ")";
                                }
                                needle.setDisplayName(displayName);
                            }

                            ArrayList<NeedleSizes> projectSizes = project.getNeedleSizes();

                            for (int i = 0; i < projectSizes.size(); i++){
                                for (int j = 0; j < crochetNeedleSizes.size(); j++ ){
                                    if (projectSizes.get(i).getId() == crochetNeedleSizes.get(j).getId())
                                        crochetNeedleSizes.get(j).setChecked(true);
                                }
                            }

                        }catch (Exception e){ Log.d(TAG, e.toString()); }

                        break;
                }
            }
        }
    }
}
