package com.global.vtg.appview.home.dashboard

import android.content.Context
import com.vtg.R
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.global.vtg.imageview.defaultLoader
import com.global.vtg.imageview.setGlideNormalImageProgress


internal class ViewPager2Adapter(ctx: Context) :
    RecyclerView.Adapter<ViewPager2Adapter.ViewHolder>() {
    private val ctx: Context
    private var images: ArrayList<String> = ArrayList()

    fun setImages(images: ArrayList<String>){
        this.images.clear()
        this.images.addAll(images)
        this.notifyDataSetChanged()
    }

    // This method returns our layout
    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.view_pager_image, parent, false)
        return ViewHolder(view)
    }

    // This method binds the screen with the view
    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        // This will set the images in imageview
        Glide.with(ctx)
            .load(R.drawable.banner1_small)
            .apply(
                RequestOptions().signature(ObjectKey(100000)).diskCacheStrategy(DiskCacheStrategy.DATA)
)
            .into( holder.images)
    }

    // This Method returns the size of the Array
    override fun getItemCount(): Int {
        return images.size
    }

    // The ViewHolder class holds the view
    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var images: ImageView = itemView.findViewById(R.id.images)
    }

    // Constructor of our ViewPager2Adapter class
    init {
        this.ctx = ctx
    }
}