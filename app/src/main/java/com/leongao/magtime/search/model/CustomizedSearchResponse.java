package com.leongao.magtime.search.model;

import com.google.gson.annotations.SerializedName;
import com.leongao.magtime.home.model.Magazine;

import java.util.List;

public class CustomizedSearchResponse {
    @SerializedName("data")
    private List<Magazine> magazineList;

    public List<Magazine> getMagazineList() {
        return magazineList;
    }
}
