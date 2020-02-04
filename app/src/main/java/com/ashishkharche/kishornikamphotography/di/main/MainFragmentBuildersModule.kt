package com.ashishkharche.kishornikamphotography.di.main

import com.ashishkharche.kishornikamphotography.ui.main.about.AboutFragment
import com.ashishkharche.kishornikamphotography.ui.main.about.UpdateAboutFragment
import com.ashishkharche.kishornikamphotography.ui.main.about.ViewAboutFragment
import com.ashishkharche.kishornikamphotography.ui.main.account.AccountFragment
import com.ashishkharche.kishornikamphotography.ui.main.account.ChangePasswordFragment
import com.ashishkharche.kishornikamphotography.ui.main.account.UpdateAccountFragment
import com.ashishkharche.kishornikamphotography.ui.main.blog.BlogFragment
import com.ashishkharche.kishornikamphotography.ui.main.blog.UpdateBlogFragment
import com.ashishkharche.kishornikamphotography.ui.main.blog.ViewBlogFragment

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeBlogFragment(): BlogFragment


    @ContributesAndroidInjector()
    abstract fun contributeAboutFragment(): AboutFragment

    @ContributesAndroidInjector()
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

//    @ContributesAndroidInjector()
//    abstract fun contributeCreateBlogFragment(): CreateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateBlogFragment(): UpdateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewBlogFragment(): ViewBlogFragment

//    @ContributesAndroidInjector()
//    abstract fun contributeCreateAboutFragment(): CreateAboutFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateAboutFragment(): UpdateAboutFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewAboutFragment(): ViewAboutFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment
}