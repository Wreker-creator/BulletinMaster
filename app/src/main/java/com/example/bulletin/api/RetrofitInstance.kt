package com.example.bulletin.api

import com.example.bulletin.util.Constants.Companion.BaseUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log

//This is a simple retrofit singleton class that enable us to make requests from everywhere
//in our code.

//lazy - > lazy here means that whatever we put inside it will only be initialized once

//HttpLoggingInterceptor - > we are using this to log our responses
//we set the level to body because we want to log the body of our response

//Then we create a client using that interceptor and pass it to retrofit

class RetrofitInstance {

    companion object{

        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder().addInterceptor(logging).build()

            Retrofit.Builder().baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        }

        val apiCall by lazy {
            retrofit.create(NewsApi :: class.java)
        }

    }

}