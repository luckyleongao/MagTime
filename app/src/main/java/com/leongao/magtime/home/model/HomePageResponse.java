package com.leongao.magtime.home.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HomePageResponse {

    @SerializedName("data")
    private List<Block> data;

    public List<Block> getData() {
        return data;
    }
}
