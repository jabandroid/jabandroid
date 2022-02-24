package com.global.vtg.appview.home.clinic

import com.global.vtg.appview.home.dashboard.DashBoardItem
import com.global.vtg.appview.home.dashboard.DashboardAdapter


import android.content.Context
import com.vtg.R
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.GridLayoutManager


internal class ClinicDashboardAdapter(ctx: Context) :
    RecyclerView.Adapter<ClinicDashboardAdapter.ViewHolder>(), DashboardAdapter.ClickListener {
    private val ctx: Context = ctx
    private var pages: ArrayList<String> = ArrayList()
    private lateinit var listenerPager: ClickListener
    private var titleList = ArrayList<String>()
    private var items = ArrayList<DashBoardItem>()


    fun setImages(images: ArrayList<String>){
        this.pages.clear()
        this.pages.addAll(images)
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



        if(position==0){
            var item= DashBoardItem(1,  R.drawable.ic_woman,ctx.resources.getString(R.string.label_profile))
            items.add(item)
            item= DashBoardItem(2, R.drawable.ic_qr_code, ctx.resources.getString(R.string.label_scan_qr_code))
            items.add(item)
            item= DashBoardItem(3, R.drawable.ic_health_info,  ctx.resources.getString(R.string.label_Vaccine))
            items.add(item)
            item= DashBoardItem(4,    R.drawable.ic_health_information,       ctx.resources.getString(R.string.label_test_upload))
            items.add(item)
            item= DashBoardItem(5,   R.drawable.ic_vaccine_card,       ctx.resources.getString(R.string.label_upload_health))
            items.add(item)
            item= DashBoardItem(6,     R.drawable.ic_health_info,      ctx.resources.getString(R.string.label_vaccine_card))
            items.add(item)

            holder. list.layoutManager = GridLayoutManager(ctx, 2)
            val dashboardAdapter = DashboardAdapter(
                ctx, items
            )
            dashboardAdapter.setListener(this)
            holder. list.adapter = dashboardAdapter
        }else{
            items.clear()
            var item= DashBoardItem(7,  R.drawable.ic_event,ctx.resources.getString(R.string.label_event))
            items.add(item)


            holder. list.layoutManager = GridLayoutManager(ctx, 2)
            val dashboardAdapter = DashboardAdapter(
                ctx, items
            )
            dashboardAdapter.setListener(this)
            holder. list.adapter = dashboardAdapter
        }
    }

    // This Method returns the size of the Array
    override fun getItemCount(): Int {
        return pages.size
    }

    // The ViewHolder class holds the view
    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var list: RecyclerView = itemView.findViewById(R.id.recyclerView)
    }

    override fun onItemClick(position: Int) {
        this.listenerPager.onItemClickMain(position)

    }

    fun setListenerPager(listener: ClickListener) {
        this.listenerPager = listener
    }

    interface ClickListener {
        fun onItemClickMain(position: Int)
    }
}