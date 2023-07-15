package com.example.newsapplication.data

import android.content.Context
import com.example.newsapplication.network.NewsApi
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class NewsApplicationContainer(
    private val context: Context
) : AppDataContainer {

    private val BASE_URL = "https://newsapi.org/v2/"

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val newsApi: NewsApi by lazy { retrofit.create(NewsApi::class.java) }

    override val newsRepository: NewsRepository by lazy { NewsRepository(context, newsApi) }
}