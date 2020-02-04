package com.ashishkharche.kishornikamphotography.di.auth


import com.ashishkharche.kishornikamphotography.ui.auth.ForgotPasswordFragment
import com.ashishkharche.kishornikamphotography.ui.auth.LauncherFragment
import com.ashishkharche.kishornikamphotography.ui.auth.LoginFragment
import com.ashishkharche.kishornikamphotography.ui.auth.RegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector()
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector()
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector()
    abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment

}