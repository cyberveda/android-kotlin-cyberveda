package com.ashishkharche.kishornikamphotography.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local data class for modeling: https://kishornikamphotography.com/ blog objects
    see example: https://gist.github.com/mitchtabian/93f287bd1370e7a1ad3c9588b0b22e3d
 * Docs: https://kishornikamphotography.com/api/
 */

@Entity(tableName = "blog_post")
data class BlogPost(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "pk")
    var pk: Int,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "slug")
    var slug: String,

    @ColumnInfo(name = "body")
    var body: String,

    @ColumnInfo(name = "image")
    var image: String,

    @ColumnInfo(name = "date_updated")
    var date_updated: Long,

    @ColumnInfo(name = "username")
    var username: String


) {

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false

        other as BlogPost

        if (pk != other.pk) return false
        if (slug != other.slug) return false
        if (title != other.title) return false
        if (image != other.image) return false

        return true
    }

    override fun toString(): String {
        return "BlogPost(pk=$pk, title='$title', slug='$slug', image='$image', date_updated=$date_updated, username='$username')"
    }


}














