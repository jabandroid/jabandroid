package com.global.vtg.appview.home.travel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import kotlinx.android.synthetic.main.adapter_airport_code.view.*
import kotlinx.android.synthetic.main.adapter_contact.view.tv_name

class CitySearchAdapter(
    private val mContext: Context,
    private val mLayoutResourceId: Int,

) :
    ArrayAdapter<Finaldata>(mContext, mLayoutResourceId) , Filterable {

    override fun getCount(): Int {
        return appReqList.size
    }
    override fun getItem(position: Int): Finaldata {
        return appReqList[position]
    }
    override fun getItemId(position: Int): Long {
        return -1
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = (mContext as Activity).layoutInflater
            convertView = inflater.inflate(mLayoutResourceId, parent, false)
        }
        try {
//
            convertView!!.tv_name.text = appReqList[position].name
            convertView!!.tv_name_1.text = appReqList[position].city
            convertView.code.text = appReqList[position].code
//            if(!TextUtils.isEmpty(appReqList[position].getPhoto()))
//            convertView.ivProfilePic.setImageURI(Uri.parse(appReqList[position].getPhoto()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return convertView!!
    }


    var countryFilterList =ArrayList<Finaldata> ()
    var finalList =ArrayList<Finaldata> ()
    fun finalList(appReqList: java.util.ArrayList<Finaldata>) {
        finalList.clear()
        finalList.addAll(appReqList)

    }
    private var appReqList: ArrayList<Finaldata> = ArrayList()
    @SuppressLint("NotifyDataSetChanged")
    fun addAll(arr: ArrayList<Finaldata> ){
        appReqList = ArrayList()
        appReqList.addAll(arr)
        finalList(appReqList)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    countryFilterList.clear()
                    countryFilterList.addAll(finalList)
                } else {
                    val resultList = ArrayList<Finaldata> ()
                    for (row in finalList) {

                            if (row.city!!.lowercase().replace(" ","").startsWith(
                                    constraint.toString().lowercase(),true
                                )/*||row.name!!.lowercase().replace(" ","").contains(
                                    constraint.toString().lowercase()
                                )*/
                       ||row.code!!.lowercase().replace(" ","").startsWith(
                            constraint.toString().lowercase(),true
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
            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults
            ) {
                countryFilterList = results.values as ArrayList<Finaldata>
                appReqList.clear()
                appReqList.addAll(
                    countryFilterList
                )
                notifyDataSetChanged()
            }
        }
    }
}