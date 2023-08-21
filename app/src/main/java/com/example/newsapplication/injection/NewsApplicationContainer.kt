package com.example.newsapplication.injection

import com.example.newsapplication.data.NewsRepository
import com.example.newsapplication.network.NewsApi
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level

class NewsApplicationContainer : AppDataContainer {

    private val BASE_URL = "https://newsapi.org/v2/"

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private fun createClient(): OkHttpClient {
        val logger: HttpLoggingInterceptor = HttpLoggingInterceptor()
        logger.level = Level.BASIC

        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(createClient())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val newsApi: NewsApi by lazy { retrofit.create(NewsApi::class.java) }

    override val newsRepository: NewsRepository by lazy { NewsRepository(newsApi) }
}