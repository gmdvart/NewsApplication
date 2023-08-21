package com.example.newsapplication.ui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.databinding.ArticleLoadStateFooterViewItemBinding

class ArticleLoadStateAdapter(
    private val retry: () -> Unit = {}
) : LoadStateAdapter<ArticleLoadStateAdapter.ArticleLoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ArticleLoadStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ArticleLoadStateFooterViewItemBinding.inflate(inflater, parent, false)
        return ArticleLoadStateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class ArticleLoadStateViewHolder(
        private val binding: ArticleLoadStateFooterViewItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            with(binding) {
                if (loadState is LoadState.Error) {
                    errorMsg.text = loadState.error.message
                }
                errorMsg.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE
                retryButton.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE
                progressBar.visibility = if (loadState is LoadState.Loading) View.VISIBLE else View.GONE
            }
        }
    }
}