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
    title: ArrayList<String>,
    image: ArrayList<Int>
) :
    RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder>() {
    private var titles: ArrayList<String> = title
    private var images: ArrayList<Int> = image
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
        holder.name.text = titles[position]
        holder.image.setImageResource(images[position])
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener {
            this.listener.onItemClick(position)
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