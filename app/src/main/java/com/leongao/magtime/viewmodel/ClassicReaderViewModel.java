package com.leongao.magtime.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.leongao.magtime.api.ApiClient;
import com.leongao.magtime.app.ResultState;
import com.leongao.magtime.home.model.MagCoverImg;
import com.leongao.magtime.reader.bean.ReaderPageResponse;
import com.leongao.magtime.repository.ClassicReaderRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class ClassicReaderViewModel extends AndroidViewModel {

    private ClassicReaderRepository classicReaderRepository;
    public ClassicReaderViewModel(@NonNull Application application) {
        super(application);
        classicReaderRepository = new ClassicReaderRepository();
    }

    public LiveData<ResultState<ReaderPageResponse>> getMagContentUrls(int magId) {
        return classicReaderRepository.getMagContentUrls(magId);
    }

    public List<String> getLocalMagContentUrls(int magId) {
        return classicReaderRepository.getLocalMagContentUrls(magId);
    }

    /**
     * 全部下载：传入本地uri集合
     * 部分下载：传入本地uri和网络url数据集合
     * 未下载：传入网络url数据集合
     *
     * @param localMagContentUrls
     * @param remoteMagContentUrls: 有网络或有缓存情形下传入，不为空
     * @return
     */
    public List<String> getAdapterData(List<String> localMagContentUrls, List<MagCoverImg> remoteMagContentUrls) {
        if (localMagContentUrls == null) return convertMagCoverImg2Url(remoteMagContentUrls);
        Timber.d("local size: %s", localMagContentUrls.size());
        Timber.d("remote size: %s", remoteMagContentUrls.size());
        if (localMagContentUrls.size() == remoteMagContentUrls.size()) return localMagContentUrls;
        // 部分下载，返回本地+网络url
        List<String> list = new ArrayList<>(localMagContentUrls);
        for (String url : convertMagCoverImg2Url(remoteMagContentUrls)) {
            String fileName = url.substring(url.lastIndexOf(File.separator) + 1);
            if (hasMatchSubstring(localMagContentUrls, fileName)) continue;
            list.add(url);
        }
        return list;
    }

    public List<String> convertMagCoverImg2Url(List<MagCoverImg> remoteMagContentUrls) {
        List<String> list = new ArrayList<>();
        for (MagCoverImg magCoverImg : remoteMagContentUrls) {
            list.add(magCoverImg.getUrl());
        }
        return list;
    }

    private boolean hasMatchSubstring(List<String> list, String str) {
        for (String s : list) {
            if (s.contains(str)) return true;
        }
        return false;
    }

}
