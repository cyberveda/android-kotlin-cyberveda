package com.ashishkharche.kishornikamphotography.di

import com.ashishkharche.kishornikamphotography.di.auth.AuthFragmentBuildersModule
import com.ashishkharche.kishornikamphotography.di.auth.AuthModule
import com.ashishkharche.kishornikamphotography.di.auth.AuthScope
import com.ashishkharche.kishornikamphotography.di.auth.AuthViewModelModule
import com.ashishkharche.kishornikamphotography.di.main.MainFragmentBuildersModule
import com.ashishkharche.kishornikamphotography.di.main.MainModule
import com.ashishkharche.kishornikamphotography.di.main.MainScope
import com.ashishkharche.kishornikamphotography.di.main.MainViewModelModule
import com.ashishkharche.kishornikamphotography.ui.auth.AuthActivity
import com.ashishkharche.kishornikamphotography.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @MainScope
    @ContributesAndroidInjector(
        modules = [MainModule::class, MainFragmentBuildersModule::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity

}













