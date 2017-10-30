package com.whitewhiskerstudios.pocketrav.Utils;

/**
 * Created by rachael on 10/6/17.
 */

public class Constants {

    //Intent Receiver
    public static final String RECEIVER = "RECEIVER";
    public static final String FETCH_TYPE = "FETCH_TYPE";
    public static final String POST_TYPE = "SEND_TYPE";
    public static final String POST_JSON_STRING = "POST_JSON_STRING";

    public static final String RESULT_DATA_KEY = "DATA_KEY";

    public static final int AUTH_INTENT = 100;

    public static final int SUCCESS_RESULT =  1;
    public static final int FAILURE_RESULT = -1;

    // Projects
    public static final String PROJECT_ID = "PROJECT ID";
    public static final String PROJECT_BUNDLE = "PROJECT_BUNDLE";
    public static final String NOTES_BUNDLE = "NOTES_BUNDLE";

    public static final int FETCH_PROJECT_LIST = 1;
    public static final int FETCH_PROJECT = 2;
    public static final int POST_PROJECT = 6;

    // User
    public static final int FETCH_USER = 3;

    // Patterns
    public static final int FETCH_PATTERN = 5;
    public static final String PATTERN_ID = "PATTERN_ID";

    // Photos
    public static final int FETCH_PHOTOS = 4;
    public static final String PHOTO_BUNDLE = "PHOTO_BUNDLE";

    // Needles
    public static final int FETCH_NEEDLES_KNITTING = 7;
    public static final int FETCH_NEEDLES_CROCHET = 8;

    // Stash
    public static final int FETCH_STASH_LIST = 9;
    public static final String STASH_SORT_ORDER = "STASH_SORT_ORDER";
}
