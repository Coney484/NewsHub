package com.example.newshub.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newshub.NewsHubApplication
import com.example.newshub.api.RetrofitInstance
import com.example.newshub.models.Article
import com.example.newshub.models.NewsResponse
import com.example.newshub.repository.NewsRepository
import com.example.newshub.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
    val newsRepository: NewsRepository
) : AndroidViewModel(app) {

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

        safeHeadlinesCall(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchCall(searchQuery)
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

    private fun isConnected(): Boolean {
        val connectivityManager = getApplication<NewsHubApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
            return false
        }


    }


    private suspend fun safeHeadlinesCall(countryCode: String) {
        headlines.postValue(Resource.Loading())

        try {
            if (isConnected()) {
                val response = newsRepository.getHeadlines(countryCode, headlinesPage)
                headlines.postValue(handleHeadlinesResponse(response))

            } else {
                headlines.postValue(Resource.Failure("No Internet Available"))
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> headlines.postValue(Resource.Failure("Network Failed"))
                else -> headlines.postValue(Resource.Failure("Conversion Error"))
            }

        }
    }


    private suspend fun safeSearchCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())

        try {
            if (isConnected()) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))

            } else {
                searchNews.postValue(Resource.Failure("No Internet Available"))
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Resource.Failure("Network Failed"))
                else -> searchNews.postValue(Resource.Failure("Conversion Error"))
            }

        }
    }

}