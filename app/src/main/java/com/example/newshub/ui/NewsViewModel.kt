package com.example.newshub.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newshub.api.RetrofitInstance
import com.example.newshub.models.Article
import com.example.newshub.models.NewsResponse
import com.example.newshub.repository.NewsRepository
import com.example.newshub.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    val newsRepository: NewsRepository
) : ViewModel() {

    val headlines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headlinesPage = 1
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var headlinesNewsResponse: NewsResponse? = null
    var searchNewsResponse: NewsResponse? = null

    init {
        getHeadlines("in")
    }


    fun getHeadlines(countryCode: String) = viewModelScope.launch {
        headlines.postValue(Resource.Loading())
        val response = newsRepository.getHeadlines(countryCode, headlinesPage)
        headlines.postValue(handleHeadlinesResponse(response))
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(searchQuery, searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }

    private fun handleHeadlinesResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                headlinesPage++
                if (headlinesNewsResponse == null) {
                    headlinesNewsResponse = it
                } else {
                    val oldArticleCount = headlinesNewsResponse?.articles
                    val newsArticleCount = it.articles
                    oldArticleCount?.addAll(newsArticleCount)
                }
                return Resource.Success(headlinesNewsResponse ?: it)
            }
        }
        return Resource.Failure(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = it
                } else {
                    val oldArticleCount = searchNewsResponse?.articles
                    val newsArticleCount = it.articles
                    oldArticleCount?.addAll(newsArticleCount)
                }
                return Resource.Success(searchNewsResponse ?: it)
            }
        }
        return Resource.Failure(response.message())
    }


    fun savedArticleToDb(article: Article) = viewModelScope.launch {
        newsRepository.updateOrInsert(article = article)
    }

    fun getSavedNewsFromDb() = newsRepository.getSavedNews()

    fun deleteArticles(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

}