package com.example.bulletin.database

import androidx.lifecycle.LiveData
import com.example.bulletin.model.Article
import androidx.room.*

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article : Article) : Long
    //onConflictStrategy - >  basically what we want to do when the article we are inserting already
    // exists in the database we want to just replace it with the new one. This function returns
    //Long which is our ID we put as primary key in the database

    @Query("SELECT * FROM article")
    fun getAllArticles() : LiveData<List<Article>>
    //The reason why this function wasn't suspended is simple.
    //This function returns a LIVE_DATA object which does not work with suspend.
    //Live Data - > As the name suggests it is part of android architectural components that enables
    //us to see the changes in the data live. Live data tell all the classes about the changes made
    //in the data so that the classes can change their recycler view accordingly

    //They are especially useful for device rotation as when we rotate our devices the activities are
    //recreated. But the view model does not get recreated and the recycler view can get the teh
    //latest data from the live data

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("DELETE FROM article")
    suspend fun deleteAllArticles()

}