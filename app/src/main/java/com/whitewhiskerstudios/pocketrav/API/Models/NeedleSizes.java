package com.whitewhiskerstudios.pocketrav.API.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by rachael on 10/24/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NeedleSizes implements Serializable {

    @JsonProperty("name")       private String name;
    @JsonProperty("metric")     private double metric;
    @JsonProperty("hook")       private String hook;
    @JsonProperty("us")         private String us;
    @JsonProperty("id")         private int id;
                                private String displayName;
                                private boolean checked = false;

    public NeedleSizes(){}

    public String getName() {return name; }

    public double getMetric() {
        return metric;
    }

    public String getHook() {
        return hook;
    }

    public String getUs() {
        return us;
    }

    public int getId() {
        return id;
    }

    public Boolean getChecked() {return checked;}

    public String getDisplayName() {return displayName; }

    public void  setDisplayName(String name){
        this.displayName = name;
    }

    public void setChecked(Boolean bool){
        this.checked = bool;
    }
}
