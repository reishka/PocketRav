package com.whitewhiskerstudios.pocketrav.API.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by rachael on 10/29/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stash implements Serializable{

    @JsonProperty("yarn")           private Yarn yarn;
    @JsonProperty("colorway_name")  private String colorwayName;
    @JsonProperty("first_photo")    private Photo firstPhoto;
    @JsonProperty("name")           private String name;
    @JsonProperty("handspun")       private Boolean handspun;

    public Stash(){}

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

    public boolean hasFirstPhoto() {

        return !(this.firstPhoto == null);
    }

    public boolean hasYarn(){
        return !(this.yarn == null);
    }

    public boolean hasColorwayName(){
        return !(this.colorwayName == null);
    }
}
