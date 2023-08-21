package com.example.newsapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsapplication.NewsApplication
import com.example.newsapplication.data.NewsRepository
import com.example.newsapplication.model.Article
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModel(private val newsRepository: NewsRepository) : ViewModel() {

    val pagingDataFlow: Flow<PagingData<Article>>

    val searchAction: (SearchInfo) -> Unit

    private var _selectedArticle: MutableStateFlow<Article?> = MutableStateFlow(null)
    val selectedArticle: StateFlow<Article?>
        get() = _selectedArticle.asStateFlow()

    private fun searchArticles(queryString: String? = null, category: String? = null): Flow<PagingData<Article>> {
        return newsRepository.getSearchResultStream(query = queryString, category = category)
    }

    fun selectArticle(article: Article) {
        _selectedArticle.value = article
    }

    init {
        val searchSharedFlow = MutableSharedFlow<SearchInfo>()

        val currentSearch = searchSharedFlow
            .distinctUntilChanged()
            .onStart { emit(SearchInfo()) }

        pagingDataFlow = currentSearch
            .flatMapLatest { searchInfo ->
                searchArticles(queryString = searchInfo.searchQuery, category = searchInfo.category)
            }.cachedIn(viewModelScope)

        searchAction = { searchQuery ->
            viewModelScope.launch {
                searchSharedFlow.emit(searchQuery)
            }
        }
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

data class SearchInfo(
    val searchQuery: String? = null,
    val category: String? = null
)
