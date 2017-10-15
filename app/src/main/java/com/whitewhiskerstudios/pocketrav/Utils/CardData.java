package com.whitewhiskerstudios.pocketrav.Utils;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by rachael on 10/6/17.
 */

public class CardData {

    public String tv_top;
    public String tv_bottom;
    public String photo;
    public int id;

    public CardData(){}

    public CardData(String top, String bottom, String photo, int id){
        this.tv_top = top;
        this.tv_bottom = bottom;
        this.photo = photo;
        this.id = id;
    }


}
