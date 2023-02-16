package com.example.newshub.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newshub.R
import com.example.newshub.databinding.ItemCardViewNewsBinding
import com.example.newshub.databinding.ItemPreviewArticleBinding
import com.example.newshub.models.Article
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.*

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(val binding: ItemCardViewNewsBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding =
            ItemCardViewNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ArticleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        with(holder) {
            Glide.with(itemView.context).load(article.urlToImage)
                .placeholder(R.drawable.placeholder_image).into(binding.ivArticleImage)
            binding.tvDescription.text = article.description
            binding.tvPublishedAt.text = article.publishedAt?.let { convertTimestampToIST(it) }
            binding.tvSource.text = article.source?.name
            binding.tvTitle.text = article.title
        }

        holder.itemView.apply {
            setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertTimestampToIST(timestamp: String): String {
        val formatter = ISO_DATE_TIME
        val dateTime = LocalDateTime.parse(timestamp, formatter)
        val sourceZone = ZoneId.of("UTC")
        val targetZone = ZoneId.of("Asia/Kolkata")
        val zonedDateTime = ZonedDateTime.of(dateTime, sourceZone).withZoneSameInstant(targetZone)
        return formatter.format(zonedDateTime)
    }
}