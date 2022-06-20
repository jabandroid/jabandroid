package com.global.vtg.appview.home.qrcode

import android.content.Context
import com.vtg.R
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.GridLayoutManager
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.utils.SharedPreferenceUtil


internal class ViewPagerQrCodeAdapter(ctx: Context) :
    RecyclerView.Adapter<ViewPagerQrCodeAdapter.ViewHolder>() {
    private val ctx: Context = ctx
    private var size=0



    fun setImages(images: Int){
        this.size=images

        this.notifyDataSetChanged()
    }

    // This method returns our layout
    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.view_pager_recycler, parent, false)
        return ViewHolder(view)
    }

    // This method binds the screen with the view
    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {



    }

    // This Method returns the size of the Array
    override fun getItemCount(): Int {
        return size
    }

    // The ViewHolder class holds the view
    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var list: RecyclerView = itemView.findViewById(R.id.recyclerView)
    }





}