package com.global.vtg.appview.home.travel


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.global.vtg.appview.authentication.registration.TestTypeResult
import com.vtg.R
import kotlinx.android.synthetic.main.adapter_test_type.view.*


class VisitReasonAdapter(
        private val context: Context?,
        private var click: OnItemClickListener

) : RecyclerView.Adapter<VisitReasonAdapter.MyViewHolder>() {

    class MyViewHolder(val layout: LinearLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_test_type,
                parent,
                false
        ) as LinearLayout

        return MyViewHolder(view)
    }

     private var appReqList: ArrayList<String> = ArrayList()

    override fun getItemCount(): Int {
        return appReqList.size
    }




    @SuppressLint("SimpleDateFormat", "SetTextI18n")

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.layout.name.text=appReqList[position]

        holder.layout.root.setOnClickListener{
            click.onItemClick(appReqList[position])
        }
//        holder.layout.setOnClickListener {
//            click.onItemClick(appReqList[position])
//        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAll(arr: Array<out String>){
        appReqList.addAll(arr)

        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(item: String)


    }





}