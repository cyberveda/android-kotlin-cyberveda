package com.ashishkharche.kishornikamphotography.ui.main.about


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ashishkharche.kishornikamphotography.models.AboutPost
import com.ashishkharche.kishornikamphotography.ui.AreYouSureCallback
import com.ashishkharche.kishornikamphotography.ui.UIMessage
import com.ashishkharche.kishornikamphotography.ui.UIMessageType
import com.ashishkharche.kishornikamphotography.ui.main.about.state.AboutStateEvent.CheckAuthorOfAboutPost
import com.ashishkharche.kishornikamphotography.ui.main.about.state.AboutStateEvent.DeleteAboutPostEvent
import com.ashishkharche.kishornikamphotography.ui.main.blog.ViewBlogFragment
import com.ashishkharche.kishornikamphotography.util.DateUtils
import com.ashishkharche.kishornikamphotography.util.SuccessHandling.Companion.SUCCESS_ABOUT_DELETED
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.fragment_view_about.*
import com.bumptech.glide.Glide
import android.text.method.TextKeyListener.clear
import androidx.annotation.NonNull
import io.noties.markwon.image.AsyncDrawable
import android.graphics.drawable.Drawable
import com.ashishkharche.kishornikamphotography.R
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin.GlideStore
import io.noties.markwon.linkify.LinkifyPlugin
import kotlinx.coroutines.NonCancellable.cancel
import android.text.style.AlignmentSpan
import android.text.Layout
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.RenderProps
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.html.tag.SimpleTagHandler

import androidx.annotation.Nullable
import com.bumptech.glide.Registry
import io.noties.markwon.AbstractMarkwonPlugin
import java.util.*


class ViewAboutFragment : BaseAboutFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            com.ashishkharche.kishornikamphotography.R.layout.fragment_view_about,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
        checkIsAuthorOfAboutPost()
        stateChangeListener.expandAppBar()


        delete_button.setOnClickListener {
            confirmDeleteRequest()
        }
    }

    fun checkIsAuthorOfAboutPost() {
        viewModel.setIsAuthorOfAboutPost(false) // reset
        viewModel.setStateEvent(CheckAuthorOfAboutPost())
    }

    fun confirmDeleteRequest() {
        val callback: AreYouSureCallback = object : AreYouSureCallback {

            override fun proceed() {
                deleteAboutPost()
            }

            override fun cancel() {
                // ignore
            }

        }
        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                getString(com.ashishkharche.kishornikamphotography.R.string.are_you_sure_delete),
                UIMessageType.AreYouSureDialog(callback)
            )
        )
    }

    fun deleteAboutPost() {
        viewModel.setStateEvent(
            DeleteAboutPostEvent()
        )
    }

    fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState?.let {
                it.data?.let { data ->
                    data.data?.getContentIfNotHandled()?.let { viewState ->
                        viewModel.setIsAuthorOfAboutPost(viewState.isAuthorOfAboutPost)
                    }
                    data.response?.peekContent()?.let { response ->
                        if (response.message.equals(SUCCESS_ABOUT_DELETED)) {
                            viewModel.removeDeletedAboutPost()
                            findNavController().popBackStack()
                        }
                    }
                }
            }

        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.aboutPost?.let { aboutPost ->
                setAboutProperties(aboutPost)
            }
            if (viewState.isAuthorOfAboutPost) {
                adaptViewToAuthorMode()
            }

        })
    }

    fun adaptViewToAuthorMode() {
        activity?.invalidateOptionsMenu()
        delete_button.visibility = View.GONE
    }

    fun setAboutProperties(aboutPost: AboutPost) {
        requestManager
            .load(aboutPost.image)
            .into(about_image)
        about_title.setText(aboutPost.title)
        about_author.setText(aboutPost.username)
//        about_update_date.setText(DateUtils.convertLongToStringDate(aboutPost.date_updated))


        /**************************************** MARKDOWN - MARKWON ****************************************/
//        var markwon = this@ViewAboutFragment.context?.let { Markwon.create(it) }
//        markwon?.setMarkdown(about_body, aboutPost.body)
        /**************************************** MARKDOWN - MARKWON ****************************************/

        /**************************************** MARKDOWN - MARKDOWNVIEW-ANDROID ****************************************/


//        markdown_view .setMarkDownText(aboutPost.body)

        // obtain an instance of Markwon

//        markwon?.setMarkdown(markdown_view, aboutPost.body)
        class CenterTagHandler : SimpleTagHandler() {

            override fun getSpans(
                configuration: MarkwonConfiguration,
                renderProps: RenderProps,
                tag: HtmlTag
            ): Any = Layout.Alignment.ALIGN_CENTER

            override fun supportedTags() = listOf("center")
        }

        class AlignTagHandler : SimpleTagHandler() {

            override fun getSpans(
                configuration: MarkwonConfiguration,
                renderProps: RenderProps,
                tag: HtmlTag
            ): Any {
                val alignment: Layout.Alignment = when {
                    tag.attributes().containsKey("center") -> Layout.Alignment.ALIGN_CENTER
                    tag.attributes().containsKey("end") -> Layout.Alignment.ALIGN_OPPOSITE
                    else -> Layout.Alignment.ALIGN_NORMAL
                }
                return AlignmentSpan.Standard(alignment)
            }

            override fun supportedTags() = listOf("align")
        }

        val markwon: Markwon = Markwon.builder(requireContext())


            .usePlugin(LinkifyPlugin.create())
            .usePlugin(HtmlPlugin.create { plugin ->
                plugin
                    .addHandler(AlignTagHandler())
                    .addHandler(CenterTagHandler())
            })
            .usePlugin(
                GlideImagesPlugin.create(
                    requireContext()
                )
            )
            .build()
        markwon?.setMarkdown(markdown_view, aboutPost.body)


        // automatically create Glide instance


        /**************************************** MARKDOWN - MARKDOWNVIEW-ANDROID ****************************************/

//        about_body.setText(aboutPost.body)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (viewModel.isAuthorOfAboutPost()) {
            inflater.inflate(com.ashishkharche.kishornikamphotography.R.menu.edit_view_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (viewModel.isAuthorOfAboutPost()) {
            when (item.itemId) {
                com.ashishkharche.kishornikamphotography.R.id.edit -> {
                    navUpdateAboutFragment()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navUpdateAboutFragment() {
        try {
            // prep for next fragment
            viewModel.setUpdatedAboutFields(
                viewModel.viewState.value!!.aboutPost!!.title,
                viewModel.viewState.value!!.aboutPost!!.body,
                viewModel.viewState.value!!.aboutPost!!.image.toUri()
            )
            findNavController().navigate(com.ashishkharche.kishornikamphotography.R.id.action_viewAboutFragment_to_updateAboutFragment)
        } catch (e: Exception) {
            // send error report or something. These fields should never be null. Not possible
            Log.e(TAG, "Exception: ${e.message}")
        }
    }

}






















