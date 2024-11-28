package com.leongao.magtime.viewmodel;

import android.app.Application;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.leongao.magtime.app.ResultState;
import com.leongao.magtime.db.entity.Download;
import com.leongao.magtime.db.entity.DownloadUpdate;
//import com.leongao.magtime.detail.ui.DetailFragmentArgs;
import com.leongao.magtime.detail.ui.DetailFragmentArgs;
import com.leongao.magtime.download.DownloadButtonStatus;
import com.leongao.magtime.home.model.Magazine;
import com.leongao.magtime.reader.bean.MagContent;
import com.leongao.magtime.reader.bean.ReaderPageResponse;
import com.leongao.magtime.repository.DownloadRepository;
import com.leongao.magtime.utils.ConstantUtil;
import com.leongao.magtime.utils.FileUtil;
import com.leongao.magtime.utils.NetworkUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import timber.log.Timber;

public class DownloadViewModel extends AndroidViewModel {

    private final DownloadRepository downloadRepository;
    private final Application application;
    private String fileDir;
    private String downloadFilePath;
    private final DetailFragmentArgs args;
//    private final Magazine args;
    public boolean isPaused;
    private int totalNum;
    private int pageNum;
    private long downloadId;
    private List<MagContent> magContentList;
    public MutableLiveData<DownloadButtonStatus> btnStatus = new MutableLiveData<>();
    public DownloadViewModel(@NonNull Application application,
                             DetailFragmentArgs args, DownloadRepository downloadRepository) {
        super(application);
        this.args = args;
        this.application = application;
        this.downloadRepository = downloadRepository;

        initDownloadFileDir();
    }

    private void initDownloadFileDir() {
        if (null == args) return;
        fileDir = Environment.DIRECTORY_DOWNLOADS + File.separator + args.getMagId();
        // 只要调用getExternalFilesDir()方法，就会创建文件夹
        // 外部存储路径：/storage/emulated/0/Android/data/packagename/files/Download/magId
        downloadFilePath = String.valueOf(application.getApplicationContext().getExternalFilesDir(fileDir));
    }

