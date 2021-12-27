package com.example.bulletin.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bulletin.repository.NewsRepository

//We cant use constructor parameters by default for our own viewModel
//If we want to do that then we need to create our own viewModelProviderFactory
//to define how our viewModel should be created

//Since we are passing NewsRepository as a parameter here which is a constructor then we
//also need to pass it as parameter in the viewModelProviderFactory
//and then return the newsViewModel with newsRepository casted as T

class NewsViewModelFactory (
    val app : Application,
    val newsRepository: NewsRepository
        ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(app, newsRepository) as T
    }
}