package com.whitewhiskerstudios.pocketrav.API.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by rachael on 10/13/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Project extends Observable implements Serializable{

    public static final int CRAFT_CROCHET = 1;
    public static final int CRAFT_KNITTING = 2;
    public static final int CRAFT_WEAVING = 5;
    public static final int CRAFT_MACHINE_KNITTING = 5;
    public static final int CRAFT_LOOM_KNITTING = 6;

    public static final String PROPERTY_COMPLETED = "completed";
    public static final String PROPERTY_STARTED = "started";
    public static final String PROPERTY_ENDS_PER_INCH = "ends_per_inch";
    public static final String PROPERTY_PICKS_PER_INCH = "picks_per_inch";
    public static final String PROPERTY_NEEDLE_SIZES = "needle_sizes";
    public static final String PROPERTY_SIZE = "size";
    public static final String PROPERTY_TAG_NAMES = "tag_names";
    public static final String PROPERTY_STATUS_ID = "project_status_id";
    public static final String PROPERTY_STATUS_NAME = "status_name";
    public static final String PROPERTY_MADE_FOR = "made_for";
    public static final String IMAGE_ID = "image_id";


    @JsonProperty(PROPERTY_COMPLETED)          private String completed;
    @JsonProperty("completed_day_set")         private Boolean completedDaySet;
    @JsonProperty("craft_id")                  private int craftId;
    @JsonProperty("craft_name")                private String craftName;
    @JsonProperty("created_at")                private String createdAt;
    @JsonProperty(PROPERTY_ENDS_PER_INCH)      private double endsPerInch;
    @JsonProperty("favorites_count")           private int favoritesCount;
    @JsonProperty("first_photo")               private Photo firstPhoto;
    @JsonProperty("id")                        private int id;
    @JsonProperty(PROPERTY_MADE_FOR)           private String madeFor;
    @JsonProperty(PROPERTY_MADE_FOR)           private int madeForUserId;
    @JsonProperty("name")                      private String name;
    @JsonProperty(PROPERTY_NEEDLE_SIZES)       private ArrayList<NeedleSizes> needleSizes;
    @JsonProperty("notes")                     private String notes;
    @JsonProperty("packs")                     private ArrayList<Pack> pack;
    @JsonProperty("pattern_id")                private int patternId;
    @JsonProperty("pattern_name")              private String patternName;
    @JsonProperty("photos")                    private ArrayList<Photo> photos;
    @JsonProperty(PROPERTY_PICKS_PER_INCH)     private double picksPerInch;
    @JsonProperty("progress")                  private int progress;
    @JsonProperty(PROPERTY_STATUS_ID)          private int projectStatusId;
    @JsonProperty("rating")                    private int rating;
    @JsonProperty(PROPERTY_SIZE)               private String size;
    @JsonProperty(PROPERTY_STARTED)            private String started;
    @JsonProperty(PROPERTY_STATUS_NAME)        private String statusName;
    @JsonProperty(PROPERTY_TAG_NAMES)          private ArrayList<String> tagNames;
    @JsonProperty("tools")                     private ArrayList<Tools> tools;

    public Project(){}

    public String getCompleted() {
        return completed;
    }

    public Boolean getCompletedDaySet() {
        return completedDaySet;
    }

    public int getCraftId() {
        return craftId;
    }

    public String getCraftName(){ return craftName; }

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

    public synchronized String getMadeFor() {
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

    public double getEndsPerInch() { return endsPerInch;  }

    public double getPicksPerInch() { return picksPerInch;  }

    public ArrayList<Tools> getTools() { return tools; }

    public boolean hasFirstPhoto() {

        return !(this.firstPhoto == null);
    }

    public ArrayList<Photo> getPhotos() { return photos; }

    public ArrayList<Pack> getPack() {
        return pack;
    }

    public ArrayList<NeedleSizes> getNeedleSizes() {
        return needleSizes;
    }

    public void setMadeFor(String madeFor) {
        synchronized (this){
        this.madeFor = madeFor;}

        setChanged();
        notifyObservers();
    }
}
