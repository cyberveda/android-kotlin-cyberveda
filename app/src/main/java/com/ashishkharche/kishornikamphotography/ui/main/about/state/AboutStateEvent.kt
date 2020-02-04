package com.ashishkharche.kishornikamphotography.ui.main.about.state

import okhttp3.MultipartBody


sealed class AboutStateEvent{

    class AboutSearchEvent: AboutStateEvent()

    class NextPageEvent: AboutStateEvent()

    class CheckAuthorOfAboutPost: AboutStateEvent()

    class DeleteAboutPostEvent: AboutStateEvent()

    data class UpdateAboutPostEvent(
        val title: String,
        val body: String,
        val image: MultipartBody.Part?
    ): AboutStateEvent()

    class None: AboutStateEvent()


}