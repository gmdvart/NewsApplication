package com.example.newsapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import coil.load
import com.example.newsapplication.databinding.FragmentDetailBinding
import com.example.newsapplication.model.Article
import com.example.newsapplication.ui.NewsViewModel

private const val TAG = "DetailFragment"

class DetailFragment : Fragment() {

    private lateinit var _binding: FragmentDetailBinding
    val binding: FragmentDetailBinding
        get() = _binding

    private val viewModel: NewsViewModel by activityViewModels { NewsViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectedArticle.observe(viewLifecycleOwner) { article ->
            displayArticleInfo(article)
            Log.d(TAG, "Les go dude")
        }
    }

    private fun displayArticleInfo(article: Article) {
        with(_binding) {
            imageView.load(article.urlToImage)
            headlineTextView.text = article.title
            authorTextView.text = article.author
            descriptionTextView.text = article.description
        }
    }
}