package com.example.newsapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.newsapplication.databinding.FragmentDetailBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding

    private val viewModel: NewsViewModel by activityViewModels { NewsViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayArticleInfo()
    }

    private fun displayArticleInfo() {
        lifecycleScope.launch {
            viewModel.selectedArticle.collectLatest { article ->
                article?.let {
                    with(binding) {
                        imageView.load(it.urlToImage)
                        headlineTextView.text = it.title
                        authorTextView.text = it.author
                        descriptionTextView.text = it.description
                    }
                }
            }
        }
    }
}