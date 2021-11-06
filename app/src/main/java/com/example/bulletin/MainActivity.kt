package com.example.bulletin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.bulletin.database.ArticleDatabase
import com.example.bulletin.repository.NewsRepository
import com.example.bulletin.viewModel.NewsViewModel
import com.example.bulletin.viewModel.NewsViewModelFactory

class MainActivity : AppCompatActivity() {

    lateinit var viewModel1 : NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newsRepository = NewsRepository(ArticleDatabase.getDatabase(this))
        val viewModelProviderFactory = NewsViewModelFactory(application,newsRepository)
        viewModel1 = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

    }
}