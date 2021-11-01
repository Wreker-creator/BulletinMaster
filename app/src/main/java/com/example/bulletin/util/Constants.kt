package com.example.bulletin.util

import com.example.bulletin.R

class Constants {

    //Companion objects help to connect variables, functions, and the
    //class definition without referring to any particular instance of that class
    //basically we don't need to create any kind of instance for this class

    companion object {
        const val ApiKey = "b814c9b8c2fd459cafe38800194f0c9e"
        const val BaseUrl = "https://newsapi.org/v2/"
        const val SEARCH_NEWS_TIME_DELAY = 800L
        const val QUERY_PAGE_SIZE = 20
    }

    object Arrays{
        val imageUrl = arrayOf(R.drawable.business_trial, R.drawable.entertainment_industry, R.drawable.news_industry, R.drawable.health, R.drawable.science, R.drawable.sports, R.drawable.technology)
        val title = arrayOf("BUSINESS", "ENTERTAINMENT", "GENERAL", "HEALTH", "SCIENCE", "SPORTS", "TECHNOLOGY")
    }

}