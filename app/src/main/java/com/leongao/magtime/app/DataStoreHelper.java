package com.leongao.magtime.app;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava3.RxDataStore;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;

/**
 * datastore 如果用Java实现，需要结合RxJava
 */
public class DataStoreHelper {
    private Activity activity;
    private RxDataStore<Preferences> dataStoreRX;
    private Preferences pref_error = new Preferences() {
        @Override
        public <T> boolean contains(@NonNull Key<T> key) {
            return false;
        }

        @Nullable
        @Override
        public <T> T get(@NonNull Key<T> key) {
            return null;
        }

        @NonNull
        @Override
        public Map<Key<?>, Object> asMap() {
            return null;
        }
    };

    public DataStoreHelper(Activity activity, RxDataStore<Preferences> dataStoreRX) {
        this.activity = activity;
        this.dataStoreRX = dataStoreRX;
    }

    public boolean putStringValue(String key, String value) {
        Preferences.Key<String> PREF_KEY = PreferencesKeys.stringKey(key);
        Single<Preferences> updateResult = dataStoreRX.updateDataAsync(preferences -> {
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.set(PREF_KEY, value);
            return Single.just(mutablePreferences);
        }).onErrorReturnItem(pref_error);
        return updateResult.blockingGet() != pref_error;
    }

    public String getStringValueByKey(String key) {
        Preferences.Key<String> PREF_KEY = PreferencesKeys.stringKey(key);
        Single<String> value = dataStoreRX.data().firstOrError().map(preferences -> preferences.get(PREF_KEY)).onErrorReturnItem("null");
        return value.blockingGet();
    }

    public List<String> getAllSearchHistoryTags() {
        Single<List<String>> values = dataStoreRX.data().map(preferences ->
                Arrays.toString(preferences.asMap().values().toArray())).toList();
        return values.blockingGet();
    }

//    public boolean putBooleanValue(String key, boolean value) {}
//    public boolean getBooleanValue(String key) {}

}
