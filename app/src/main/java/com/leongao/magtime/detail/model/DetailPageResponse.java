package com.leongao.magtime.detail.model;

import com.leongao.magtime.home.model.Magazine;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetailPageResponse {
    @SerializedName("data")
    private List<Magazine> data;

    public List<Magazine> getData() {
        return data;
    }
}
