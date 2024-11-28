package com.leongao.magtime.home.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BlockItem {

    @SerializedName("id")
    private int id;

    @SerializedName("blockName")
    private String blockName;

    @SerializedName("magazines")
    private List<Magazine> magazineList;

    public int getId() {
        return id;
    }

    public String getBlockName() {
        return blockName;
    }

    public List<Magazine> getMagazineList() {
        return magazineList;
    }
}
