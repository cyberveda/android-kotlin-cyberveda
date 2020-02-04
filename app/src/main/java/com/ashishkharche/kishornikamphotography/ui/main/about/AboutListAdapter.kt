package com.ashishkharche.kishornikamphotography.ui.main.about

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.ashishkharche.kishornikamphotography.models.AboutPost
import com.ashishkharche.kishornikamphotography.util.DateUtils
import kotlinx.android.synthetic.main.layout_about_list_item.view.*
import com.ashishkharche.kishornikamphotography.util.GenericViewHolder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.ashishkharche.kishornikamphotography.R


class AboutListAdapter(
    private val requestManager: RequestManager,
    private var aboutClickListener: AboutViewHolder.AboutClickListener

) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    private val TAG: String = "AppDebug"

    private val NO_MORE_RESULTS = -1
    private val ABOUT_ITEM = 0
    private val NO_MORE_RESULTS_ABOUT_MARKER = AboutPost(
        NO_MORE_RESULTS,
        "" ,
        "",
        "",
        "",
        0,
        ""
    )

    val DIFF_CALLBACK = object: DiffUtil.ItemCallback<AboutPost>(){

        override fun areItemsTheSame(oldItem: AboutPost, newItem: AboutPost): Boolean {
            return oldItem.pk == newItem.pk
        }

        override fun areContentsTheSame(oldItem: AboutPost, newItem: AboutPost): Boolean {
            return oldItem == newItem
        }
    }
    private val differ =
        AsyncListDiffer(
            AboutRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class AboutRecyclerChangeCallback(
        private val adapter: AboutListAdapter
    ) : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            Log.d(TAG, "MyCallback: onChanged...")
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            Log.d(TAG, "MyCallback: onInserted...")
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            Log.d(TAG, "MyCallback: onMoved...")
            adapter.notifyDataSetChanged()
        }

        override fun onRemoved(position: Int, count: Int) {
            Log.d(TAG, "MyCallback: onRemoved...")
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when(viewType){

            NO_MORE_RESULTS ->{
                Log.e(TAG, "onCreateViewHolder: No more results...")
                return GenericViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_no_more_results,
                        parent,
                        false
                    )
                )
            }

            ABOUT_ITEM ->{
                return AboutViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.layout_about_list_item, parent, false),
                    aboutClickListener = aboutClickListener,
                    requestManager = requestManager
                )
            }
            else -> {
                return AboutViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.layout_about_list_item, parent, false),
                    aboutClickListener = aboutClickListener,
                    requestManager = requestManager
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {

            is AboutViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }

            is GenericViewHolder -> {
                // do nothing
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(differ.currentList.get(position).pk > -1){
            return ABOUT_ITEM
        }
        return differ.currentList.get(position).pk
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    fun submitList(aboutList: List<AboutPost>?, isQueryExhausted: Boolean){
        val newList = aboutList?.toMutableList()
        if (isQueryExhausted)
            newList?.add(NO_MORE_RESULTS_ABOUT_MARKER)
        differ.submitList(newList)
    }

    fun findAboutPost(position: Int): AboutPost{
        return differ.currentList[position]
    }

    class AboutViewHolder
    constructor(
        itemView: View,
        val requestManager: RequestManager,
        val aboutClickListener: AboutClickListener
    ): RecyclerView.ViewHolder(itemView), View.OnClickListener{


        init {
            itemView.setOnClickListener(this)
        }

        fun bind(aboutPost: AboutPost) = with(itemView){
            requestManager
                .load(aboutPost.image)
                .transition(withCrossFade())
                .into(itemView.about_image)
            itemView.about_title.text = aboutPost.title
            itemView.about_author.text = aboutPost.username
//            itemView.about_update_date.text = DateUtils.convertLongToStringDate(aboutPost.date_updated)
        }

        override fun onClick(v: View?) {
            aboutClickListener.onAboutSelected(adapterPosition)
        }


        interface AboutClickListener{
            fun onAboutSelected(itemPosition: Int)
        }
    }

}
























