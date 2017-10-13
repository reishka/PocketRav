package com.whitewhiskerstudios.pocketrav.API.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by rachael on 10/12/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {


    @JsonProperty("about_me")           private String aboutMe;
    @JsonProperty("first_name")         private String firstName;
    @JsonProperty("id")                 private int id;
    @JsonProperty("large_photo_url")    private String largePhotoURL;
    //@JsonProperty("pattern_author")     private PatternAuthor patternAuthor;
    @JsonProperty("photo_url")          private String photoURL;
    @JsonProperty("small_photo_url")    private String smallPhotoURL;
    @JsonProperty("tiny_photo_url")     private String tinyPhotoURL;
    //@JsonProperty("user_sites")         private UserSite userSite;
    @JsonProperty("username")           private String username;

    public User(){}

    public String getAboutMe() {
        return aboutMe;
    }

    public String getFirstName() {
        return firstName;
    }

    public int getId() {
        return id;
    }

    public String getLargePhotoURL() {
        return largePhotoURL;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public String getSmallPhotoURL() {
        return smallPhotoURL;
    }

    public String getTinyPhotoURL() {
        return tinyPhotoURL;
    }

    public String getUsername() {
        return username;
    }
}
