package com.global.vtg.appview.home.vaccinehistory

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.global.vtg.utils.DateUtils.API_DATE_FORMAT_VACCINE
import com.global.vtg.utils.DateUtils.DDMMYYYY
import com.vtg.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class VaccineHistoryAdapter(
    var context: Context
) :
    RecyclerView.Adapter<VaccineHistoryAdapter.DashboardViewHolder>() {
    private var list: ArrayList<VaccineHistory> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        // inflate the item Layout
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_vaccine_history, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return DashboardViewHolder(v) // pass the view to View Holder
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        // set the data in items
        holder.tvVaccineName.text =
            "${position + 1}. ${list[position].type?.let { getVaccineName(it) }}"
        val institute = list[position].instituteId?.let { Constants.getInstituteName(it) }
        holder.tvHospitalName.text = if (TextUtils.isEmpty(institute)) "-" else institute
        holder.tvDate.text = list[position].date?.let {
            DateUtils.convertDate(
                SimpleDateFormat(API_DATE_FORMAT_VACCINE, Locale.US),
                SimpleDateFormat(DDMMYYYY, Locale.US),
                it
            )
        }
        holder.tvStatus.text = list[position].status
        holder.tvBatchNo.text = if (list[position].srId.isNullOrEmpty() || list[position].srId.equals("null")) "-" else list[position].srId
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class DashboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // init the item view's
        var tvVaccineName: TextView = itemView.findViewById(R.id.tvVaccineName)
        var tvHospitalName: TextView = itemView.findViewById(R.id.tvHospitalName)
        var tvDate: TextView = itemView.findViewById(R.id.tvDate)
        var tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        var tvBatchNo: TextView = itemView.findViewById(R.id.tvBatchNoValue)
    }

    fun setList(historyList: ArrayList<VaccineHistory>) {
        list.clear()
        list.addAll(historyList)
        notifyDataSetChanged()
    }

    fun getVaccineName(id: Int): String? {
        val list = Constants.CONFIG?.vaccineType
        if (list?.isNotEmpty() == true) {
            for (vaccine in list) {
                if (vaccine?.id == id) {
                    return vaccine.type
                }
            }
        }
        return ""
    }
}