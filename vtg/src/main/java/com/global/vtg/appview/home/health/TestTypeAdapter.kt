package com.global.vtg.appview.home.health


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


class TestTypeAdapter(
        private val context: Context?,
        private var click: OnItemClickListener

) : RecyclerView.Adapter<TestTypeAdapter.MyViewHolder>(), Filterable {

    class MyViewHolder(val layout: LinearLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_test_type,
                parent,
                false
        ) as LinearLayout

        return MyViewHolder(view)
    }

     private var appReqList: ArrayList<TestTypeResult> = ArrayList()

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
    fun addAll(arr: ArrayList<TestTypeResult> ){
        appReqList.addAll(arr)
        finalList(appReqList)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(item: TestTypeResult)


    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    countryFilterList.clear()
                    countryFilterList.addAll(finalList)
                } else {
                    val resultList = ArrayList<TestTypeResult> ()
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
                countryFilterList = results?.values as ArrayList<TestTypeResult>
                appReqList.clear()
                appReqList.addAll(
                    countryFilterList
                )
                notifyDataSetChanged()
            }
        }
    }

    var countryFilterList =ArrayList<TestTypeResult> ()
    var finalList =ArrayList<TestTypeResult> ()
    fun finalList(appReqList: java.util.ArrayList<TestTypeResult>) {
        finalList.clear()
        finalList.addAll(appReqList)

    }
}