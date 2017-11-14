package com.whitewhiskerstudios.pocketrav.API.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rachael on 10/30/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stash implements Serializable{


    // Specific to Fiber Stash
    @JsonProperty("fiber_company_name")     private String fiberCompanyName;
    @JsonProperty("fiber_packs")            private ArrayList<FiberPack> fiberPacks;
    @JsonProperty("location")               private String location;
    @JsonProperty("long_name")              private String longName;
    @JsonProperty("stash_status")           private StashStatus stashStatus;

    //Specific to Yarn Stash
    @JsonProperty("yarn")                   private Yarn yarn;
    @JsonProperty("handspun")               private Boolean handspun;
    @JsonProperty("yarn_weight_name")       private String yarnWeight;
    @JsonProperty("color_family_name")      private String colorFamilyName;
    @JsonProperty("packs")                  private ArrayList<Pack> yarnPacks;

    // Shared
    @JsonProperty("colorway_name")          private String colorwayName;
    @JsonProperty("first_photo")            private Photo firstPhoto;
    @JsonProperty("id")                     private int id;
    @JsonProperty("name")                   private String name;
    @JsonProperty("notes")                  private String notes;
    @JsonProperty("photos")                 private ArrayList<Photo> photos;

    private boolean isYarn = false;

    public Stash(){}


    public String getYarnWeight() {
        return yarnWeight;
    }

    public String getColorFamilyName() {
        return colorFamilyName;
    }

    public Boolean isYarn(){ return !(this.yarn==null); }

    public Yarn getYarn() {
        return yarn;
    }

    public String getColorwayName(){
        return colorwayName;
    }

    public Photo getFirstPhoto() {
        return firstPhoto;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Pack> getYarnPacks() {
        return yarnPacks;
    }

    public Boolean isHandspun() {
        if (this.handspun != null) return this.handspun;
        else return false;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public String getNotes() {
        return notes;
    }

    public boolean hasNotes() {
        return !(this.notes == null);
    }

    public boolean hasFirstPhoto() {

        return !(this.firstPhoto == null);
    }

    public boolean hasYarn(){
        return !(this.yarn == null);
    }

    public String getFiberCompanyName() {
        return fiberCompanyName;
    }

    public ArrayList<FiberPack> getFiberPacks() {
        return fiberPacks;
    }

    public String getLocation() {
        return location;
    }

    public String getLongName() {
        return longName;
    }

    public StashStatus getStashStatus() {
        return stashStatus;
    }

    public boolean hasColorwayName(){

        if (this.colorwayName == null)
            return false;
        else if (this.colorwayName.equals(""))
            return false;
        else
            return true;
    }

}
