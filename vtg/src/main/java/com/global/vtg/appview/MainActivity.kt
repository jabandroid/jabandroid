package com.global.vtg.appview

import android.content.Intent
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.ViewDataBinding
import com.google.android.material.navigation.NavigationView
import com.global.vtg.App
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.base.AppActivity
import com.global.vtg.utils.Constants
import com.global.vtg.utils.Constants.USER_TYPE
import com.global.vtg.utils.KeyboardUtils
import com.vtg.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun postDataBinding(binding: ViewDataBinding?) {

    }

    override fun initializeComponent() {
        btnVendor.setOnClickListener {
            val authIntent = Intent(this, AuthenticationActivity::class.java)
            authIntent.putExtra(USER_TYPE, Constants.USERTYPE.VENDOR.type)
            startActivity(authIntent)
        }

        btnUser.setOnClickListener {
            val authIntent = Intent(this, AuthenticationActivity::class.java)
            authIntent.putExtra(USER_TYPE, Constants.USERTYPE.USER.type)
            startActivity(authIntent)
        }
    }
}
