package com.leongao.magtime.search.model;

import com.leongao.magtime.home.model.Block;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchPageResponse {

    @SerializedName("data")
    @Expose
    private List<Block> data;

    public List<Block> getData() {
        return data;
    }
}
