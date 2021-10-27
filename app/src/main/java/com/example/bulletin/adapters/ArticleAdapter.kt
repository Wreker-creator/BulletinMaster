package com.example.bulletin.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.bulletin.R
import com.example.bulletin.model.Article
import java.text.SimpleDateFormat
import java.util.*

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>(){

    inner class ArticleViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    //we use diff util because previously we used to add list as a parameter and whenever
    //we wanted to update the list we used to call adapter.notifyDatasetChanged() which is
    //highly inefficient as it will update the entire list even the items that have not changed
    //but
    //diff  util will compare the two lists the current existing one and the new one and change
    //only the items that were not their in the original list. It will also run on the background
    //thread so it wont block the main thread

    //to do this we create a callback for our asynclistdiffer which will compare the two lists

    private val differCallBack = object : DiffUtil.ItemCallback<Article>(){

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.news_layout, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        holder.itemView.apply {

            findViewById<TextView>(R.id.CurrentArticleSource).text = article.source?.name
            findViewById<TextView>(R.id.CurrentArticleDescription).text = article.description
            findViewById<TextView>(R.id.CurrentArticleTitle).text = article.title

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
            val dateAndTime = dateFormat.parse(article.publishedAt)

            findViewById<TextView>(R.id.CurrentArticleTime).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(dateAndTime)
            findViewById<TextView>(R.id.CurrentArticleDate).text = SimpleDateFormat("dd LL yyyy", Locale.ENGLISH).format(dateAndTime)

            val url = article.urlToImage

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
                }).transition(DrawableTransitionOptions.withCrossFade()).into(findViewById(R.id.CurrentArticleImage))

                //what we are doing here is quite simple actually
                //we setting up an on item click listener in which we are calling the
                //our var onItemClickListener and we are giving it the article we just selected
                //then later this var onItemClickListener get passed to our method
                //which is setOnItemClickListener which takes our articles and then we are free
                //to do whatever we want with it
                //This kind of onItemClickListener is much better that the traditional ones
                //as this is much easier to use
                setOnClickListener {
                    onItemClickListener?.let { it(article) }
                }
            }
        }
    }

    private var onItemClickListener : ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit){
        onItemClickListener = listener
    }

    override fun getItemCount(): Int = differ.currentList.size

}