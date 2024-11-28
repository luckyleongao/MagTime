package com.leongao.magtime.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.leongao.magtime.utils.ConstantUtil;

@Entity(tableName = "download")
public class Download {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "magId")
    private int magId;

    @ColumnInfo(name = "magName")
    private String magName;

    @ColumnInfo(name = "magPubDate")
    private String magPubDate;

    @ColumnInfo(name = "magCoverImgUrl")
    private String magCoverImgUrl;

    @ColumnInfo(name = "totalNum")
    private int totalNum;

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

    public int getMagId() {
        return magId;
    }

    public void setMagId(int magId) {
        this.magId = magId;
    }

    public String getMagName() {
        return magName;
    }

    public void setMagName(String magName) {
        this.magName = magName;
    }

    public String getMagPubDate() {
        return magPubDate;
    }

    public void setMagPubDate(String magPubDate) {
        this.magPubDate = magPubDate;
    }

    public String getMagCoverImgUrl() {
        return magCoverImgUrl;
    }

    public void setMagCoverImgUrl(String magCoverImgUrl) {
        this.magCoverImgUrl = magCoverImgUrl;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int pageNum) {
        this.totalNum = pageNum;
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
