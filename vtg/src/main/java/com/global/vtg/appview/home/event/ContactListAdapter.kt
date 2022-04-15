package com.global.vtg.appview.home.event

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.vtg.R
import kotlinx.android.synthetic.main.adapter_contact.view.*


class ContactListAdapter(
    private val context: Context?,
    private var appReqList: ArrayList<ContactItem>

) : RecyclerView.Adapter<ContactListAdapter.MyViewHolder>() {
    class MyViewHolder(val layout: ConstraintLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_contact,
            parent,
            false
        ) as ConstraintLayout
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return appReqList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.layout.tv_name.text = appReqList[position].getName()
        holder.layout.tv_number.text = appReqList[position].getNumber()
    }
}