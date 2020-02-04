package com.ashishkharche.kishornikamphotography.ui.main.create_blog

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import com.ashishkharche.kishornikamphotography.repository.main.CreateBlogRepository
import com.ashishkharche.kishornikamphotography.session.SessionManager
import com.ashishkharche.kishornikamphotography.ui.BaseViewModel
import com.ashishkharche.kishornikamphotography.ui.DataState
import com.ashishkharche.kishornikamphotography.ui.Loading
import com.ashishkharche.kishornikamphotography.ui.main.create_blog.state.CreateBlogStateEvent
import com.ashishkharche.kishornikamphotography.ui.main.create_blog.state.CreateBlogStateEvent.CreateNewBlogEvent
import com.ashishkharche.kishornikamphotography.ui.main.create_blog.state.CreateBlogStateEvent.None
import com.ashishkharche.kishornikamphotography.ui.main.create_blog.state.CreateBlogViewState
import com.ashishkharche.kishornikamphotography.ui.main.create_blog.state.CreateBlogViewState.NewBlogFields
import com.ashishkharche.kishornikamphotography.util.AbsentLiveData
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

class CreateBlogViewModel
@Inject
constructor(
    val createBlogRepository: CreateBlogRepository,
    val sessionManager: SessionManager
) : BaseViewModel<CreateBlogStateEvent, CreateBlogViewState>() {


    override fun handleStateEvent(stateEvent: CreateBlogStateEvent): LiveData<DataState<CreateBlogViewState>> {
        when (stateEvent) {
            is CreateNewBlogEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->


                    val title = RequestBody.create(MediaType.parse("text/plain"), stateEvent.title)
                    val body = RequestBody.create(MediaType.parse("text/plain"), stateEvent.body)
                    if (authToken.equals("07ea47bc0688b07668e59e22a7f69d9056d474df")) {

                        Log.d("TOKEN", "HERE")
                    }

//                    if (authToken.token.equals("f5a49603a069b081cf4fedb21958c2a6fe4e6885")){

                    if (authToken.account_pk!! <= 15) {




                        Log.d("LGX", "IF ${authToken}")

                        createBlogRepository.createNewBlogPost(
                            authToken,
                            title,
                            body,
                            stateEvent.image
                        )
                    } else {
                        Log.d("LGX", "ELSE ${authToken}")
                        return object : LiveData<DataState<CreateBlogViewState>>() {
                            override fun onActive() {
                                super.onActive()
                                value = DataState(null, Loading(false), null)
                            }
                        }
                    }

                } ?: AbsentLiveData.create()
            }

            is None -> {
                return object : LiveData<DataState<CreateBlogViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState(null, Loading(false), null)
                    }
                }
            }
        }
    }

    override fun initNewViewState(): CreateBlogViewState {
        return CreateBlogViewState()
    }

    fun setNewBlogFields(title: String?, body: String?, uri: Uri?) {
        val update = getCurrentViewStateOrNew()
        val newBlogFields = update.blogFields
        title?.let { newBlogFields.newBlogTitle = it }
        body?.let { newBlogFields.newBlogBody = it }
        uri?.let { newBlogFields.newImageUri = it }
        update.blogFields = newBlogFields
        _viewState.value = update
    }

    fun clearNewBlogFields() {
        val update = getCurrentViewStateOrNew()
        update.blogFields = NewBlogFields()
        _viewState.value = update
    }

    fun cancelActiveJobs() {
        createBlogRepository.cancelActiveJobs()
        handlePendingData()
    }

    fun handlePendingData() {
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}




















