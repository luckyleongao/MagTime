package com.leongao.magtime.repository;

import android.content.Context;
import android.os.Environment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.leongao.magtime.api.ApiClient;
import com.leongao.magtime.app.AppDatabase;
import com.leongao.magtime.app.ResultState;
import com.leongao.magtime.db.dao.DownloadDao;
import com.leongao.magtime.db.entity.Download;
import com.leongao.magtime.db.entity.DownloadUpdate;
import com.leongao.magtime.db.entity.Favorite;
import com.leongao.magtime.reader.bean.ReaderPageResponse;
import com.leongao.magtime.utils.ConstantUtil;
import com.leongao.magtime.utils.FileUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DownloadRepository {

    private AppDatabase mAppDatabase;
    private DownloadDao mDownloadDao;
    public CompositeDisposable compositeDisposable;

    public DownloadRepository(Context context) {
        mAppDatabase = AppDatabase.getInstance(context);
        mDownloadDao = mAppDatabase.downloadDao();
        compositeDisposable = new CompositeDisposable();
    }

    public void insert(Download download) {
        Disposable disposable = mDownloadDao.insert(download)
                .subscribeOn(Schedulers.io())
                .subscribe();
        compositeDisposable.add(disposable);
    }

    public Download findById(int magId) {
        Future<Download> result = AppDatabase.databaseExecutor.submit(() -> mDownloadDao.findById(magId));
        try {
            return result.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public LiveData<ResultState<List<Download>>> findAll() {
        Flowable<ResultState<List<Download>>> flowable = mDownloadDao.findAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(downloads -> new ResultState<>(downloads, null))
                .onErrorReturn(throwable -> new ResultState<>(null, throwable.getMessage()));
        return LiveDataReactiveStreams.fromPublisher(flowable);
    }

    public void deleteById(int magId) {
        Disposable disposable = mDownloadDao.deleteById(magId)
                .subscribeOn(Schedulers.io())
                .subscribe();
        compositeDisposable.add(disposable);
    }

    public void deleteByIds(int[] magIds) {
        Disposable disposable = mDownloadDao.deleteByIds(magIds)
                .subscribeOn(Schedulers.io())
                .subscribe();
        compositeDisposable.add(disposable);
    }

    public void deleteAll() {
        Disposable disposable = mDownloadDao.deleteALl()
                .subscribeOn(Schedulers.io())
                .subscribe();
        compositeDisposable.add(disposable);
    }

    public void update(int downloadedNum, ConstantUtil.DOWNLOAD_STATUS status, int magId) {
        Disposable disposable = mDownloadDao.update(downloadedNum, status, magId)
                .subscribeOn(Schedulers.io())
                .subscribe();
        compositeDisposable.add(disposable);
    }

    public void deleteFromStorageByIds(Context context, int[] magIds) {
        Disposable disposable = Observable.create(emitter -> {
            for (int magId : magIds) {
                String fileDir = Environment.DIRECTORY_DOWNLOADS + File.separator + magId;
                String filePath = String.valueOf(context.getExternalFilesDir(fileDir));
                // TODO: 删除失败待处理
                FileUtil.deleteDir(new File(filePath), true);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe();
        compositeDisposable.add(disposable);
    }

    public void deleteAllFromStorage(Context context) {
        Disposable disposable = Observable.create(emitter -> {
                    String fileDir = Environment.DIRECTORY_DOWNLOADS;
                    String filePath = String.valueOf(context.getExternalFilesDir(fileDir));
                    // TODO: 删除失败待处理
                    FileUtil.deleteDir(new File(filePath), false);
                }).subscribeOn(Schedulers.io())
                .subscribe();
        compositeDisposable.add(disposable);
    }

    public void getMagazineContentUrls(int magId,
                                       DisposableSingleObserver<ReaderPageResponse> observer) {
        ApiClient.getApiService().getMagContentUrls(magId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
