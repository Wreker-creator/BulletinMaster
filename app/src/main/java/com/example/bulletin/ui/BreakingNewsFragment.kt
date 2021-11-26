package com.example.bulletin.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bulletin.MainActivity
import com.example.bulletin.R
import com.example.bulletin.adapters.ArticleAdapter
import com.example.bulletin.adapters.TopicAdapter
import com.example.bulletin.model.DataObject
import com.example.bulletin.util.Constants
import com.example.bulletin.util.NewsResource
import com.example.bulletin.viewModel.NewsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class BreakingNewsFragment : Fragment() {

    //viewModel Reference
    private lateinit var viewModel : NewsViewModel

    //breaking News Recycler view
    private lateinit var recycler : RecyclerView
    private lateinit var breakingNewsAdapter : ArticleAdapter

    //topic recycler view
    private lateinit var recyclerTopic : RecyclerView
    private lateinit var topicAdapter: TopicAdapter
    private val data1 : MutableList<DataObject> = ArrayList()

    init {
        for(i in 0..6){
            data1.add(DataObject(Constants.Arrays.imageUrl[i], Constants.Arrays.title[i]))
        }
    }

    //animations
    private val rotateOpen : Animation by lazy {
        AnimationUtils.loadAnimation(activity, R.anim.rotate_open_anim)
    }
    private val rotateClose : Animation by lazy {
        AnimationUtils.loadAnimation(activity, R.anim.rotate_close_anim)
    }
    private val fromBottomUp : Animation by lazy {
        AnimationUtils.loadAnimation(activity, R.anim.from_bottom_up)
    }
    private val toBottomDown : Animation by lazy {
        AnimationUtils.loadAnimation(activity, R.anim.to_bottom_down)
    }
    private val fromBottomLeft : Animation by lazy {
        AnimationUtils.loadAnimation(activity, R.anim.from_bottom_left)
    }
    private val toBottomRight : Animation by lazy {
        AnimationUtils.loadAnimation(activity, R.anim.to_bottom_right)
    }

    //floating action buttons
    private lateinit var moreOptions: FloatingActionButton
    private lateinit var toSearch: FloatingActionButton
    private lateinit var toSaved: FloatingActionButton

    private var click: Boolean = false

    //Tag
    private val TAG = "BreakingNewsFragment"

    private var category : String = "general"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_breaking_news, container, false)


        viewModel = (activity as MainActivity).viewModel1


        //Initialize Floating Action Buttons
        moreOptions = view.findViewById(R.id.MoreOption)
        toSearch = view.findViewById(R.id.SearchActionButton)
        toSaved = view.findViewById(R.id.SavedActionButton)


        //topic recycler view
        recyclerTopic = view.findViewById(R.id.BreakingNewsTopicRecyclerView)
        topicAdapter = TopicAdapter(data1)
        recyclerTopic.adapter = topicAdapter
        recyclerTopic.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerTopic.isNestedScrollingEnabled = false


        //breaking News Recycler View
        recycler = view.findViewById(R.id.BreakingNewsArticleRecyclerView)
        breakingNewsAdapter = ArticleAdapter()
        recycler.adapter = breakingNewsAdapter
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.isNestedScrollingEnabled = false


        //Topic adapter click listener to call different topic news
        topicAdapter.setOnTopicClickListener {
            category = it.title.lowercase()
            Snackbar.make(view, "Fetching News For Category ${category.uppercase()}", Snackbar.LENGTH_SHORT).show()
            viewModel.breakingNewsResponse = null
            viewModel.getBreakingNews("in", category)
        }


        //lifecycle observer
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is NewsResource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        breakingNewsAdapter.differ.submitList(newsResponse.articles.toList())
                    }
                }

                is NewsResource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                        view.findViewById<ImageView>(R.id.No_internet).visibility = View.VISIBLE
                        view.findViewById<ScrollView>(R.id.ScrollView).offsetTopAndBottom(1)
                        recycler.visibility = View.INVISIBLE
                    }
                }

                is NewsResource.Loading -> {
                    showProgressBar()
                    recycler.visibility = View.VISIBLE
                    view.findViewById<ImageView>(R.id.No_internet).visibility = View.INVISIBLE
                }
            }
        })



        //we are calling our own custom function setOnItemClickListener here.
        //which takes an article and returns unit. So what we are doing here
        // is we take the article attach it to a bundle and then pass it along
        //with the navigation components which will then handle the transition for
        //us and carry the article with it to the article fragment
        breakingNewsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleWebViewFragment, bundle)
        }


        moreOptions.setOnClickListener {
            onClick()
        }


        toSearch.setOnClickListener {
            findNavController().navigate(R.id.action_breakingNewsFragment_to_searchNewsFragment)
            onClick()
        }


        toSaved.setOnClickListener {
            findNavController().navigate(R.id.action_breakingNewsFragment_to_savedNewsFragment)
            onClick()
        }


        return view
    }


    private fun onClick() {
        setVisibility(click)
        setAnimation(click)
        setClickable(click)
        click = !click
    }


    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            toSaved.startAnimation(fromBottomUp)
            toSearch.startAnimation(fromBottomLeft)
            moreOptions.startAnimation(rotateOpen)
        } else {
            toSaved.startAnimation(toBottomDown)
            toSearch.startAnimation(toBottomRight)
            moreOptions.startAnimation(rotateClose)
        }
    }


    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            toSaved.visibility = View.GONE
            toSearch.visibility = View.GONE
        } else {
            toSaved.visibility = View.INVISIBLE
            toSearch.visibility = View.INVISIBLE
        }
    }


    private fun setClickable(clicked: Boolean) {
        if (!clicked) {
            toSaved.isClickable = true
            toSearch.isClickable = true
        } else {
            toSearch.isClickable = false
            toSaved.isClickable = false
        }
    }


    private fun hideProgressBar(){
        view?.findViewById<RelativeLayout>(R.id.BreakingNewsLoading)?.visibility = View.GONE
    }


    private fun showProgressBar(){
        view?.findViewById<RelativeLayout>(R.id.BreakingNewsLoading)?.visibility = View.VISIBLE
    }

}