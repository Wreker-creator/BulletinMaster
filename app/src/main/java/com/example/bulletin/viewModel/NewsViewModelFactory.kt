package com.example.bulletin.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bulletin.repository.NewsRepository

class NewsViewModelFactory (
    val app : Application,
    val newsRepository: NewsRepository
        ) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return NewsViewModel(app, newsRepository) as T

    }
}