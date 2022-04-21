package com.global.vtg.appview.home.help

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.utils.Constants
import com.global.vtg.utils.SharedPreferenceUtil
import com.global.vtg.utils.AppAlertDialog
import com.vtg.BuildConfig
import com.vtg.R
import com.vtg.databinding.FragmentHelpBinding
import kotlinx.android.synthetic.main.fragment_help.*
import kotlinx.android.synthetic.main.fragment_vaccine_qr_code.*
import kotlinx.android.synthetic.main.fragment_vaccine_qr_code.ivBack
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class HelpFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentHelpBinding
    private val viewModel by viewModel<HelpViewModel>()
    var bitmap: Bitmap? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_help
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentHelpBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {

        tvVersion.text= BuildConfig.VERSION_NAME
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        viewModel.policy.observe(this, {
            viewHtml(Constants.PRIVACY_POLICY)
        })

        viewModel.terms.observe(this, {
            viewHtml(Constants.TERMS_CONDITION)
        })

        viewModel.setting.observe(this, {
         when(it){
             "language"->{
                 AppAlertDialog().ShowLanguage(
                     activity!! as AppCompatActivity,
                     object : AppAlertDialog.GetClick {
                         override fun response(type: String) {
                             val locale = Locale(type)
                             Locale.setDefault(locale)
                             val resources: Resources = activity!!.resources
                             val config: Configuration = resources.configuration
                             config.setLocale(locale)
                             resources.updateConfiguration(config, resources.displayMetrics)
                             val role = SharedPreferenceUtil.getInstance(getAppActivity())
                                 ?.getData(PreferenceManager.KEY_ROLE, "user")

                             SharedPreferenceUtil.getInstance(getAppActivity())!!.saveData(PreferenceManager.KEY_LAN_CODE,type)
                             if (role?.equals("user") == true )
                                 startActivity(Intent(activity!!, HomeActivity::class.java))
                             else if  (role?.equals("clinic") == true)
                                 startActivity(Intent(activity!!, ClinicActivity::class.java))
                             else
                                 startActivity(Intent(activity!!, VendorActivity::class.java))
                         }
                     }

                 )
             }
             "about_us"->{

             }
         }
        })
    }

    override fun pageVisible() {

    }
}