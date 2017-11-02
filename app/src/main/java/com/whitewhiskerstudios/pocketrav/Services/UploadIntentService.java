package com.whitewhiskerstudios.pocketrav.Services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.google.gson.Gson;
import com.whitewhiskerstudios.pocketrav.API.RavelryAPI;
import com.whitewhiskerstudios.pocketrav.Interfaces.PocketRavPrefs_;
import com.whitewhiskerstudios.pocketrav.Utils.Constants;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import static com.whitewhiskerstudios.pocketrav.API.Keys.ACCESS_KEY;
import static com.whitewhiskerstudios.pocketrav.API.Keys.SECRET_KEY;

/**
 * Created by rachael on 10/23/17.
 */

@EService
public class UploadIntentService extends IntentService{

    @Pref
    PocketRavPrefs_ prefs;


    private static final String TAG = "UploadIntentService";
    private ResultReceiver resultReceiver;

    private String responseBody = null;
    int postType = -1;

    public UploadIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String errorMessage = "";
        resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        if (resultReceiver == null) {
            Log.wtf(TAG, "No receiver recieved. There is nowhere to send the results.");
            return;
        }

        postType = intent.getIntExtra(Constants.POST_TYPE, -1);

        if (postType == -1) {
            Log.wtf(TAG, "No fetch type declared. There is no data to fetch. What are you doing here?");
            return;
        }

        OAuth10aService service = new ServiceBuilder()
                .apiKey(ACCESS_KEY)
                .apiSecret(SECRET_KEY)
                .build(RavelryAPI.instance());



        String apiCall = "";
        String username = prefs.username().get();

        Gson gson = new Gson();
        String json = prefs.accessToken().get();
        OAuth1AccessToken accessToken = gson.fromJson(json, OAuth1AccessToken.class);

        Log.d("Access Token ", accessToken.getToken());
        Log.d("Token secret ", accessToken.getTokenSecret());

        OAuthRequest oAuthRequest = null;

        switch (postType) {

            case Constants.POST_PROJECT:
                int projectId = intent.getIntExtra(Constants.PROJECT_ID, -1);
                String projectString = intent.getStringExtra(Constants.POST_JSON_STRING);
                apiCall = RavelryAPI.instance().getProject(username, projectId);
                oAuthRequest = new OAuthRequest(Verb.POST, apiCall);
                oAuthRequest.addBodyParameter("data", projectString);
                Log.d(TAG, projectString);
                Log.d(TAG, String.valueOf(projectId));
                break;

            case Constants.POST_STASH:
                int stashId = intent.getIntExtra(Constants.STASH_ID, -1);
                String stashString = intent.getStringExtra(Constants.POST_JSON_STRING);
                int type = intent.getIntExtra(Constants.STASH_TYPE, -1);

                if (type == Constants.STASH_TYPE_YARN)
                    apiCall = RavelryAPI.instance().getStashYarn(username, stashId);
                else
                    apiCall = RavelryAPI.instance().getStashFiber(username, stashId);

                oAuthRequest = new OAuthRequest(Verb.POST, apiCall);
                oAuthRequest.addBodyParameter("data", stashString);
                Log.d(TAG, stashString);
                Log.d(TAG, String.valueOf(stashId));
                break;

        }

        if (apiCall == ""){
            Log.wtf(TAG, "Could not get the API call");
            errorMessage = "Could not create the API call";
        }else if (oAuthRequest == null) {
            Log.wtf(TAG, "oAuthRequest could not be created.");
            errorMessage = "oAuthRequest could not be created.";
        }else{

            // Do the actual work of making the API call
            try {
                service.signRequest(accessToken, oAuthRequest);
                final Response response = service.execute(oAuthRequest);
                responseBody = response.getBody();

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }


        if (errorMessage == "" && responseBody != null) {
            deliverResultToReceiver(Constants.SUCCESS_RESULT, responseBody);
        }
        else
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);

    }

    void deliverResultToReceiver(int resultCode, String message)
    {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        bundle.putInt(Constants.POST_TYPE, postType);
        resultReceiver.send(resultCode, bundle);

    }
}

