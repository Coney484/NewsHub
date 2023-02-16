package com.example.newshub.repository

import com.example.newshub.api.RetrofitInstance
import com.example.newshub.db.ArticleDatabase
import com.example.newshub.models.Article
import retrofit2.http.Query

class NewsRepository(
    val db: ArticleDatabase
) {

    suspend fun getHeadlines(countryCode: String, pagNumber: Int) =
        RetrofitInstance.api.getHeadlines(countryCode, pagNumber)


    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun updateOrInsert(article: Article) = db.articleDao().updateOrInsert(article)

    suspend fun deleteArticle(article: Article) = db.articleDao().deleteArticle(article)

    fun getSavedNews() = db.articleDao().getAllArticles()
}