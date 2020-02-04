//package com.ashishkharche.kishornikamphotography.ui.main.create_about
//
//import android.net.Uri
//import android.util.Log
//import androidx.lifecycle.LiveData
//import com.ashishkharche.kishornikamphotography.repository.main.CreateAboutRepository
//import com.ashishkharche.kishornikamphotography.session.SessionManager
//import com.ashishkharche.kishornikamphotography.ui.BaseViewModel
//import com.ashishkharche.kishornikamphotography.ui.DataState
//import com.ashishkharche.kishornikamphotography.ui.Loading
//import com.ashishkharche.kishornikamphotography.ui.main.create_about.state.CreateAboutStateEvent.CreateNewAboutEvent
//import com.ashishkharche.kishornikamphotography.ui.main.create_about.state.CreateAboutStateEvent.None
//import com.ashishkharche.kishornikamphotography.ui.main.create_about.state.CreateAboutViewState.NewAboutFields
//import com.ashishkharche.kishornikamphotography.util.AbsentLiveData
//import okhttp3.MediaType
//import okhttp3.RequestBody
//import javax.inject.Inject
//
//class CreateAboutViewModel
//@Inject
//constructor(
//    val createAboutRepository: CreateAboutRepository,
//    val sessionManager: SessionManager
//) : BaseViewModel<CreateAboutStateEvent, CreateAboutViewState>() {
//
//
//    override fun handleStateEvent(stateEvent: CreateAboutStateEvent): LiveData<DataState<CreateAboutViewState>> {
//        when (stateEvent) {
//            is CreateNewAboutEvent -> {
//                return sessionManager.cachedToken.value?.let { authToken ->
//
//
//                    val title = RequestBody.create(MediaType.parse("text/plain"), stateEvent.title)
//                    val body = RequestBody.create(MediaType.parse("text/plain"), stateEvent.body)
//                    if (authToken.equals("07ea47bc0688b07668e59e22a7f69d9056d474df")) {
//
//                        Log.d("TOKEN", "HERE")
//                    }
//
////                    if (authToken.token.equals("f5a49603a069b081cf4fedb21958c2a6fe4e6885")){
//
//                    if (authToken.account_pk!! <= 10) {
//
//
////                        }
//
//                        Log.d("LGX", "IF ${authToken}")
//
//                        createAboutRepository.createNewAboutPost(
//                            authToken,
//                            title,
//                            body,
//                            stateEvent.image
//                        )
//                    } else {
//                        Log.d("LGX", "ELSE ${authToken}")
//                        return object : LiveData<DataState<CreateAboutViewState>>() {
//                            override fun onActive() {
//                                super.onActive()
//                                value = DataState(null, Loading(false), null)
//                            }
//                        }
//                    }
//
//                } ?: AbsentLiveData.create()
//            }
//
//            is None -> {
//                return object : LiveData<DataState<CreateAboutViewState>>() {
//                    override fun onActive() {
//                        super.onActive()
//                        value = DataState(null, Loading(false), null)
//                    }
//                }
//            }
//        }
//    }
//
//    override fun initNewViewState(): CreateAboutViewState {
//        return CreateAboutViewState()
//    }
//
//    fun setNewAboutFields(title: String?, body: String?, uri: Uri?) {
//        val update = getCurrentViewStateOrNew()
//        val newAboutFields = update.aboutFields
//        title?.let { newAboutFields.newAboutTitle = it }
//        body?.let { newAboutFields.newAboutBody = it }
//        uri?.let { newAboutFields.newImageUri = it }
//        update.aboutFields = newAboutFields
//        _viewState.value = update
//    }
//
//    fun clearNewAboutFields() {
//        val update = getCurrentViewStateOrNew()
//        update.aboutFields = NewAboutFields()
//        _viewState.value = update
//    }
//
//    fun cancelActiveJobs() {
//        createAboutRepository.cancelActiveJobs()
//        handlePendingData()
//    }
//
//    fun handlePendingData() {
//        setStateEvent(None())
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        cancelActiveJobs()
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
