package com.leongao.magtime.app;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.rxjava3.RxDataStore;

public class DataStoreSingleton {
    private RxDataStore<Preferences> dataStore;
    private static DataStoreSingleton dataStoreInstance;

    public static DataStoreSingleton getInstance() {
        if (dataStoreInstance == null) {
            dataStoreInstance = new DataStoreSingleton();
        }
        return dataStoreInstance;
    }

    public void setDataStore(RxDataStore<Preferences> dataStore) {
        this.dataStore = dataStore;
    }

    public RxDataStore<Preferences> getDataStore() {
        return dataStore;
    }
}
