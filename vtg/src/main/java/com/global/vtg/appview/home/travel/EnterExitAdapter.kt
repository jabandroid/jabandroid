package com.global.vtg.appview.home.travel

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.global.vtg.appview.authentication.registration.ResUser
import com.vtg.R
import kotlinx.android.synthetic.main.adapter_form_list.view.*


class EnterExitAdapter(
    var context: Context,
    var click: onItemClick
) :
    RecyclerView.Adapter<EnterExitAdapter.DashboardViewHolder>() {
    private var list: ArrayList<FormList> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_form_list, parent, false)
        return DashboardViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        // set the data in items


        holder.itemView.tvName.text = list[position].name
        if (list[position].isDone) {
            holder.itemView.root.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                )
            )
            holder.itemView.done.visibility = View.VISIBLE
        } else {
            holder.itemView.root.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.lightGrey
                )
            )

            holder.itemView.done.visibility = View.GONE
        }

        holder.itemView.setOnClickListener{
            click.response(list[position],it,position)
        }


    }

    override fun getItemCount(): Int {
        return list.size

    }

    inner class DashboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


    @SuppressLint("NotifyDataSetChanged")
    fun setList(historyList: ArrayList<FormList>) {
        list.clear()
        list.addAll(historyList)
        notifyDataSetChanged()
    }

    interface onItemClick {
        fun response(item: FormList, v: View, position: Int)
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