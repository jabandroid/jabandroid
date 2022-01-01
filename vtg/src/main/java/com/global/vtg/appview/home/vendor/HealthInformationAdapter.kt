package com.global.vtg.appview.home.vendor

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
            DateUtils.formatDateUTCToLocal(
                it,
                DateUtils.API_DATE_FORMAT_VACCINE,
                DateUtils.DDMMYYYY

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

        holder.itemView.setOnClickListener{
            if (!list[position].documentLink.isNullOrEmpty())
                openFile(list[position].documentLink.toString())
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