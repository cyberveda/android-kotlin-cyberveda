package com.ashishkharche.kishornikamphotography.ui.main.about

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.bumptech.glide.RequestManager
import com.ashishkharche.kishornikamphotography.models.AboutPost
import com.ashishkharche.kishornikamphotography.repository.main.AboutQueryUtils
import com.ashishkharche.kishornikamphotography.repository.main.AboutRepository
import com.ashishkharche.kishornikamphotography.session.SessionManager
import com.ashishkharche.kishornikamphotography.ui.BaseViewModel
import com.ashishkharche.kishornikamphotography.ui.DataState
import com.ashishkharche.kishornikamphotography.ui.Loading
import com.ashishkharche.kishornikamphotography.ui.main.about.state.AboutStateEvent
import com.ashishkharche.kishornikamphotography.ui.main.about.state.AboutStateEvent.*
import com.ashishkharche.kishornikamphotography.ui.main.about.state.AboutViewState
import com.ashishkharche.kishornikamphotography.util.AbsentLiveData
import com.ashishkharche.kishornikamphotography.util.PreferenceKeys.Companion.ABOUT_FILTER
import com.ashishkharche.kishornikamphotography.util.PreferenceKeys.Companion.ABOUT_ORDER
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

class AboutViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val aboutRepository: AboutRepository,
    private val sharedPreferences: SharedPreferences,
    private val requestManager: RequestManager
)
    : BaseViewModel<AboutStateEvent, AboutViewState>()
{

    init {
        // set empty list to start
        setAboutListData(ArrayList<AboutPost>())
    }
    override fun handleStateEvent(stateEvent: AboutStateEvent): LiveData<DataState<AboutViewState>> {
        when(stateEvent){
            is AboutStateEvent.AboutSearchEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    aboutRepository.searchAboutPosts(
                        authToken,
                        viewState.value!!.aboutFields.searchQuery,
                        viewState.value!!.aboutFields.order + viewState.value!!.aboutFields.filter,
                        viewState.value!!.aboutFields.page
                    )
                }?: AbsentLiveData.create()
            }

            is AboutStateEvent.NextPageEvent -> {
             Log.d(TAG, "AboutViewModel: NextPageEvent detected...")
             return sessionManager.cachedToken.value?.let { authToken ->
                 aboutRepository.searchAboutPosts(
                     authToken,
                     viewState.value!!.aboutFields.searchQuery,
                     viewState.value!!.aboutFields.order + viewState.value!!.aboutFields.filter,
                     viewState.value!!.aboutFields.page
                 )
             }?: AbsentLiveData.create()
            }

            is AboutStateEvent.CheckAuthorOfAboutPost ->{
                Log.d(TAG, "CheckAuthorOfAboutPost: called.")
                if(sessionManager.isConnectedToTheInternet()){
                    return sessionManager.cachedToken.value?.let { authToken ->
                        aboutRepository.isAuthorOfAboutPost(
                            authToken,
                            viewState.value!!.aboutPost!!.slug
                        )
                    }?: AbsentLiveData.create()
                }
                return AbsentLiveData.create()
            }

            is AboutStateEvent.UpdateAboutPostEvent -> {

                return sessionManager.cachedToken.value?.let { authToken ->

                    val title = RequestBody.create(MediaType.parse("text/plain"), stateEvent.title)
                    val body = RequestBody.create(MediaType.parse("text/plain"), stateEvent.body)

                    aboutRepository.updateAboutPost(
                        authToken,
                        viewState.value!!.aboutPost!!.slug,
                        title,
                        body,
                        stateEvent.image
                    )
                }?: AbsentLiveData.create()
            }

            is AboutStateEvent.DeleteAboutPostEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    viewState.value?.let{aboutViewState ->
                        aboutViewState.aboutPost?.let { aboutPost ->
                            aboutRepository.deleteAboutPost(
                                authToken,
                                aboutPost
                            )
                        }?: AbsentLiveData.create()
                    }?: AbsentLiveData.create()
                }?: AbsentLiveData.create()
            }

            is None ->{
                return object: LiveData<DataState<AboutViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        value = DataState(null, Loading(false), null)
                    }
                }
            }
        }
    }

    override fun initNewViewState(): AboutViewState {
        return AboutViewState()
    }

    fun loadInitialAbouts(){
        // if the user hasn't made a query yet, show some abouts
        val value = getCurrentViewStateOrNew()
        if(value.aboutFields.aboutList.size == 0){
            setQuery("")
            loadFirstPage()
        }
    }

    fun loadFirstPage() {
        setQueryInProgress(true)
        setQueryExhausted(false)
        resetPage()
        setAboutFilter(
            sharedPreferences.getString(ABOUT_FILTER, AboutQueryUtils.ABOUT_FILTER_DATE_UPDATED)
        )
        setAboutOrder(
            sharedPreferences.getString(ABOUT_ORDER, AboutQueryUtils.ABOUT_ORDER_DESC)
        )
        setStateEvent(AboutSearchEvent())
        Log.e(TAG, "AboutViewModel: loadFirstPage: ${viewState.value!!.aboutFields.searchQuery}")
    }

    fun loadNextPage(){
        if(!viewState.value!!.aboutFields.isQueryInProgress && !viewState.value!!.aboutFields.isQueryExhausted){
            Log.d(TAG, "AboutViewModel: Attempting to load next page...")
            setQueryInProgress(true)
            incrementPageNumber()
            setStateEvent(NextPageEvent())
        }
    }

    fun resetPage(){
        val update = getCurrentViewStateOrNew()
        update.aboutFields.page = 1
        _viewState.value = update
    }

    fun setQuery(query: String){
        val update = getCurrentViewStateOrNew()
        update.aboutFields.searchQuery = query
        _viewState.value = update
    }

    fun setAboutListData(aboutList: List<AboutPost>){
        val update = getCurrentViewStateOrNew()
        update.aboutFields.aboutList = aboutList
        _viewState.value = update
        preloadGlideImages(aboutList)
    }

    // Prepare the images that will be displayed in the RecyclerView.
    // This also ensures if the network connection is lost, they will be in the cache
    private fun preloadGlideImages(list: List<AboutPost>){
        for(aboutPost in list){
            requestManager
                .load(aboutPost.image)
                .preload()
        }
    }

    fun incrementPageNumber(){
        val update = getCurrentViewStateOrNew()
        val page = update.copy().aboutFields.page
        update.aboutFields.page = page + 1
        _viewState.value = update
    }

    fun setQueryExhausted(isExhausted: Boolean){
        val update = getCurrentViewStateOrNew()
        update.aboutFields.isQueryExhausted = isExhausted
        _viewState.value = update
    }

    fun setQueryInProgress(isInProgress: Boolean){
        val update = getCurrentViewStateOrNew()
        update.aboutFields.isQueryInProgress = isInProgress
        _viewState.value = update
    }

    // Filter can be "date_updated" or "username"
    fun setAboutFilter(filter: String?){
        filter?.let{
            val update = getCurrentViewStateOrNew()
            update.aboutFields.filter = filter
            _viewState.value = update
        }
    }

    // Order can be "-" or ""
    // Note: "-" = DESC, "" = ASC
    fun setAboutOrder(order: String){
        val update = getCurrentViewStateOrNew()
        update.aboutFields.order = order
        _viewState.value = update
    }

    fun setAboutPost(aboutPost: AboutPost){
        val update = getCurrentViewStateOrNew()
        update.aboutPost = aboutPost
        _viewState.value = update
    }

    fun updateListItem(newAboutPost: AboutPost){
        val update = getCurrentViewStateOrNew()
        val list = update.aboutFields.aboutList.toMutableList()
        for(i in 0..(list.size - 1)){
            if(list[i].pk == newAboutPost.pk){
                list[i] = newAboutPost
                break
            }
        }
        update.aboutFields.aboutList = list
        _viewState.value = update
    }

    fun removeDeletedAboutPost(){
        val update = getCurrentViewStateOrNew()
        val list = update.aboutFields.aboutList.toMutableList()
        for(i in 0..(list.size - 1)){
            if(list[i] == viewState.value!!.aboutPost){
                list.remove(viewState.value!!.aboutPost)
                break
            }
        }
        update.aboutFields.aboutList = list
        _viewState.value = update
    }

    fun setUpdatedAboutFields(title: String?, body: String?, uri: Uri?){
        val update = getCurrentViewStateOrNew()
        val updatedAboutFields = update.updatedAboutFields
        title?.let{ updatedAboutFields.updatedAboutTitle = it }
        body?.let{ updatedAboutFields.updatedAboutBody = it }
        uri?.let{ updatedAboutFields.updatedImageUri = it }
        update.updatedAboutFields = updatedAboutFields
        _viewState.value = update
    }

    fun setIsAuthorOfAboutPost(isAuthorOfAboutPost: Boolean){
        val update = getCurrentViewStateOrNew()
        update.isAuthorOfAboutPost = isAuthorOfAboutPost
        _viewState.value = update
    }

    fun isAuthorOfAboutPost(): Boolean{
        Log.d(TAG, "isAuthorOfAboutPost: ${viewState.value!!.isAuthorOfAboutPost}")
        return viewState.value!!.isAuthorOfAboutPost
    }

    fun cancelActiveJobs(){
        aboutRepository.cancelActiveJobs()
        handlePendingData()
    }

    fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    
}


















