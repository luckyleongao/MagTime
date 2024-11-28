package com.leongao.magtime.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.leongao.magtime.app.ResultState;
import com.leongao.magtime.detail.model.DetailPageResponse;
import com.leongao.magtime.repository.DetailRepository;

public class DetailViewModel extends AndroidViewModel {
    private DetailRepository detailRepository;
    private long downloadId;

    public DetailViewModel(@NonNull Application application) {
        super(application);
        detailRepository = new DetailRepository();
    }

    public LiveData<ResultState<DetailPageResponse>> getSimilarMagazines(String magName, int magId) {
        return detailRepository.getSimilarMagazines(magName, magId);
    }


}