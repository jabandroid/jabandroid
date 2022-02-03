package com.tslogistics.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.View

import android.view.WindowManager

import android.widget.TextView

import androidx.fragment.app.FragmentActivity
import com.vtg.R


class AppAlertDialog {

    @SuppressLint("SetTextI18n")
    fun showAlert(
        activity: FragmentActivity, mInteractor: GetClick,  message:String, btn:String, btn2: String
    ) {
        val builder = AlertDialog.Builder(activity)
        val dialog: AlertDialog = builder.create()
        dialog.setCancelable(true)
        val wlp: WindowManager.LayoutParams = dialog.window!!.attributes
        if (dialog.window != null)
            wlp.windowAnimations = R.style.DialogSlideAnim
        //wlp.gravity = Gravity.BOTTOM
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        dialog.window!!.attributes = wlp
        dialog.setCancelable(false)
        val inflater = activity.layoutInflater
        val dialogLayout = inflater.inflate(R.layout.popup_alert, null)
        val exit_btn = dialogLayout.findViewById<TextView>(R.id.exit_btn)
        val yes = dialogLayout.findViewById<TextView>(R.id.yes)
        val title = dialogLayout.findViewById<TextView>(R.id.title)
        title.text = message
        if(!TextUtils.isEmpty(btn)) {
            yes.visibility = View.VISIBLE
            yes.text=btn
        }
        if(!TextUtils.isEmpty(btn2)) {
            exit_btn.visibility = View.VISIBLE
            exit_btn.text=btn2
        }
        exit_btn.setOnClickListener { dialog.dismiss() }
        yes.setOnClickListener {
            dialog.dismiss()
            mInteractor.response("end")
        }

        builder.setView(dialogLayout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.setView(dialogLayout)
        dialog.show()
    }



    interface GetClick {
        fun response(type: String) {}
    }


}