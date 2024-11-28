package com.leongao.magtime.app;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.leongao.magtime.db.dao.DownloadDao;
import com.leongao.magtime.db.dao.FavoriteDao;
import com.leongao.magtime.db.entity.Download;
import com.leongao.magtime.db.entity.Favorite;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Favorite.class, Download.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "MagTime-db";

    private static final int THREAD_POOL_NUM = 4;
    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(THREAD_POOL_NUM);
    public abstract FavoriteDao favoriteDao();
    public abstract DownloadDao downloadDao();

    public static AppDatabase INSTANCE;

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object.
                            // TODO: Using this fallback is almost certainly a bad idea
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
