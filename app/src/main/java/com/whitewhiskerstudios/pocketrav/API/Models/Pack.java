package com.whitewhiskerstudios.pocketrav.API.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by rachael on 10/22/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pack implements Serializable{

    @JsonProperty("colorway")               private String colorway;
    @JsonProperty("dye_lot")                private String dyelot;
    @JsonProperty("grams_per_skein")        private int gramsPerSkein;
    @JsonProperty("id")                     private int id;
    @JsonProperty("meters_per_skein")       private int metersPerSkein;
    @JsonProperty("ounces_per_skein")       private int ouncesPerSkein;
    @JsonProperty("personal_name")          private String personalName;
    @JsonProperty("primary_pack_id")        private int primaryPackId;
    @JsonProperty("project_id")             private int projectId;
    @JsonProperty("quantity_description")   private String quantityDescription;
    @JsonProperty("shop_id")                private int shopId;
    @JsonProperty("shop_name")              private String shopName;
    @JsonProperty("skeins")                 private String skeins;
    @JsonProperty("stash_id")               private int stashId;
    @JsonProperty("thread_size")            private String threadSize;
    @JsonProperty("total_grams")            private int totalGrams;
    @JsonProperty("total_meters")           private int totalMeters;
    @JsonProperty("total_ounces")           private int totalOunces;
    @JsonProperty("total_yards")            private int totalYards;
    @JsonProperty("yarn")                   private Yarn yarn;
    @JsonProperty("yarn_id")                private int yarnId;
    @JsonProperty("yarn_name")              private String yarnName;
    @JsonProperty("yarn_weight")            private YarnWeight yarnWeight;


    public Pack(){}

    public String getColorway() {
        return colorway;
    }

    public String getDyelot() {
        return dyelot;
    }

    public int getGramsPerSkein() {
        return gramsPerSkein;
    }

    public int getId() {
        return id;
    }

    public int getMetersPerSkein() {
        return metersPerSkein;
    }

    public int getOuncesPerSkein() {
        return ouncesPerSkein;
    }

    public String getPersonalName() {
        return personalName;
    }

    public int getPrimaryPackId() {
        return primaryPackId;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getQuantityDescription() {
        return quantityDescription;
    }

    public int getShopId() {
        return shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public String getSkeins() {
        return skeins;
    }

    public int getStashId() {
        return stashId;
    }

    public String getThreadSize() {
        return threadSize;
    }

    public int getTotalGrams() {
        return totalGrams;
    }

    public int getTotalMeters() {
        return totalMeters;
    }

    public int getTotalOunces() {
        return totalOunces;
    }

    public int getTotalYards() {
        return totalYards;
    }

    public Yarn getYarn() {
        return yarn;
    }

    public int getYarnId() {
        return yarnId;
    }

    public String getYarnName() {
        return yarnName;
    }

    public YarnWeight getYarnWeight() {
        return yarnWeight;
    }
}
