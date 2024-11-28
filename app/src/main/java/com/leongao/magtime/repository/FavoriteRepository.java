package com.leongao.magtime.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.leongao.magtime.app.AppDatabase;
import com.leongao.magtime.db.dao.FavoriteDao;
import com.leongao.magtime.db.entity.Favorite;
import com.leongao.magtime.home.model.Magazine;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FavoriteRepository {

    private AppDatabase mAppDatabase;
    private FavoriteDao mFavoriteDao;
    private LiveData<List<Magazine>> allFavoriteMagazines;

    public FavoriteRepository(Context context) {
        mAppDatabase = AppDatabase.getInstance(context);
        mFavoriteDao = mAppDatabase.favoriteDao();
    }

    public void insert(Favorite favorite) {
        AppDatabase.databaseExecutor.execute(() -> {
            mFavoriteDao.insert(favorite);
        });
    }

    public void delete(Favorite favorite) {
        AppDatabase.databaseExecutor.execute(() -> {
            mFavoriteDao.delete(favorite);
        });
    }

    public void deleteAll() {
        AppDatabase.databaseExecutor.execute(() -> {
            mFavoriteDao.deleteAll();
        });
    }

    public void deleteByIds(int[] magIds) {
        AppDatabase.databaseExecutor.execute(() -> {
            mFavoriteDao.deleteByIds(magIds);
        });
    }

    public void deleteById(int magId) {
        AppDatabase.databaseExecutor.execute(() -> {
            mFavoriteDao.deleteById(magId);
        });
    }

    public Favorite findById(int magId) {
//        // TODO: replace with RxJava
        Future<Favorite> result = AppDatabase.databaseExecutor.submit(() -> mFavoriteDao.findById(magId));
        try {
            return result.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
//        Flowable<Favorite> flowable = mFavoriteDao.findById(magId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                // TODO: Error -> LiveData does not handle errors.
//                //      Errors from publishers should be handled upstream and propagated as state
//                .onErrorReturn(throwable -> null)
//                .toFlowable();
//        return LiveDataReactiveStreams.fromPublisher(flowable);
    }

    public LiveData<List<Favorite>> findAll() {
        // TODO: replace with RxJava
        // Future blocks the thread
        Future<LiveData<List<Favorite>>> result = AppDatabase.databaseExecutor.submit(() -> mFavoriteDao.findAll());
        try {
            return result.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

//        Flowable<LiveData<List<Favorite>>> flowable = mFavoriteDao.findAll()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .toFlowable();
//        return LiveDataReactiveStreams.fromPublisher(flowable);
    }

}
