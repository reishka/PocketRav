package com.whitewhiskerstudios.pocketrav.Services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by rachael on 11/3/17.
 */

public class RavIntentService extends IntentService {

    // Response Codes
    public static final int RESPONSE_OK = 200;                  // Everything is OK!
    public static final int RESPONSE_BAD_REQUEST = 400;         // API call is not valid
    public static final int RESPONSE_UNAUTHORIZED = 401;        // OAUTH has expired, or user has revoked access
    public static final int RESPONSE_FORBIDDEN = 403;           // OAUTH/API key is not valid, user is not permitted to use requested method
    public static final int RESPONSE_NOT_FOUND = 404;           // Not there, hurr-durr
    public static final int RESPONSE_METHOD_NOT_ALLOWED = 405;  // Attempted POST instead of GET, reverse
    public static final int RESPONSE_REQUEST_TOO_LARGE = 413;   // POST was too large
    public static final int RESPONSE_TOO_MANY_REQUESTS = 429;   // Check docs for per-method rate limit
    public static final int RESPONSE_SERVER_ERROR = 500;        // Outside of OAUTH, indicates issue on Ravelry's side
    public static final int RESPONSE_SERVICE_UNAVAILABLE = 503; // Ravelry API is down/unavailable
    public static final int RESPONSE_GATEWAY_TIME_OUT = 504;    // Request was canceled because it took more than 10 seconds to generate a response (pagination!)



    public RavIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
    }


    public String getErrorResponseString(int responseCode){
        switch(responseCode){
            case RESPONSE_BAD_REQUEST:
                return "Code: " + RESPONSE_BAD_REQUEST + ": API Call is not valid.";


            case RESPONSE_UNAUTHORIZED:
                return "Code: " + RESPONSE_UNAUTHORIZED + ": OAuth has expired, or user has revoked access.";


            case RESPONSE_FORBIDDEN:
                return "Code: " + RESPONSE_FORBIDDEN + ": OAUTH/API key is not valid, user is not permitted to use requested method.";


            case RESPONSE_NOT_FOUND:
                return "Code: " + RESPONSE_NOT_FOUND + ": Resource not found.";


            case RESPONSE_METHOD_NOT_ALLOWED:
                return "Code: " + RESPONSE_METHOD_NOT_ALLOWED + ": Attempted POST instead of GET, or GET instead of POST.";


            case RESPONSE_REQUEST_TOO_LARGE:
                return "Code: " + RESPONSE_REQUEST_TOO_LARGE + ": POST was too large.";


            case RESPONSE_TOO_MANY_REQUESTS:
                return "Code: " + RESPONSE_TOO_MANY_REQUESTS + ": Too many requests are being made.";


            case RESPONSE_SERVER_ERROR:
                return "Code: " + RESPONSE_SERVER_ERROR + ": An unknown error has occurred on Ravelry's side.";


            case RESPONSE_SERVICE_UNAVAILABLE:
                return "Code: " + RESPONSE_SERVICE_UNAVAILABLE + ": Ravelry's API and/or other services are down.";


            case RESPONSE_GATEWAY_TIME_OUT:
                return "Code: " + RESPONSE_GATEWAY_TIME_OUT + ": The request was canceled because it was not completed within the allotted time.";

            default:
                return "";
        }
    }
}
