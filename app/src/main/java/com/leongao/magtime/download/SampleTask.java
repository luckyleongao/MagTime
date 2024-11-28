package com.leongao.magtime.download;

import android.os.Handler;


public class SampleTask implements Runnable {
    private Handler handler = new Handler();
    private DownloadButtonProgress downloadButtonProgress;

    private int progress = 0;

    public SampleTask(DownloadButtonProgress downloadButtonProgress) {
//        this.handler = handler;
        this.downloadButtonProgress = downloadButtonProgress;
    }

    @Override
    public void run() {
        try {
            setBtnIndeterminate();
            Thread.sleep(2000);
            setBtnResume();
            Thread.sleep(2000);
            setBtnDeterminate();
            while (progress <= 100) {
                Thread.sleep(30);
                progress++;
                setBtnProgress();
            }
            setBtnFinish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setBtnIndeterminate() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                downloadButtonProgress.setIndeterminate();
            }
        });
    }

    private void setBtnDeterminate() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                downloadButtonProgress.setDeterminate();
            }
        });
    }

    private void setBtnProgress() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                downloadButtonProgress.setCurrentProgress(progress);
            }
        });
    }

    private void setBtnResume() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                downloadButtonProgress.setResume();
            }
        });
    }

    private void setBtnFinish() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                downloadButtonProgress.setFinish();
            }
        });
    }
}
