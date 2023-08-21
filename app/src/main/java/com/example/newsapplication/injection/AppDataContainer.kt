package com.example.newsapplication.injection

import com.example.newsapplication.data.NewsRepository

interface AppDataContainer {
    val newsRepository: NewsRepository
}