package com.leongao.magtime.reader.bean;

import com.leongao.magtime.home.model.MagCoverImg;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MagContent {
    @SerializedName("magId")
    private int magId;

    @SerializedName("magContentUrl")
    private List<MagCoverImg> magContentImgUrlList;

    public int getMagId() {
        return magId;
    }

    public List<MagCoverImg> getMagContentImgUrlList() {
        return magContentImgUrlList;
    }
}
