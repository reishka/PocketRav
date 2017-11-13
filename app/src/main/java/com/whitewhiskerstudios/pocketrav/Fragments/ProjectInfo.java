package com.whitewhiskerstudios.pocketrav.Fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.ResultReceiver;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.eclipsesource.json.Json;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.whitewhiskerstudios.pocketrav.API.Models.NeedleSizes;
import com.whitewhiskerstudios.pocketrav.API.Models.Pack;
import com.whitewhiskerstudios.pocketrav.API.Models.Project;
import com.whitewhiskerstudios.pocketrav.API.Models.Tools;
import com.whitewhiskerstudios.pocketrav.Activities.ProjectActivity;
import com.whitewhiskerstudios.pocketrav.Adapters.RecyclerViewAdapterWithFontAwesome;
import com.whitewhiskerstudios.pocketrav.Interfaces.PocketRavPrefs_;
import com.whitewhiskerstudios.pocketrav.R;
import com.whitewhiskerstudios.pocketrav.Services.DownloadIntentService_;
import com.whitewhiskerstudios.pocketrav.Services.UploadIntentService_;
import com.whitewhiskerstudios.pocketrav.Utils.CardData;
import com.whitewhiskerstudios.pocketrav.Utils.Constants;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;

/**
 * Created by rachael on 10/14/17.
 */

@EFragment
public class ProjectInfo extends Fragment{

    @Pref
    PocketRavPrefs_ prefs;

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
    ArrayList<NeedleSizes> crochetHookSizes = new ArrayList<>();

    private UploadIntentResultReceiver uploadIntentResultReceiver;
    private DownloadIntentResultReceiver downloadIntentResultReceiver;

    String uploadToken = "";
    Uri selectedImage = null;
    String imagePath = "";
    ArrayList<String> imageIds = null;

    private static final int POSITION_DATE_STARTED = 0;
    private static final int POSITION_DATE_FINISHED = 1;
    private static final int POSITION_MADE_FOR = 2;
    private static final int POSITION_STATUS = 3;
    private static final int POSITION_TAGS = 4;
    private static final int POSITION_SIZE = 5;
    private static final int POSITION_TOOLS = 6;
    private static final int POSITION_EPI_PPI = 7;

    private static final int CAMERA_LOAD_IMAGE = 1000;

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


                ObjectMapper mapper = new ObjectMapper();

