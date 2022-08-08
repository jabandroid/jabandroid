package com.global.vtg.appview.home.health

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.config.HealthInfo
import com.global.vtg.appview.home.vendor.HealthInformationAdapter
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.utils.Constants
import com.global.vtg.utils.ToastUtils
import com.vtg.R
import com.vtg.databinding.FragmetHealthInfoDashboardBinding
import kotlinx.android.synthetic.main.fragment_health_information.ivBack
import kotlinx.android.synthetic.main.fragment_health_information.ivProfilePic
import kotlinx.android.synthetic.main.fragment_health_information.tvUserNam
import kotlinx.android.synthetic.main.fragmet_health_info_dashboard.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HealthInformationDashboardFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmetHealthInfoDashboardBinding
    private val viewModel by viewModel<HealthInformationViewModel>()
    val healthList = ArrayList<HealthInfo>()
    private lateinit var healthAdapter: HealthInformationAdapter

    override fun getLayoutId(): Int {
        return R.layout.fragmet_health_info_dashboard
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmetHealthInfoDashboardBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    @SuppressLint("SetTextI18n")
    override fun initializeComponent(view: View?) {
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        if (!Constants.USER?.profileUrl.isNullOrEmpty())
            ivProfilePic.setGlideNormalImage(Constants.USER?.profileUrl)


        if(!TextUtils.isEmpty(Constants.USER?.firstName!!)&&!TextUtils.isEmpty(Constants.USER?.lastName!!))
            tvUserNam.text = Constants.USER?.firstName!!.replace("null","") + " " + Constants.USER?.lastName!!.replace("null","")

        healthHistory.setOnClickListener {
            addFragmentInStack<Any>(AppFragmentState.F_HEALTH_INFORMATION)
        }
        healthBio.setOnClickListener {
            ToastUtils.shortToast(0,"Coming soon")
        }
        healthAllergies.setOnClickListener {
            ToastUtils.shortToast(0,"Coming soon")
        }
        healthchronic.setOnClickListener {
            ToastUtils.shortToast(0,"Coming soon")
        }
        medication.setOnClickListener {
            ToastUtils.shortToast(0,"Coming soon")
        }
        donor.setOnClickListener {
            ToastUtils.shortToast(0,"Coming soon")
        }
        appointments.setOnClickListener {
            ToastUtils.shortToast(0,"Coming soon")
        }
        doctor.setOnClickListener {
            ToastUtils.shortToast(0,"Coming soon")
        }
        emergency.setOnClickListener {
            ToastUtils.shortToast(0,"Coming soon")
        }
        benefits.setOnClickListener {
            ToastUtils.shortToast(0,"Coming soon")
        }
        vaccine.setOnClickListener {
            addFragmentInStack<Any>(AppFragmentState.F_VACCINE_HISTORY)
        }
        scanqr.setOnClickListener {
            addFragmentInStack<Any>(AppFragmentState.F_VACCINE_QR_CODE)
        }

    }

    override fun pageVisible() {

    }


}