package com.global.vtg.appview.home.vendor

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.appview.home.dashboard.ViewPager2Adapter
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.AppAlertDialog
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.SharedPreferenceUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.vtg.R
import com.vtg.databinding.FragmentVendorDashboardBinding


import kotlinx.android.synthetic.main.fragment_vendor_dashboard.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class VendorHomeFragment : AppFragment(), VendorDashboardAdapter.ClickListener {
    private lateinit var mFragmentBinding: FragmentVendorDashboardBinding
    private val viewModel by viewModel<VendorDashboardViewModel>()
    private lateinit var viewPager2Adapter: ViewPager2Adapter
    private var titleList = ArrayList<String>()
    private val imagesList = arrayListOf(
        R.drawable.ic_woman, R.drawable.ic_qr_code,
        R.drawable.ic_event, R.drawable.ic_qr_code, R.drawable.ic_health_information,
        R.drawable.ic_travel_information
    )

    private var clickedPosition :Int=-1

    override fun getLayoutId(): Int {
        return R.layout.fragment_vendor_dashboard
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentVendorDashboardBinding
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
        TabLayoutMediator(vpDots, viewPager) { _, _ ->
            //Some implementation
        }.attach()

        ivHelp.setOnClickListener {
            addFragmentInStack<Any>(AppFragmentState.F_HELP)
        }


        viewPager2Adapter.setImages(
            arrayListOf(
                "https://i.ibb.co/s6MgjJR/Mask-Group-2.png"

            )
        )

        recyclerView.layoutManager = GridLayoutManager(context, 2)
        titleList = arrayListOf(
            resources.getString(R.string.label_profile),
            resources.getString(R.string.label_scan_qr_code),
            resources.getString(R.string.label_event)
        )
        val dashboardAdapter = VendorDashboardAdapter(
            getAppActivity(), titleList, imagesList
        )
        dashboardAdapter.setListener(this)
        recyclerView.adapter = dashboardAdapter

        viewModel.userConfigLiveData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    (activity as VendorActivity).hideProgressBar()
                    Constants.USER = it.data
                    loadData()

                    if(clickedPosition!=-1){
                        onItemClick(clickedPosition)
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
                    (activity as VendorActivity).hideProgressBar()
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    (activity as VendorActivity).showProgressBar()
                }
            }
        })
    }

    override fun pageVisible() {

    }

    override fun onItemClick(position: Int) {

        if(Constants.USER!=null) {
            clickedPosition=-1
            when (position) {
                0 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_PROFILE)
                }
                1 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_VENDOR_QR_CODE)
                }
                2 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_EVENT_LIST)
                }
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
                ivVendorCountry.visibility = View.INVISIBLE
            } else {
                ivVendorCountry.visibility = View.VISIBLE
                country.let { ivVendorCountry.setCountryForNameCode(it) }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!Constants.USER?.profileUrl.isNullOrEmpty())
            ivVendorProfilePic.setGlideNormalImage(Constants.USER?.profileUrl)
    }
}