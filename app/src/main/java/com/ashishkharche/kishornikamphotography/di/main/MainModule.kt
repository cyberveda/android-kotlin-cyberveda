package com.ashishkharche.kishornikamphotography.di.main

import com.ashishkharche.kishornikamphotography.api.main.OpenApiMainService
import com.ashishkharche.kishornikamphotography.persistence.AboutPostDao
import com.ashishkharche.kishornikamphotography.persistence.AccountPropertiesDao
import com.ashishkharche.kishornikamphotography.persistence.AppDatabase
import com.ashishkharche.kishornikamphotography.persistence.BlogPostDao
import com.ashishkharche.kishornikamphotography.repository.main.*
import com.ashishkharche.kishornikamphotography.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {

    @MainScope
    @Provides
    fun provideOpenApiMainService(retrofitBuilder: Retrofit.Builder): OpenApiMainService {
        return retrofitBuilder
            .build()
            .create(OpenApiMainService::class.java)
    }

    @MainScope
    @Provides
    fun provideMainRepository(
        openApiMainService: OpenApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
        ): AccountRepository {
        return AccountRepository(openApiMainService, accountPropertiesDao, sessionManager)
    }

    @MainScope
    @Provides
    fun provideBlogPostDao(db: AppDatabase): BlogPostDao {
        return db.getBlogPostDao()
    }

    @MainScope
    @Provides
    fun provideBlogRepository(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): BlogRepository{
        return BlogRepository(openApiMainService, blogPostDao, sessionManager)
    }


//    @MainScope
//    @Provides
//    fun provideCreateBlogRepository(
//        openApiMainService: OpenApiMainService,
//        blogPostDao: BlogPostDao,
//        sessionManager: SessionManager
//    ): CreateBlogRepository{
//        return CreateBlogRepository(openApiMainService, blogPostDao, sessionManager)
//    }


    /*ABOUT*/

    @MainScope
    @Provides
    fun provideAboutPostDao(db: AppDatabase): AboutPostDao {
        return db.getAboutPostDao()
    }

    @MainScope
    @Provides
    fun provideAboutRepository(
        openApiMainService: OpenApiMainService,
        aboutPostDao: AboutPostDao,
        sessionManager: SessionManager
    ): AboutRepository {
        return AboutRepository(openApiMainService, aboutPostDao, sessionManager)
    }


//    @MainScope
//    @Provides
//    fun provideCreateAboutRepository(
//        openApiMainService: OpenApiMainService,
//        aboutPostDao: AboutPostDao,
//        sessionManager: SessionManager
//    ): CreateAboutRepository {
//        return CreateAboutRepository(openApiMainService, aboutPostDao, sessionManager)
//    }


    /*ENDABOUT*/

}




















