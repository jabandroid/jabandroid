package com.global.vtg.appview.home.testHistory

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.global.vtg.appview.authentication.registration.TestType
import com.global.vtg.appview.config.TestInfo
import com.global.vtg.appview.home.vaccinehistory.VaccineHistory
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.global.vtg.utils.SharedPreferenceUtil
import com.vtg.R
import kotlinx.android.synthetic.main.recycler_vendor_health_info.view.*
import java.util.*
import kotlin.collections.ArrayList

//

class TestInformationAdapter(
    var context: Context,
    var click: ClickListener
) :
    RecyclerView.Adapter<TestInformationAdapter.HealthViewHolder>() {
    var list: ArrayList<TestInfo> = ArrayList()
    var testType: TestType = TestType()
    private lateinit var listener: ClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthViewHolder {
        // inflate the item Layout
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_vendor_health_info, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return HealthViewHolder(v) // pass the view to View Holder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HealthViewHolder, position: Int) {
        // set the data in items
        val institute = list[position].instituteId?.let { Constants.getInstituteName(it) }
        holder.tvHospitalName.text = if (TextUtils.isEmpty(institute)) "-" else institute

        if (!TextUtils.isEmpty(list[position].test)) {
            if (list[position].test.equals("1")) {
                holder.tvTest.text = context.getString(R.string.rtpcr)
            } else {
                holder.tvTest.text = context.getString(R.string.label_rapid)
            }

        } else {
            holder.tvTest.text = ""
        }

        if (!TextUtils.isEmpty(list[position].kit)) {
            holder.itemView.testKit.visibility = View.VISIBLE
            holder.itemView.tvKit.text = list[position].kit
        } else {
            holder.itemView.testKit.visibility = View.GONE
        }

        //  if (TextUtils.isEmpty(list[position].test)) "-" else list[position].test
        holder.testRoot.visibility = View.VISIBLE
        holder.tvDate.text = list[position].date?.let {
            DateUtils.formatDateUTCToLocal(
                it,
                DateUtils.API_DATE_FORMAT_VACCINE,
                true
            )
        }


        if (list[position].addedBy!!.contains("clinic"))
            holder.itemView.addedBy.setImageResource(R.drawable.ic_clinic)
        else
            holder.itemView.addedBy.setImageResource(R.drawable.ic_user)

        holder.tvBatchNo.text =
            if (list[position].srId.isNullOrEmpty() || list[position].srId.equals("null")) "-" else list[position].srId
        val role = SharedPreferenceUtil.getInstance(context)
            ?.getData(PreferenceManager.KEY_ROLE, "user")



        if (role?.equals("user") == true) {
            holder.tvStatusValue.visibility=View.VISIBLE
            holder.tvStatus.visibility=View.VISIBLE
        }else{
            holder.tvStatusValue.visibility=View.GONE
            holder.tvStatus.visibility=View.GONE
        }
        if (list[position].result != null) {
            when (list[position].result!!.lowercase(Locale.getDefault())) {
                "positive" -> {
                    holder.tvStatusValue.text =
                        holder.itemView.resources.getString(R.string.label_positive)
//                    holder.ivStatus.setImageResource(R.drawable.ic_drawable_cross)
//                    holder.llHealth.setBackgroundResource(R.drawable.red_border)
                }
                "negative" -> {
                    holder.tvStatusValue.text =
                        holder.itemView.resources.getString(R.string.label_negative)
//                    holder.ivStatus.setImageResource(R.drawable.ic_drawable_tick)
//                    holder.llHealth.setBackgroundResource(R.drawable.green_border)
                }
                "invalid" -> {
                    holder.tvStatusValue.text =
                      "Invalid"
//                    holder.ivStatus.setImageResource(R.drawable.ic_drawable_pending)
//                    holder.llHealth.setBackgroundResource(R.drawable.green_border)
                }
                "na" -> {
                    holder.tvStatusValue.text =
                        "NA"
//                    holder.ivStatus.setImageResource(R.drawable.ic_drawable_na)
//                    holder.llHealth.setBackgroundResource(R.drawable.green_border)
                }
                else -> {
                    holder.tvStatusValue.text =
                       "NA"
//                    holder.ivStatus.setImageResource(R.drawable.ic_drawable_pending)
//                    holder.llHealth.setBackgroundResource(R.drawable.yellow_border)
                }
            }
        } else {
            holder.tvStatusValue.text =
                "NA"
            holder.ivStatus.setImageResource(R.drawable.ic_drawable_pending)
            holder.llHealth.setBackgroundResource(R.drawable.yellow_border)
        }

        holder.itemView.setOnClickListener {
//            if (!list[position].documentLink.isNullOrEmpty())
//                openFile(list[position].documentLink.toString())
            click.response(list[position], it, position)
        }

    }
    interface onItemClick {
        fun response(item: TestInfo, v:View, position: Int)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    inner class HealthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // init the item view's
        var tvHospitalName: TextView = itemView.findViewById(R.id.tvHospitalName)
        var tvTest: TextView = itemView.findViewById(R.id.tvTestName)
        var testRoot: LinearLayout = itemView.findViewById(R.id.test_root)
        var tvDate: TextView = itemView.findViewById(R.id.tvDate)
        var tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        var tvStatusValue: TextView = itemView.findViewById(R.id.tvStatusValue)
        var ivStatus: ImageView = itemView.findViewById(R.id.ivStatus)
        var llHealth: RelativeLayout = itemView.findViewById(R.id.llHealth)
        var tvBatchNo: TextView = itemView.findViewById(R.id.tvBatchNoValue)
    }

    fun setListener(listener: ClickListener) {
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setHealthList(historyList: ArrayList<TestInfo>) {
        list.clear()
        list.addAll(historyList)
        notifyDataSetChanged()
    }

    fun setTestName(nam: TestType) {
        testType = nam

    }

    interface ClickListener {
        fun response(item: TestInfo, v:View, position: Int)
    }


}