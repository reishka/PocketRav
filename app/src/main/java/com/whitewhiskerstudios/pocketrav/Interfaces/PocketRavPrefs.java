package com.whitewhiskerstudios.pocketrav.Interfaces;

import com.whitewhiskerstudios.pocketrav.API.Models.Project;

import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.annotations.sharedpreferences.SharedPref.Scope;

import java.util.ArrayList;

@SharedPref(Scope.UNIQUE)
public interface PocketRavPrefs {

    String accessSecret();

    String accessToken();

    String requestToken();

    String username();

    String project();

    String knittingNeedleSizes();

    String crochetHookSizes();


}
