package com.global.vtg.appview.home.event

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import kotlinx.android.synthetic.main.adapter_contact.view.*

class ContactSearchAdapter(
    private val mContext: Context,
    private val mLayoutResourceId: Int,

) :
    ArrayAdapter<ContactItem>(mContext, mLayoutResourceId), Filterable {

    override fun getCount(): Int {
        return appReqList.size
    }
    override fun getItem(position: Int): ContactItem {
        return appReqList[position]
    }
    override fun getItemId(position: Int): Long {
        return appReqList[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = (mContext as Activity).layoutInflater
            convertView = inflater.inflate(mLayoutResourceId, parent, false)
        }
        try {

            convertView!!.tv_name.text = appReqList[position].getName()
            convertView.tv_number.text = appReqList[position].getNumber()
            if(!TextUtils.isEmpty(appReqList[position].getPhoto()))
            convertView.ivProfilePic.setImageURI(Uri.parse(appReqList[position].getPhoto()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return convertView!!
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    countryFilterList.clear()
                    countryFilterList.addAll(finalList)
                } else {
                    val resultList = ArrayList<ContactItem> ()
                    for (row in finalList) {
                        if (row.name!!.lowercase().contains(
                                constraint.toString().lowercase()
                            )||row.number!!.lowercase().contains(
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
            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults
            ) {
                countryFilterList = results?.values as ArrayList<ContactItem>
                appReqList.clear()
                appReqList.addAll(
                    countryFilterList
                )
                notifyDataSetChanged()
            }
        }
    }
    var countryFilterList =ArrayList<ContactItem> ()
    var finalList =ArrayList<ContactItem> ()
    fun finalList(appReqList: java.util.ArrayList<ContactItem>) {
        finalList.clear()
        finalList.addAll(appReqList)

    }
    private var appReqList: ArrayList<ContactItem> = ArrayList()
    @SuppressLint("NotifyDataSetChanged")
    fun addAll(arr: ArrayList<ContactItem> ){
        appReqList.addAll(arr)
        finalList(appReqList)
        notifyDataSetChanged()
    }
}