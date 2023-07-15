package com.example.newsapplication

import android.app.Application
import com.example.newsapplication.data.AppDataContainer
import com.example.newsapplication.data.NewsApplicationContainer

class NewsApplication : Application() {

    private lateinit var _container: AppDataContainer
    val container: AppDataContainer
        get() = _container

    override fun onCreate() {
        super.onCreate()
        _container = NewsApplicationContainer(this)
    }
}