package com.whitewhiskerstudios.pocketrav.API.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rachael on 10/13/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Project implements Serializable{

    @JsonProperty("completed")          private String completed;
    @JsonProperty("completed_day_set")  private Boolean completedDaySet;
    @JsonProperty("created_at")         private String createdAt;
    @JsonProperty("favorites_count")    private int favoritesCount;
    @JsonProperty("first_photo")        private Photo firstPhoto;
    @JsonProperty("id")                 private int id;
    @JsonProperty("made_for")           private String madeFor;
    @JsonProperty("made_for_user_id")   private int madeForUserId;
    @JsonProperty("name")               private String name;
    @JsonProperty("notes")              private String notes;
    //@JsonProperty("packs")              private Pack pack;
    @JsonProperty("pattern_id")         private int patternId;
    @JsonProperty("pattern_name")       private String patternName;
    @JsonProperty("photos")             private ArrayList<Photo> photos;
    @JsonProperty("progress")           private int progress;
    @JsonProperty("project_status_id")  private int projectStatusId;
    @JsonProperty("rating")             private int rating;
    @JsonProperty("size")               private String size;
    @JsonProperty("started")            private String started;
    @JsonProperty("status_name")        private String statusName;
    @JsonProperty("tag_names")          private ArrayList<String> tagNames;

    public Project(){}

    public String getCompleted() {
        return completed;
    }

    public Boolean getCompletedDaySet() {
        return completedDaySet;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getFavoritesCount() {
        return favoritesCount;
    }

    public Photo getFirstPhoto() {
        return firstPhoto;
    }

    public int getId() {
        return id;
    }

    public String getMadeFor() {
        return madeFor;
    }

    public int getMadeForUserId() {
        return madeForUserId;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }

    public int getPatternId() {
        return patternId;
    }

    public String getPatternName() {
        return patternName;
    }

    public int getProgress() {
        return progress;
    }

    public int getProjectStatusId() {
        return projectStatusId;
    }

    public int getRating() {
        return rating;
    }

    public String getSize() {
        return size;
    }

    public String getStarted() {
        return started;
    }

    public String getStatusName() {
        return statusName;
    }

    public ArrayList<String> getTagNames() {
        return tagNames;
    }

    public boolean hasFirstPhoto() {

        return !(this.firstPhoto == null);
    }

    public ArrayList<Photo> getPhotos() { return photos; }
}
