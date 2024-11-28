package com.leongao.magtime.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.leongao.magtime.db.entity.Favorite;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM favorite")
    LiveData<List<Favorite>> findAll();

    @Query("SELECT * FROM favorite WHERE magId LIKE :magId")
    Favorite findById(int magId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Favorite favorite);

    // TODO: delete不管用，deleteById管用，But Why？？
    @Delete
    void delete(Favorite favorite);

    @Query("DELETE FROM favorite WHERE magId IN (:magId)")
    void deleteById(int magId);

    @Query("DELETE FROM favorite WHERE magId IN (:magIds)")
    void deleteByIds(int[] magIds);

    @Query("DELETE FROM favorite")
    void deleteAll();

}
