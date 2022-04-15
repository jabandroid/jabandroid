package com.global.vtg.appview.home.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import com.global.vtg.appview.config.ResConfig
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.*
import com.global.vtg.wscoroutine.ApiInterface
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.vtg.R
import com.vtg.databinding.FragmentDashboardBinding
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.ivProfilePic
import kotlinx.android.synthetic.main.fragment_dashboard.tvCountry
import kotlinx.android.synthetic.main.fragment_dashboard.tvState
import okhttp3.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException

class DashboardFragment : AppFragment(), ViewPagerDashAdapter.ClickListener {
    private lateinit var mFragmentBinding: FragmentDashboardBinding
    private val viewModel by viewModel<DashboardViewModel>()
    private lateinit var viewPager2Adapter: ViewPager2Adapter
    private lateinit var viewPagerDash: ViewPagerDashAdapter

    private var clickedPosition: Int = -1
    private val client = OkHttpClient()


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

    @SuppressLint("HardwareIds")
    override fun initializeComponent(view: View?) {
        loadData()
        viewModel.getUser()
        viewPager2Adapter = ViewPager2Adapter(getAppActivity())
        viewPager.adapter = viewPager2Adapter

        viewPagerDash = ViewPagerDashAdapter(getAppActivity())
        viewPager_dash.adapter = viewPagerDash
        TabLayoutMediator(vpDots, viewPager_dash) { _, _ ->
            //Some implementation
        }.attach()

        viewPagerDash.setImages(
            arrayListOf(
                "1", "2"
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
                    (activity as HomeActivity).hideProgressBar()
                    Constants.USER = it.data
                    loadData()

                    if(!TextUtils.isEmpty(SharedPreferenceUtil.getInstance(getAppActivity())
                            ?.getData(PreferenceManager.KEY_TOKEN, ""))){
                        val v =JsonObject()
                        v.addProperty("userId",   Constants.USER!!.id.toString())
                        v.addProperty("deviceId", Settings.Secure.getString(activity!!.contentResolver,
                            Settings.Secure.ANDROID_ID))
                        v.addProperty("token",SharedPreferenceUtil.getInstance(getAppActivity())
                            ?.getData(PreferenceManager.KEY_TOKEN, ""))
                        v.addProperty("deviceType","android")
                        viewModel.updateToken(v)
                    }
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
                    (activity as HomeActivity).hideProgressBar()
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    (activity as HomeActivity).showProgressBar()
                }
            }
        })
    }

    override fun pageVisible() {

    }


    private fun loadData() {
        if (!Constants.USER?.address.isNullOrEmpty()) {
            tvState.text = Constants.USER?.address?.get(0)?.state
            tvCountry.text = Constants.USER?.address?.get(0)?.country
            var country = Constants.USER?.address?.get(0)?.country
            country = country?.let { getCountryCode(it) }
            if (country.isNullOrEmpty()) {
                ivCountry.visibility = View.GONE
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

    override fun onItemClickMain(position: Int) {

        if (Constants.USER != null) {
            clickedPosition=-1
            when (position) {
                1 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_PROFILE)
                }
                2 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_VACCINE_HISTORY)
                }
                3 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_VACCINE_QR_CODE)

                }
                4 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_TEST_BASE)

                }
                5 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_VACCINE_CARD)
                }
                6 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_HEALTH_INFORMATION)
                }
                11 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_EVENT_LIST)
                }
                else -> {
                    ToastUtils.shortToast(0, "Coming soon")
                }

//            5 -> {
//                addFragmentInStack<Any>(AppFragmentState.F_PAYMENT)
//            }
        }
        }else{
            clickedPosition = position
            viewModel.getUser()
        }
    }


}