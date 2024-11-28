package com.leongao.magtime.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReaderPageResponse {
    @SerializedName("data")
    private List<MagContent> magContentList;

    public List<MagContent> getMagContentList() {
        return magContentList;
    }
}