    public BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            // Checking if the received broadcast is for our enqueued download by matching download id
            Timber.d("id: " + id +  " || downloadId: " + downloadId);
            if (downloadId == id) {
                updateDownloadStatus(ConstantUtil.DOWNLOAD_STATUS.IN_PROGRESS);
                // 继续下载下一页
                pageNum++;
                runDownload();
            }
        }
    };

    public void insert(Download download) {
        downloadRepository.insert(download);
    }

    public Download findById(int magId) {
        return downloadRepository.findById(magId);
    }

    public LiveData<ResultState<List<Download>>> findAll() {
        return downloadRepository.findAll();
    }

    public void deleteById(int magId) {
        downloadRepository.deleteById(magId);
    }

    public void deleteByIds(int[] magIds) {
        downloadRepository.deleteByIds(magIds);
    }

    public void deleteAll() {
        downloadRepository.deleteAll();
    }

    public void update(int downloadedNum, ConstantUtil.DOWNLOAD_STATUS status, int magId) {
        downloadRepository.update(downloadedNum, status, magId);
    }

    // 从下载目录删除已下载的杂志
    public void deleteFromStorageByIds(int[] magIds) {
        downloadRepository.deleteFromStorageByIds(application.getApplicationContext(), magIds);
    }

    public void deleteAllFromStorage() {
        downloadRepository.deleteAllFromStorage(application.getApplicationContext());
    }

    public void downloadMagazine(int magId) {
        DisposableSingleObserver<ReaderPageResponse> observer = new DisposableSingleObserver<ReaderPageResponse>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull ReaderPageResponse readerPageResponse) {
                        magContentList = readerPageResponse.getMagContentList();
                        startDownload();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Toast.makeText(application.getApplicationContext(),
                                "something is wrong, try again!", Toast.LENGTH_SHORT).show();
                    }
        };
        downloadRepository.getMagazineContentUrls(magId, observer);
    }


    private void startDownload() {
        if (!downloadConditionAvailable(magContentList)) return;
        totalNum = magContentList.get(0).getMagContentImgUrlList().size();
        runDownload();
    }

    private void runDownload() {
        Timber.d("pageNum: " + pageNum + "| totalNum: " + totalNum + "| magId: " + args.getMagId());
        // 全部下载完成
        if (pageNum == totalNum) {
            Toast.makeText(application.getApplicationContext(),
                    "Download Complete!", Toast.LENGTH_SHORT).show();
            updateDownloadStatus(ConstantUtil.DOWNLOAD_STATUS.COMPLETE);
            return;
        }
        // 用户取消下载
        if (isPaused) {
            updateDownloadStatus(ConstantUtil.DOWNLOAD_STATUS.PAUSE);
            return;
        }
        String url = magContentList.get(0).getMagContentImgUrlList().get(pageNum).getUrl();
        String fileName = url.substring(url.lastIndexOf(File.separator) + 1);
        Path path = Paths.get(downloadFilePath + File.separator + fileName);

        // 下载目录已存在下载，跳过本页下载，开始下一页
        if (FileUtil.isFileExist(path)) {
            pageNum++;
            runDownload();
            return;
        }

        // 执行下载
        executeDownloadManager(url, fileName);
    }

    private void executeDownloadManager(String url, String fileName) {
        Timber.d("url: " + url + "| fileName: " + fileName);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setDestinationInExternalFilesDir(application, fileDir, fileName)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                .setTitle(fileName)
                .setDescription("Downloading")
                .setRequiresCharging(false) // Set if charging is required to begin the download
                .setAllowedOverMetered(true) // Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true); // Set if download is allowed on roaming network

        DownloadManager downloadManager = (DownloadManager) application.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = downloadManager.enqueue(request);
    }

    private void updateDownloadStatus(ConstantUtil.DOWNLOAD_STATUS downloadStatus) {
        // TODO: room partial update OR upsert
//        DownloadUpdate downloadUpdate = new DownloadUpdate();
        DownloadButtonStatus status = new DownloadButtonStatus();
        int downloadedNum = pageNum;

        // 暂停操作，没有触发后续下载，已下载页-1
        if (downloadStatus == ConstantUtil.DOWNLOAD_STATUS.PAUSE) {
            downloadedNum = (pageNum == 0) ? 0 : pageNum - 1;
        }

        status.setDownloadStatus(downloadStatus);
        status.setProgress(pageNum * 100 / totalNum);

        updateDB(downloadedNum, downloadStatus);

        updateBtnState(status);
    }

    /**
     * 更新数据库
     **/
    private void updateDB(int downloadedNum, ConstantUtil.DOWNLOAD_STATUS status) {
        // 首次插入
        if (pageNum == 0) {
            this.insert(initDownloadEntity(status));
            return;
        }
        // 后续更新
        this.update(downloadedNum, status, args.getMagId());
    }

    /**
     * 更新下载按钮状态
     **/
    private void updateBtnState(DownloadButtonStatus downloadButtonStatus) {
        btnStatus.postValue(downloadButtonStatus);
    }

    private Download initDownloadEntity(ConstantUtil.DOWNLOAD_STATUS downloadStatus) {
        Download download = new Download();
        download.setMagId(args.getMagId());
        download.setMagName(args.getMagName());
        download.setMagPubDate(args.getMagPubDate());
        download.setMagCoverImgUrl(args.getMagCoverImgUrl());
        download.setTotalNum(totalNum);
        download.setDownloadedNum(pageNum);
        download.setStatus(downloadStatus);
        return download;
    }


    private boolean downloadConditionAvailable(List<MagContent> magContentList) {
        if (null == magContentList || magContentList.size() == 0) {
            Toast.makeText(application.getApplicationContext(),
                    "mag content is null", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!NetworkUtil.isNetworkAvailable(application.getApplicationContext(), NetworkUtil.NETWORK_TYPE.ALL)) {
            Toast.makeText(application.getApplicationContext(),
                    "network is unavailable!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * A creator is used to inject the args into the ViewModel
     * <p>
     * This creator is to showcase how to inject dependencies into ViewModels. It's not
     * actually necessary in this case, as the args can be passed in a public method.
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Application application;
        private final DetailFragmentArgs args;
        private final DownloadRepository downloadRepository;
        public Factory(@NonNull Application application, DetailFragmentArgs args) {
            this.application = application;
            this.args = args;
            downloadRepository = new DownloadRepository(application);
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new DownloadViewModel(application, args, downloadRepository);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        downloadRepository.compositeDisposable.dispose();
    }
}
