package com.ashishkharche.kishornikamphotography.api.main.network_responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Class for modeling the response when querying https://kishornikamphotography.com/
 * See example response here: https://gist.github.com/mitchtabian/ae03573737067c9269701ea662460205
 */
class BlogListSearchResponse(

    @SerializedName("results")
    @Expose
    var results: List<BlogSearchResponse>,

    @SerializedName("detail")
    @Expose
    var detail: String
) {

    override fun toString(): String {
        return "BlogListSearchResponse(results=$results, detail='$detail')"
    }
}











