package com.leongao.magtime.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.leongao.magtime.api.ApiClient;
import com.leongao.magtime.app.ResultState;
import com.leongao.magtime.search.model.CustomizedSearchResponse;
import com.leongao.magtime.search.model.SearchBlockResponse;
import com.leongao.magtime.search.model.SearchPageResponse;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchRepository {

    /**
     * 获取所有搜索分类
     *
     **/
    public LiveData<ResultState<SearchPageResponse>> getSearchCategories() {
        Flowable<ResultState<SearchPageResponse>> flowable = ApiClient.getApiService().getSearchCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(searchPageResponse -> new ResultState<>(searchPageResponse, null))
                .onErrorReturn(throwable -> new ResultState<>(null, throwable.getMessage()))
                .toFlowable();
        return LiveDataReactiveStreams.fromPublisher(flowable);
    }

    /**
     * 获取某个搜索板块下的所有杂志
     *
     **/
    public LiveData<ResultState<SearchBlockResponse>> getSearchBlockItem(int blockId) {
        Flowable<ResultState<SearchBlockResponse>> flowable = ApiClient.getApiService().getBlockItem(blockId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(searchBlockResponse -> new ResultState<>(searchBlockResponse, null))
                .onErrorReturn(throwable -> new ResultState<>(null, throwable.getMessage()))
                .toFlowable();
        return LiveDataReactiveStreams.fromPublisher(flowable);
    }

    /**
     * 关键字搜索
     *
     **/
    public LiveData<ResultState<CustomizedSearchResponse>> getSearchResultByKeywords(String keywords) {
        Flowable<ResultState<CustomizedSearchResponse>> flowable = ApiClient.getApiService().getSearchResultByKeywords(keywords)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(customizedSearchResponse -> new ResultState<>(customizedSearchResponse, null))
                .onErrorReturn(throwable -> new ResultState<>(null, throwable.getMessage()))
                .toFlowable();
        return LiveDataReactiveStreams.fromPublisher(flowable);
    }
}
