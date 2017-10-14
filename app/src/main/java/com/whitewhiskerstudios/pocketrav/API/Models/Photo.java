package com.whitewhiskerstudios.pocketrav.API.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by rachael on 10/13/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Photo {

    @JsonProperty("caption")            private String caption;
    @JsonProperty("copyright_holder")   private String copyrightHolder;
    @JsonProperty("id")                 private int id;
    @JsonProperty("medium2_url")        private String medium2Url;
    @JsonProperty("medium_url")         private String mediumUrl;
    @JsonProperty("small2_url")         private String small2Url;
    @JsonProperty("small_url")          private String smallUrl;
    @JsonProperty("square_url")         private String squareUrl;
    @JsonProperty("thumbnail_url")      private String thumbnailUrl;

    public Photo(){}

    public String getCaption() {
        return caption;
    }

    public String getCopyrightHolder() {
        return copyrightHolder;
    }

    public int getId() {
        return id;
    }

    public String getMedium2Url() {
        return medium2Url;
    }

    public String getMediumUrl() {
        return mediumUrl;
    }

    public String getSmall2Url() {
        return small2Url;
    }

    public String getSmallUrl() {
        return smallUrl;
    }

    public String getSquareUrl() {
        return squareUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
