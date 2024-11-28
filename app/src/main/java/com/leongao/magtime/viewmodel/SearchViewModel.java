package com.leongao.magtime.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.leongao.magtime.app.ResultState;
import com.leongao.magtime.repository.SearchRepository;
import com.leongao.magtime.search.model.CustomizedSearchResponse;
import com.leongao.magtime.search.model.SearchBlockResponse;
import com.leongao.magtime.search.model.SearchPageResponse;

public class SearchViewModel extends AndroidViewModel {

    private SearchRepository searchRepository;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        searchRepository = new SearchRepository();
    }

    public LiveData<ResultState<SearchPageResponse>> getSearchCategories() {
        return searchRepository.getSearchCategories();
    }

    public LiveData<ResultState<SearchBlockResponse>> getSearchBlockItem(int blockId) {
        return searchRepository.getSearchBlockItem(blockId);
    }

    public LiveData<ResultState<CustomizedSearchResponse>> getSearchResultByKeywords(String keywords) {
        return searchRepository.getSearchResultByKeywords(keywords);
    }
}
