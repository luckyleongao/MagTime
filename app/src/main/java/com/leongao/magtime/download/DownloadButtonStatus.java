package com.leongao.magtime.download;

import com.leongao.magtime.utils.ConstantUtil;

public class DownloadButtonStatus {
    private ConstantUtil.DOWNLOAD_STATUS downloadStatus;
    private int progress;

    public ConstantUtil.DOWNLOAD_STATUS getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(ConstantUtil.DOWNLOAD_STATUS downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
