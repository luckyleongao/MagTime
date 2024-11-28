package com.leongao.magtime.home.model;

import com.leongao.magtime.search.model.BlockImg;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Block {
    @SerializedName("id")
    private int id;
    @SerializedName("blockImg")
    private BlockImg blockImg;
    @SerializedName("block_items")
    private List<BlockItem> blockItemList;

    public int getId() {
        return id;
    }

    public BlockImg getBlockImg() {
        return blockImg;
    }

    public List<BlockItem> getBlockItemList() {
        return blockItemList;
    }
}
