package com.global.vtg.appview

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.ViewDataBinding
import com.global.vtg.FragmentReplaceActivity
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppActivity
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.utils.Constants
import com.global.vtg.utils.Constants.BUNDLE_ID
import com.global.vtg.utils.SharedPreferenceUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.vtg.R

import io.branch.referral.Branch
import java.util.*


class SplashActivity : AppActivity() {
    private  var  isEvent : Boolean = false
    private  var  id : String = ""
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
        val code = SharedPreferenceUtil.getInstance(getAppActivity())
            ?.getData(PreferenceManager.KEY_LAN_CODE, "en")

        val locale = Locale(code)
        Locale.setDefault(locale)
        val resources: Resources = resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
               // Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            SharedPreferenceUtil.getInstance(getAppActivity())
                ?.saveData(PreferenceManager.KEY_TOKEN, token)

            // Log and toast
//            val msg = getString(R.string.msg_token_fmt, token)
//            Log.d(TAG, msg)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })

        Handler().postDelayed({
            if (isUserLoggedIn == false)
                startActivity(Intent(this, AuthenticationActivity::class.java))
            else {
                if (role?.equals("user") == true )
                    startActivity(Intent(this, HomeActivity::class.java))
                else if   (role?.equals("clinic") == true)
                    startActivity(Intent(this, ClinicActivity::class.java))
                else
                    startActivity(Intent(this, VendorActivity::class.java))

                if(isEvent){
                    startActivity(Intent(this, FragmentReplaceActivity::class.java).putExtra(BUNDLE_ID,id))
                }
            }
            finish()
        }, 3000)
        getConfig()
//
//        Firebase.dynamicLinks
//            .getDynamicLink(intent)
//            .addOnSuccessListener(this) { pendingDynamicLinkData ->
//                // Get deep link from result (may be null if no link is found)
//                var deepLink: Uri? = null
//                if(pendingDynamicLinkData!=null)
//                deepLink = pendingDynamicLinkData.link
//
//            }
//            .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }
    }


    override fun onStart() {
        super.onStart()
        Branch.getInstance().initSession({ referringParams, error ->
            if (error == null) {
                Log.i("BRANCH SDK", referringParams.toString())
                if(referringParams.has("event_id")){
                    var idWork:String=referringParams.getString("event_id")
                    if(!TextUtils.isEmpty(idWork)) {
                        isEvent=true
                        id=idWork
                    }
                }
                // Retrieve deeplink keys from 'referringParams' and evaluate the values to determine where to route the user
                // Check '+clicked_branch_link' before deciding whether to use your Branch routing logic
            } else {
                Log.i("BRANCH SDK", error.message)
            }
        }, this.intent.data, this)    }


}