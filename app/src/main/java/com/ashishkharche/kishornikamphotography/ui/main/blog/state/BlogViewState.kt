package com.ashishkharche.kishornikamphotography.ui.main.blog.state

import android.net.Uri
import com.ashishkharche.kishornikamphotography.models.BlogPost
import com.ashishkharche.kishornikamphotography.repository.main.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.ashishkharche.kishornikamphotography.repository.main.BlogQueryUtils.Companion.ORDER_BY_ASC_DATE_UPDATED

data class BlogViewState (

    // BlogFragment vars
    var blogFields: BlogFields = BlogFields(),

    // ViewBlogFragment vars
    var blogPost: BlogPost? = null,
    var isAuthorOfBlogPost: Boolean = false,

    // UpdateBlogFragment vars
    var updatedBlogFields: UpdatedBlogFields = UpdatedBlogFields()
)
{
    data class BlogFields(
        var searchQuery: String = "",
        var filter: String = ORDER_BY_ASC_DATE_UPDATED,
        var order: String = BLOG_ORDER_ASC,
        var page: Int = 1,
        var blogList: List<BlogPost> = ArrayList<BlogPost>(),
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false
    )

    data class UpdatedBlogFields(
        var updatedBlogTitle: String? = null,
        var updatedBlogBody: String? = null,
        var updatedImageUri: Uri? = null
    )
}
