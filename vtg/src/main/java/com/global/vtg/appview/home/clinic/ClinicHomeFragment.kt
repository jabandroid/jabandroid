package com.global.vtg.appview.home.clinic

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.dashboard.DashboardViewModel
import com.global.vtg.imageview.setGlideNormalImage

import com.global.vtg.appview.home.dashboard.ViewPager2Adapter

import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.*
import com.google.android.material.tabs.TabLayoutMediator
import com.vtg.R
import com.vtg.databinding.FragmentDashboardBinding
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.ivHelp
import kotlinx.android.synthetic.main.fragment_dashboard.ivQrCode

import kotlinx.android.synthetic.main.fragment_dashboard.tvCountry
import kotlinx.android.synthetic.main.fragment_dashboard.tvState
import kotlinx.android.synthetic.main.fragment_dashboard.viewPager
import kotlinx.android.synthetic.main.fragment_dashboard.vpDots
import kotlinx.android.synthetic.main.fragment_vendor_dashboard.*

import org.koin.androidx.viewmodel.ext.android.viewModel

class ClinicHomeFragment : AppFragment(), ClinicDashboardAdapter.ClickListener {
    private lateinit var mFragmentBinding: FragmentDashboardBinding
    private val viewModel by viewModel<DashboardViewModel>()
    private lateinit var viewPager2Adapter: ViewPager2Adapter
    private lateinit var viewPagerDash: ClinicDashboardAdapter
    private var titleList = ArrayList<String>()
    private var clickedPosition :Int=-1
    private val imagesList = arrayListOf(
        R.drawable.ic_woman, R.drawable.ic_qr_code,R.drawable.ic_health_info,R.drawable.ic_health_information,
          R.drawable.ic_health_info,R.drawable.
        ic_vaccine_card,
        R.drawable.ic_travel_information,
        R.drawable.ic_event
    )

    override fun getLayoutId(): Int {
        return R.layout.fragment_dashboard
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentDashboardBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        loadData()
        viewModel.getUser()
        ivQrCode.setOnClickListener {
            addFragmentInStack<Any>(AppFragmentState.F_VENDOR_QR_CODE)
        }
        viewPager2Adapter = ViewPager2Adapter(getAppActivity())
        viewPager.adapter = viewPager2Adapter

        viewPagerDash = ClinicDashboardAdapter(getAppActivity())
        viewPager_dash.adapter = viewPagerDash
        TabLayoutMediator(vpDots, viewPager_dash) { _, _ ->
            //Some implementation
        }.attach()

        viewPagerDash.setImages(
            arrayListOf(
                "1","2"
            )
        )

        viewPagerDash.setListenerPager(this)
        ivQrCode.setOnClickListener {
            addFragmentInStack<Any>(AppFragmentState.F_VACCINE_QR_CODE)
        }
        ivHelp.setOnClickListener {
            addFragmentInStack<Any>(AppFragmentState.F_HELP)
        }

        viewPager2Adapter.setImages(
            arrayListOf(
                "https://i.ibb.co/s6MgjJR/Mask-Group-2.png"
            )
        )

        viewModel.userConfigLiveData1.observe(this, {
            when (it) {
                is Resource.Success -> {
                    (activity as ClinicActivity).hideProgressBar()
                    Constants.USER = it.data
                    loadData()
                    if(clickedPosition!=-1){
                        onItemClickMain(clickedPosition)
                    }

                    if(SharedPreferenceUtil.getInstance(getAppActivity())
                            ?.getData(
                                PreferenceManager.KEY_USER_REG,
                                false
                            ) == true
                    ){
                        AppAlertDialog().showRegMessage(
                            activity!! as AppCompatActivity,
                            Constants.USER!!.pin!!

                        )
                    }
                }
                is Resource.Error -> {
                    (activity as ClinicActivity).hideProgressBar()
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    (activity as ClinicActivity).showProgressBar()
                }
            }
        })
    }

    override fun pageVisible() {

    }

    override fun onItemClickMain(position: Int) {

        if(Constants.USER!=null) {
            clickedPosition=-1
            when (position) {
                1 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_PROFILE)
                }
                2 -> {
                    ToastUtils.shortToast(0, "Coming soon")
                    //addFragmentInStack<Any>(AppFragmentState.F_VENDOR_QR_CODE)
                }
                3 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_UPLOAD_DOCUMENT)
                }
                4 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_TEST_UPLOAD)
                }
                5 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_UPLOAD_HEALTH_INFORMATION)
                }
                6 -> {
                    ToastUtils.shortToast(0, "Coming soon")
                }
                7 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_EVENT_LIST)
                }
//            5 -> {
//                addFragmentInStack<Any>(AppFragmentState.F_PAYMENT)
//            }
            }
        }else{
            clickedPosition=position
            viewModel.getUser()
        }
    }



    private fun loadData() {
        if (!Constants.USER?.address.isNullOrEmpty()) {
            tvState.text = Constants.USER?.address?.get(0)?.state
            tvCountry.text = Constants.USER?.address?.get(0)?.country
            var country = Constants.USER?.address?.get(0)?.country
            country = country?.let { getCountryCode(it) }
            if (country.isNullOrEmpty()) {
                ivCountry.visibility = View.INVISIBLE
            } else {
                ivCountry.visibility = View.VISIBLE
                country.let { ivCountry.setCountryForNameCode(it) }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!Constants.USER?.profileUrl.isNullOrEmpty())
            ivProfilePic.setGlideNormalImage(Constants.USER?.profileUrl)
    }
}