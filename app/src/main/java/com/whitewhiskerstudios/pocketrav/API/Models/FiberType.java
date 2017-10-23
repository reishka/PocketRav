package com.whitewhiskerstudios.pocketrav.API.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by rachael on 10/22/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class FiberType implements Serializable{

    @JsonProperty("animal_fiber")   private Boolean animalFiber;
    @JsonProperty("id")             private int id;
    @JsonProperty("name")           private String name;
    @JsonProperty("synthetic")      private Boolean synthetic;
    @JsonProperty("vegetable_fiber")private Boolean vegetableFiber;

    public FiberType(){}

    public Boolean getAnimalFiber() {
        return animalFiber;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getSynthetic() {
        return synthetic;
    }

    public Boolean getVegetableFiber() {
        return vegetableFiber;
    }
}
