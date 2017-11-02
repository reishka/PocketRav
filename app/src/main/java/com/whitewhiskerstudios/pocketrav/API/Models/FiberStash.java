package com.whitewhiskerstudios.pocketrav.API.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rachael on 10/30/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class FiberStash implements Serializable{

    @JsonProperty("colorway_name")          private String colorwayName;
    @JsonProperty("fiber_company_name")     private String fiberCompanyName;
    @JsonProperty("fiber_packs")            private ArrayList<FiberPack> fiberPacks;
    @JsonProperty("first_photo")            private Photo firstPhoto;
    @JsonProperty("id")                     private int id;
    @JsonProperty("location")               private String location;
    @JsonProperty("long_name")              private String longName;
    @JsonProperty("name")                   private String name;
    @JsonProperty("notes")                  private String notes;
    @JsonProperty("photos")                 private ArrayList<Photo> photos;
    @JsonProperty("stash_status")           private StashStatus stashStatus;


    public FiberStash(){}

    public String getColorwayName() {
        return colorwayName;
    }

    public String getFiberCompanyName() {
        return fiberCompanyName;
    }

    public ArrayList<FiberPack> getFiberPacks() {
        return fiberPacks;
    }

    public Photo getFirstPhoto() {
        return firstPhoto;
    }

    public int getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getLongName() {
        return longName;
    }

    public String getNotes() {
        return notes;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
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

    public boolean hasFirstPhoto() {

        return !(this.firstPhoto == null);
    }
}
