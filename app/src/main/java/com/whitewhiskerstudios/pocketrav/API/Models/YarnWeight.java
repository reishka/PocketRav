package com.whitewhiskerstudios.pocketrav.API.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by rachael on 10/22/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class YarnWeight implements Serializable{

    @JsonProperty("crochet_gauge")      private String crochetGauge;
    @JsonProperty("id")                 private int id;
    @JsonProperty("knit_gauge")         private String knitGauge;
    @JsonProperty("max_gauge")          private String maxGauge;
    @JsonProperty("min_gauge")          private String minGauge;
    @JsonProperty("name")               private String name;
    @JsonProperty("ply")                private String ply;
    @JsonProperty("wpi")                private int wpi;

    public YarnWeight(){}

    public String getCrochetGauge() {
        return crochetGauge;
    }

    public int getId() {
        return id;
    }

    public String getKnitGauge() {
        return knitGauge;
    }

    public String getMaxGauge() {
        return maxGauge;
    }

    public String getMinGauge() {
        return minGauge;
    }

    public String getName() {
        return name;
    }

    public String getPly() {
        return ply;
    }

    public int getWpi() {
        return wpi;
    }
}
