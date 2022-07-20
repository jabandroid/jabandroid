package com.global.vtg.appview.home.parentchild

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.global.vtg.imageview.setGlideNormalImage
import com.vtg.R
import kotlinx.android.synthetic.main.adapter_child_list.view.*
import kotlinx.android.synthetic.main.adapter_child_list.view.ivProfilePic
import kotlinx.android.synthetic.main.adapter_child_list.view.tvName
import kotlinx.android.synthetic.main.adapter_shared_parent_list.view.*


class SharedParentListAdapter(
    var context: Context,
    var click: onItemClick
) :
    RecyclerView.Adapter<SharedParentListAdapter.DashboardViewHolder>() {
    private var list: ArrayList<ParentList> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_shared_parent_list, parent, false)
        return DashboardViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        if(!TextUtils.isEmpty(list[position].profilePic))
            holder.itemView.ivProfilePic.setGlideNormalImage(list[position].profilePic!!)
        holder.itemView.tvName.text = list[position].firstName + " " + list[position].lastName
        holder.itemView.imageRemove.setOnClickListener{
            click.response(list[position],it,position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class DashboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    public fun getID(pos: Int): String? {
        return  list[pos].id.toString()
    }

  
    @SuppressLint("NotifyDataSetChanged")
    fun setList(historyList: ArrayList<ParentList>) {
        list.clear()
        list.addAll(historyList)
        notifyDataSetChanged()
    }

    interface onItemClick {
        fun response(item: ParentList, v: View, position: Int)
    }


    @SuppressLint("NotifyDataSetChanged")
    public  fun remove(index:Int){
        list.removeAt(index)
        notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    public  fun getSize() : Int{
   return list.size
    }

}