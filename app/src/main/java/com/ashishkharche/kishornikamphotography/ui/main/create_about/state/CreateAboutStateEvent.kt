//package com.ashishkharche.kishornikamphotography.ui.main.create_about.state
//
//import okhttp3.MultipartBody
//
//
//sealed class CreateAboutStateEvent {
//
//
//    data class CreateNewAboutEvent(
//        val title: String,
//        val body: String,
//        val image: MultipartBody.Part
//    ): CreateAboutStateEvent()
//
//    class None: CreateAboutStateEvent()
//}