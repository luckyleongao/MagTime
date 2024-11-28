package com.leongao.magtime.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.leongao.magtime.api.ApiClient;
import com.leongao.magtime.home.model.HomePageResponse;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeViewModel extends AndroidViewModel {

    public MutableLiveData<HomePageResponse> homeResponse = new MutableLiveData<>();
    public MutableLiveData<String> error = new MutableLiveData<>();

    private Disposable disposable;

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    public void getHomePage() {
//        TODO: LiveData Error Handle???
//        Flowable<HomePageResponse> flowable = ApiClient.getApiService().getHomePage()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .onErrorReturn(throwable -> null)
//                .toFlowable();
//        return LiveDataReactiveStreams.fromPublisher(flowable);
        disposable = ApiClient.getApiService().getHomePage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<HomePageResponse>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull HomePageResponse homePageResponse) {
                        homeResponse.postValue(homePageResponse);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        error.postValue(e.toString());
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
