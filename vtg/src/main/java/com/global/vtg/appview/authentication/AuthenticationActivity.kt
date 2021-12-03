package com.global.vtg.appview.authentication

import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppActivity
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.replaceAllFragment
import com.global.vtg.utils.Constants.USER_TYPE
import com.global.vtg.utils.Constants.isShowing
import com.vtg.R
import kotlinx.android.synthetic.main.activity_authentication.*

class AuthenticationActivity : AppActivity() {
    private var userType: Int? = 1

    override fun getLayoutId(): Int {
        return R.layout.activity_authentication
    }

    override fun postDataBinding(binding: ViewDataBinding?) {

    }

    override fun initializeComponent() {
        userType = intent?.getIntExtra(USER_TYPE, 1)
        replaceAllFragment<Any>(AppFragmentState.F_SIGN_IN)

    }

    fun showProgressBar() {
        isShowing = true
        progressbar.visibility = View.VISIBLE
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
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