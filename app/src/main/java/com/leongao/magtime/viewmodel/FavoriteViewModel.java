package com.leongao.magtime.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.leongao.magtime.db.entity.Favorite;
import com.leongao.magtime.repository.FavoriteRepository;

import java.util.List;

public class FavoriteViewModel extends AndroidViewModel {

    private FavoriteRepository favoriteRepository;
    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        favoriteRepository = new FavoriteRepository(application);
    }

    public void insert(Favorite favorite) {
        favoriteRepository.insert(favorite);
    }

    public void delete(Favorite favorite) {
        favoriteRepository.delete(favorite);
    }

    public LiveData<List<Favorite>> findAll() {
        return favoriteRepository.findAll();
    }

    public Favorite findById(int magId) {
        return favoriteRepository.findById(magId);
    }

    public void deleteById(int magId) {
        favoriteRepository.deleteById(magId);
    }

    public void deleteAll() {
        favoriteRepository.deleteAll();
    }

    public void deleteByIds(int[] magIds) {
        favoriteRepository.deleteByIds(magIds);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private final Application application;

        public Factory(Application application) {
            this.application = application;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new FavoriteViewModel(application);
        }
    }

}
