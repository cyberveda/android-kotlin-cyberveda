package com.ashishkharche.kishornikamphotography.repository.main

import androidx.lifecycle.LiveData
import com.ashishkharche.kishornikamphotography.util.ApiSuccessResponse
import com.ashishkharche.kishornikamphotography.util.GenericApiResponse
import com.ashishkharche.kishornikamphotography.api.main.OpenApiMainService
import com.ashishkharche.kishornikamphotography.api.main.network_responses.BlogCreateUpdateResponse
import com.ashishkharche.kishornikamphotography.models.AuthToken
import com.ashishkharche.kishornikamphotography.models.BlogPost
import com.ashishkharche.kishornikamphotography.persistence.BlogPostDao
import com.ashishkharche.kishornikamphotography.repository.JobManager
import com.ashishkharche.kishornikamphotography.repository.NetworkBoundResource
import com.ashishkharche.kishornikamphotography.session.SessionManager
import com.ashishkharche.kishornikamphotography.ui.DataState
import com.ashishkharche.kishornikamphotography.ui.Response
import com.ashishkharche.kishornikamphotography.ui.ResponseType
import com.ashishkharche.kishornikamphotography.ui.main.create_blog.state.CreateBlogViewState
import com.ashishkharche.kishornikamphotography.util.AbsentLiveData
import com.ashishkharche.kishornikamphotography.util.DateUtils
import com.ashishkharche.kishornikamphotography.util.SuccessHandling.Companion.RESPONSE_MUST_BECOME_CODINGWITHMITCH_MEMBER
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class CreateBlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
): JobManager() {
    private val TAG: String = "AppDebug"

    fun createNewBlogPost(
        authToken: AuthToken,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?
    ): LiveData<DataState<CreateBlogViewState>> {
        return object :
            NetworkBoundResource<BlogCreateUpdateResponse, BlogPost, CreateBlogViewState>(
                "createNewBlogPost",
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {

            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogCreateUpdateResponse>) {

                // If they don't have a paid membership account it will still return a 200
                // Need to account for that
                if (!response.body.response.equals(RESPONSE_MUST_BECOME_CODINGWITHMITCH_MEMBER)) {
                    val updatedBlogPost = BlogPost(
                        response.body.pk,
                        response.body.title,
                        response.body.slug,
                        response.body.body,
                        response.body.image,
                        DateUtils.convertServerStringDateToLong(response.body.date_updated),
                        response.body.username
                    )
                    updateLocalDb(updatedBlogPost)
                }

                withContext(Dispatchers.Main) {
                    // finish with success response
                    onCompleteJob(
                        DataState.data(
                            null,
                            Response(response.body.response, ResponseType.Dialog())
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogCreateUpdateResponse>> {
                return openApiMainService.createBlog(
                    "Token ${authToken.token!!}",
                    title,
                    body,
                    image
                )
            }

            // not applicable
            override fun loadFromCache(): LiveData<CreateBlogViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: BlogPost?) {
                cacheObject?.let {
                    blogPostDao.insert(it)
                }
            }

            override fun setJob(job: Job) {
                addJob(methodName, job)
            }

        }.asLiveData()
    }

}

























