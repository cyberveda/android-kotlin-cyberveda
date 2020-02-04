package com.ashishkharche.kishornikamphotography.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ashishkharche.kishornikamphotography.models.AboutPost
import com.ashishkharche.kishornikamphotography.models.AccountProperties
import com.ashishkharche.kishornikamphotography.models.AuthToken
import com.ashishkharche.kishornikamphotography.models.BlogPost

@Database(entities = arrayOf(AuthToken::class, AccountProperties::class, BlogPost::class, AboutPost::class), version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getAuthTokenDao(): AuthTokenDao

    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    abstract fun getBlogPostDao(): BlogPostDao

    abstract fun getAboutPostDao(): AboutPostDao

    companion object{
        val DATABASE_NAME: String = "app_db"
    }


}