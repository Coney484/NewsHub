package com.example.newshub.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newshub.api.RetrofitInstance
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

    init {
        getHeadlines("in")
    }


    fun getHeadlines(countryCode: String) = viewModelScope.launch {
        headlines.postValue(Resource.Loading())
        val response = newsRepository.getHeadlines(countryCode, headlinesPage)
        headlines.postValue(handleHeadlinesResponse(response))
    }

    private fun handleHeadlinesResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Failure(response.message())
    }
}