package com.ashishkharche.kishornikamphotography.ui.main.about.state

import android.net.Uri
import com.ashishkharche.kishornikamphotography.models.AboutPost
import com.ashishkharche.kishornikamphotography.repository.main.AboutQueryUtils.Companion.ABOUT_ORDER_ASC
import com.ashishkharche.kishornikamphotography.repository.main.AboutQueryUtils.Companion.ORDER_BY_ASC_DATE_UPDATED

data class AboutViewState (

    // AboutFragment vars
    var aboutFields: AboutFields = AboutFields(),

    // ViewAboutFragment vars
    var aboutPost: AboutPost? = null,
    var isAuthorOfAboutPost: Boolean = false,

    // UpdateAboutFragment vars
    var updatedAboutFields: UpdatedAboutFields = UpdatedAboutFields()
)
{
    data class AboutFields(
        var searchQuery: String = "",
        var filter: String = ORDER_BY_ASC_DATE_UPDATED,
        var order: String = ABOUT_ORDER_ASC,
        var page: Int = 1,
        var aboutList: List<AboutPost> = ArrayList<AboutPost>(),
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false
    )

    data class UpdatedAboutFields(
        var updatedAboutTitle: String? = null,
        var updatedAboutBody: String? = null,
        var updatedImageUri: Uri? = null
    )
}
