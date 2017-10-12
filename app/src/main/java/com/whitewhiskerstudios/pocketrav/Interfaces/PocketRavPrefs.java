package com.whitewhiskerstudios.pocketrav.Interfaces;

import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.annotations.sharedpreferences.SharedPref.Scope;

@SharedPref(Scope.APPLICATION_DEFAULT)
public interface PocketRavPrefs {

    String accessSecret();

    String accessToken();

    String requestToken();

    String username();


}
