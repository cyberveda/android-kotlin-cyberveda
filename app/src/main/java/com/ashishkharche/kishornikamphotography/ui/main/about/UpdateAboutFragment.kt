package com.ashishkharche.kishornikamphotography.ui.main.about

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer

import com.ashishkharche.kishornikamphotography.R
import kotlinx.android.synthetic.main.fragment_view_about.about_body
import kotlinx.android.synthetic.main.fragment_view_about.about_image
import kotlinx.android.synthetic.main.fragment_view_about.about_title
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.ashishkharche.kishornikamphotography.ui.*
import com.ashishkharche.kishornikamphotography.ui.main.about.state.AboutStateEvent
import com.ashishkharche.kishornikamphotography.util.Constants.Companion.GALLERY_REQUEST_CODE
import com.ashishkharche.kishornikamphotography.util.FileUtil
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_update_about.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*


class UpdateAboutFragment : BaseAboutFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()

        image_container.setOnClickListener {
            if(stateChangeListener.isStoragePermissionGranted()){
                pickFromGallery()
            }
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun launchImageCrop(uri: Uri){
        context?.let{
            CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(it, this)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                GALLERY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        activity?.let{
                            launchImageCrop(uri)
                        }
                    }?: showImageSelectionError()
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    Log.d(TAG, "CROP: CROP_IMAGE_ACTIVITY_REQUEST_CODE")
                    val result = CropImage.getActivityResult(data)
                    val resultUri = result.uri
                    Log.d(TAG, "CROP: CROP_IMAGE_ACTIVITY_REQUEST_CODE: uri: ${resultUri}")
                    viewModel.setUpdatedAboutFields(
                        title = null,
                        body = null,
                        uri = resultUri
                    )
                }

                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    Log.d(TAG, "CROP: ERROR")
                    showImageSelectionError()
                }
            }
        }
    }

    fun showImageSelectionError(){
        stateChangeListener.onDataStateChange(
            DataState(
                Event(StateError(Response("Something went wrong with the image.", ResponseType.Dialog()))),
                Loading(isLoading = false),
                Data(Event.dataEvent(null), null)
            )
        )
    }

    fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState.data?.let{ data ->
                data.data?.getContentIfNotHandled()?.let{ viewState ->
                    viewState.aboutPost?.let{ aboutPost ->
                        viewModel.setUpdatedAboutFields(
                            uri = null,
                            title = aboutPost.title,
                            body = aboutPost.body
                        )
                        viewModel.setAboutPost(aboutPost)
                        viewModel.updateListItem(aboutPost)
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.updatedAboutFields.let{ updatedAboutFields ->
                setAboutProperties(
                    updatedAboutFields.updatedAboutTitle,
                    updatedAboutFields.updatedAboutBody,
                    updatedAboutFields.updatedImageUri
                )
            }
        })
    }

    fun setAboutProperties(title: String?, body: String?, image: Uri?){
        requestManager
            .load(image)
            .into(about_image)
        about_title.text = title
        about_body.text = body
    }

    private fun saveChanges(){
        var multipartBody: MultipartBody.Part? = null
        viewModel.viewState.value?.updatedAboutFields?.updatedImageUri?.let{ imageUri ->
            imageUri.path?.let{filePath ->
                view?.context?.let{ context ->
                    FileUtil.getUriRealPathAboveKitkat(context, imageUri)?.let{ filepath ->
                        val imageFile = File(filepath)
                        Log.d(TAG, "UpdateAboutFragment, imageFile: file: ${imageFile}")
                        if(imageFile.exists()){
                            val requestBody =
                                RequestBody.create(
                                    MediaType.parse("image/*"),
                                    imageFile
                                )
                            // name = field name in serializer
                            // filename = name of the image file
                            // requestBody = file with file type information
                            multipartBody = MultipartBody.Part.createFormData(
                                "image",
                                imageFile.name,
                                requestBody
                            )
                        }
                    }
                }
            }
        }
        viewModel.setStateEvent(
            AboutStateEvent.UpdateAboutPostEvent(
                about_title.text.toString(),
                about_body.text.toString(),
                multipartBody
            )
        )
        stateChangeListener.hideSoftKeyboard()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save -> {
                saveChanges()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        viewModel.setUpdatedAboutFields(
            uri = null,
            title = about_title.text.toString(),
            body = about_body.text.toString()
        )
    }

}




































