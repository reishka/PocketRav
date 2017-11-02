package com.whitewhiskerstudios.pocketrav.API.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by rachael on 11/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tools implements Serializable{

    @JsonProperty("id")         private int id;
    @JsonProperty("make")       private String make;
    @JsonProperty("model")     private String model;
    @JsonProperty("notes")      private String notes;

    public Tools(){}

    public int getId() {
        return id;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getNotes() {
        return notes;
    }
}
