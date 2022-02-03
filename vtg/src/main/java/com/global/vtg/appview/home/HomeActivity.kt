package com.global.vtg.appview.home

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppActivity
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.base.fragment.replaceAllFragment
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.utils.Constants
import com.global.vtg.utils.Constants.isShowing
import com.global.vtg.utils.SharedPreferenceUtil
import com.vtg.R
import kotlinx.android.synthetic.main.activity_authentication.*

class HomeActivity : AppActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_home
    }

    override fun postDataBinding(binding: ViewDataBinding?) {

    }

    override fun initializeComponent() {
//        Constants.assistActivity(this)
        var step = SharedPreferenceUtil.getInstance(getAppActivity())
            ?.getData(
                PreferenceManager.KEY_FIRST_STEP,
                false
            )
        if(step!!) {

            replaceAllFragment<Any>(AppFragmentState.F_DASHBOARD)

        }
        else {


            addFragmentInStack<Any>(AppFragmentState.F_REG_STEP1)
        }
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