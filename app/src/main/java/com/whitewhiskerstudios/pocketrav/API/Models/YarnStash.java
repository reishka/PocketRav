package com.whitewhiskerstudios.pocketrav.API.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rachael on 10/29/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class YarnStash implements Serializable{

    @JsonProperty("yarn")           private Yarn yarn;
    @JsonProperty("colorway_name")  private String colorwayName;
    @JsonProperty("first_photo")    private Photo firstPhoto;
    @JsonProperty("name")           private String name;
    @JsonProperty("handspun")       private Boolean handspun;
    @JsonProperty("id")             private int id;
    @JsonProperty("photos")         private ArrayList<Photo> photos;
    @JsonProperty("notes")          private String notes;

    public YarnStash(){}

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

    public Boolean getHandspun() {
        return handspun;
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

    public boolean hasFirstPhoto() {

        return !(this.firstPhoto == null);
    }

    public boolean hasYarn(){
        return !(this.yarn == null);
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
