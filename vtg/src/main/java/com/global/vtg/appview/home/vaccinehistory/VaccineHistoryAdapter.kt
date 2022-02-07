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
import com.global.vtg.utils.DateUtils.DDMMYYYY
import com.vtg.R
import kotlinx.android.synthetic.main.recycler_view_vaccine_history.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.widget.Toast

import android.content.ActivityNotFoundException

import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import java.io.File


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
                DDMMYYYY,

                )
        }

        holder.tvBatchNo.text =
            if (list[position].srId.isNullOrEmpty() || list[position].srId.equals("null")) "-" else list[position].srId
        if (list[position].dose != null) {
            var valid: Int = 0
            for (data in Constants.CONFIG?.doses!!) {
                if (data!!.id.equals(list[position].dose))
                {
                    if(data.name!!.contains("Dose 2")){
                        valid=1
                        holder.tvStatus.text=data.name
                        break
                    }else  if(data.name!!.contains("Dose 1")){
                        holder.tvStatus.text=data.name
                        valid=3
                        break
                    }else  if(data.name!!.contains("Booster")||data.name!!.contains("2")){
                        holder.tvStatus.text=data.name
                        valid=2
                        break
                    }
                }
            }


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
            }
        } else {
            holder.itemView.ivStatus.setImageResource(0)
            holder.itemView.llVaccine.setBackgroundResource(R.drawable.red_border)
        }

        holder.itemView.setOnClickListener {
            if (!list[position].documentLink.isNullOrEmpty())
                openFile(list[position].documentLink.toString())
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

    private fun openFile(url: String) {
        try {
            val uri: Uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW)
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword")
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf")
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel")
            } else if (url.toString().contains(".zip")) {
                // ZIP file
                intent.setDataAndType(uri, "application/zip")
            } else if (url.toString().contains(".rar")) {
                // RAR file
                intent.setDataAndType(uri, "application/x-rar-compressed")
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf")
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav")
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif")
            } else if (url.toString().contains(".jpg") || url.toString()
                    .contains(".jpeg") || url.toString().contains(".png")
            ) {
                // JPG file
                intent.setDataAndType(uri, "image/*")
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain")
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") ||
                url.toString().contains(".mpeg") || url.toString()
                    .contains(".mpe") || url.toString().contains(".mp4") || url.toString()
                    .contains(".avi")
            ) {
                // Video files
                intent.setDataAndType(uri, "video/*")
            } else {
                intent.setDataAndType(uri, "*/*")
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "No application found which can open the file",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}