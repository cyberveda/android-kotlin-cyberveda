package com.ashishkharche.kishornikamphotography.ui.main.about

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.RequestManager
import com.ashishkharche.kishornikamphotography.R
import com.ashishkharche.kishornikamphotography.ui.DataStateChangeListener
import com.ashishkharche.kishornikamphotography.ui.UICommunicationListener
import com.ashishkharche.kishornikamphotography.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseAboutFragment : DaggerFragment(){

    val TAG: String = "AppDebug"

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var stateChangeListener: DataStateChangeListener

    lateinit var uiCommunicationListener: UICommunicationListener

    lateinit var viewModel: AboutViewModel

    @Inject
    lateinit var requestManager: RequestManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setup back navigation for this graph
        setupActionBarWithNavController(R.id.aboutFragment, activity as AppCompatActivity)

        viewModel = activity?.run {
            ViewModelProvider(this, providerFactory).get(AboutViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        Log.d(TAG, "AboutViewModel: ${viewModel}")

        // Cancels jobs when switching between fragments in the same graph
        // ex: from AccountFragment to UpdateAccountFragment
        // NOTE: Must call before "subscribeObservers" b/c that will create new jobs for the next fragment
        cancelActiveJobs()
    }

    fun cancelActiveJobs(){
        Log.d(TAG, "cancelling jobs...: ")
        // When a fragment is destroyed make sure to cancel any on-going requests.
        // Note: If you wanted a particular request to continue even if the fragment was destroyed, you could write a
        //       special condition in the repository or something.
        viewModel.cancelActiveJobs()
    }


    /*
      @fragmentId is id of fragment from graph to be EXCLUDED from action back bar nav
    */
    fun setupActionBarWithNavController(fragmentId: Int, activity: AppCompatActivity){
        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            appBarConfiguration
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            stateChangeListener = context as DataStateChangeListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement DataStateChangeListener" )
        }

        try{
            uiCommunicationListener = context as UICommunicationListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement UICommunicationListener" )
        }
    }

}

























