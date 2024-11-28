package com.leongao.magtime.repository;

import android.os.Environment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.leongao.magtime.api.ApiClient;
import com.leongao.magtime.app.MyApplication;
import com.leongao.magtime.app.ResultState;
import com.leongao.magtime.reader.bean.ReaderPageResponse;
import com.leongao.magtime.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ClassicReaderRepository {

    /**
     * 从网络获取杂志内容页url
     * @param magId
     * @return
     */
    public LiveData<ResultState<ReaderPageResponse>> getMagContentUrls(int magId) {
        Flowable<ResultState<ReaderPageResponse>> flowable = ApiClient.getApiService().getMagContentUrls(magId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(readerPageResponse -> new ResultState<>(readerPageResponse, null))
                .onErrorReturn(throwable -> new ResultState<>(null, throwable.getMessage()))
                .toFlowable();
        return LiveDataReactiveStreams.fromPublisher(flowable);
    }

    /**
     * 从本地下载目录获取杂志内容页url
     * 注释：Glide加载本地图片，支持String和File类型，不支持Uri？
     * @param magId
     */
    public List<String> getLocalMagContentUrls(int magId) {
        List<String> localUrls = null;
        File downloadFileDir = MyApplication.getAppContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS + File.separator + magId);
        Path path = Paths.get(String.valueOf(downloadFileDir));
        try {
            if (!FileUtil.isDirectoryEmpty(path)) {
                localUrls = FileUtil.getAllFilesAbsolutePath(downloadFileDir);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return localUrls;
    }
}
