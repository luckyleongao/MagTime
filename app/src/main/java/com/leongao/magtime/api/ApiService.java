package com.leongao.magtime.api;

import com.leongao.magtime.detail.model.DetailPageResponse;
import com.leongao.magtime.home.model.HomePageResponse;
import com.leongao.magtime.reader.bean.ReaderPageResponse;
import com.leongao.magtime.search.model.CustomizedSearchResponse;
import com.leongao.magtime.search.model.SearchBlockResponse;
import com.leongao.magtime.search.model.SearchPageResponse;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/search-pages?populate[0]=blockImg&populate[1]=block_items")
    Single<SearchPageResponse> getSearchCategories();

    @GET("api/home-pages?populate[0]=block_items&populate[1]=block_items.magazines&populate[2]=block_items.magazines.magInfo&populate[3]=block_items.magazines.magInfo.magCoverImg")
    Single<HomePageResponse> getHomePage();

    @GET("api/block-items/{id}?populate[0]=magazines&populate[1]=magazines.magInfo&populate[2]=magazines.magInfo.magCoverImg")
    Single<SearchBlockResponse> getBlockItem(@Path("id") int blockId);

    @GET("api/mag-contents?&populate=*&")
    Single<ReaderPageResponse> getMagContentUrls(@Query("filters[magId][$eq]=magId") int magId);

    @GET("api/magazines?&populate[magInfo][fields][0]=magName&populate[magInfo][fields][1]=magPublishDate&populate[magInfo][populate][magCoverImg][fields][0]=url")
    Single<DetailPageResponse> getSameMagByName(@Query("filters[magInfo][magName][$eq]=magName") String magName,
                                              @Query("filters[id][$ne]=magId") int magId);

    @GET("api/magazines?fields[0]=id&populate[magInfo][populate][magCoverImg][fields][0]=url")
    Single<CustomizedSearchResponse> getSearchResultByKeywords(@Query("filters[magInfo][magName][$contains]=keywords") String keywords);
}
