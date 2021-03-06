package com.global.vtg.appview.home.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vtg.R


class DashboardAdapter(
    var context: Context,
    title: ArrayList<DashBoardItem>

) :
    RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder>() {
    private var titles: ArrayList<DashBoardItem> = title

    private lateinit var listener: ClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        // inflate the item Layout
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_dashboard, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return DashboardViewHolder(v) // pass the view to View Holder
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        // set the data in items
        holder.name.text = titles[position].title
        holder.image.setImageResource(titles[position].icon)
        // implement setOnClickListener event on item view.
        holder.itemView.tag = titles[position].id
        holder.itemView.setOnClickListener {
            this.listener.onItemClick(holder.itemView.tag as Int)
        }
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    inner class DashboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // init the item view's
        var name: TextView = itemView.findViewById(R.id.tvTitle)
        var image: ImageView = itemView.findViewById(R.id.ivDashboard) as ImageView
    }

    fun setListener(listener: ClickListener) {
        this.listener = listener
    }

    interface ClickListener {
        fun onItemClick(position: Int)
    }
}