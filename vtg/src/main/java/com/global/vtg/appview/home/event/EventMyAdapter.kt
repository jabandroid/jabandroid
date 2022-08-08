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


class EventMyAdapter(
    var context: Context,
    private var click: onItemClick
) :
    RecyclerView.Adapter<EventMyAdapter.DashboardViewHolder>() {
    private var list: ArrayList<Event> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_event_list, parent, false)
        return DashboardViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
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

        holder.itemView.tvDate.text =  DateUtils.formatDateUTCToLocal(
            list[position].startDate!!,
            DateUtils.API_DATE_FORMAT_VACCINE,
            true
        )
        holder.itemView.share.visibility =View.VISIBLE
        holder.itemView.tvEventName.text = list[position].eventName

        if(list[position].eventAddress!!.isNotEmpty()) {
            holder.itemView.tvLocation.text = list[position].eventAddress!![0].city + " " +
                    list[position].eventAddress!![0].state + " " +
                    list[position].eventAddress!![0].country
        }

       
        holder.itemView.setOnClickListener {
            click.onClick(list[position],it)
        }
        holder.itemView.share.setOnClickListener {
            click.onClick(list[position],it)
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

    interface onItemClick {
        fun onClick(item: Event, v:View)


    }


}