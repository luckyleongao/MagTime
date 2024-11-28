package com.leongao.magtime.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.leongao.magtime.api.ApiClient;
import com.leongao.magtime.app.ResultState;
import com.leongao.magtime.detail.model.DetailPageResponse;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DetailRepository {

    /*
    * 详情页推荐板块
    * */
    public LiveData<ResultState<DetailPageResponse>> getSimilarMagazines(String magName, int magId) {
        @NonNull Flowable<ResultState<DetailPageResponse>> flowable = ApiClient.getApiService().getSameMagByName(magName, magId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(detailPageResponse -> new ResultState<>(detailPageResponse, null))
                .onErrorReturn(throwable -> new ResultState<>(null, throwable.getMessage()))
                .toFlowable();
        return LiveDataReactiveStreams.fromPublisher(flowable);
    }


}
