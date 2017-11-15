package com.whitewhiskerstudios.pocketrav.Utils;

/**
 * Created by rachael on 10/6/17.
 */

public class CardData {

    private String tv_top;
    private String tv_bottom;
    private String image;
    private int mainId = 0;
    private int stashType;
    private int secondaryId = 0;

    public CardData(){}

    public CardData(String top, String bottom, String photo, int id){
        this.tv_top = top;
        this.tv_bottom = bottom;
        this.image = photo;
        this.mainId = id;
    }

    public CardData(String top, String bottom, String photo, int id, int stashType){
        this.tv_top = top;
        this.tv_bottom = bottom;
        this.image = photo;
        this.mainId = id;
        this.stashType = stashType;
    }

    public CardData(String top, String bottom, String photo){
        this.tv_top = top;
        this.tv_bottom = bottom;
        this.image = photo;
    }

    public String getTv_top() {
        return tv_top;
    }

    public void setTv_top(String tv_top) {
        this.tv_top = tv_top;
    }

    public String getTv_bottom() {
        return tv_bottom;
    }

    public void setTv_bottom(String tv_bottom) {
        this.tv_bottom = tv_bottom;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getMainId() {
        return mainId;
    }

    public void setMainId(int mainId) {
        this.mainId = mainId;
    }

    public int getStashType() {
        return stashType;
    }

    public void setStashType(int stashType) {
        this.stashType = stashType;
    }

    public int getSecondaryId() {
        return secondaryId;
    }

    public void setSecondaryId(int secondaryId) {
        this.secondaryId = secondaryId;
    }
}
