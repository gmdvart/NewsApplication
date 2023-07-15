package com.example.newsapplication.network

import com.example.newsapplication.model.ArticlesResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NewsApi {
    @GET("top-headlines")
    suspend fun getArticles(
        @Header("X-Api-Key") apiKey: String,
        @Query("country") country: String,
        @Query("category") category: String,
        @Query("q") query: String?,
        @Query("page") pageIndex: Int
    ): ArticlesResponse
}