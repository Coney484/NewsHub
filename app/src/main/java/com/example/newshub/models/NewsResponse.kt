package com.example.newshub.models

import com.example.newshub.models.Article

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)