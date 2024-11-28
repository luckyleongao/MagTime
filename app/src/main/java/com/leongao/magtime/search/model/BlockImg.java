package com.leongao.magtime.search.model;

import com.google.gson.annotations.SerializedName;

public class BlockImg {
    @SerializedName("id")
    private int id;
    @SerializedName("url")
    private String url;

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
