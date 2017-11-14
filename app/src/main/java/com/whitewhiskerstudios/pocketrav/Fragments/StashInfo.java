package com.whitewhiskerstudios.pocketrav.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.eclipsesource.json.Json;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.whitewhiskerstudios.pocketrav.API.Models.Stash;
import com.whitewhiskerstudios.pocketrav.Activities.StashActivity;
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
 * Created by rachael on 10/31/17.
 */

@EFragment
public class StashInfo extends Fragment {

    @Pref
    PocketRavPrefs_ prefs;

    private static final String TAG = "Stash Info Fragment";

    private View rootView;
    private Bundle bundle;
    private Stash stash = null;
    int stashType = -1;

    private RatingBar ratingBar;
    private DiscreteSeekBar seekBar;
    private RecyclerView recyclerView;
    RecyclerViewAdapterWithFontAwesome recyclerViewAdapter;
    ArrayList<CardData> stashItems;

    private UploadIntentResultReceiver uploadIntentResultReceiver;
    private DownloadIntentResultReceiver downloadIntentResultReceiver;

    String uploadToken = "";
    Uri selectedImage = null;
    String imagePath = "";
    ArrayList<String> imageIds = null;

    // Yarn Stash
    private static final int POSITION_YARNSTASH_COMPANY = 0;
    private static final int POSITION_YARNSTASH_COLORWAY = 1;
    private static final int POSITION_YARNSTASH_COLOR_FAMILY = 2;
    private static final int POSITION_YARNSTASH_STATUS = 3;
    private static final int POSITION_YARNSTASH_HANDSPUN = 4;
    private static final int POSITION_YARNSTASH_LOCATION = 5;
    private static final int POSITION_YARNSTASH_WEIGHT = 6;
    //private static final int POSITION_YARNSTASH_NEEDLESIZES = 7;
    //private static final int POSITION_QUANTITY = 7;

    // Fiber Stash

    private static final int POSITION_FIBER_COMPANY_NAME = 0;
    private static final int POSITION_FIBER_COLORWAY_NAME = 1;
    private static final int POSITION_FIBER_LOCATION = 2;
    private static final int POSITION_FIBER_STASH_STATUS = 3;
    private static final int POSITION_FIBER_STASH_COLOR_FAMILY = 4;
    private static final int POSITION_FIBER_PURCHASED_AT = 5;

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
            stashType = bundle.getInt(Constants.STASH_TYPE); // We need to know this for POST, since rav has different methods for stash/yarn_stash
            stash = (Stash) bundle.getSerializable(Constants.STASH_BUNDLE);

            initViews();
            loadData();

            try {

                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
                    @Override
                    public int getSpanSize(int position){

                        if (position == POSITION_YARNSTASH_COLORWAY     ||
                                position == POSITION_YARNSTASH_COLOR_FAMILY ||
                                position == POSITION_YARNSTASH_STATUS       ||
                                position == POSITION_YARNSTASH_HANDSPUN     ||
                                position == POSITION_YARNSTASH_LOCATION     ||
                                position == POSITION_YARNSTASH_WEIGHT )

                                //position == POSITION_QUANTITY               )
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
                int type = resultData.getInt(Constants.POST_TYPE);

                switch(type) {

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

                                startUploadIntentService(Constants.POST_CREATE_PHOTO, stash.getId(), imageId);
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
                                startDownloadIntentService(Constants.FETCH_PROJECT, stashType, stash.getId());
                            }
                        }, 15000);

                        break;

                    case Constants.POST_STASH:

                        if (stashType == Constants.STASH_TYPE_YARN) {
                            try {
                                JSONObject jObject = new JSONObject(resultDataString);
                                String s_yarnStash = jObject.get("stash").toString();
                                stash = mapper.readValue(s_yarnStash, Stash.class);

                            } catch (Exception e) {
                                Log.d(TAG, e.toString());
                            }
                        } else {
                            try {
                                JSONObject jObject = new JSONObject(resultDataString);
                                String s_fiberStash = jObject.get("fiber_stash").toString();
                                stash = mapper.readValue(s_fiberStash, Stash.class);

                            } catch (Exception e) {
                                Log.d(TAG, e.toString());
                            }
                        }

