package com.example.newsapplication.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentHomeBinding
import com.example.newsapplication.model.Article
import com.example.newsapplication.ui.recyclerview.ArticleLoadStateAdapter
import com.example.newsapplication.ui.recyclerview.NewsAdapter
import com.example.newsapplication.ui.recyclerview.NewsItemDecoration
import com.example.newsapplication.utils.KeyboardManger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: NewsViewModel by activityViewModels { NewsViewModel.Factory }

    private var selectedCategory = "general"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            SlidePaneOnBackPressedCallback(binding.slidingPaneLayout)
        )

        binding.slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED
        binding.recyclerView.addItemDecoration(NewsItemDecoration())

        binding.bindState(
            pagingDataFlow = viewModel.pagingDataFlow,
            searchAction = viewModel.searchAction
        )
    }

    private fun FragmentHomeBinding.bindState(
        pagingDataFlow: Flow<PagingData<Article>>,
        searchAction: (SearchInfo) -> Unit
    ) {
        bindList(pagingDataFlow)
        bindSearch(searchAction)
    }

    private fun FragmentHomeBinding.bindList(pagingDataFlow: Flow<PagingData<Article>>) {
        val newsAdapter = NewsAdapter {
            viewModel.selectArticle(it)
            slidingPaneLayout.openPane()
        }

        recyclerView.adapter = newsAdapter.withLoadStateHeaderAndFooter(
            header = ArticleLoadStateAdapter { newsAdapter.retry() },
            footer = ArticleLoadStateAdapter { newsAdapter.retry() }
        )

        retryButton.setOnClickListener { newsAdapter.retry() }

        lifecycleScope.launch {
            pagingDataFlow.collectLatest(newsAdapter::submitData)
        }

        lifecycleScope.launch {
            newsAdapter.loadStateFlow.collect { loadState ->
                val isResultListEmpty = loadState.refresh is LoadState.NotLoading && newsAdapter.itemCount == 0

                messageTextView.isVisible = isResultListEmpty
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                retryButton.isVisible = loadState.source.refresh is LoadState.Error

                val errorState = loadState.refresh as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                    ?: loadState.source.refresh as? LoadState.Error
                    ?: loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error

                recyclerView.isVisible = !isResultListEmpty && errorState == null

                errorState?.let {
                    Toast.makeText(context, "Wooops ${it.error}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun FragmentHomeBinding.bindSearch(searchAction: (SearchInfo) -> Unit) {
        searchTextView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                performSearch(searchAction)
                true
            } else {
                false
            }
        }

        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val checkedId = if (checkedIds.size > 0) checkedIds[0] else 0
            selectedCategory = when (checkedId) {
                R.id.chip_business -> { getString(R.string.category_business) }
                R.id.chip_entertainment -> { getString(R.string.category_entertainment) }
                R.id.chip_health -> { getString(R.string.category_health) }
                R.id.chip_science -> { getString(R.string.category_science) }
                R.id.chip_sports -> { getString(R.string.category_sports) }
                R.id.chip_technology -> { getString(R.string.category_technology) }
                else -> { getString(R.string.category_general) }
            }
            selectCategory(
                selectedCategory = selectedCategory.lowercase(),
                searchAction = searchAction
            )
        }
    }

    private fun performSearch(searchAction: (SearchInfo) -> Unit) {
        with(binding) {
            searchTextView.text?.trim().let {
                if (!it.isNullOrBlank()) {
                    KeyboardManger(requireContext()).hideKeyboard(view)
                    recyclerView.scrollToPosition(0)
                    searchTextView.clearFocus()
                    searchAction(SearchInfo(searchQuery = it.toString()))
                }
            }
        }
    }

    private fun selectCategory(selectedCategory: String, searchAction: (SearchInfo) -> Unit) {
        binding.recyclerView.scrollToPosition(0)
        searchAction(SearchInfo(category = selectedCategory))
    }

    inner class SlidePaneOnBackPressedCallback(
        private val slidingPaneLayout: SlidingPaneLayout
    ) : OnBackPressedCallback(slidingPaneLayout.isOpen && slidingPaneLayout.isSlideable), SlidingPaneLayout.PanelSlideListener {

        override fun handleOnBackPressed() { slidingPaneLayout.closePane() }

        override fun onPanelSlide(panel: View, slideOffset: Float) {}

        override fun onPanelOpened(panel: View) { isEnabled = true }

        override fun onPanelClosed(panel: View) { isEnabled = false }

        init {
            slidingPaneLayout.addPanelSlideListener(this)
        }
    }
}