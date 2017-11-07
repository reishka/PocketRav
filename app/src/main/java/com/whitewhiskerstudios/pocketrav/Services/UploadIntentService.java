package com.whitewhiskerstudios.pocketrav.Services;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.core.json.UTF8JsonGenerator;
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
import org.apache.http.client.HttpClient;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.whitewhiskerstudios.pocketrav.API.Keys.ACCESS_KEY;
import static com.whitewhiskerstudios.pocketrav.API.Keys.SECRET_KEY;

/**
 * Created by rachael on 10/23/17.
 */

@EService
public class UploadIntentService extends RavIntentService{

    @Pref
    PocketRavPrefs_ prefs;


    private static final String TAG = "UploadIntentService";
    private ResultReceiver resultReceiver;

    private String responseBody = null;
    private int responseCode = -1;
    int postType = -1;

    public UploadIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String errorMessage = "";
        resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        if (resultReceiver == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
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
                .debug()
                .build(RavelryAPI.instance());


        String apiCall = "";
        String username = prefs.username().get();

        Gson gson = new Gson();
        String json = prefs.accessToken().get();
        OAuth1AccessToken accessToken = gson.fromJson(json, OAuth1AccessToken.class);

        Log.d("Access Token ", accessToken.getToken());
        Log.d("Token secret ", accessToken.getTokenSecret());

        OAuthRequest oAuthRequest = null;
        String dataString = "";
        String uploadToken = "";
        String image = "";

        switch (postType) {

            case Constants.POST_PROJECT:
                int projectId = intent.getIntExtra(Constants.PROJECT_ID, -1);
                dataString = intent.getStringExtra(Constants.POST_JSON_STRING);
                apiCall = RavelryAPI.instance().getProject(username, projectId);
                Log.d(TAG, dataString);
                Log.d(TAG, String.valueOf(projectId));
                break;

            case Constants.POST_STASH:
                int stashId = intent.getIntExtra(Constants.STASH_ID, -1);
                dataString= intent.getStringExtra(Constants.POST_JSON_STRING);
                int type = intent.getIntExtra(Constants.STASH_TYPE, -1);

                if (type == Constants.STASH_TYPE_YARN)
                    apiCall = RavelryAPI.instance().getStashYarn(username, stashId);
                else
                    apiCall = RavelryAPI.instance().getStashFiber(username, stashId);

                Log.d(TAG, dataString);
                Log.d(TAG, String.valueOf(stashId));
                break;

            // We're getting a token back from Ravelry, but Ravelry actually wants
            // us to make a POST for reasons I don't understand...

            case Constants.POST_UPLOAD_TOKEN:
                apiCall = RavelryAPI.instance().getUploadToken();
                break;


            case Constants.POST_UPLOAD_PHOTOS:

                image = intent.getStringExtra(Constants.UPLOAD_PHOTO);
                uploadToken = intent.getStringExtra(Constants.UPLOAD_TOKEN);

                if (!image.isEmpty() && !uploadToken.isEmpty())
                    apiCall = RavelryAPI.instance().getUploadPhoto();

                break;

            case Constants.POST_CREATE_PHOTO:

                dataString = intent.getStringExtra(Constants.POST_JSON_STRING);
                projectId =  intent.getIntExtra(Constants.PROJECT_ID, -1);

                if (projectId != -1){
                    apiCall = RavelryAPI.instance().getCreatePhoto(username, projectId);
                }

            default:
                break;

        }

        if (apiCall.isEmpty() ){
            if (!errorMessage.isEmpty())
            {
                Log.wtf(TAG, errorMessage);
            }else {
                Log.wtf(TAG, "Could not get the API call");
                errorMessage = "Could not create the API call";
            }

        }else{

            // Do the actual work of making the API call
            try {
                oAuthRequest = new OAuthRequest(Verb.POST, apiCall);
                if (!dataString.isEmpty())
                    oAuthRequest.addBodyParameter("data", dataString);

                else if (!image.isEmpty()){

                    MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                    //MultipartEntity multipartEntity = new MultipartEntity();

                    // Image uploading succeeds, but processing fails. Rav docs say most common cause is
                    // bad filetype...?
                    multipartEntity.addPart("upload_token", new StringBody(uploadToken));
                    multipartEntity.addPart("access_key", new StringBody(accessToken.getTokenSecret()));

                    //File imageFile = new File(image);

                    File file = new File(getApplication().getCacheDir(), "temp_image");
                    file.createNewFile();

                    Bitmap bitmap = BitmapFactory.decodeFile(image);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();


                    multipartEntity.addPart("file0", new FileBody(file));

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    multipartEntity.writeTo(outputStream);

                    oAuthRequest.setPayload(outputStream.toByteArray());
                    oAuthRequest.addHeader(multipartEntity.getContentType().getName(), multipartEntity.getContentType().getValue());
                    Log.d(TAG, outputStream.toString());
                }


                service.signRequest(accessToken, oAuthRequest);
                final Response response = service.execute(oAuthRequest);
                responseBody = response.getBody();
                responseCode = response.getCode();

                Log.e(TAG, responseBody);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }


        if (responseCode == RESPONSE_OK) {

            if (errorMessage.isEmpty() && responseBody != null) {

                deliverResultToReceiver(Constants.SUCCESS_RESULT, responseBody);
            } else
                deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);

        } else {
            if (responseCode == -1) {     // We never even got to make a request...
                if (!errorMessage.isEmpty())
                    deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
                else
                    deliverResultToReceiver(Constants.FAILURE_RESULT, "Something really screwed up!");
            }else{
                errorMessage = getErrorResponseString(responseCode);
                Log.d(TAG, errorMessage);
                deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            }
        }
    }

    void deliverResultToReceiver(int resultCode, String message)
    {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        bundle.putInt(Constants.POST_TYPE, postType);
        resultReceiver.send(resultCode, bundle);

    }
}

