package com.whitewhiskerstudios.pocketrav.API;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.whitewhiskerstudios.pocketrav.API.Models.Project;
import com.whitewhiskerstudios.pocketrav.Services.UploadIntentService;

import org.json.JSONObject;

/**
 * Created by rachael on 9/16/17.
 */

public class RavelryAPI extends DefaultApi10a {

    private static final String TAG = "RavelryAPI";

    // CONNECTION
    private static final String AUTHORIZE_URL = "https://www.ravelry.com/oauth/authorize/?oauth_token=%s";
    private static final String CALLBACK_URL = "x-oauthflow://com.whitewhiskerstudios.pocketrav";
    private static final String RAV_API_URL = "https://api.ravelry.com";

    private static final String DEBUG_HOOKBIN_URL = "https://hookb.in/Kb824Rbx";

    // USER
    private static final String USER = "/people/%s.json";

    // PROJECTS
    private static final String PROJECT_LIST = "/projects/%s/list.json";
    private static final String PROJECT = "/projects/%s/%s.json";
    private static final String PHOTO_CREATE = "/projects/%s/%s/create_photo.json?image_id=%s";

    // PATTERNS

    // YARNS

    // NEEDLES
    private static final String NEEDLE_SIZES_KNITTING = "/needles/sizes.json?craft=knitting";
    private static final String NEEDLE_SIZES_CROCHET = "/needles/sizes.json?craft=crochet";

    // STASHES
    private static final String STASH_LIST = "/people/%s/stash/list.json";
    private static final String STASH_UNIFIED_LIST = "/people/%s/stash/unified/list.json";
    private static final String STASH_LIST_SORTED = STASH_LIST + "?sort=%s";
    private static final String STASH_YARN = "/people/%s/stash/%s.json";
    private static final String STASH_FIBER = "/people/%s/fiber/%s.json";

    // UPLOAD
    private static final String UPLOAD_PHOTO_TOKEN = "/upload/request_token.json";
    private static final String UPLOAD_PHOTO = "/upload/image.json";
    private static final String UPLOAD_STATUS = "/upload/image/status.json?%s";


    protected RavelryAPI() {}
    private static class InstanceHolder { private static final RavelryAPI INSTANCE = new RavelryAPI(); }
    public static RavelryAPI instance() {
        return InstanceHolder.INSTANCE;
    }

    // CONNECTION
    @Override
    public String getRequestTokenEndpoint() { return "https://www.ravelry.com/oauth/request_token"; }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://www.ravelry.com/oauth/access_token";
    }

    @Override
    public String getAuthorizationUrl(OAuth1RequestToken requestToken) { return String.format(AUTHORIZE_URL, requestToken.getToken()); }

    public String getCallbackURL() {
        return CALLBACK_URL;
    }


    // PROJECTS
    public String getProjectListRequest(String username) { return String.format(RAV_API_URL + PROJECT_LIST, username); }

    public String getProject(String username, int projectId) {
        Log.d(TAG, String.valueOf(projectId));
        return String.format(RAV_API_URL + PROJECT, username, projectId);
    }

    public String getCreatePhoto(String username, int projectId, int photoId) { return String.format(RAV_API_URL + PHOTO_CREATE, username, projectId, photoId); }

    // USER
    public String getUser(String username) {
        return String.format(RAV_API_URL + USER, username);
    }

    // NEEDLES
    public String getNeedleSizesKnitting() {
        return RAV_API_URL + NEEDLE_SIZES_KNITTING;
    }
    public String getNeedleSizesCrochet(){
        return RAV_API_URL + NEEDLE_SIZES_CROCHET;
    }

    // STASH
    public String getStashList(String username){ return String.format(RAV_API_URL + STASH_LIST, username); }
    public String getStashList(String username, String sort){ return String.format(RAV_API_URL + STASH_LIST_SORTED, username, sort); }
    public String getStashUnifiedList(String username){ return String.format(RAV_API_URL + STASH_UNIFIED_LIST, username); }
    public String getStashYarn(String username, int stashId) { Log.d(TAG, String.valueOf(stashId)); return String.format(RAV_API_URL + STASH_YARN, username, stashId); }
    public String getStashFiber(String username, int stashId) { Log.d(TAG, String.valueOf(stashId)); return String.format(RAV_API_URL + STASH_FIBER, username, stashId); }

    // UPLOAD
    public String getUploadToken() { return RAV_API_URL + UPLOAD_PHOTO_TOKEN; }
    public String getUploadPhoto() {

        if (!UploadIntentService.DEBUG)
            return RAV_API_URL + UPLOAD_PHOTO;
        else
            return DEBUG_HOOKBIN_URL;
    }

    public String getUploadStatus() { return RAV_API_URL + UPLOAD_STATUS; }

    // PHOTO

}

