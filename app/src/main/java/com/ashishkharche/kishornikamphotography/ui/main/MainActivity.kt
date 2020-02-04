package com.ashishkharche.kishornikamphotography.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.ashishkharche.kishornikamphotography.R
import com.ashishkharche.kishornikamphotography.ui.*
import com.ashishkharche.kishornikamphotography.ui.auth.AuthActivity
import com.ashishkharche.kishornikamphotography.ui.main.about.BaseAboutFragment
import com.ashishkharche.kishornikamphotography.ui.main.account.*
import com.ashishkharche.kishornikamphotography.ui.main.blog.BaseBlogFragment
import com.ashishkharche.kishornikamphotography.ui.main.blog.UpdateBlogFragment
import com.ashishkharche.kishornikamphotography.ui.main.blog.ViewBlogFragment
import com.ashishkharche.kishornikamphotography.util.*
import com.ashishkharche.kishornikamphotography.util.Constants.Companion.PERMISSIONS_REQUEST_READ_STORAGE
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(),
    BottomNavController.NavGraphProvider,
    BottomNavController.OnNavigationGraphChanged,
    BottomNavController.OnNavigationReselectedListener
{


    private val TAG: String = "AppDebug"

    /**************************************** NEW VAR ****************************************/
    var tokenVar: String? = null
    /**************************************** end NEW VAR ****************************************/

    private lateinit var bottomNavigationView: BottomNavigationView

    private val bottomNavController by lazy(LazyThreadSafetyMode.NONE) {
        BottomNavController(
            this,
            R.id.main_nav_host_fragment,
            R.id.nav_blog,
            this,
            this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        setupActionBar()
        Log.d(TAG, "MainActivity: onCreate: called.")

        bottomNavigationView.setUpNavigation(bottomNavController, this)
        if (savedInstanceState == null) {
            bottomNavController.onNavigationItemSelected()
        }
        subscribeObservers()
    }


    override fun isStoragePermissionGranted(): Boolean{
        if (
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED  ) {


            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSIONS_REQUEST_READ_STORAGE
            )

            return false
        } else {
            // Permission has already been granted
            return true
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(tool_bar)
    }

    override fun getNavGraphId(itemId: Int) = when (itemId) {
        R.id.nav_blog -> {
            R.navigation.nav_blog
        }
        R.id.nav_about -> {

            if (tokenVar.equals("56cc4217d7b585d17f6f3323a9d798f09fb97e89")){
                Log.d("LGX", "token matches create BLOG")
                R.navigation.nav_about
            }else{
                R.navigation.nav_about
            }

        }
        R.id.nav_account -> {
            R.navigation.nav_account
        }
        else -> {
            R.navigation.nav_blog
        }
    }

    // Cancel previous jobs when navigating to a new graph
    override fun onGraphChange() {
        cancelActiveJobs()
        expandAppBar()
    }

    private fun cancelActiveJobs(){
        val fragments = bottomNavController.fragmentManager.findFragmentById(bottomNavController.containerId)?.childFragmentManager?.fragments
        if(fragments != null){
            for(fragment in fragments){
                if(fragment is BaseAccountFragment){
                    fragment.cancelActiveJobs()
                }
                if(fragment is BaseBlogFragment){
                    fragment.cancelActiveJobs()
                }
                if(fragment is BaseAboutFragment){
                    fragment.cancelActiveJobs()
                }
            }
        }
        displayProgressBar(false)
    }

    override fun onBackPressed() = bottomNavController.onBackPressed()

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    // This was shown to cause problems in testing due to our custom navigation system with the nav bar
    // Instead I'll be using 'onOptionsItemSelected'
//    override fun onSupportNavigateUp(): Boolean = navController
//        .navigateUp()

    fun subscribeObservers(){
        sessionManager.cachedToken.observe(this, Observer{ authToken ->
            Log.d(TAG, "MainActivity, subscribeObservers: ViewState: ${authToken}")



            tokenVar = authToken?.token

            tokenVar?.let {
                if(tokenVar.equals("56cc4217d7b585d17f6f3323a9d798f09fb97e89")){
                    Log.d("LGX", "tokenVar matches ${authToken.token}")
                }else{
                    Log.d("LGX", "tokenVar does not matches ${authToken.token}")

                }

            }


            if(authToken == null || authToken.account_pk == -1 || authToken.token == null){
                navAuthActivity()
                finish()
            }
        })
    }

    private fun navAuthActivity(){
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(bool: Boolean){
        if(bool){
            progress_bar.visibility = View.VISIBLE
        }
        else{
            progress_bar.visibility = View.GONE
        }
    }

    override fun setActionBarTitle(title: String) {
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setTitle(title)
    }

    override fun expandAppBar() {
        findViewById<AppBarLayout>(R.id.app_bar).setExpanded(true)
    }


    override fun onReselectNavItem(navController: NavController, fragment: Fragment) {
        when(fragment){

            is ViewBlogFragment -> {
                navController.navigate(R.id.action_viewBlogFragment_to_home)
            }

            is UpdateBlogFragment -> {
                navController.navigate(R.id.action_updateBlogFragment_to_home)
            }

            is UpdateAccountFragment -> {
                navController.navigate(R.id.action_updateAccountFragment_to_home)
            }

            is ChangePasswordFragment -> {
                navController.navigate(R.id.action_changePasswordFragment_to_home)
            }
        }
    }


}






















