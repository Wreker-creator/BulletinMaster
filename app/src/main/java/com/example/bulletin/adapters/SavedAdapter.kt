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

class SavedAdapter : RecyclerView.Adapter<SavedAdapter.SavedViewHolder>(){

    inner class SavedViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    //we use diff util because previously we used to add list as a parameter and whenever
    //we wanted to update the list we used to call adapter.notifyDatasetChanged() which is
    //highly inefficient as it will update the entire list even the items that have not changed
    //but
    //diff  util will compare the two lists the current existing one and the new one and change
    //only the items that were not their in the original list. It will also run on the background
    //thread so it wont block the main thread

    //to do this we create a callback for our asynclistdiffer which will compare the two lists

    private  val differCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    //Now we create the async list differ
    val savedDiffer = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        return SavedViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.saved_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {

        val currentSavedArticle = savedDiffer.currentList[position]

        holder.itemView.apply {

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
            val dateAndTime = dateFormat.parse(currentSavedArticle.publishedAt)
            findViewById<TextView>(R.id.SavedArticleTitle).text = currentSavedArticle.title
            findViewById<TextView>(R.id.CurrentSavedArticleDate).text = SimpleDateFormat("dd LLL yyyy", Locale.ENGLISH).format(dateAndTime)

            val url = currentSavedArticle.urlToImage
            if(url != null){

                Glide.with(context).load(url).listener(object : RequestListener<Drawable?> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        findViewById<ProgressBar>(R.id.SavedProgressBar).visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        findViewById<ProgressBar>(R.id.SavedProgressBar).visibility = View.GONE
                        return false
                    }

                }).transition(DrawableTransitionOptions.withCrossFade()).into(findViewById(R.id.CurrentSavedArticleImage))
            }

            setOnClickListener {
                onSavedItemClickListener?.let {
                    it(currentSavedArticle)
                }
            }
        }
    }

    private var onSavedItemClickListener : ((Article) -> Unit)? = null

    fun setOnSavedItemClickListener(listener: (Article) -> Unit){
        onSavedItemClickListener = listener
    }

    override fun getItemCount(): Int = savedDiffer.currentList.size

}