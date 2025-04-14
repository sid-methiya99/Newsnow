package com.example.newsnow.api;

import com.example.newsnow.model.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {
    @GET("v2/top-headlines")
    Call<NewsResponse> getTopHeadlines(
        @Query("apiKey") String apiKey,
        @Query("language") String language,
        @Query("category") String category,
        @Query("q") String query
    );
} 