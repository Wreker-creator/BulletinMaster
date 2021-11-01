package com.example.bulletin.model
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

//Entity  - > to tell android that this class is a table in our database
//The reason why we made this properties nullable is because sometimes
//some articles don't have some data and if we want to set null to a non
//nullable property then our app will crash

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