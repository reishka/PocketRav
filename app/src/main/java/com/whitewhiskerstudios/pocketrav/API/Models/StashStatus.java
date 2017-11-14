package com.whitewhiskerstudios.pocketrav.API.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by rachael on 10/30/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class StashStatus implements Serializable{

    @JsonProperty("id")     private int id;
    @JsonProperty("name")   private String name;

    public StashStatus(){}

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
