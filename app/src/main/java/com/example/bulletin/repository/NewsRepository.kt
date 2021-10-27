package com.example.bulletin.repository

import com.example.bulletin.api.RetrofitInstance
import com.example.bulletin.database.ArticleDatabase
import com.example.bulletin.model.Article
import retrofit2.Retrofit
import retrofit2.http.Query

//We passed our database as a parameter here as we will need it
//to access all the functions of our database and it will also need
//access to the api but we can gte that from our database so we don't need
class NewsRepository(val db : ArticleDatabase) {

    //Because the function getTopNews is a suspend fun so we make this function
    // a suspend function as well

    suspend fun getBreakingNews(country : String, pageNumber : Int) =
        RetrofitInstance.apiCall.getTopNews(country, pageNumber)

    suspend fun searchNews(query: String, pageNumber: Int) =
        RetrofitInstance.apiCall.searchNews(query, pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

    suspend fun deleteAllArticles() = db.getArticleDao().deleteAllArticles()

    fun getSavedNews() = db.getArticleDao().getAllArticles()

}