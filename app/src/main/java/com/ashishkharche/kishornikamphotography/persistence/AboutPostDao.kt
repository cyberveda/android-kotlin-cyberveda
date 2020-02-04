package com.ashishkharche.kishornikamphotography.persistence

import androidx.room.*
import com.ashishkharche.kishornikamphotography.models.AboutPost
import androidx.lifecycle.LiveData
import com.ashishkharche.kishornikamphotography.util.Constants.Companion.PAGINATION_PAGE_SIZE

@Dao
interface AboutPostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(aboutPost: AboutPost): Long

    @Query("UPDATE about_post SET title = :title, body = :body, image = :image WHERE pk = :pk")
    fun updateAboutPost(pk: Int, title: String, body: String, image: String)

    @Delete
    suspend fun deleteAboutPost(aboutPost: AboutPost)

    @Query("SELECT * FROM about_post WHERE pk = :pk")
    fun getAboutPost(pk: Int): LiveData<AboutPost>

    @Query("SELECT * FROM about_post WHERE title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%' ORDER BY date_updated DESC LIMIT (:page * :pageSize)")
    fun searchAboutPostsOrderByDateDESC(query: String, page: Int, pageSize: Int = PAGINATION_PAGE_SIZE): LiveData<List<AboutPost>>

    @Query("SELECT * FROM about_post WHERE title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%' ORDER BY date_updated  ASC LIMIT (:page * :pageSize)")
    fun searchAboutPostsOrderByDateASC(query: String, page: Int, pageSize: Int = PAGINATION_PAGE_SIZE): LiveData<List<AboutPost>>

    @Query("SELECT * FROM about_post WHERE title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%' ORDER BY username DESC LIMIT (:page * :pageSize)")
    fun searchAboutPostsOrderByAuthorDESC(query: String, page: Int, pageSize: Int = PAGINATION_PAGE_SIZE): LiveData<List<AboutPost>>

    @Query("SELECT * FROM about_post WHERE title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%' ORDER BY username  ASC LIMIT (:page * :pageSize)")
    fun searchAboutPostsOrderByAuthorASC(query: String, page: Int, pageSize: Int = PAGINATION_PAGE_SIZE): LiveData<List<AboutPost>>
}
















