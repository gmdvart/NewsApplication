package com.example.newsapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.newsapplication.databinding.FragmentHomeBinding
import com.example.newsapplication.model.Article
import com.example.newsapplication.ui.NewsViewModel
import com.example.newsapplication.ui.adapter.NewsAdapter
import com.example.newsapplication.ui.itemdecoration.NewsItemDecoration

private const val TAG = "HomeFragment"

class HomeFragment : Fragment(), CompoundButton.OnCheckedChangeListener, NewsAdapter.OnArticleClickListener {

    private lateinit var _binding: FragmentHomeBinding
    val binding: FragmentHomeBinding
        get() = _binding

    private val viewModel: NewsViewModel by activityViewModels { NewsViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeApiStatus()
        setRecyclerView()
        setSearchView()
        setCategoryChips()
    }

    private fun observeApiStatus() {
        val progressBar = _binding.progressCircular

        viewModel.apiStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                NewsViewModel.ResponseStatus.ERROR -> {
                    Toast.makeText(context, "Unable to fetch news!", Toast.LENGTH_LONG).show()
                }

                NewsViewModel.ResponseStatus.SUCCESS -> {
                    progressBar.visibility = View.GONE
                }

                else -> {
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setRecyclerView() {
        val recyclerView = _binding.recyclerView
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = NewsAdapter(this@HomeFragment)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(NewsItemDecoration())

        viewModel.articlesResponse.observe(viewLifecycleOwner) { response ->
            val articles = response.articles
            adapter.submitList(articles)
        }
    }

    override fun onClickArticle(article: Article) {
        viewModel.selectArticle(article)

        val direction = HomeFragmentDirections.actionHomeFragmentToDetailFragment()
        findNavController().navigate(direction)
    }

    private fun setSearchView() {
        val searchView = _binding.searchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String?): Boolean {
                viewModel.loadArticles(query = newText)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            _binding.chipGroupLayout.visibility = if (hasFocus) {
                View.GONE
            } else {
                _binding.chipGroup.clearCheck()
                viewModel.loadArticles()
                View.VISIBLE
            }
        }
    }

    private fun setCategoryChips() {
        with(_binding) {
            chipBusiness.setOnCheckedChangeListener(this@HomeFragment)
            chipEntertainment.setOnCheckedChangeListener(this@HomeFragment)
            chipHealth.setOnCheckedChangeListener(this@HomeFragment)
            chipScience.setOnCheckedChangeListener(this@HomeFragment)
            chipSports.setOnCheckedChangeListener(this@HomeFragment)
            chipTechnology.setOnCheckedChangeListener(this@HomeFragment)
        }
    }

    override fun onCheckedChanged(compoundButton: CompoundButton, checked: Boolean) {
        if (checked) viewModel.loadArticles(category = compoundButton.text.toString())
        else viewModel.loadArticles()
    }
}