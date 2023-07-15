package com.example.newsapplication.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.newsapplication.R
import com.example.newsapplication.databinding.ListItemBinding
import com.example.newsapplication.model.Article

class NewsAdapter(
    private val onArticleClickListener: OnArticleClickListener
) : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    private var articles: List<Article> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(layoutInflater, parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.itemView.setOnClickListener {
            Log.d("HomeFragment", "onClick")
            onArticleClickListener.onClickArticle(article)
        }
        holder.onBind(article)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    fun submitList(articles: List<Article>) {
        this.articles = articles
        notifyDataSetChanged()
    }

    inner class ArticleViewHolder(
        private val binding: ListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(article: Article) {
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

    interface OnArticleClickListener {
        fun onClickArticle(article: Article)
    }
}