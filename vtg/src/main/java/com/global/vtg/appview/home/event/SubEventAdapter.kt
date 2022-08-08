package com.global.vtg.appview.home.event

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.global.vtg.utils.DateUtils
import com.vtg.R
import kotlinx.android.synthetic.main.adapter_event_list.view.*
import kotlinx.android.synthetic.main.adapter_event_list.view.eventImage
import kotlinx.android.synthetic.main.adapter_event_list.view.tvEventName
import kotlinx.android.synthetic.main.adapter_event_list.view.tvLocation
import kotlinx.android.synthetic.main.adapter_sub_event_list.view.*
import kotlinx.android.synthetic.main.recycler_view_vaccine_history.view.tvDate


class SubEventAdapter(
    var context: Context,
    private var click: onItemClick,
   var isMyEvent:Boolean
) :
    RecyclerView.Adapter<SubEventAdapter.DashboardViewHolder>() {
    private var list: ArrayList<Event> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_sub_event_list, parent, false)
        return DashboardViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        // set the data in items

        if(list[position].eventImage!!.isNotEmpty()) {
            for ( item in list[position].eventImage!!){
                if(item.banner){
                    Glide.with(context)
                        .asBitmap()
                        .load(item.url)
                        .into(holder.itemView.eventImage)
                    break
                }
            }

        }else{
            Glide.with(context)
                .asBitmap()
                .load(R.drawable.events)
                .into(holder.itemView.eventImage)
        }

//        holder.itemView.tvDate.text =  DateUtils.formatDateUTCToLocal(
//            list[position].startDate!!,
//            DateUtils.API_DATE_FORMAT_VACCINE,
//            true
//        )

        try {
            holder.itemView.tvDate.text  =
                DateUtils.formatDateUTCToLocal(
                    list[position].startDate!!,
                    DateUtils.API_DATE_FORMAT_VACCINE,
                    true
                ) + " \u2192 " + DateUtils.formatDateUTCToLocal(
                    list[position].endDate!!,
                    DateUtils.API_DATE_FORMAT_VACCINE,
                    true
                )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.itemView.tvEventName.text = list[position].eventName

        if(list[position].eventAddress!!.isNotEmpty()) {
            holder.itemView.tvLocation.text = list[position].eventAddress!![0].city + " " +
                    list[position].eventAddress!![0].state + " " +
                    list[position].eventAddress!![0].country
        }
        holder.itemView.setOnClickListener {
            click.onClick(list[position],it,position)
        }

        if(isMyEvent)
            holder.itemView.more.visibility=View.VISIBLE
        holder.itemView.more.setOnClickListener {
            click.onClick(list[position],it,position)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class DashboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // init the item view's

    }

    fun setList(historyList: ArrayList<Event>) {
        list.clear()
        list.addAll(historyList)
        notifyDataSetChanged()
    }


    fun remove(pos:Int) {

        list.removeAt(pos)
        notifyDataSetChanged()
    }

    interface onItemClick {
        fun onClick(item: Event, v:View, position:Int)


    }



}