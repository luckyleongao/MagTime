package com.leongao.magtime.search.model;

import com.leongao.magtime.home.model.BlockItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchBlockResponse {

    @SerializedName("data")
    @Expose
    private BlockItem data;

    public BlockItem getData() {
        return data;
    }
}
