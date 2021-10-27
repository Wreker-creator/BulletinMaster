package com.example.bulletin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bulletin.R
import com.example.bulletin.model.DataObject
import com.google.android.material.snackbar.Snackbar

class TopicAdapter (
    private val data : List<DataObject>
    ) : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>(){

    inner class TopicViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val title : TextView = itemView.findViewById(R.id.TopicTitle)
        val image : ImageView = itemView.findViewById(R.id.TopicImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        return TopicViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.select_topic, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val currentData = data[position]
        holder.title.text = currentData.title
        holder.image.setImageDrawable(holder.itemView.context.getDrawable(currentData.imageUrl))

        holder.itemView.apply {
            setOnClickListener {
                onTopicCLickListener?.let { it(currentData) }
            }
        }
    }

    private var onTopicCLickListener : ((DataObject) -> Unit)?=null

    fun setOnTopicClickListener(listener : (DataObject) -> Unit){
        onTopicCLickListener = listener
    }

    override fun getItemCount(): Int = data.size

}