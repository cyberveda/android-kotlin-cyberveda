package com.ashishkharche.kishornikamphotography.di.auth

import androidx.lifecycle.ViewModel
import com.ashishkharche.kishornikamphotography.di.ViewModelKey
import com.ashishkharche.kishornikamphotography.ui.auth.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel

}