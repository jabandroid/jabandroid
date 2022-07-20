package com.global.vtg.appview.home.travel

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.global.vtg.appview.authentication.registration.ResUser
import com.vtg.R


class FlightStatusAdapter(
    var context: Context,
    var click: onItemClick
) :
    RecyclerView.Adapter<FlightStatusAdapter.DashboardViewHolder>() {
    private var list: ArrayList<ResUser> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_flight_list, parent, false)
        return DashboardViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        // set the data in items






    }

    override fun getItemCount(): Int {
       // return list.size
        return 2
    }

    inner class DashboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    public fun getID(pos: Int): Int? {
        return list[pos].id
    }



    @SuppressLint("NotifyDataSetChanged")
    fun setList(historyList: ArrayList<ResUser>) {
        list.clear()
        list.addAll(historyList)
        notifyDataSetChanged()
    }

    interface onItemClick {
        fun response(item: ResUser, v: View, position: Int)
    }


    @SuppressLint("NotifyDataSetChanged")
    public fun remove(index: Int) {
        list.removeAt(index)
        notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    public fun getSize(): Int {
        return list.size
    }

}