package com.whitewhiskerstudios.pocketrav.API;

import android.util.Log;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;

/**
 * Created by rachael on 9/16/17.
 */

public class RavelryAPI extends DefaultApi10a {

    private static final String TAG = "RavelryAPI";

    private static final String AUTHORIZE_URL = "https://www.ravelry.com/oauth/authorize/?oauth_token=%s";
    private static final String CALLBACK_URL = "x-oauthflow://com.whitewhiskerstudios.pocketrav";

    private static final String RAV_API_URL = "https://";
    private static final String OAUTH = "?oauth_token=%s";

    // USER

    // PROJECTS
    private static final String PROJECT_LIST = "/projects/%s/list.json";


    // PATTERNS

    // YARNS

    protected RavelryAPI(){}

    private static class InstanceHolder {
        private static final RavelryAPI INSTANCE = new RavelryAPI();
    }

    public static RavelryAPI instance(){
        return InstanceHolder.INSTANCE;
    }


    @Override
    public String getRequestTokenEndpoint() {
        return "https://www.ravelry.com/oauth/request_token";
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://www.ravelry.com/oauth/access_token";
    }

    @Override
    public String getAuthorizationUrl(OAuth1RequestToken requestToken) {
        return String.format(AUTHORIZE_URL, requestToken.getToken());
    }

    public String getCallbackURL(){
        return CALLBACK_URL;
    }

    public String getProjectListRequest(String username, OAuth1AccessToken accessToken){
        Log.d(TAG, accessToken.toString());
        return String.format(RAV_API_URL + PROJECT_LIST + OAUTH, username, accessToken); }

}
