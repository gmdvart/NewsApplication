package com.example.newsapplication.data

import android.content.Context
import com.example.newsapplication.R
import com.example.newsapplication.model.ArticlesResponse
import com.example.newsapplication.network.NewsApi

class NewsRepository(
    private val context: Context,
    private val newsApi: NewsApi
) {
    suspend fun getArticles(
        country: String,
        category: String,
        query: String?,
        pageIndex: Int
    ): ArticlesResponse {
        val apiKey: String = context.getString(R.string.api_key)
        return newsApi.getArticles(apiKey, country, category, query, pageIndex)
    }
}