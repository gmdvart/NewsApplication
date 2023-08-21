package com.example.newsapplication.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsapplication.data.NewsRepository.Companion.DEFAULT_PAGE_LOAD_SIZE
import com.example.newsapplication.model.Article
import com.example.newsapplication.network.NewsApi
import okio.IOException
import retrofit2.HttpException

private const val NEWS_INITIAL_PAGE_INDEX = 1

class ArticlePagingSource(
    private val service: NewsApi,
    private val query: String?,
    private val category: String?
) : PagingSource<Int, Article>() {
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.minus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.plus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val position = params.key ?: NEWS_INITIAL_PAGE_INDEX
        return try {
            val response = service.getArticles(
                country = "us",
                query = query,
                category = category,
                pageSize = DEFAULT_PAGE_LOAD_SIZE,
                pageIndex = position
            )
            val articles = response.articles

            val prevKey = if (position == NEWS_INITIAL_PAGE_INDEX) null else position - 1
            val nextKey = if (articles.isEmpty()) null else position + (params.loadSize / NewsRepository.DEFAULT_PAGE_LOAD_SIZE)

            LoadResult.Page(
                data = articles,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}