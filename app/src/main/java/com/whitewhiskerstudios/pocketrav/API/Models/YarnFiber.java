package com.whitewhiskerstudios.pocketrav.API.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by rachael on 10/22/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class YarnFiber implements Serializable{

    @JsonProperty("fiber_type")     private FiberType fiberType;
    @JsonProperty("id")             private int id;
    @JsonProperty("percentage")     private int percentage;

    public YarnFiber(){}

    public FiberType getFiberType() {
        return fiberType;
    }

    public void setFiberType(FiberType fiberType) {
        this.fiberType = fiberType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
