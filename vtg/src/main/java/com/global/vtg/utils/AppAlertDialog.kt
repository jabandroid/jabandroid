package com.global.vtg.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.View

import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RadioButton

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.FragmentActivity
import com.global.vtg.model.factory.PreferenceManager
import com.vtg.R
import kotlinx.android.synthetic.main.include_language.view.*


class AppAlertDialog {

    var lanCode: Array<String> = arrayOf("en", "af", "ar","nl", "fr",   "pt","es", "sw")
    private var position: Int = 0
    private lateinit var selecte: RadioButton

    @SuppressLint("SetTextI18n")
    fun showAlert(
        activity: FragmentActivity,
        mInteractor: GetClick,
        message: String,
        btn: String,
        btn2: String
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
        if (!TextUtils.isEmpty(btn)) {
            yes.visibility = View.VISIBLE
            yes.text = btn
        }
        if (!TextUtils.isEmpty(btn2)) {
            exit_btn.visibility = View.VISIBLE
            exit_btn.text = btn2
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


    @SuppressLint("SetTextI18n")
    fun ShowLanguage(
        activity: AppCompatActivity, mInteractor: GetClick
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
        dialog.setCancelable(true)
        val inflater = activity.layoutInflater
        val dialogLayout = inflater.inflate(R.layout.popup_language, null)
        val yes = dialogLayout.findViewById<TextView>(R.id.yes)
        var ll = dialogLayout.findViewById<LinearLayout>(R.id.language_container)

        val language: Array<out String> = activity.resources.getStringArray(R.array.lanuage)
        val language1: Array<out String> = activity.resources.getStringArray(R.array.lanuage_1)
        for (i in 0 until language.size) {
            val child: View = activity.layoutInflater.inflate(R.layout.include_language, null)
            child.tv_1.text = language.get(i)
            child.tv_2.text = language1.get(i)
            child.radio_button.tag = i
            val code = SharedPreferenceUtil.getInstance(activity)
                ?.getData(PreferenceManager.KEY_LAN_CODE, "en")
            var postion = lanCode.indexOf(code)

            if (postion == i) {
                selecte = child.radio_button
                child.radio_button.isChecked = true
            } else {
                child.radio_button.isChecked = false
            }
            child.radio_button.setOnClickListener {

                position = it.tag as Int
                var radio = it as RadioButton
                if (selecte != radio) {
                    selecte.isChecked = false
                } else {
                    radio.isChecked = true
                }


            }
            ll.addView(child)
        }
        yes.setOnClickListener {
            val s = lanCode[position]
            mInteractor.response(s)
            dialog.dismiss()

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