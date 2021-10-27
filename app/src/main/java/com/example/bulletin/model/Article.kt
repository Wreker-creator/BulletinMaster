package com.example.bulletin.model
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

//Entity  - > to tell android that this class is a table in our database


@Entity(
    tableName = "article", indices = [Index( "url", unique = true)]
)
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id : Int? = null,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
) : Serializable