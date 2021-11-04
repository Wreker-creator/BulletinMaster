package com.example.bulletin.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bulletin.MainActivity
import com.example.bulletin.R
import com.example.bulletin.adapters.ArticleAdapter
import com.example.bulletin.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.bulletin.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.bulletin.util.NewsResource
import com.example.bulletin.viewModel.NewsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment() {

    private lateinit var searchViewModel : NewsViewModel

    //recycler view
    private lateinit var searchRecycler : RecyclerView
    private lateinit var searchAdapter : ArticleAdapter

    private var  TAG = "SearchNewsFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_search_news, container, false)

        searchViewModel = (activity as MainActivity).viewModel1

        //GO  BACK TO MAIN FRAGMENT
        view.findViewById<ImageButton>(R.id.SearchToMain).setOnClickListener {
            activity?.onBackPressed()
        }

        //recycler view
        searchAdapter = ArticleAdapter()
        searchRecycler = view.findViewById(R.id.SearchFragmentRecyclerView)
        searchRecycler.layoutManager = LinearLayoutManager(activity)
        searchRecycler.adapter = searchAdapter

        //edit text settings for when we search a query and the text changes
        val clear = view.findViewById<ImageButton>(R.id.ClearEditTextSearch)
        var job : Job? = null
        val search = view.findViewById<EditText>(R.id.SearchEditText)
        search.addTextChangedListener{query ->
            if(query?.isNotEmpty()==true){
                clear.visibility = View.VISIBLE
            }else{
                clear.visibility = View.GONE
            }
            job?.cancel()
            searchViewModel.searchNewsResponse = null
            searchViewModel.searchNewsPage = 1
            job = MainScope().launch {
                clear.setOnClickListener {
                    search.setText("")
                }
                delay(SEARCH_NEWS_TIME_DELAY)
                query?.let {
                    if(query.toString().isNotEmpty()){
                        searchViewModel.searchNews(query.toString())
                    }
                }
            }

        }

        //we are calling our own custom function setOnItemClickListener here.
        //which takes an article and returns unit. So what we are doing here
        // is we take the article attach it to a bundle and then pass it along
        //with the navigation components which will then handle the transition for
        //us and carry the article with it to the article fragment
        searchAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_searchNewsFragment_to_articleWebViewFragment, bundle)
        }

        //lifecycle observer
        searchViewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is NewsResource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        searchAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLAstPage = searchViewModel.searchNewsPage == totalPages
                    }
                }

                is NewsResource.Error -> {
                    hideProgressBar()
                    response.data?.let { message ->

                        Log.e(TAG, "Error -> $message")
                    }
                }

                is NewsResource.Loading -> {
                    showProgressBar()
                }
            }
        })

        //add on Scroll Listener
        searchRecycler.addOnScrollListener(this@SearchNewsFragment.scrollListener)

        return view
    }

    private fun hideProgressBar() {
        view?.findViewById<RelativeLayout>(R.id.SearchProgress)?.visibility = View.GONE
        isLoading = false
    }

    private fun showProgressBar() {
        view?.findViewById<RelativeLayout>(R.id.SearchProgress)?.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLAstPage = false
    var isScrolling = false

     val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage : Boolean = !isLoading && !isLAstPage
            val isAtLastItem : Boolean = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning : Boolean = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible : Boolean = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate){
                searchViewModel.searchNews(view?.findViewById<EditText>(R.id.SearchEditText)?.text.toString())
            }

        }
    }

}