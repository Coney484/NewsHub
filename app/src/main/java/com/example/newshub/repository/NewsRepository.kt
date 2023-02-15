package com.example.newshub.repository

import com.example.newshub.api.RetrofitInstance
import com.example.newshub.db.ArticleDatabase

class NewsRepository(
    val db: ArticleDatabase
) {

    suspend fun getHeadlines(countryCode: String, pagNumber: Int) =
        RetrofitInstance.api.getHeadlines(countryCode, pagNumber)
}