package com.example.bulletin.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.example.bulletin.MainActivity
import com.example.bulletin.R
import com.example.bulletin.viewModel.NewsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class ArticleWebViewFragment : Fragment() {

    lateinit var articleViewModel: NewsViewModel
    private val args : ArticleWebViewFragmentArgs by navArgs()
    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_article_web_view, container, false)

        articleViewModel = (activity as MainActivity).viewModel1
        val article = args.article

        view.findViewById<WebView>(R.id.ArticleWebView).apply {
            webViewClient = WebViewClient()
            article?.url?.let { loadUrl(it) }
        }

        view.findViewById<FloatingActionButton>(R.id.SaveThisArticle).setOnClickListener {
            articleViewModel.saveArticle(article)
            Snackbar.make(view, "Article Saved Successfully", Snackbar.LENGTH_LONG).apply {
                setAction("Undo"){
                    articleViewModel.deleteArticle(article)
                }.show()
            }
        }


        return view
    }

}