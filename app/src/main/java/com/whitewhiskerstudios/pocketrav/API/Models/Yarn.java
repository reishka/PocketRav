package com.whitewhiskerstudios.pocketrav.API.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rachael on 10/22/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Yarn implements Serializable{

    @JsonProperty("discontinued")       private Boolean discontinued;
    @JsonProperty("grams")              private int grams;
    @JsonProperty("id")                 private int id;
    @JsonProperty("machine_washable")   private Boolean machineWashable;
    @JsonProperty("max_gauge")          private int maxGauge;
    @JsonProperty("min_gauge")          private int minGauge;
    @JsonProperty("max_needle_size")    private int maxNeedleSize;
    @JsonProperty("min_needle_size")    private int minNeedleSize;
    @JsonProperty("max_hook_size")      private int maxHookSize;
    @JsonProperty("min_hook_size")      private int minHookSize;
    @JsonProperty("name")               private String name;
    @JsonProperty("notes_html")         private String notes;
    @JsonProperty("organic")            private Boolean organic;
    @JsonProperty("photos")             private ArrayList<Photo> photos;
    @JsonProperty("rating_average")     private float ratingAverage;
    @JsonProperty("rating_count")       private int ratingCount;
    @JsonProperty("texture")            private String texture;
    @JsonProperty("thread_size")        private int threadSize;
    @JsonProperty("wpi")                private int wpi;
    @JsonProperty("yardage")            private int yardage;
    @JsonProperty("yarn_company")       private YarnCompany yarnCompany;
    @JsonProperty("yarn_fibers")        private ArrayList<YarnFiber> yarnFibers;
    @JsonProperty("yarn_weight")        private YarnWeight yarnWeight;

    public Yarn(){}

    public Boolean getDiscontinued() {
        return discontinued;
    }

    public int getGrams() {
        return grams;
    }

    public int getId() {
        return id;
    }

    public Boolean getMachineWashable() {
        return machineWashable;
    }

    public int getMaxGauge() {
        return maxGauge;
    }

    public int getMinGauge() {
        return minGauge;
    }

    public int getMaxNeedleSize() {
        return maxNeedleSize;
    }

    public int getMinNeedleSize() {
        return minNeedleSize;
    }

    public int getMaxHookSize() {
        return maxHookSize;
    }

    public int getMinHookSize() {
        return minHookSize;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }

    public Boolean getOrganic() {
        return organic;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public float getRatingAverage() {
        return ratingAverage;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public String getTexture() {
        return texture;
    }

    public int getThreadSize() {
        return threadSize;
    }

    public int getWpi() {
        return wpi;
    }

    public int getYardage() {
        return yardage;
    }

    public YarnCompany getYarnCompany() {
        return yarnCompany;
    }

    public ArrayList<YarnFiber> getYarnFibers() {
        return yarnFibers;
    }

    public YarnWeight getYarnWeight() {
        return yarnWeight;
    }
}
