package com.example.bulletin.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.engine.Resource
import com.example.bulletin.NewsApplication
import com.example.bulletin.model.Article
import com.example.bulletin.model.NewsResponse
import com.example.bulletin.repository.NewsRepository
import com.example.bulletin.util.NewsResource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app : Application,
    private val newsRepository: NewsRepository
    ) : AndroidViewModel(app){

    val breakingNews : MutableLiveData<NewsResource<NewsResponse>> = MutableLiveData()
    var breakingNewsResponse : NewsResponse?= null

    //The reason why we are setting this brakingNewsPage is because when we rotate our device
    //the activity gets recreated and we lose our current page number but the viewModel
    //remains so to avoid that we are declaring which NewsPage we are currently on here.

    var breakingNewsPage = 1
    var maxPage = 2

    val searchNews : MutableLiveData<NewsResource<NewsResponse>> = MutableLiveData()
    var searchNewsResponse : NewsResponse?=null
    var searchNewsPage = 1

    init {
        getBreakingNews("in", "general")
    }

     fun getBreakingNews(countryCode : String, category : String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode, category)
    }

    private fun handleBreakingNewsResponse(response : Response<NewsResponse>) : NewsResource<NewsResponse> {

        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if(breakingNewsPage > maxPage){
                    breakingNewsPage = 1
                }
                if(breakingNewsResponse == null){
                    breakingNewsResponse = resultResponse
                }else{
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return NewsResource.Success(breakingNewsResponse?:resultResponse)
            }
        }

        return NewsResource.Error(response.message())

    }

    private suspend fun safeBreakingNewsCall(countryCode: String, category: String){
        breakingNews.postValue(NewsResource.Loading())
        try{
            if(hasInternetFunction()){
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage, category)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }else{
                breakingNews.postValue(NewsResource.Error("No Internet Connection"))
            }
        }catch(t : Throwable){
            when(t){
                is IOException -> breakingNews.postValue(NewsResource.Error("Network Failure"))
                else -> breakingNews.postValue(NewsResource.Error("Conversion Error"))
            }
        }
    }

    fun searchNews(query : String) = viewModelScope.launch {
        safeSearchNewsCall(query)
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : NewsResource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if(searchNewsResponse == null){
                    searchNewsResponse = resultResponse
                }else{
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return NewsResource.Success(searchNewsResponse?:resultResponse)
            }
        }
        return NewsResource.Error(response.message())
    }

    private suspend fun safeSearchNewsCall(query: String){
        searchNews.postValue(NewsResource.Loading())
        try{
            if(hasInternetFunction()){
                val response = newsRepository.searchNews(query, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }else{
                searchNews.postValue(NewsResource.Error("No Internet Connection"))
            }
        }catch(t : Throwable){
            when(t){
                is IOException -> searchNews.postValue(NewsResource.Error("Network Failure"))
                else -> searchNews.postValue(NewsResource.Error("Conversion Error"))
            }
        }
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

    // we want to check if the user is connected to the internet or not
    //to do that we need Connectivity Manager for which we need context so we create
    //the class NewsApplication which we add to our Manifest in the name parameter
    // we also inherit from Android View Model here to use context which take our News
    //Application class as the parameter.

    //Then we want to check if the user has an old os or a new one
    //depending on which we decide which function we use to check for internet
    //
    private fun hasInternetFunction() : Boolean{
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}