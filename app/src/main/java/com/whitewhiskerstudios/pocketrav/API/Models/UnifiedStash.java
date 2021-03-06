package com.whitewhiskerstudios.pocketrav.API.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by rachael on 10/31/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class UnifiedStash implements Serializable {

    @JsonProperty("stash")          private Stash yarnStash;
    @JsonProperty("fiber_stash")    private Stash stash;

    public UnifiedStash(){}

    public Stash getStash() {

        if (stash != null)
            return stash;
        else
            return yarnStash;
    }

    public Boolean isYarn(){
        return !(yarnStash==null);
    }

    public Boolean isFiber(){
        return !(stash ==null);
    }

}