                try {

                    String knittingJson = prefs.knittingNeedleSizes().get();
                    String crochetingJson = prefs.crochetHookSizes().get();

                    if (knittingJson == null || knittingJson.isEmpty()) {
                        startDownloadIntentService(Constants.FETCH_NEEDLES_KNITTING);
                    } else {
                        knittingNeedleSizes = mapper.readValue(knittingJson, new TypeReference<ArrayList<NeedleSizes>>() {
                        });
                    }

                    if (crochetingJson == null || crochetingJson.isEmpty()) {
                        startDownloadIntentService(Constants.FETCH_NEEDLES_CROCHET);
                    } else {
                        crochetHookSizes = mapper.readValue(crochetingJson, new TypeReference<ArrayList<NeedleSizes>>() {
                        });
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

                initViews();
                loadData();

                try {

                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {

                            if (position == POSITION_DATE_STARTED ||
                                    position == POSITION_DATE_FINISHED ||
                                    position == POSITION_MADE_FOR ||
                                    position == POSITION_STATUS ||
                                    position == POSITION_SIZE ||
                                    position == POSITION_TOOLS) {
                                return 1;
                            } else
                                return 2;
                        }
                    });

                    recyclerView.setLayoutManager(gridLayoutManager);

                    recyclerViewAdapter = new RecyclerViewAdapterWithFontAwesome(projectItems);
                    recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapterWithFontAwesome.MyClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
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

        LinearLayout stash_bar = (LinearLayout)rootView.findViewById(R.id.stash_bar);
        stash_bar.setVisibility(View.GONE);

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        LinearLayout recyclerViewParent = (LinearLayout)recyclerView.getParent();
        recyclerViewParent.setBackgroundColor(Color.TRANSPARENT); // RecyclerView has a gradient, but our activity also has a gradient, so remove the rv gradient

        Button cameraButton = rootView.findViewById(R.id.add_photo);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(cameraIntent, CAMERA_LOAD_IMAGE);
                    }
                }
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_LOAD_IMAGE && resultCode == RESULT_OK && data != null){

            selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imagePath = cursor.getString(columnIndex);

                cursor.close();
            }


            // TODO:  Project Create Photo API Call goes here
            // Note that in order to upload photos, we need to:
            // 1. Get a token from POST /upload/request_token.json
            // 2. Use this token with an array of images with POST /upload/image.json
            // 3. Keep an eye on the image uploading/processing with GET /upload/image/status.json (may not be necessary)
            // 4. Get the Image ID and associate it with our project

            if (uploadToken.isEmpty())
                startUploadIntentService(Constants.POST_UPLOAD_TOKEN);
            else
                uploadPhotos();
        }
    }

    private void uploadPhotos(){

        if (!uploadToken.isEmpty() && !imagePath.isEmpty()){
        //if (selectedImage != null && !uploadToken.isEmpty()){
            startUploadIntentService(Constants.POST_UPLOAD_PHOTOS, uploadToken, imagePath);
        }

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

                        jsonObject.addProperty(Project.PROPERTY_STARTED, date);
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

                        jsonObject.addProperty(Project.PROPERTY_COMPLETED, date);
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
                        jsonObject.addProperty(Project.PROPERTY_MADE_FOR, et_input.getText().toString());
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
                        jsonObject.addProperty(Project.PROPERTY_STATUS_ID, String.valueOf(i_status));
                        jsonObject.addProperty(Project.PROPERTY_STATUS_NAME, s_status);
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
                        String[] a_tags = new String[1];
                        JsonArray jsonArray = new JsonArray();

                        tags = tags.trim();

                        if (tags.contains(","))
                             a_tags = tags.split(",");  // Multiple tags with commas
                        else if (tags.contains(" "))
                            a_tags = tags.split(" ");   // Multiple tags with spaces
                        else
                                a_tags[0] = tags;       // Only 1 tag

                        if (a_tags.length > 0 ) {
                            for (int i = 0; i < a_tags.length; i++) {
                                a_tags[i] = a_tags[i].trim();
                                if (a_tags[i].contains(" ")) {
                                    a_tags[i] = a_tags[i].replace(" ", ""); // Rav smushes everything together into one tag
                                }
                            }
                            for (String tag : a_tags)
                                jsonArray.add(tag);
                        }
                            jsonObject.add(Project.PROPERTY_TAG_NAMES, jsonArray);
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
                        jsonObject.addProperty(Project.PROPERTY_SIZE, et_input.getText().toString());
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

                    String[] displayNames = new String[crochetHookSizes.size()];
                    boolean[] checkedStatus = new boolean[crochetHookSizes.size()];

                    for (int i = 0; i < crochetHookSizes.size(); i++) {
                        displayNames[i] = crochetHookSizes.get(i).getDisplayName();
                        checkedStatus[i] = crochetHookSizes.get(i).getChecked();
                    }

                    builder.setMultiChoiceItems(displayNames, checkedStatus, new DialogInterface.OnMultiChoiceClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                            if (isChecked) {
                                crochetHookSizes.get(which).setChecked(true);
                            } else if (crochetHookSizes.get(which).getChecked())
                                crochetHookSizes.get(which).setChecked(false);
                        }
                    });
                }else {

                    TextView textView = new TextView(getActivity());
                    textView.setPadding(30, 10, 30, 10 );  // Left, top, right, bottom
                    builder.setView(textView);
                    textView.setText(R.string.project_update_not_supported);

                }

                if (project.getCraftId() == Project.CRAFT_CROCHET ||
                        project.getCraftId() == Project.CRAFT_KNITTING) {
                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            JsonObject jsonObject = new JsonObject();
                            JsonArray jsonArray = new JsonArray();

                            switch (project.getCraftId()){
                                case Project.CRAFT_KNITTING:
                                    for (int i = 0; i < knittingNeedleSizes.size(); i++) {
                                        if (knittingNeedleSizes.get(i).getChecked())
                                            jsonArray.add(knittingNeedleSizes.get(i).getId());
                                    }
                                    break;
                                case Project.CRAFT_CROCHET:
                                    for (int i = 0; i < crochetHookSizes.size(); i++) {
                                        if (crochetHookSizes.get(i).getChecked())
                                            jsonArray.add(crochetHookSizes.get(i).getId());
                                    }
                                    break;
                            }

                            jsonObject.add(Project.PROPERTY_NEEDLE_SIZES, jsonArray);
                            startUploadIntentService(Constants.POST_PROJECT, project.getId(), jsonObject.toString());

                            dialog.dismiss();
                        }
                    });
                }
                break;

            case POSITION_EPI_PPI:
                if (project.getCraftId() == Project.CRAFT_WEAVING)
                {
                    //builder.setView(et_input);
                    builder.setTitle("EPI & PPI");
                    final EditText et_input2 = new EditText(getActivity());
                    TextView tv_epi = new TextView(getActivity());
                    TextView tv_ppi = new TextView(getActivity());
                    LinearLayout linearLayout = new LinearLayout(getActivity());



                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    linearLayout.setLayoutParams(params);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    tv_epi.setText("EPI: ");
                    tv_ppi.setText("PPI: ");

                    linearLayout.addView(tv_epi);
                    linearLayout.addView(et_input);
                    linearLayout.addView(tv_ppi);
                    linearLayout.addView(et_input2);

                    builder.setView(linearLayout);

                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            JsonObject jsonObject = new JsonObject();

                            if (!et_input.getText().toString().isEmpty())
                                jsonObject.addProperty(Project.PROPERTY_ENDS_PER_INCH, Float.valueOf(et_input.getText().toString()));

                            if (!et_input2.getText().toString().isEmpty())
                                jsonObject.addProperty(Project.PROPERTY_PICKS_PER_INCH, Float.valueOf(et_input2.getText().toString()));

                            startUploadIntentService(Constants.POST_PROJECT, project.getId(), jsonObject.toString());

                        dialog.dismiss();
                                }
                    });



                }

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


    // Weird shit for RavelryAPI quirks in getting the upload token...
    private void startUploadIntentService(int type){

        Intent intent = new Intent(getActivity(), UploadIntentService_.class);
        intent.putExtra(Constants.RECEIVER, uploadIntentResultReceiver);
        intent.putExtra(Constants.POST_TYPE, type);
        getActivity().startService(intent);
    }

    // Project update
    private void startUploadIntentService(int type, int id, String string){

        showUploadDataMessage("Updating your project");

        Intent intent = new Intent(getActivity(), UploadIntentService_.class);
        intent.putExtra(Constants.RECEIVER, uploadIntentResultReceiver);
        intent.putExtra(Constants.POST_TYPE, type);
        intent.putExtra(Constants.PROJECT_ID, id);
        intent.putExtra(Constants.POST_JSON_STRING, string);
        getActivity().startService(intent);
    }

    // Image upload
    private void startUploadIntentService(int type, String token, String file){

        showUploadDataMessage("Uploading your image...");

        Intent intent = new Intent(getActivity(), UploadIntentService_.class);
        intent.putExtra(Constants.RECEIVER, uploadIntentResultReceiver);
        intent.putExtra(Constants.POST_TYPE, type);
        intent.putExtra(Constants.UPLOAD_TOKEN, token);
        intent.putExtra(Constants.UPLOAD_PHOTO, file);
        getActivity().startService(intent);
    }

    // Associating image with a project
    private void startUploadIntentService(int type, int projectId, int photoId){

        Intent intent = new Intent(getActivity(), UploadIntentService_.class);
        intent.putExtra(Constants.RECEIVER, uploadIntentResultReceiver);
        intent.putExtra(Constants.POST_TYPE, type);
        intent.putExtra(Constants.PROJECT_ID, projectId);
        intent.putExtra(Constants.PHOTO_ID, photoId);
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
                Log.d(TAG, resultData.toString());

                String resultDataString = resultData.getString(Constants.RESULT_DATA_KEY);
                ObjectMapper mapper = new ObjectMapper();

                int type = resultData.getInt(Constants.POST_TYPE);

                switch (type) {

                    case Constants.POST_PROJECT:

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

                        break;

                    case Constants.POST_UPLOAD_TOKEN:

                        if (resultDataString != null && !resultDataString.isEmpty()) {

                            String[] response = new String[0];
                            response = resultDataString.split(":");
                            response[1] = response[1].replace("\"", "");
                            response[1] = response[1].replace("}", "");

                            uploadToken = response[1];
                            uploadPhotos();
                        }
                        break;

                    case Constants.POST_UPLOAD_PHOTOS:

                        com.eclipsesource.json.JsonObject object = Json.parse(resultDataString).asObject();
                        //imageIds = new ArrayList<>();

                        try {

                            // We can have at most 10 images. Ravelry returns the image IDs as an array
                            // inside of a "file#" array. So, let's try to examine each one by one and
                            // store the IDs so we can POST them back to the project.

                            // Currently, we only allow the user to upload one image at a time. Maybe
                            // in the future we can consider multiple image upload.

                            com.eclipsesource.json.JsonObject file = object.get("uploads").asObject();

                            for (int i = 0; i < file.size(); i++) {
                                String fileString = "file" + i;
                                com.eclipsesource.json.JsonObject image = file.get(fileString).asObject();
                                int imageId = image.get("image_id").asInt();

                                startUploadIntentService(Constants.POST_CREATE_PHOTO, project.getId(), imageId);
                                }

                            uploadToken = "";

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                            uploadToken = "";

                        }

                        break;

                    case Constants.POST_CREATE_PHOTO:
                        Log.d(TAG, resultDataString);

                        showUploadDataMessage("Waiting for the server to process your uploaded image.");

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                // this code will be executed after 15 seconds
                                startDownloadIntentService(Constants.FETCH_PROJECT, project.getId());
                            }
                        }, 15000);
                }
            }
            else{
                String errorMessage = resultData.getString(Constants.RESULT_DATA_KEY);
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    final Snackbar snackbar = Snackbar.make(rootView, errorMessage + " Your project was not updated.", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
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

        ArrayList<Tools> a_tools = project.getTools();
        String s_tools = "";
        for (int i = 0; i < a_tools.size(); i++){
            if (i == a_tools.size() - 1)
                s_tools += a_tools.get(i).getMake() + " " + a_tools.get(i).getModel();
            else
                s_tools += a_tools.get(i).getMake() + " " + a_tools.get(i).getModel() + ", \n";
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
        else if (project.getCraftId() == Project.CRAFT_WEAVING) {
            String epi_ppi = project.getEndsPerInch() + " EPI x " + project.getPicksPerInch() + " PPI";
            projectItems.add(POSITION_TOOLS, new CardData(getString(R.string.project_loom), s_tools, "{fa-wrench}"));
            projectItems.add(POSITION_EPI_PPI, new CardData(getString(R.string.project_epi_ppi), epi_ppi, "{fa-wrench}" ));
        }

        for (int i = 0; i < a_packs.size(); i++) {
            String yarnString = "";
            yarnString += a_packs.get(i).getYarnName() + "\nColorway: ";
            yarnString += a_packs.get(i).getColorway();

            if (a_packs.get(i).getQuantityDescription() != null)
                yarnString += "\nAmount used: " + a_packs.get(i).getQuantityDescription();

            projectItems.add(new CardData("Yarn: ", yarnString, "{fa-bookmark}"));
        }
    }

    private void showUploadDataMessage(String message){

        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    private void showDownloadDataMessage(){

        Snackbar.make(rootView, "Downloading data.", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();

    }

    private void reloadData() {

        loadData();

        recyclerViewAdapter.updateList(projectItems);
        Snackbar.make(rootView, "Project updated successfully.", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    private void startDownloadIntentService(int type){

        showDownloadDataMessage();

        Intent intent = new Intent(getActivity(), DownloadIntentService_.class);
        intent.putExtra(Constants.RECEIVER, downloadIntentResultReceiver);
        intent.putExtra(Constants.FETCH_TYPE, type);
        getActivity().startService(intent);
    }

    private void startDownloadIntentService(int type, int id){

        showDownloadDataMessage();

        Intent intent = new Intent(getActivity(), DownloadIntentService_.class);
        intent.putExtra(Constants.RECEIVER, downloadIntentResultReceiver);
        intent.putExtra(Constants.FETCH_TYPE, type);
        intent.putExtra(Constants.PROJECT_ID, id);
        getActivity().startService(intent);
    }

    private class DownloadIntentResultReceiver extends ResultReceiver {
        private DownloadIntentResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == Constants.SUCCESS_RESULT) {

                int fetchType = resultData.getInt(Constants.FETCH_TYPE);
                String resultDataString = resultData.getString(Constants.RESULT_DATA_KEY);
                ObjectMapper mapper = new ObjectMapper();

                switch (fetchType) {

                    case Constants.FETCH_NEEDLES_KNITTING:

                        try {
                            JSONObject jObject = new JSONObject(resultDataString);
                            String s_needles = jObject.get("needle_sizes").toString();
                            knittingNeedleSizes = mapper.readValue(s_needles, new TypeReference<ArrayList<NeedleSizes>>() {
                            });

                            for (NeedleSizes needle : knittingNeedleSizes) {
                                String displayName;
                                if (needle.getUs() != null) {
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
                                } else
                                    displayName = needle.getMetric() + " mm";

                                needle.setDisplayName(displayName);
                            }


                            Gson gson = new Gson();
                            String json_knitting = gson.toJson(knittingNeedleSizes);
                            prefs.knittingNeedleSizes().put(json_knitting);


                            ArrayList<NeedleSizes> projectSizes = project.getNeedleSizes();

                            for (int i = 0; i < projectSizes.size(); i++) {
                                for (int j = 0; j < knittingNeedleSizes.size(); j++) {
                                    if (projectSizes.get(i).getId() == knittingNeedleSizes.get(j).getId())
                                        knittingNeedleSizes.get(j).setChecked(true);
                                }
                            }


                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }

                        break;

                    case Constants.FETCH_NEEDLES_CROCHET:

                        try {
                            JSONObject jObject = new JSONObject(resultDataString);
                            String s_needles = jObject.get("needle_sizes").toString();
                            crochetHookSizes = mapper.readValue(s_needles, new TypeReference<ArrayList<NeedleSizes>>() {
                            });

                            for (NeedleSizes needle : crochetHookSizes) {
                                String displayName = needle.getMetric() + " mm";
                                if (needle.getHook() != null) {
                                    displayName += " (" + needle.getHook() + ")";
                                }
                                needle.setDisplayName(displayName);
                            }

                            Gson gson = new Gson();
                            String json_crochet = gson.toJson(crochetHookSizes);
                            prefs.crochetHookSizes().put(json_crochet);

                            ArrayList<NeedleSizes> projectSizes = project.getNeedleSizes();

                            for (int i = 0; i < projectSizes.size(); i++) {
                                for (int j = 0; j < crochetHookSizes.size(); j++) {
                                    if (projectSizes.get(i).getId() == crochetHookSizes.get(j).getId())
                                        crochetHookSizes.get(j).setChecked(true);
                                }
                            }

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }

                        break;

                    case Constants.FETCH_PROJECT:

                        try {
                            JSONObject jObject = new JSONObject(resultDataString);
                            String s_project = jObject.get("project").toString();
                            Project tempProject = mapper.readValue(s_project, Project.class);

                            if (tempProject != null) {
                                if (tempProject.getPhotos().size() != project.getPhotos().size())
                                    ((ProjectActivity) getActivity()).setupSlideshow(tempProject);
                            }

                            project = tempProject;

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

            else{
                String errorMessage = resultData.getString(Constants.RESULT_DATA_KEY);
                if (errorMessage != null && !errorMessage.isEmpty()) {

                    final Snackbar snackbar = Snackbar.make(rootView, errorMessage + " Your project was not updated.", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                }

            }


        }
    }

}
