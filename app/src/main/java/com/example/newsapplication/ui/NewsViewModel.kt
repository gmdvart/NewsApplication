package com.example.newsapplication.ui

import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.newsapplication.NewsApplication
import com.example.newsapplication.data.NewsRepository
import com.example.newsapplication.model.Article
import com.example.newsapplication.model.ArticlesResponse
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import java.lang.Exception

private const val TAG = "NewsViewModel"

class NewsViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {

    enum class ResponseStatus {
        SUCCESS,
        LOADING,
        ERROR
    }

    private var _apiStatus: MutableLiveData<ResponseStatus> = MutableLiveData()
    val apiStatus: LiveData<ResponseStatus>
        get() = _apiStatus

    private var _articlesResponse: MutableLiveData<ArticlesResponse> = MutableLiveData()
    val articlesResponse: LiveData<ArticlesResponse>
        get() = _articlesResponse

    private var _selectedArticle: MutableLiveData<Article> = MutableLiveData()
    val selectedArticle: LiveData<Article>
        get() = _selectedArticle

    fun loadArticles(country: String = "us", category: String = "general", query: String? = null, pageIndex: Int = 0) {
        _apiStatus.value = ResponseStatus.LOADING
        viewModelScope.launch {
            try {
                _articlesResponse.value = newsRepository.getArticles(country, category, query, pageIndex)
                _apiStatus.value = ResponseStatus.SUCCESS
            } catch (e: Exception) {
                _apiStatus.value = ResponseStatus.ERROR
                when (e) {
                    is IOException -> {
                        e.message?.let { Log.e(TAG, "Caught an IOException: $it") }
                    }
                    is HttpException -> {
                        e.message?.let { Log.e(TAG, "Caught an HttpException: $it") }
                    }
                }
            }
        }
    }

    fun selectArticle(article: Article) {
        _selectedArticle.value = article
    }

    init {
        loadArticles()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as NewsApplication).container.newsRepository
                NewsViewModel(repository)
            }
        }
    }
}