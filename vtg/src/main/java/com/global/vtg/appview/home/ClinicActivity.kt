package com.global.vtg.appview.home

import android.view.View
import android.view.WindowManager
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppActivity
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.replaceAllFragment
import com.global.vtg.utils.Constants
import com.global.vtg.utils.Constants.isShowing
import com.vtg.R
import kotlinx.android.synthetic.main.activity_authentication.*

class ClinicActivity  : AppActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_vendor_home
    }

    override fun postDataBinding(binding: ViewDataBinding?) {

    }

    override fun initializeComponent() {
        replaceAllFragment<Any>(AppFragmentState.F_CLINIC_DASHBOARD)
    }

    fun showProgressBar() {
        isShowing = true
        progressbar.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun hideProgressBar() {
        isShowing = false
        progressbar.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun onBackPressed() {
        if (!isShowing)
            super.onBackPressed()
    }
}