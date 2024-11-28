package com.leongao.magtime.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.leongao.magtime.db.entity.Download;

import java.util.List;
import com.leongao.magtime.db.entity.Download;
import com.leongao.magtime.db.entity.DownloadUpdate;
import com.leongao.magtime.utils.ConstantUtil;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface DownloadDao {

    /* 由于数据库中 Download 量可能很大，可能会因为背压导致内存溢出
       故采用 Flowable 模式，取代 Observable
     */
    @Query("SELECT * FROM download")
    Flowable<List<Download>> findAll();

    @Query("SELECT * FROM download WHERE magId LIKE :magId")
    Download findById(int magId);

    /* Completable 可以看作是 RxJava 的 Runnale 接口
       但他只能调用 onComplete 和 onError 方法，不能进行 map、flatMap 等操作
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Download download);

    @Query("DELETE FROM download WHERE magId LIKE :magId")
    Completable deleteById(int magId);

    @Query("DELETE FROM download WHERE magId IN (:magIds)")
    Completable deleteByIds(int[] magIds);

    @Query("DELETE FROM download")
    Completable deleteALl();

    @Query("UPDATE download SET downloadedNum =:downloadedNum, status =:status WHERE magId LIKE :magId")
    Completable update(int downloadedNum, ConstantUtil.DOWNLOAD_STATUS status, int magId);
}
