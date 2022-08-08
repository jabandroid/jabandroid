package com.global.vtg

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppActivity
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.utils.Constants
import com.global.vtg.utils.Constants.isShowing
import com.vtg.R
import kotlinx.android.synthetic.main.activity_authentication.*

class FragmentReplaceActivity : AppActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_home
    }

    override fun postDataBinding(binding: ViewDataBinding?) {

    }

    override fun initializeComponent() {
//        Constants.assistActivity(this)
        val bundle = Bundle()
        bundle.putString(Constants.BUNDLE_ID, intent.extras!!.getString(Constants.BUNDLE_ID))
        bundle.putBoolean(Constants.BUNDLE_FROM_PROFILE, false)
        addFragmentInStack<Any>(AppFragmentState.F_EVENT_EVENT_DETAIL, bundle)


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