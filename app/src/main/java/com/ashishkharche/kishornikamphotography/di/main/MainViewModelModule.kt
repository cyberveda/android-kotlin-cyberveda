package com.ashishkharche.kishornikamphotography.di.main

import androidx.lifecycle.ViewModel
import com.ashishkharche.kishornikamphotography.di.ViewModelKey
import com.ashishkharche.kishornikamphotography.ui.main.about.AboutViewModel
import com.ashishkharche.kishornikamphotography.ui.main.account.AccountViewModel
import com.ashishkharche.kishornikamphotography.ui.main.blog.BlogViewModel

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(BlogViewModel::class)
    abstract fun bindBlogViewModel(blogViewModel: BlogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accoutViewModel: AccountViewModel): ViewModel

//    @Binds
//    @IntoMap
//    @ViewModelKey(CreateBlogViewModel::class)
//    abstract fun bindCreateBlogViewModel(createBlogViewModel: CreateBlogViewModel): ViewModel

    /*ABOUT*/


    @Binds
    @IntoMap
    @ViewModelKey(AboutViewModel::class)
    abstract fun bindAboutViewModel(aboutViewModel: AboutViewModel): ViewModel

//
//    @Binds
//    @IntoMap
//    @ViewModelKey(CreateAboutViewModel::class)
//    abstract fun bindCreateAboutViewModel(createAboutViewModel: CreateAboutViewModel): ViewModel

    /*ENDABOUT*/
}