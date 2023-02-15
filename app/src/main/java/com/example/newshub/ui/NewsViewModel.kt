package com.example.newshub.ui

import androidx.lifecycle.ViewModel
import com.example.newshub.repository.NewsRepository

class NewsViewModel(
    val newsRepository: NewsRepository
) : ViewModel() {
}