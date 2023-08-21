package com.example.newsapplication.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.newsapplication.model.Article
import com.example.newsapplication.network.NewsApi
import kotlinx.coroutines.flow.Flow

class NewsRepository(private val newsApi: NewsApi) {

    fun getSearchResultStream(query: String?, category: String?): Flow<PagingData<Article>> {
        val pagingSourceFactory = {
            ArticlePagingSource(
                service = newsApi,
                query = query,
                category = category
            )
        }

        return Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGE_LOAD_SIZE, enablePlaceholders = false),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    companion object {
        const val DEFAULT_PAGE_LOAD_SIZE = 6
    }
}