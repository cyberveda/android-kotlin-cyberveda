package com.ashishkharche.kishornikamphotography.ui.main.blog


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ashishkharche.kishornikamphotography.models.BlogPost
import com.ashishkharche.kishornikamphotography.ui.AreYouSureCallback
import com.ashishkharche.kishornikamphotography.ui.UIMessage
import com.ashishkharche.kishornikamphotography.ui.UIMessageType
import com.ashishkharche.kishornikamphotography.ui.main.blog.state.BlogStateEvent.CheckAuthorOfBlogPost
import com.ashishkharche.kishornikamphotography.ui.main.blog.state.BlogStateEvent.DeleteBlogPostEvent
import com.ashishkharche.kishornikamphotography.util.DateUtils
import com.ashishkharche.kishornikamphotography.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import kotlinx.android.synthetic.main.fragment_view_blog.*


class ViewBlogFragment : BaseBlogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            com.ashishkharche.kishornikamphotography.R.layout.fragment_view_blog,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
        checkIsAuthorOfBlogPost()
        stateChangeListener.expandAppBar()

        delete_button.setOnClickListener {
            confirmDeleteRequest()
        }
    }

    fun checkIsAuthorOfBlogPost() {
        viewModel.setIsAuthorOfBlogPost(false) // reset
        viewModel.setStateEvent(CheckAuthorOfBlogPost())
    }

    fun confirmDeleteRequest() {
        val callback: AreYouSureCallback = object : AreYouSureCallback {

            override fun proceed() {
                deleteBlogPost()
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

    fun deleteBlogPost() {
        viewModel.setStateEvent(
            DeleteBlogPostEvent()
        )
    }

    fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState?.let {
                it.data?.let { data ->
                    data.data?.getContentIfNotHandled()?.let { viewState ->
                        viewModel.setIsAuthorOfBlogPost(viewState.isAuthorOfBlogPost)
                    }
                    data.response?.peekContent()?.let { response ->
                        if (response.message.equals(SUCCESS_BLOG_DELETED)) {
                            viewModel.removeDeletedBlogPost()
                            findNavController().popBackStack()
                        }
                    }
                }
            }

        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.blogPost?.let { blogPost ->
                setBlogProperties(blogPost)
            }
            if (viewState.isAuthorOfBlogPost) {
                adaptViewToAuthorMode()
            }

        })
    }

    fun adaptViewToAuthorMode() {
        activity?.invalidateOptionsMenu()
//        delete_button.visibility = View.VISIBLE
        delete_button.visibility = View.GONE
    }

    fun setBlogProperties(blogPost: BlogPost) {
        requestManager
            .load(blogPost.image)
            .into(blog_image)
        blog_title.setText(blogPost.title)
        blog_author.setText(blogPost.username)
//        blog_update_date.setText(DateUtils.convertLongToStringDate(blogPost.date_updated))


        /**************************************** MARKDOWN - MARKWON ****************************************/
//        var markwon = this@ViewBlogFragment.context?.let { Markwon.create(it) }
//        markwon?.setMarkdown(blog_body, blogPost.body)
        /**************************************** MARKDOWN - MARKWON ****************************************/

        /**************************************** MARKDOWN - MARKDOWNVIEW-ANDROID ****************************************/


//        markdown_view .setMarkDownText(blogPost.body)


        var markwon: Markwon = Markwon.builder(requireContext())
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(HtmlPlugin.create())
            .usePlugin(
                GlideImagesPlugin.create(
                    requireContext()
                )
            )
            .build()
        markwon?.setMarkdown(markdown_view, blogPost.body)
        /**************************************** MARKDOWN - MARKDOWNVIEW-ANDROID ****************************************/

//        blog_body.setText(blogPost.body)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (viewModel.isAuthorOfBlogPost()) {
            inflater.inflate(com.ashishkharche.kishornikamphotography.R.menu.edit_view_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (viewModel.isAuthorOfBlogPost()) {
            when (item.itemId) {
                com.ashishkharche.kishornikamphotography.R.id.edit -> {
                    navUpdateBlogFragment()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navUpdateBlogFragment() {
        try {
            // prep for next fragment
            viewModel.setUpdatedBlogFields(
                viewModel.viewState.value!!.blogPost!!.title,
                viewModel.viewState.value!!.blogPost!!.body,
                viewModel.viewState.value!!.blogPost!!.image.toUri()
            )
            findNavController().navigate(com.ashishkharche.kishornikamphotography.R.id.action_viewBlogFragment_to_updateBlogFragment)
        } catch (e: Exception) {
            // send error report or something. These fields should never be null. Not possible
            Log.e(TAG, "Exception: ${e.message}")
        }
    }

}






















