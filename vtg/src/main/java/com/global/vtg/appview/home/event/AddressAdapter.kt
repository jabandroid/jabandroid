package com.global.vtg.appview.home.event

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.vtg.R
import kotlinx.android.synthetic.main.adapter_event_address.view.*


class AddressAdapter(
    private val context: Context?,
    private var appReqList: ArrayList<EventAddress>,
    private var click: onItemClick


) : RecyclerView.Adapter<AddressAdapter.MyViewHolder>(), Filterable {
    class MyViewHolder(val layout: CardView) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_event_address,
            parent,
            false
        ) as CardView
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return appReqList.size
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
//        holder.layout.tv_name.text = appReqList[position].getName()

        holder.layout.tvAddress1.text =
            appReqList[position].addr1 + " " + appReqList[position].addr2
        holder.layout.tvAddress2.text =
            appReqList[position].city + " " + appReqList[position].state
        holder.layout.tvPhone.text =
            appReqList[position].phoneNo
        holder.layout.tvCountry.text =
           if (TextUtils.isEmpty(appReqList[position].zipCode)) "" else appReqList[position].zipCode+" "+  appReqList[position].country

        if (position == 0)
            holder.layout.remove.visibility = View.GONE
        else
            holder.layout.remove.visibility = View.VISIBLE


        holder.layout.edit.setOnClickListener {
            click.onClick(appReqList[position], it, position)
        }
        holder.layout.remove.setOnClickListener {
            click.onClick(appReqList[position], it, position)
        }

        holder.layout.check.setOnClickListener {
            checkedPosition=position
            notifyDataSetChanged()
            if ((it as AppCompatCheckBox).isChecked)
                click.onClick(appReqList[position], it, position)
            else
                click.onClick(appReqList[position], it, -1)
        }

        if (isSubEvent) {
            holder.layout.subButton.visibility = View.GONE
            holder.layout.check.visibility = View.VISIBLE

            holder.layout.check.isChecked =  addressID == appReqList[position].addressID
        }

    }

    private  var checkedPosition: Int=-1

    @SuppressLint("NotifyDataSetChanged")
    public fun remove(index: Int) {
        appReqList.removeAt(index)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun addAll(arr: ArrayList<EventAddress>) {
        appReqList = ArrayList()
        appReqList.addAll(arr)
        notifyDataSetChanged()
    }

    interface onItemClick {
        fun onClick(item: EventAddress, v: View, position: Int)
    }

    var countryFilterList = ArrayList<EventAddress>()
    var finalList = ArrayList<EventAddress>()
    fun finalList(appReqList: java.util.ArrayList<EventAddress>) {
        finalList.clear()
        finalList.addAll(appReqList)

    }

    var isSubEvent: Boolean = false
    var addressID: String = ""

    fun isSubEvent(isSub: Boolean) {
        isSubEvent = isSub

    }

    fun setSelectedID(id:String){
        addressID=id
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    countryFilterList.clear()
                    countryFilterList.addAll(finalList)
                } else {
                    val resultList = ArrayList<EventAddress>()
                    for (row in finalList) {
                        if (row.addr1!!.lowercase().contains(
                                constraint.toString().lowercase()
                            ) || row.addr2!!.lowercase().contains(
                                constraint.toString().lowercase()
                            )
                            || row.addr3!!.lowercase().contains(
                                constraint.toString().lowercase()
                            )
                            || row.city!!.lowercase().contains(
                                constraint.toString().lowercase()
                            ) || row.state!!.lowercase().contains(
                                constraint.toString().lowercase()
                            ) || row.country!!.lowercase().contains(
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

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults
            ) {
                countryFilterList = results.values as ArrayList<EventAddress>
                appReqList.clear()
                appReqList.addAll(
                    countryFilterList
                )
                notifyDataSetChanged()
            }
        }
    }
}