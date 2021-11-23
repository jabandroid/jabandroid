package com.global.vtg.appview.home.uploaddocument

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.global.vtg.appview.config.Institute
import com.vtg.R
import kotlin.collections.ArrayList


class InstituteAdapter (context: Context) :
    RecyclerView.Adapter<InstituteAdapter.InstituteViewHolder>() {
    private var items: ArrayList<Institute> = ArrayList()
    private lateinit var listener: InstituteAdapter.ClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstituteAdapter.InstituteViewHolder {
        // inflate the item Layout
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_institute, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return InstituteViewHolder(v) // pass the view to View Holder
    }

    override fun onBindViewHolder(holder: InstituteAdapter.InstituteViewHolder, position: Int) {
        // set the data in items
        holder.tvInstitute.text = items[position].name
        holder.itemView.setOnClickListener {
            listener.onItemClick(items[position])
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class InstituteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // init the item view's
        var tvInstitute: TextView = itemView.findViewById(R.id.tvInstitute)
    }

    fun setListener(listener: ClickListener) {
        this.listener = listener
    }

    fun setInstituteList(list: ArrayList<Institute>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    interface ClickListener {
        fun onItemClick(institute: Institute)
    }
}