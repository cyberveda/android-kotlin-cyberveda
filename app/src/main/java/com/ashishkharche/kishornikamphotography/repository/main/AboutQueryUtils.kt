package com.ashishkharche.kishornikamphotography.repository.main

import androidx.lifecycle.LiveData
import com.ashishkharche.kishornikamphotography.models.AboutPost
import com.ashishkharche.kishornikamphotography.persistence.AboutPostDao

class AboutQueryUtils{

    companion object{

        private val TAG: String = "AppDebug"

        // values
        const val ABOUT_ORDER_ASC: String = ""
        const val ABOUT_ORDER_DESC: String = "-"
        const val ABOUT_FILTER_USERNAME = "username"
        const val ABOUT_FILTER_DATE_UPDATED = "date_updated"

        val ORDER_BY_ASC_DATE_UPDATED = ABOUT_ORDER_ASC + ABOUT_FILTER_DATE_UPDATED
        val ORDER_BY_DESC_DATE_UPDATED = ABOUT_ORDER_DESC + ABOUT_FILTER_DATE_UPDATED
        val ORDER_BY_ASC_USERNAME = ABOUT_ORDER_ASC + ABOUT_FILTER_USERNAME
        val ORDER_BY_DESC_USERNAME = ABOUT_ORDER_DESC + ABOUT_FILTER_USERNAME

        /**
         * Options:
         * 1) -date_updated (DESC)
         * 2) date_updated (ASC)
         * 3) -username (alphabetical DESC)
         * 4) username (alphabetical ASC)
         * @see AboutQueryUtils
         *
         * I didn't want to have to build this class but it's necessary because you can't use variables as fields in
         * a Room query. (ex: can't pass 'date_updated' and use it as a field reference in the query)
         * @see AboutPostDao
         */
        fun returnOrderedAboutQuery(aboutPostDao: AboutPostDao, query: String, filterAndOrder: String, page: Int): LiveData<List<AboutPost>> {

            when{

                filterAndOrder.contains(ORDER_BY_DESC_DATE_UPDATED) ->{
                    return aboutPostDao.searchAboutPostsOrderByDateDESC(
                        query = query,
                        page = page)
                }

                filterAndOrder.contains(ORDER_BY_ASC_DATE_UPDATED) ->{
                    return aboutPostDao.searchAboutPostsOrderByDateASC(
                        query = query,
                        page = page)
                }

                filterAndOrder.contains(ORDER_BY_DESC_USERNAME) ->{
                    return aboutPostDao.searchAboutPostsOrderByAuthorDESC(
                        query = query,
                        page = page)
                }

                filterAndOrder.contains(ORDER_BY_ASC_USERNAME) ->{
                    return aboutPostDao.searchAboutPostsOrderByAuthorASC(
                        query = query,
                        page = page)
                }
                else -> throw Exception("Must specify a valid order for all about queries. See AboutQueryUtils class.")
            }
        }
    }
}























