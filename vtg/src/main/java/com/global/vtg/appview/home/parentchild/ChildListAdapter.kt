package com.global.vtg.appview.home.parentchild

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport
import com.vtg.R
import kotlinx.android.synthetic.main.adapter_child_list.view.*
import kotlinx.android.synthetic.main.fragment_reg_step1.*
import java.util.*
import kotlin.collections.ArrayList


class ChildListAdapter(
    var context: Context,
    var click: onItemClick
) :
    RecyclerView.Adapter<ChildListAdapter.DashboardViewHolder>() {
    private var list: ArrayList<ResUser> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_child_list, parent, false)
        return DashboardViewHolder(v)
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        // set the data in items

        holder.itemView.tvName.text = list[position].firstName + " " + list[position].lastName
        val age = getAge(list[position].dateOfBirth!!)
        if (age != "") {
            holder.itemView.tvAge.visibility = View.VISIBLE

            holder.itemView.tvAge.text = getAge(list[position].dateOfBirth!!)
        } else
            holder.itemView.tvAge.visibility = View.GONE

        if (!TextUtils.isEmpty(list[position].profileUrl))
            holder.itemView.ivProfilePic.setGlideNormalImage(list[position].profileUrl!!)
        holder.itemView.setOnClickListener {
            click.response(list[position], it, position)

        }
        holder.itemView.shared_with.removeAllViews()
        if (list[position].sharedAccount != null) {


            if (list[position].sharedAccount!!.size > 0) {
//                if (list[position].sharedAccount!!.size == 1) {
//                    if (list[position].sharedAccount?.get(0)!!.ParentID!!.toInt() == Constants.USER!!.id) {
//                        Constants.USER!!.sharedAccount!!.removeAt(0)
//
//                    }
//                }

                    for (i in 0 until list[position].sharedAccount!!.size) {

                        if (list[position].sharedAccount?.get(i)!!.ParentID!!.toInt() != Constants.USER!!.id) {
                            holder.itemView.root2.visibility = View.VISIBLE
                            val inflater =
                                holder.itemView.shared_with.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                            val view = inflater.inflate(R.layout.include_child, null)
                            view.ivProfilePic.setGlideNormalImage(
                                list[position].sharedAccount?.get(
                                    i
                                )!!.profilePic!!
                            )
                            holder.itemView.shared_with.addView(view)
                        } else {
                            //Constants.USER!!.sharedAccount!!.removeAt(i)
                        }
                    }

            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class DashboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    fun getID(pos: Int): Int? {
        return list[pos].id
    }

    fun getAge(date: String): String {


        val date = DateUtils.getDate(
            date,
            DateUtils.API_DATE_FORMAT
        )

        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()

        dob.time = date
        var k = today[Calendar.YEAR]
        var t = dob[Calendar.YEAR]
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        var ageInt = ""

        if (age > 0) {
            if (age == 1)
                ageInt = "$age year"
            else
                ageInt = "$age years"
        } else {
            age = (today[Calendar.MONTH] + 1) - (dob[Calendar.MONTH] + 1)
            if (age > 0) {
                if (age == 1)
                    ageInt = "$age month"
                else
                    ageInt = "$age months"
            } else {
                age = today[Calendar.DAY_OF_YEAR] - dob[Calendar.DAY_OF_YEAR]
                if (age == 0)
                    age = 1
                if (age == 1)
                    ageInt = "$age day"
                else
                    ageInt = "$age Days"
            }
        }
        return ageInt
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(historyList: ArrayList<ResUser>) {
        list.clear()
        list.addAll(historyList)
        notifyDataSetChanged()
    }

    interface onItemClick {
        fun response(item: ResUser, v: View, position: Int)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun remove(index: Int) {
        list.removeAt(index)
        notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun getSize(): Int {
        return list.size
    }

}