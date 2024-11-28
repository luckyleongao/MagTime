package com.leongao.magtime.api;

import com.leongao.magtime.app.MyApplication;
import com.leongao.magtime.utils.ConstantUtil;
import com.leongao.magtime.utils.NetworkUtil;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static ApiService apiService;
    private static int cacheSize = 10 * 1024 * 1024; // 10 MB
    private static Cache cache = new Cache(MyApplication.getAppContext().getCacheDir(), cacheSize);
    private static Interceptor onlineInterceptor = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response response = chain.proceed(chain.request());
            // read from cache for 60 seconds even if there is internet connection
            int maxAge = 60;
            return response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .header("Authorization", "Bearer" + ConstantUtil.API_TOKEN)
                    .removeHeader("Pragma") // This lets you override the server's caching protocol
                    .build();
        }
    };

    private static Interceptor offlineInterceptor = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetworkUtil.isNetworkAvailable(MyApplication.getAppContext())) {
                // Offline cache available for 30 days
                int maxStale = 60 * 60 * 24 * 30;
                request = request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .header("Authorization", "Bearer" + ConstantUtil.API_TOKEN)
                        .removeHeader("Pragma") // This lets you override the server's caching protocol
                        .build();
            }
            return chain.proceed(request);
        }
    };

    private static final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .addInterceptor(offlineInterceptor)
            .addNetworkInterceptor(onlineInterceptor)
            .cache(cache)
            .build();

    public static ApiService getApiService() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ConstantUtil.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

}
