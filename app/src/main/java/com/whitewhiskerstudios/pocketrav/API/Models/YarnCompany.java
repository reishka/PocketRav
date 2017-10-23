package com.whitewhiskerstudios.pocketrav.API.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by rachael on 10/22/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class YarnCompany implements Serializable{

    @JsonProperty("id")         private int id;
    @JsonProperty("logo_url")   private String logoUrl;
    @JsonProperty("name")       private String name;
    @JsonProperty("yarns_count")private int yarnsCount;

    public YarnCompany(){}

    public int getId() {
        return id;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getName() {
        return name;
    }

    public int getYarnsCount() {
        return yarnsCount;
    }
}