                        if (stash != null) {
                            reloadData();

                        } else {
                            Snackbar.make(rootView, "Whoops, something went wrong somewhere. We couldn't update your yarn stash.", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }

                        break;
                }
            }
        }
    }

    private void loadData(){

        stashItems = new ArrayList<>();

        switch (stashType){
            case Constants.STASH_TYPE_YARN:
                stashItems.add(POSITION_YARNSTASH_COMPANY, new CardData(getString(R.string.stash_yarn_company), stash.getYarn().getYarnCompany().getName(), "{fa-user}"));
                stashItems.add(POSITION_YARNSTASH_COLORWAY, new CardData(getString(R.string.stash_colorway), stash.getColorwayName(), "{fa-comment}"));
                stashItems.add(POSITION_YARNSTASH_COLOR_FAMILY, new CardData(getString(R.string.stash_color_family), stash.getColorFamilyName(), "{fa-cog}"));
                stashItems.add(POSITION_YARNSTASH_STATUS, new CardData(getString(R.string.stash_status), stash.getStashStatus().getName(), "{fa-list-ol}"));
                stashItems.add(POSITION_YARNSTASH_HANDSPUN, new CardData(getString(R.string.stash_handspun), stash.isHandspun().toString(), "{fa-check}"));
                stashItems.add(POSITION_YARNSTASH_LOCATION, new CardData(getString(R.string.stash_location), stash.getLocation(), "{fa-location-arrow}"));
                stashItems.add(POSITION_YARNSTASH_WEIGHT, new CardData(getString(R.string.stash_weight_name), stash.getYarnWeight(), "{fa-balance-scale}"));

                break;

            case Constants.STASH_TYPE_FIBER:

                stashItems.add(POSITION_FIBER_COMPANY_NAME, new CardData("Fiber Company :", stash.getFiberCompanyName(), "{fa-calendar}"));
                stashItems.add(POSITION_FIBER_COLORWAY_NAME, new CardData("Colorway :", stash.getColorwayName(), "{fa-calendar}"));
                stashItems.add(POSITION_FIBER_LOCATION, new CardData("Location :", stash.getLocation(), "{fa-calendar}"));
                stashItems.add(POSITION_FIBER_STASH_STATUS, new CardData("Status :", stash.getStashStatus().getName(), "{fa-calendar}"));
                //stashItems.add(POSITION_FIBER_STASH_COLOR_FAMILY, new CardData("Status :", stash.getLongName(), "{fa-calendar}"));
                //stashItems.add(POSITION_FIBER_PURCHASED_AT, new CardData("Status :", stash.get, "{fa-calendar}"));


                // Eventually we should show what projects are associated with a stash.
                //ArrayList<FiberPack> packs = stash.getFiberPacks();

                //for (FiberPack pack : packs){

                  //  stashItems.add(new CardData("Yarn: ", yarnString, "{fa-bookmark}"));

                //}

                break;

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
        Snackbar.make(rootView, "Stash updated successfully.", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    // Weird shit for RavelryAPI quirks in getting the upload token...
    private void startUploadIntentService(int type){

        Intent intent = new Intent(getActivity(), UploadIntentService_.class);
        intent.putExtra(Constants.RECEIVER, uploadIntentResultReceiver);
        intent.putExtra(Constants.POST_TYPE, type);
        getActivity().startService(intent);
    }

    // Stash update
    private void startUploadIntentService(int type, int id, String string){

        showUploadDataMessage("Updating your stash.");

        Intent intent = new Intent(getActivity(), UploadIntentService_.class);
        intent.putExtra(Constants.RECEIVER, uploadIntentResultReceiver);
        intent.putExtra(Constants.POST_TYPE, type);
        intent.putExtra(Constants.STASH_ID, id);
        intent.putExtra(Constants.POST_JSON_STRING, string);
        getActivity().startService(intent);
    }

    // Image upload
    private void startUploadIntentService(int type, String token, String file){

        showUploadDataMessage("Uploading your image.");

        Intent intent = new Intent(getActivity(), UploadIntentService_.class);
        intent.putExtra(Constants.RECEIVER, uploadIntentResultReceiver);
        intent.putExtra(Constants.POST_TYPE, type);
        intent.putExtra(Constants.UPLOAD_TOKEN, token);
        intent.putExtra(Constants.UPLOAD_PHOTO, file);
        getActivity().startService(intent);
    }

    // Associating image with a stash
    private void startUploadIntentService(int type, int stashId, int photoId){

        Intent intent = new Intent(getActivity(), UploadIntentService_.class);
        intent.putExtra(Constants.RECEIVER, uploadIntentResultReceiver);
        intent.putExtra(Constants.POST_TYPE, type);
        intent.putExtra(Constants.STASH_ID, stashId);
        intent.putExtra(Constants.PHOTO_ID, photoId);
        getActivity().startService(intent);
    }



    private void showUploadDataMessage(String message){

        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    private void showDownloadDataMessage(){

        Snackbar.make(rootView, "Downloading data.", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();

    }

    private void startDownloadIntentService(int type, int stashType,  int id){

        showDownloadDataMessage();

        Intent intent = new Intent(getActivity(), DownloadIntentService_.class);
        intent.putExtra(Constants.RECEIVER, downloadIntentResultReceiver);
        intent.putExtra(Constants.FETCH_TYPE, type);
        intent.putExtra(Constants.STASH_TYPE, stashType);
        intent.putExtra(Constants.STASH_ID, id);
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
                Stash tempStash = null;

                switch (fetchType) {


                    case Constants.FETCH_STASH_FIBER:
                        try {

                            JSONObject jObject = new JSONObject(resultDataString);
                            String s_fiberStash = jObject.get("fiber_stash").toString();
                            tempStash = mapper.readValue(s_fiberStash, Stash.class);

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                        if (tempStash != null) {
                            if (tempStash.getPhotos().size() != stash.getPhotos().size()) {
                                ((StashActivity) getActivity()).setupSlideshow(tempStash);
                                stash = tempStash;
                            } else {
                                Snackbar.make(rootView, "Whoops, something went wrong somewhere. We couldn't update your yarn stash.", Snackbar.LENGTH_SHORT)
                                        .setAction("Action", null).show();
                            }
                        }
                        break;

                    case Constants.FETCH_STASH_YARN:
                        try {

                            JSONObject jObject = new JSONObject(resultDataString);
                            String s_yarnStash = jObject.get("stash").toString();
                            tempStash = mapper.readValue(s_yarnStash, Stash.class);

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                        if (tempStash != null) {
                            if (tempStash.getPhotos().size() != stash.getPhotos().size()) {
                                ((StashActivity) getActivity()).setupSlideshow(tempStash);
                                stash = tempStash;
                            } else {
                                Snackbar.make(rootView, "Whoops, something went wrong somewhere. We couldn't update your yarn stash.", Snackbar.LENGTH_SHORT)
                                        .setAction("Action", null).show();
                            }
                        }
                        break;
                }
            }
            else{
                String errorMessage = resultData.getString(Constants.RESULT_DATA_KEY);
                if (errorMessage != null && !errorMessage.isEmpty()) {

                    final Snackbar snackbar = Snackbar.make(rootView, errorMessage + " Your stash was not updated.", Snackbar.LENGTH_INDEFINITE);
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
