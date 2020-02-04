package com.ashishkharche.kishornikamphotography.di.auth

import android.content.SharedPreferences
import com.ashishkharche.kishornikamphotography.api.auth.OpenApiAuthService
import com.ashishkharche.kishornikamphotography.persistence.AccountPropertiesDao
import com.ashishkharche.kishornikamphotography.persistence.AuthTokenDao
import com.ashishkharche.kishornikamphotography.repository.auth.AuthRepository
import com.ashishkharche.kishornikamphotography.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule{

    @AuthScope
    @Provides
    fun provideOpenApiAuthService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder
            .build()
            .create(OpenApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService,
        preferences: SharedPreferences,
        editor: SharedPreferences.Editor): AuthRepository {
        return AuthRepository(
            sessionManager,
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            preferences,
            editor
        )
    }

}