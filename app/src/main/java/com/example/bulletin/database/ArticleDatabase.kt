package com.example.bulletin.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bulletin.model.Article

@Database(
    entities = [Article::class], version = 2
)

@TypeConverters(TypeConverter::class)
abstract class ArticleDatabase : RoomDatabase(){

    abstract fun getArticleDao() : ArticleDao

    companion object{

        @Volatile
        private var INSTANCE : ArticleDatabase? = null

        fun getDatabase(context: Context) : ArticleDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }

            //we create a synchronised block here so that whatever happens here cannot be
            //accessed bu other threads

            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArticleDatabase::class.java,
                    "user-database"
                ).fallbackToDestructiveMigration().build()

                INSTANCE = instance
                return instance
            }

        }

    }

}