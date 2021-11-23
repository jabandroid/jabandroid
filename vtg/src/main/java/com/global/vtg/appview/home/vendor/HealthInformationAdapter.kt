package com.global.vtg.appview.home.vendor

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.global.vtg.appview.config.HealthInfo
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.vtg.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HealthInformationAdapter(
    var context: Context
) :
    RecyclerView.Adapter<HealthInformationAdapter.HealthViewHolder>() {
    var list: ArrayList<HealthInfo> = ArrayList()
    private lateinit var listener: ClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthViewHolder {
        // inflate the item Layout
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_vendor_health_info, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return HealthViewHolder(v) // pass the view to View Holder
    }

    override fun onBindViewHolder(holder: HealthViewHolder, position: Int) {
        // set the data in items
        val institute = list[position].instituteId?.let { Constants.getInstituteName(it) }
        holder.tvHospitalName.text = if (TextUtils.isEmpty(institute)) "-" else institute

        holder.tvDate.text = list[position].date?.let {
            DateUtils.convertDate(
                SimpleDateFormat(DateUtils.API_DATE_FORMAT_VACCINE, Locale.US),
                SimpleDateFormat(DateUtils.DDMMYYYY, Locale.US),
                it
            )
        }
        holder.tvBatchNo.text = if (list[position].srId.isNullOrEmpty()) "-" else list[position].srId
        when (list[position].result) {
            "positive" -> {
                holder.tvStatusValue.text =
                    holder.itemView.resources.getString(R.string.label_positive)
                holder.ivStatus.setImageResource(R.drawable.ic_not_verified)
                holder.llHealth.setBackgroundResource(R.drawable.red_border)
            }
            "negative" -> {
                holder.tvStatusValue.text =
                    holder.itemView.resources.getString(R.string.label_negative)
                holder.ivStatus.setImageResource(R.drawable.ic_check_circle)
                holder.llHealth.setBackgroundResource(R.drawable.green_border)
            }
            else -> {
                holder.tvStatusValue.text =
                    holder.itemView.resources.getString(R.string.label_pending)
                holder.ivStatus.setImageResource(R.drawable.ic_warning)
                holder.llHealth.setBackgroundResource(R.drawable.yellow_border)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class HealthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // init the item view's
        var tvHospitalName: TextView = itemView.findViewById(R.id.tvHospitalName)
        var tvDate: TextView = itemView.findViewById(R.id.tvDate)
        var tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        var tvStatusValue: TextView = itemView.findViewById(R.id.tvStatusValue)
        var ivStatus: ImageView = itemView.findViewById(R.id.ivStatus)
        var llHealth: LinearLayout = itemView.findViewById(R.id.llHealth)
        var tvBatchNo: TextView = itemView.findViewById(R.id.tvBatchNoValue)
    }

    fun setListener(listener: ClickListener) {
        this.listener = listener
    }

    fun setHealthList(historyList: ArrayList<HealthInfo>) {
        list.clear()
        list.addAll(historyList)
        notifyDataSetChanged()
    }

    interface ClickListener {
        fun onItemClick(position: Int)
    }
}