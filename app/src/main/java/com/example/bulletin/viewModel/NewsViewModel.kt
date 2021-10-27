package com.example.bulletin.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.engine.Resource
import com.example.bulletin.model.Article
import com.example.bulletin.model.NewsResponse
import com.example.bulletin.repository.NewsRepository
import com.example.bulletin.util.NewsResource
import kotlinx.coroutines.launch
import retrofit2.Response

//We cant use constructor parameters by default for our own viewModel
//If we want to do that then we need to create our own viewModelProviderFactory
//to define how our viewModel should be created

//Since we are passing NewsRepository as a parameter here which is a constructor then we
//also need to pass it as parameter in the viewModelProviderFactory
//and then return the newsViewModel with newsRepository casted as T


class NewsViewModel(private val newsRepository: NewsRepository ) : ViewModel(){

    val breakingNews : MutableLiveData<NewsResource<NewsResponse>> = MutableLiveData()
    var breakingNewsResponse : NewsResponse?= null

    //The reason why we are setting this brakingNewsPage is because when we rotate our device
    //the activity gets recreated and we lose our current page number but the viewModel
    //remains so to avoid that we are declaring which NewsPage we are currently on here.

    var breakingNewsPage = 1

    val searchNews : MutableLiveData<NewsResource<NewsResponse>> = MutableLiveData()
    var searchNewsResponse : NewsResponse?=null
    var searchNewsPage = 1

    init {
        getBreakingNews("in")
    }

    fun searchNews(query : String) = viewModelScope.launch {
        searchNews.postValue(NewsResource.Loading())
        val response = newsRepository.searchNews(query, searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : NewsResource<NewsResponse>{
       if(response.isSuccessful){
           response.body()?.let { resultResponse ->
               return NewsResource.Success(resultResponse)
           }
       }
        return NewsResource.Error(response.message())
    }

    fun getBreakingNews(countryCode : String) = viewModelScope.launch {
        breakingNews.postValue(NewsResource.Loading())
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsReponse(response))
    }

    private fun handleBreakingNewsReponse(response: Response<NewsResponse>) : NewsResource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                return NewsResource.Success(resultResponse)
            }
        }
        return NewsResource.Error(response.message())
    }


    //we can call another suspend fun only from a suspend fun but we don't want to make this
    //fun a suspend fun because then would also need to create a coroutine in our fragment
    //as well but we don't want that and the best way to do that is by using viewModelScope
    //The ViewModelScope will remain alive as long as the viewModel is alive

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun deleteAllArticles() = viewModelScope.launch {
        newsRepository.deleteAllArticles()
    }

}