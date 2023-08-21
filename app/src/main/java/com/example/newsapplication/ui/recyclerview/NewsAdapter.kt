package com.example.newsapplication.ui.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.newsapplication.R
import com.example.newsapplication.databinding.ListItemBinding
import com.example.newsapplication.model.Article

class NewsAdapter(
    private val onArticleClick: (Article) -> Unit = {}
) : PagingDataAdapter<Article, NewsAdapter.ArticleViewHolder>(ARTICLE_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(layoutInflater, parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { article ->
            holder.itemView.setOnClickListener { onArticleClick.invoke(item) }
            holder.bind(article)
        }
    }

    inner class ArticleViewHolder(
        private val binding: ListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            with(binding) {
                headlineTextView.text = article.title
                sourceTextView.text = article.source?.name
                imageView.load(article.urlToImage) {
                    crossfade(true)
                    placeholder(R.drawable.ic_broken_image)
                }
            }
        }
    }

    companion object {
        private val ARTICLE_COMPARATOR = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }
        }
    }
}