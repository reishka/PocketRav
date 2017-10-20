package com.whitewhiskerstudios.pocketrav.API;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.google.gson.Gson;
import com.whitewhiskerstudios.pocketrav.Interfaces.PocketRavPrefs_;
import com.whitewhiskerstudios.pocketrav.R;
import com.whitewhiskerstudios.pocketrav.Utils.Constants;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;


import static com.whitewhiskerstudios.pocketrav.API.Keys.ACCESS_KEY;
import static com.whitewhiskerstudios.pocketrav.API.Keys.SECRET_KEY;

@EActivity
public class AccessTokenActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_REQUEST_TOKEN = "requestToken";
    public static final String EXTRA_ACCESS_TOKEN = "accessToken";
    public static final String EXTRA_ACCESS_SECRET = "accessSecret";

    @Pref
    PocketRavPrefs_ prefs;

    OAuth1RequestToken requestToken;
    OAuth10aService service;
    WebView mWebView;

    final String CALLBACK_URL = RavelryAPI.instance().getCallbackURL();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize);

        service = new ServiceBuilder()
                .apiKey(ACCESS_KEY)
                .apiSecret(SECRET_KEY)
                .callback(CALLBACK_URL)
                .build(RavelryAPI.instance());

        mWebView = (WebView) findViewById(R.id.webview_login);
        mWebView.clearCache(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setWebViewClient(mWebViewClient);

        startAuthorize();
    }

    private void startAuthorize(){

        (new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... params){
                try{
                    requestToken = service.getRequestToken();} catch (Exception e){
                    Log.e("Failure", e.toString());}
                return service.getAuthorizationUrl(requestToken);
            }

            @Override
            protected void onPostExecute(String url){
                mWebView.loadUrl(url);
            }
        }).execute();
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(final WebView view, String url, Bitmap favicon) {
            if ((url != null) && (url.startsWith(CALLBACK_URL))) { // Override webview when user came back to CALLBACK_URL
                mWebView.stopLoading();
                mWebView.setVisibility(View.INVISIBLE); // Hide webview if necessary
                Uri uri = Uri.parse(url);
                final String verifier = uri.getQueryParameter("oauth_verifier");
                final String username = uri.getQueryParameter("username");

                (new AsyncTask<Void, Void, Token>() {
                    @Override
                    protected Token doInBackground(Void... params) {
                        OAuth1AccessToken at = null;
                        try{

                            at = service.getAccessToken(requestToken, verifier);} catch (Exception e){}

                        if (at == null) // we probably logged in but we don't have an auth token
                        {
                            startAuthorize();

                        }

                        Gson gson = new Gson();
                        String json_at = gson.toJson(at);
                        String json_rt = gson.toJson(requestToken);

                        prefs.username().put(username);
                        prefs.accessToken().put(json_at);
                        prefs.accessSecret().put(at.getTokenSecret());
                        prefs.requestToken().put(json_rt);

                        return at;
                    }


                    @Override
                    protected void onPostExecute(Token accessToken) {

                        Intent intent = new Intent();

                        if (accessToken != null) {
                            intent.putExtra(Constants.RESULT_DATA_KEY, prefs.username().get());
                            setResult(RESULT_OK, intent);
                        }else {
                            setResult(RESULT_CANCELED);
                        }

                        finish();


                    }
                }).execute();
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }
    };


}
