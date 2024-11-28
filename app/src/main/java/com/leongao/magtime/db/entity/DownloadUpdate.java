package com.leongao.magtime.db.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.leongao.magtime.utils.ConstantUtil;

@Entity
public class DownloadUpdate {
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "downloadedNum")
    private int downloadedNum;
    @ColumnInfo(name = "status")
    private ConstantUtil.DOWNLOAD_STATUS status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDownloadedNum() {
        return downloadedNum;
    }

    public void setDownloadedNum(int downloadedNum) {
        this.downloadedNum = downloadedNum;
    }

    public ConstantUtil.DOWNLOAD_STATUS getStatus() {
        return status;
    }

    public void setStatus(ConstantUtil.DOWNLOAD_STATUS status) {
        this.status = status;
    }
}
