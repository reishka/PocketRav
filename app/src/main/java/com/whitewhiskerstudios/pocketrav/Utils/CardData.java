package com.whitewhiskerstudios.pocketrav.Utils;

/**
 * Created by rachael on 10/6/17.
 */

public class CardData {

    public String tv_top;
    public String tv_bottom;
    public String image;
    public int id;
    public int stashType;

    public CardData(){}

    public CardData(String top, String bottom, String photo, int id){
        this.tv_top = top;
        this.tv_bottom = bottom;
        this.image = photo;
        this.id = id;
    }

    public CardData(String top, String bottom, String photo, int id, int stashType){
        this.tv_top = top;
        this.tv_bottom = bottom;
        this.image = photo;
        this.id = id;
        this.stashType = stashType;
    }

    public CardData(String top, String bottom, String photo){
        this.tv_top = top;
        this.tv_bottom = bottom;
        this.image = photo;
    }


}
