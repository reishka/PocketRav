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
    @JsonProperty("meters_per_skein")       private float metersPerSkein;
    @JsonProperty("ounces_per_skein")       private float ouncesPerSkein;
    @JsonProperty("personal_name")          private String personalName;
    @JsonProperty("primary_pack_id")        private int primaryPackId;
    @JsonProperty("project_id")             private int projectId;
    @JsonProperty("quantity_description")   private String quantityDescription;
    @JsonProperty("shop_id")                private int shopId;
    @JsonProperty("shop_name")              private String shopName;
    @JsonProperty("skeins")                 private String skeins;
    @JsonProperty("stash_id")               private int stashId;
    @JsonProperty("thread_size")            private String threadSize;
    @JsonProperty("total_grams")            private float totalGrams;
    @JsonProperty("total_meters")           private float totalMeters;
    @JsonProperty("total_ounces")           private float totalOunces;
    @JsonProperty("total_yards")            private float totalYards;
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

    public float getMetersPerSkein() {
        return metersPerSkein;
    }

    public float getOuncesPerSkein() {
        return ouncesPerSkein;
    }

    public String getPersonalName() {
        return personalName;
    }

    // When this comes in as a null value, 0 becomes the value
    public boolean hasPrimaryPackId() { return !(primaryPackId== 0);}

    public int getPrimaryPackId() {
        return primaryPackId;
    }

    // When this comes in as a null value, 0 becomes the value
    public boolean hasProjectId() {return !(projectId == 0); }

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

    public float getTotalGrams() {
        return totalGrams;
    }

    public float getTotalMeters() {
        return totalMeters;
    }

    public float getTotalOunces() {
        return totalOunces;
    }

    public float getTotalYards() {
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
