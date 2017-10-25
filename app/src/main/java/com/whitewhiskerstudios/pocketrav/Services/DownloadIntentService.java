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
 * Created by rachael on 10/6/17.
 */

@EService
public class DownloadIntentService extends IntentService {

    @Pref
    PocketRavPrefs_ prefs;


    private static final String TAG = "DownloadIntentService";
    private ResultReceiver resultReceiver;

    private String responseBody = null;
    int fetchType = -1;

    public DownloadIntentService() { super(TAG); }

    @Override
    protected void onHandleIntent(Intent intent) {

        String errorMessage = "";
        resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        if (resultReceiver == null) {
            Log.wtf(TAG, "No receiver recieved. There is nowhere to send the results.");
            return;
        }

        fetchType = intent.getIntExtra(Constants.FETCH_TYPE, -1);

        if (fetchType == -1) {
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

        OAuthRequest oAuthRequest = null;

        switch (fetchType) {

            case Constants.FETCH_USER:
                apiCall = RavelryAPI.instance().getUser(username);
                break;

            case Constants.FETCH_PROJECT_LIST:
                apiCall = RavelryAPI.instance().getProjectListRequest(username);
                break;

            case Constants.FETCH_PROJECT:
                int projectId = intent.getIntExtra(Constants.PROJECT_ID, -1);
                if (projectId != -1){
                    apiCall = RavelryAPI.instance().getProject(username, projectId);

                }else{
                    errorMessage = "No project ID.";
                }
                break;

            case Constants.FETCH_NEEDLES_KNITTING:
                apiCall = RavelryAPI.instance().getNeedleSizesKnitting();
                break;

            case Constants.FETCH_NEEDLES_CROCHET:
                apiCall = RavelryAPI.instance().getNeedleSizesCrochet();
            default:
                break;
        }

        if (apiCall == ""){
            Log.wtf(TAG, "Could not get the API call");
            errorMessage = "Could not create the API call";
        }else{

            // Do the actual work of making the API call
            try {
                oAuthRequest = new OAuthRequest(Verb.GET, apiCall);
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
        bundle.putInt(Constants.FETCH_TYPE, fetchType);
        resultReceiver.send(resultCode, bundle);

    }
}
