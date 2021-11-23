package com.global.vtg.appview

import android.content.Intent
import android.os.Handler
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppActivity
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.utils.Constants
import com.global.vtg.utils.SharedPreferenceUtil
import com.vtg.R


class SplashActivity : AppActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun postDataBinding(binding: ViewDataBinding?) {

    }

    override fun initializeComponent() {
        Constants.PREFERENCE_NAME = resources.getString(R.string.app_name)
        val isUserLoggedIn = SharedPreferenceUtil.getInstance(getAppActivity())
            ?.getData(PreferenceManager.KEY_USER_LOGGED_IN, false)
        val role = SharedPreferenceUtil.getInstance(getAppActivity())
            ?.getData(PreferenceManager.KEY_ROLE, "user")

        Handler().postDelayed({
            if (isUserLoggedIn == false)
                startActivity(Intent(this, AuthenticationActivity::class.java))
            else {
                if (role?.equals("user") == true || role?.equals("clinic") == true)
                    startActivity(Intent(this, HomeActivity::class.java))
                else
                    startActivity(Intent(this, VendorActivity::class.java))
            }
            finish()
        }, 3000)
        getConfig()
    }
}