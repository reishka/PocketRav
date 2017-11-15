package com.whitewhiskerstudios.pocketrav.Services;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.whitewhiskerstudios.pocketrav.API.Keys;
import com.whitewhiskerstudios.pocketrav.API.RavelryAPI;
import com.whitewhiskerstudios.pocketrav.Interfaces.PocketRavPrefs_;
import com.whitewhiskerstudios.pocketrav.Utils.Constants;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.ByteArrayOutputStream;
import java.io.File;

import static com.whitewhiskerstudios.pocketrav.API.Keys.CONSUMER_KEY;
import static com.whitewhiskerstudios.pocketrav.API.Keys.CONSUMER_SECRET;

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
    private int postType = -1;

    public final static boolean DEBUG = false;

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
                .apiKey(CONSUMER_KEY)
                .apiSecret(CONSUMER_SECRET)
                //.debug()
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
        int imageId = -1;

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

                //dataString = intent.getStringExtra(Constants.POST_JSON_STRING);
                projectId =  intent.getIntExtra(Constants.PROJECT_ID, -1);
                imageId = intent.getIntExtra(Constants.PHOTO_ID, -1);

                if (projectId != -1 && imageId != -1){
                    apiCall = RavelryAPI.instance().getCreatePhoto(username, projectId, imageId);
                }
                break;

            case Constants.POST_CREATE_PHOTO_STASH:

                stashId =  intent.getIntExtra(Constants.STASH_ID, -1);
                imageId = intent.getIntExtra(Constants.PHOTO_ID, -1);
                int stashType = intent.getIntExtra(Constants.STASH_TYPE, -1);


                if (stashId != -1 && imageId != -1 && stashType != -1){

                    if (stashType == Constants.STASH_TYPE_YARN)
                        apiCall = RavelryAPI.instance().getCreatePhotoYarn(username, stashId, imageId);
                    else
                        apiCall = RavelryAPI.instance().getCreatePhotoFiber(username, stashId, imageId);
                }
                break;

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

        }else {

            // Do the actual work of making the API call
            try {
                oAuthRequest = new OAuthRequest(Verb.POST, apiCall);

                // Add Body Parameters for the requests that need it

                if (!image.isEmpty()){

                    //MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                    //MultipartEntity multipartEntity = new MultipartEntity();

                    // Image uploading succeeds, but processing fails. Rav docs say most common cause is
                    // bad filetype...?
                    // Posted on rav to see if there is a reason why all image processing is failing.

                    Log.d(TAG, "image: " + image );

                    //Uri uri = Uri.parse(image);
                    File imageFile = new File(image);

                    StringBody multipartUploadToken = new StringBody(uploadToken);
                    StringBody multipartAccessKey = new StringBody(accessToken.getTokenSecret());
                    //FileBody multipartFile0 = new FileBody(imageFile);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Bitmap bm = BitmapFactory.decodeFile(image);
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                    byte[] byteImage_photo = baos.toByteArray();

                    Log.i("BM Size:", " Byte Count: " + bm.getByteCount());
                    Log.i("baos Size:", " Size: " + baos.size());
                    Log.i("Byte Image photo:", " Image photo Size: "
                            + byteImage_photo.length);


                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    //builder.addTextBody("access_key", accessToken.getTokenSecret());
                    builder.addTextBody("access_key", Keys.CONSUMER_KEY);
                    //builder.addTextBody("access_key", accessToken.getToken());
                    builder.addTextBody("upload_token", uploadToken);
                    builder.addBinaryBody("file0", byteImage_photo, ContentType.create("image/jpeg"), imageFile.getName());

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(apiCall);
                    httpPost.setEntity(builder.build());

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    httpPost.getEntity().writeTo(outputStream);

                    Log.d(TAG, outputStream.toString());

                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();

                    httpClient.getConnectionManager().shutdown();

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                    httpEntity.writeTo(bytes);

                    responseBody = bytes.toString();
                    responseCode = httpResponse.getStatusLine().getStatusCode();

                    //FileInputStream fis = new FileInputStream(imageFile);
                    //Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    //bitmap.compress(Bitmap.CompressFormat.PNG, 25, baos);
                    //byte[] b = baos.toByteArray();

                    //ByteArrayBody multipartFile0 = new ByteArrayBody(b, "image/png", "upload.png");


                    //multipartEntity.addPart("access_key", multipartAccessKey);
                    //multipartEntity.addPart("upload_token", multipartUploadToken);
                    //multipartEntity.addPart("file0", multipartFile0);

                    //FileInputStream fis = new FileInputStream(imageFile);       // imageFile = my file path
                    //DataInputStream in = new DataInputStream(fis);
                    //BufferedReader br =
                    //        new BufferedReader(new InputStreamReader(in));
                    //String strLine = "";
                    //String binData = "";
                    //while ((strLine = br.readLine()) != null) {
                    //    binData = binData + strLine;
                    //}
                    //in.close();

                    //ByteArrayBody multipartFile0 = new ByteArrayBody(binData.getBytes(), imageFile.getName());

                    //multipartEntity.addPart("file0", multipartFile0);

                    //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    //multipartEntity.writeTo(outputStream);



                    //oAuthRequest.addHeader(multipartEntity.getContentType().getName(), multipartEntity.getContentType().getValue());
                    //oAuthRequest.setPayload(outputStream.toByteArray());

                    //Log.d(TAG, outputStream.toString());


                } else {

                    if (!dataString.isEmpty())
                        oAuthRequest.addBodyParameter("data", dataString);

                    service.signRequest(accessToken, oAuthRequest);
                    final Response response = service.execute(oAuthRequest);
                    responseBody = response.getBody();
                    responseCode = response.getCode();
                }

                Log.d(TAG, responseBody);

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

