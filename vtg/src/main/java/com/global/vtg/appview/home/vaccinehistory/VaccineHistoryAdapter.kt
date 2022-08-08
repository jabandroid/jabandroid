package com.global.vtg.appview.home.vaccinehistory

import android.annotation.SuppressLint
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
import com.vtg.R
import kotlinx.android.synthetic.main.recycler_view_vaccine_history.view.*


class VaccineHistoryAdapter(
    var context: Context,
    var click : onItemClick
) :
    RecyclerView.Adapter<VaccineHistoryAdapter.DashboardViewHolder>() {
    private var list: ArrayList<VaccineHistory> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_vaccine_history, parent, false)
        return DashboardViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        // set the data in items
        holder.tvVaccineName.text =
            "${position + 1}. ${list[position].type?.let { getVaccineName(it) }}"
        val institute = list[position].instituteId?.let { Constants.getInstituteName(it) }
        holder.tvHospitalName.text = if (TextUtils.isEmpty(institute)) "-" else institute
        holder.tvDate.text = list[position].date?.let {
            DateUtils.formatDateUTCToLocal(
                it,
                API_DATE_FORMAT_VACCINE,
                true
            )
        }

        if (list[position].addedBy != null) {
            if (list[position].addedBy!!.contains("clinic"))
                holder.itemView.addedBy.setImageResource(R.drawable.ic_clinic)
            else
                holder.itemView.addedBy.setImageResource(R.drawable.ic_user)
        }

        holder.tvBatchNo.text =
            if (list[position].srId.isNullOrEmpty() || list[position].srId.equals("null")) "-" else list[position].srId
        if (list[position].dose != null) {
            var valid: Int = 0
            try {
                for (data in Constants.CONFIG?.doses!!) {
                    if (data!!.id.equals(list[position].dose)) {
                        if (data.name!!.contains("Dose 2")) {
                            valid = 1
                            holder.tvStatus.text = data.name
                            break
                        } else if (data.name.contains("Dose 1")) {
                            holder.tvStatus.text = data.name
                            valid = 3
                            break
                        } else if (data.name.contains("Booster") || data.name.contains("2")) {
                            holder.tvStatus.text = data.name
                            valid = 2
                            break
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

/*
            when (valid) {
                1 -> {
                    holder.itemView.ivStatus.setImageResource(R.drawable.ic_check_circle_yellow)
                    holder.itemView.llVaccine.setBackgroundResource(R.drawable.yellow_border)
                }
               2 -> {
                    holder.itemView.ivStatus.setImageResource(R.drawable.ic_check_circle)
                    holder.itemView.llVaccine.setBackgroundResource(R.drawable.green_border)
                }
                3 -> {
                    holder.itemView.ivStatus.setImageResource(R.drawable.ic_warning)
                    holder.itemView.ivStatus.setColorFilter(ContextCompat.getColor(context
                        , R.color.sea_green))
                    holder.itemView.llVaccine.setBackgroundResource(R.drawable.blue_border)
                }
                else -> {
                    holder.itemView.ivStatus.setImageResource(R.drawable.ic_not_verified)
                    holder.itemView.llVaccine.setBackgroundResource(R.drawable.red_border)
                }
            }*/
        } else {
//            holder.itemView.ivStatus.setImageResource(0)
//            holder.itemView.llVaccine.setBackgroundResource(R.drawable.red_border)
        }

        holder.itemView.setOnClickListener {
            click.response(list[position], it, position)

        }

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

    interface onItemClick {
        fun response(item: VaccineHistory, v:View, position: Int)
    }

    fun getVaccineName(id: Int): String? {
        val list = Constants.CONFIG?.vaccineType
        if (list?.isNotEmpty() == true) {
            for (vaccine in list) {
                if (vaccine.id == id) {
                    return vaccine.type
                }
            }
        }
        return ""
    }


}