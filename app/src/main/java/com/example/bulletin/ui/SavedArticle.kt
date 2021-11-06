package com.example.bulletin.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.bulletin.R
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class SavedArticle : Fragment() {

    private val args : SavedArticleArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_saved_article, container, false)

        val article = args.articleSaved

        //Read Article In WebView
        view.findViewById<Button>(R.id.ReadArticleOnline).setOnClickListener {
            val bundle = Bundle().apply{
                putSerializable("article", article)
            }
            findNavController().navigate(R.id.action_savedArticle_to_articleWebViewFragment, bundle)
        }

        //title
        view.findViewById<TextView>(R.id.OfflineArticleTitle).text = article.title


        //description
        view.findViewById<TextView>(R.id.OfflineArticleDesc).text = article.description


        //published by
        view.findViewById<TextView>(R.id.OfflineArticleSource).text = article.source?.name


        //article image
        /*val url = article.urlToImage

            if(url != null){
                Glide.with(context).load(url).listener(object : RequestListener<Drawable?>{

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        findViewById<ProgressBar>(R.id.ProgressBar).visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        findViewById<ProgressBar>(R.id.ProgressBar).visibility = View.GONE
                        return false
                    }
                }).transition(DrawableTransitionOptions.withCrossFade()).into(findViewById(R.id.CurrentArticleImage))*/

        val url = article.urlToImage

        if(url != null){

            activity?.let {
                Glide.with(it).load(url).listener(object : RequestListener<Drawable?>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                }).transition(DrawableTransitionOptions.withCrossFade()).into(view.findViewById(R.id.OfflineArticleImage))
            }

        }

        //date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        val dateAndTime = dateFormat.parse(article.publishedAt)
        view.findViewById<TextView>(R.id.OfflineArticleDateOfPublishing).text = SimpleDateFormat("dd LLL yyyy", Locale.ENGLISH).format(dateAndTime)

        return view
    }

}