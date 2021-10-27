package com.example.bulletin.api

import com.example.bulletin.model.NewsResponse
import com.example.bulletin.util.Constants.Companion.ApiKey
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    //Get request - > because we want something from the server we are sending a get request
    //Suspend fun - > because we want to make all our requests in the background thread so
    //                that we don't block the main thread. So we can do that using coroutines
    //                whose syntax is suspend fun. By doing this we make an synchronous function
    @GET("top-headlines")
    suspend fun getTopNews(
        @Query("country")
        country : String = "in",
        @Query("page")
        page : Int = 1,
        @Query("apiKey")
        apiKey : String = ApiKey
    ) : Response<NewsResponse>

    @GET("everything")
    suspend fun searchNews(
        @Query("q")
        search : String,
        @Query("page")
        page: Int = 1,
        @Query("apiKey")
        apiKey: String = ApiKey
    ) : Response<NewsResponse>

}