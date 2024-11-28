package com.leongao.magtime.shelf.model;

import androidx.annotation.Nullable;

import com.leongao.magtime.db.entity.Download;
import com.leongao.magtime.db.entity.Favorite;

public class DownloadMagazine extends Download {
    // isSelected 字段用于收藏页面，编辑删除收藏杂志
    private boolean isSelected = false;

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof DownloadMagazine) {
            DownloadMagazine magazine = (DownloadMagazine) obj;
            // TODO: download entity 需要增加其它字段的比较吗？
            return this.getId() == magazine.getId()
                    && this.getMagId() == magazine.getMagId()
                    && this.getMagName().equals(magazine.getMagName())
                    && this.getMagPubDate().equals(magazine.getMagPubDate())
                    && this.getMagCoverImgUrl().equals(magazine.getMagCoverImgUrl());
        }
        return false;
    }
}
