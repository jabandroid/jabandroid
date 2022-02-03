package com.global.vtg.appview.home.testHistory

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView


import kotlin.collections.ArrayList

import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import com.global.vtg.appview.authentication.registration.TestTypeResult
import com.vtg.R
import kotlinx.android.synthetic.main.adapter_test_type.view.*


class TestKitAdapter(
        private val context: Context?,
        private var click: OnItemClickListener

) : RecyclerView.Adapter<TestKitAdapter.MyViewHolder>(), Filterable {

    class MyViewHolder(val layout: LinearLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_test_type,
                parent,
                false
        ) as LinearLayout

        return MyViewHolder(view)
    }

     private var appReqList: ArrayList<TestKitResult> = ArrayList()

    override fun getItemCount(): Int {
        return appReqList.size
    }




    @SuppressLint("SimpleDateFormat", "SetTextI18n")

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.layout.name.text=appReqList[position].name

        holder.layout.root.setOnClickListener{
            click.onItemClick(appReqList[position])
        }
//        holder.layout.setOnClickListener {
//            click.onItemClick(appReqList[position])
//        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAll(arr: ArrayList<TestKitResult> ){
        appReqList.addAll(arr)
        finalList(appReqList)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(item: TestKitResult)


    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    countryFilterList.clear()
                    countryFilterList.addAll(finalList)
                } else {
                    val resultList = ArrayList<TestKitResult> ()
                    for (row in appReqList) {
                        if (row.name!!.lowercase().contains(
                                constraint.toString().lowercase()
                            )
                        ) {
                            resultList.add(row)
                        }
                    }
                    countryFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = countryFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                countryFilterList = results?.values as ArrayList<TestKitResult>
                appReqList.clear()
                appReqList.addAll(
                    countryFilterList
                )
                notifyDataSetChanged()
            }
        }
    }

    var countryFilterList =ArrayList<TestKitResult> ()
    var finalList =ArrayList<TestKitResult> ()
    fun finalList(appReqList: java.util.ArrayList<TestKitResult>) {
        finalList.clear()
        finalList.addAll(appReqList)

    }
}