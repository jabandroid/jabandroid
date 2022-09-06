package com.global.vtg.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.global.vtg.appview.authentication.registration.TestType
import com.global.vtg.appview.home.travel.VisitReasonAdapter
import com.global.vtg.model.factory.PreferenceManager
import com.vtg.R
import kotlinx.android.synthetic.main.include_language.view.*
import kotlinx.android.synthetic.main.popoup_add_clinic.view.*
import kotlinx.android.synthetic.main.popup_register.view.message
import kotlinx.android.synthetic.main.popup_update_pin.view.*
import kotlinx.android.synthetic.main.popup_update_pin.view.tvPin
import kotlinx.android.synthetic.main.popup_update_pin.view.yes
import kotlinx.android.synthetic.main.poputadd_child.view.*


class AppAlertDialog {

    var lanCode: Array<String> = arrayOf("en", "af", "ar", "nl", "fr", "pt", "es", "sw","tl")
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

    @SuppressLint("SetTextI18n")
    fun updatePin(
        activity: AppCompatActivity, mInteract: GetClick
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
        val dialogLayout = inflater.inflate(R.layout.popup_update_pin, null)


        dialogLayout.yes.setOnClickListener {


            if (!TextUtils.isEmpty(dialogLayout.etPin.text.toString())) {
                if (dialogLayout.etPin.text.toString() == dialogLayout.etConfirm_Pin.text.toString()) {
                    mInteract.response(dialogLayout.etPin.text.toString())
                    dialog.dismiss()
                } else
                    ToastUtils.shortToast(0, activity.getString(R.string.both_pin))
            } else
                ToastUtils.shortToast(0, activity.getString(R.string.enter_pin))


        }

        dialogLayout.etConfirm_Pin.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_check_circle_small_gray,
            0
        )

        dialogLayout.etConfirm_Pin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                var k = dialogLayout.tvPin.text.toString()
                if (!TextUtils.isEmpty(dialogLayout.etPin.text.toString())) {
                    if (dialogLayout.etPin.text.toString() == s.toString()) {
                        dialogLayout.etConfirm_Pin.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_check_circle_small,
                            0
                        )
                    } else
                        dialogLayout.etConfirm_Pin.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_check_circle_small_gray,
                            0
                        )

                }

            }
        })
        builder.setView(dialogLayout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.setView(dialogLayout)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    fun validatePin(
        activity: AppCompatActivity, mInteract: GetClick
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
        val dialogLayout = inflater.inflate(R.layout.popup_verify_pin, null)


        dialogLayout.yes.setOnClickListener {
            if (!TextUtils.isEmpty(dialogLayout.etConfirm_Pin.text.toString())) {
                mInteract.response(dialogLayout.etConfirm_Pin.text.toString())
                dialog.dismiss()
            }


        }

        builder.setView(dialogLayout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.setView(dialogLayout)
        dialog.show()
    }

    @SuppressLint("SetTextI18n", "StringFormatInvalid")
    fun showRegMessage(
        activity: AppCompatActivity, pin: String
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
        val dialogLayout = inflater.inflate(R.layout.popup_register, null)

        dialogLayout.message.text = activity.getString(R.string.thanking_you, pin)
        dialogLayout.yes.setOnClickListener {

            SharedPreferenceUtil.getInstance(activity)
                ?.saveData(
                    PreferenceManager.KEY_USER_REG,
                    false
                )
            dialog.dismiss()

        }

        builder.setView(dialogLayout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.setView(dialogLayout)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    fun addChild(
        activity: AppCompatActivity,  mInteractor: GetClick,
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
        val dialogLayout = inflater.inflate(R.layout.poputadd_child, null)


        dialogLayout.add.setOnClickListener {
            dialog.dismiss()
            mInteractor.response("add")


        }

        dialogLayout.scan.setOnClickListener {
            dialog.dismiss()
            mInteractor.response("scan")


        }



        builder.setView(dialogLayout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.setView(dialogLayout)
        dialog.show()
    }

    fun showVisitReason(
        activity: AppCompatActivity,  mInteractor: GetClick,
    ) {
        val builder = AlertDialog.Builder(activity)
        val dialog: AlertDialog = builder.create()

        val wlp: WindowManager.LayoutParams = dialog.window!!.attributes
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        dialog.window!!.attributes = wlp
        dialog.setCancelable(true)
        val inflater = activity.layoutInflater
        val dialogLayout = inflater.inflate(R.layout.popup_visit_reason, null)
        val list = dialogLayout.findViewById<RecyclerView>(R.id.rv_list)

        var layoutManager = LinearLayoutManager(activity)
        lateinit var mListner: VisitReasonAdapter.OnItemClickListener
        val language1: Array<out String> = activity.resources.getStringArray(R.array.reason)
        mListner = object :
            VisitReasonAdapter.OnItemClickListener {
            @SuppressLint("SimpleDateFormat")
            override fun onItemClick(item: String) {

                    mInteractor.response(item)
                    dialog.dismiss()

            }
        }
        val adapter = VisitReasonAdapter(activity, mListner)


        adapter.addAll(language1)
        list.layoutManager = layoutManager
        list.adapter = adapter


        builder.setView(dialogLayout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.setView(dialogLayout)
        dialog.show()
    }


    fun showVisitAccomodation(
        activity: AppCompatActivity,  mInteractor: GetClick,
    ) {
        val builder = AlertDialog.Builder(activity)
        val dialog: AlertDialog = builder.create()

        val wlp: WindowManager.LayoutParams = dialog.window!!.attributes
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        dialog.window!!.attributes = wlp
        dialog.setCancelable(true)
        val inflater = activity.layoutInflater
        val dialogLayout = inflater.inflate(R.layout.popup_visit_reason, null)
        val list = dialogLayout.findViewById<RecyclerView>(R.id.rv_list)

        var layoutManager = LinearLayoutManager(activity)
        lateinit var mListner: VisitReasonAdapter.OnItemClickListener
        val language1: Array<out String> = activity.resources.getStringArray(R.array.accomodation)
        mListner = object :
            VisitReasonAdapter.OnItemClickListener {
            @SuppressLint("SimpleDateFormat")
            override fun onItemClick(item: String) {

                mInteractor.response(item)
                dialog.dismiss()

            }
        }
        val adapter = VisitReasonAdapter(activity, mListner)


        adapter.addAll(language1)
        list.layoutManager = layoutManager
        list.adapter = adapter


        builder.setView(dialogLayout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.setView(dialogLayout)
        dialog.show()
    }


    fun addClinicName(
        activity: AppCompatActivity,  mInteractor: GetClick,
    ) {
        val builder = AlertDialog.Builder(activity)
        val dialog: AlertDialog = builder.create()

        val wlp: WindowManager.LayoutParams = dialog.window!!.attributes
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        dialog.window!!.attributes = wlp
        dialog.setCancelable(true)
        val inflater = activity.layoutInflater
        val dialogLayout = inflater.inflate(R.layout.popoup_add_clinic, null)


dialogLayout.save.setOnClickListener {
    if(!TextUtils.isEmpty(dialogLayout.et_name.text.toString())) {
        mInteractor.response(dialogLayout.et_name.text.toString())
        dialog.dismiss()
    }
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