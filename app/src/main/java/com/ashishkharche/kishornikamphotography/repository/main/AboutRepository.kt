package com.ashishkharche.kishornikamphotography.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.ashishkharche.kishornikamphotography.api.*
import com.ashishkharche.kishornikamphotography.api.main.OpenApiMainService
import com.ashishkharche.kishornikamphotography.api.main.network_responses.AboutCreateUpdateResponse
import com.ashishkharche.kishornikamphotography.api.main.network_responses.AboutListSearchResponse
import com.ashishkharche.kishornikamphotography.models.AuthToken
import com.ashishkharche.kishornikamphotography.models.AboutPost
import com.ashishkharche.kishornikamphotography.persistence.AboutPostDao
import com.ashishkharche.kishornikamphotography.repository.JobManager
import com.ashishkharche.kishornikamphotography.repository.NetworkBoundResource
import com.ashishkharche.kishornikamphotography.session.SessionManager
import com.ashishkharche.kishornikamphotography.ui.DataState
import com.ashishkharche.kishornikamphotography.ui.Response
import com.ashishkharche.kishornikamphotography.ui.ResponseType
import com.ashishkharche.kishornikamphotography.ui.main.about.state.AboutViewState
import com.ashishkharche.kishornikamphotography.ui.main.about.state.AboutViewState.*
import com.ashishkharche.kishornikamphotography.util.AbsentLiveData
import com.ashishkharche.kishornikamphotography.util.ApiSuccessResponse
import com.ashishkharche.kishornikamphotography.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.ashishkharche.kishornikamphotography.util.DateUtils
import com.ashishkharche.kishornikamphotography.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.ashishkharche.kishornikamphotography.util.GenericApiResponse
import com.ashishkharche.kishornikamphotography.util.SuccessHandling.Companion.RESPONSE_HAS_PERMISSION_TO_EDIT
import com.ashishkharche.kishornikamphotography.util.SuccessHandling.Companion.RESPONSE_NO_PERMISSION_TO_EDIT
import com.ashishkharche.kishornikamphotography.util.SuccessHandling.Companion.SUCCESS_ABOUT_DELETED
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AboutRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val aboutPostDao: AboutPostDao,
    val sessionManager: SessionManager
): JobManager()
{
    private val TAG: String = "AppDebug"

    fun searchAboutPosts(authToken: AuthToken, query: String, filterAndOrder: String, page: Int): LiveData<DataState<AboutViewState>> {

        return object: NetworkBoundResource<AboutListSearchResponse, List<AboutPost>, AboutViewState>(
            "searchAboutPosts",
            sessionManager.isConnectedToTheInternet(),
            true,
            false,
            true
        ){


            // if network is down, view cache only and return
            override suspend fun createCacheRequestAndReturn() {
                withContext(Dispatchers.Main){

                    // finishing by viewing db cache
                    result.addSource(loadFromCache()){ viewState ->
                        viewState.aboutFields.isQueryInProgress = false
                        if(page * PAGINATION_PAGE_SIZE > viewState.aboutFields.aboutList.size){
                            viewState.aboutFields.isQueryExhausted = true
                        }
                        onCompleteJob(DataState.data(viewState, null))
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<AboutListSearchResponse>) {
                val aboutPostList: ArrayList<AboutPost> = ArrayList()
                for(aboutPostResponse in response.body.results){
                    aboutPostList.add(
                        AboutPost(
                            pk = aboutPostResponse.pk,
                            title = aboutPostResponse.title,
                            slug = aboutPostResponse.slug,
                            body = aboutPostResponse.body,
                            image = aboutPostResponse.image,
                            date_updated = DateUtils.convertServerStringDateToLong(aboutPostResponse.date_updated),
                            username = aboutPostResponse.username
                        )
                    )
                }
                updateLocalDb(aboutPostList)

                withContext(Dispatchers.Main){

                    // finishing by viewing db cache
                    result.addSource(loadFromCache()){ viewState ->
                        viewState.aboutFields.isQueryInProgress = false
                        if(page * PAGINATION_PAGE_SIZE > viewState.aboutFields.aboutList.size){
                            viewState.aboutFields.isQueryExhausted = true
                        }
                        onCompleteJob(DataState.data(viewState, null))
                    }
                }
            }

            override fun loadFromCache(): LiveData<AboutViewState> {
                return AboutQueryUtils.returnOrderedAboutQuery(
                    aboutPostDao = aboutPostDao,
                    query = query,
                    filterAndOrder = filterAndOrder,
                    page = page)
                    .switchMap {
                        object: LiveData<AboutViewState>(){
                            override fun onActive() {
                                super.onActive()
                                value = AboutViewState(
                                    AboutFields(
                                        aboutList = it,
                                        isQueryInProgress = true
                                    )
                                )
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: List<AboutPost>?) {
                // loop through list and update the local db
                if(cacheObject != null){
                    withContext(IO) {
                        for(aboutPost in cacheObject){
                            try{
                                // Launch each insert as a separate job to be executed in parallel
                                val j = launch {
                                    Log.d(TAG, "updateLocalDb: inserting about: ${aboutPost}")
                                    aboutPostDao.insert(aboutPost)
                                }
                                j.join() // wait for completion before proceeding to next
                            }catch (e: Exception){
                                Log.e(TAG, "updateLocalDb: error updating cache data on about post with slug: ${aboutPost.slug}. " +
                                        "${e.message}")
                                // Could send an error report here or something but I don't think you should throw an error to the UI
                                // Since there could be many about posts being inserted/updated.
                            }
                        }
                    }
                }
                else{
                    Log.d(TAG, "updateLocalDb: about post list is null")
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<AboutListSearchResponse>> {
                return openApiMainService.searchListAboutPosts(
                    "Token ${authToken.token!!}",
                    query = query,
                    ordering = filterAndOrder,
                    page = page
                )
            }

            override fun setJob(job: Job) {
                addJob(methodName, job)
            }


        }.asLiveData()
    }


    fun isAuthorOfAboutPost(
        authToken: AuthToken,
        slug: String
    ): LiveData<DataState<AboutViewState>> {
        return object: NetworkBoundResource<GenericResponse, Any, AboutViewState>(
            "isAuthorOfAboutPost",
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){


            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                withContext(Dispatchers.Main){

                    Log.d(TAG, "handleApiSuccessResponse: ${response.body.response}")
                    if(response.body.response.equals(RESPONSE_NO_PERMISSION_TO_EDIT)){
                        onCompleteJob(
                            DataState.data(
                                data = AboutViewState(
                                    isAuthorOfAboutPost = false
                                ),
                                response = null
                            )
                        )
                    }
                    else if(response.body.response.equals(RESPONSE_HAS_PERMISSION_TO_EDIT)){
                        onCompleteJob(
                            DataState.data(
                                data = AboutViewState(
                                    isAuthorOfAboutPost = true
                                ),
                                response = null
                            )
                        )
                    }
                    else{
                        onErrorReturn(ERROR_UNKNOWN, shouldUseDialog = false, shouldUseToast = false)
                    }
                }
            }

            // not applicable
            override fun loadFromCache(): LiveData<AboutViewState> {
                return AbsentLiveData.create()
            }

            // Make an update and change nothing.
            // If they are not the author it will return: "You don't have permission to edit that."
            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.isAuthorOfAboutPost(
                    "Token ${authToken.token!!}",
                    slug
                )
            }

            // not applicable
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            override fun setJob(job: Job) {
                addJob(methodName, job)
            }


        }.asLiveData()
    }


    fun deleteAboutPost(
        authToken: AuthToken,
        aboutPost: AboutPost
    ): LiveData<DataState<AboutViewState>>{
        return object: NetworkBoundResource<GenericResponse, AboutPost, AboutViewState>(
            "deleteAboutPost",
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){

            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {

                if(response.body.response == SUCCESS_ABOUT_DELETED){
                    updateLocalDb(aboutPost)
                }
                else{
                    onCompleteJob(
                        DataState.error(
                            Response(
                                ERROR_UNKNOWN,
                                ResponseType.Dialog()
                            )
                        )
                    )
                }
            }

            // not applicable
            override fun loadFromCache(): LiveData<AboutViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.deleteAboutPost(
                    "Token ${authToken.token!!}",
                    aboutPost.slug
                )
            }

            override suspend fun updateLocalDb(cacheObject: AboutPost?) {
                cacheObject?.let{aboutPost ->
                    aboutPostDao.deleteAboutPost(aboutPost)
                    onCompleteJob(
                        DataState.data(
                            null,
                            Response(SUCCESS_ABOUT_DELETED, ResponseType.Toast())
                        )
                    )
                }
            }

            override fun setJob(job: Job) {
                addJob(methodName, job)
            }

        }.asLiveData()
    }

    fun updateAboutPost(
        authToken: AuthToken,
        slug: String,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?
    ): LiveData<DataState<AboutViewState>> {
        return object: NetworkBoundResource<AboutCreateUpdateResponse, AboutPost, AboutViewState>(
            "updateAboutPost",
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){

            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<AboutCreateUpdateResponse>) {

                val updatedAboutPost = AboutPost(
                    response.body.pk,
                    response.body.title,
                    response.body.slug,
                    response.body.body,
                    response.body.image,
                    DateUtils.convertServerStringDateToLong(response.body.date_updated),
                    response.body.username
                )
                updateLocalDb(updatedAboutPost)
                withContext(Dispatchers.Main){
                    // finish with success response
                    onCompleteJob(
                        DataState.data(
                            AboutViewState(aboutPost = updatedAboutPost),
                            Response(response.body.response, ResponseType.Toast())
                        ))
                }
            }

            // not applicable
            override fun loadFromCache(): LiveData<AboutViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<AboutCreateUpdateResponse>> {
                return openApiMainService.updateAbout(
                    "Token ${authToken.token!!}",
                    slug,
                    title,
                    body,
                    image
                )
            }

            override suspend fun updateLocalDb(cacheObject: AboutPost?) {
                cacheObject?.let{aboutPost ->
                    aboutPostDao.updateAboutPost(
                        aboutPost.pk,
                        aboutPost.title,
                        aboutPost.body,
                        aboutPost.image
                    )
                }
            }

            override fun setJob(job: Job) {
                addJob(methodName, job)
            }

        }.asLiveData()
    }


}


























