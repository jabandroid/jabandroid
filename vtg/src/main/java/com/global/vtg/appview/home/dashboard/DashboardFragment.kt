package com.global.vtg.appview.home.dashboard

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.SharedPreferenceUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.vtg.R
import com.vtg.databinding.FragmentDashboardBinding
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.ivProfilePic
import kotlinx.android.synthetic.main.fragment_dashboard.tvCountry
import kotlinx.android.synthetic.main.fragment_dashboard.tvState
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : AppFragment(), DashboardAdapter.ClickListener {
    private lateinit var mFragmentBinding: FragmentDashboardBinding
    private val viewModel by viewModel<DashboardViewModel>()
    private lateinit var viewPager2Adapter: ViewPager2Adapter
    private var titleList = ArrayList<String>()

    private val imagesList = arrayListOf(
        R.drawable.ic_woman, R.drawable.ic_vaccine_history,
        R.drawable.ic_vaccine_card, R.drawable.ic_qr_code, R.drawable.ic_health_information,
        R.drawable.ic_travel_information
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
        viewPager2Adapter = ViewPager2Adapter(getAppActivity())
        viewPager.adapter = viewPager2Adapter
        TabLayoutMediator(vpDots, viewPager) { _, _ ->
            //Some implementation
        }.attach()

        ivQrCode.setOnClickListener {
            addFragmentInStack<Any>(AppFragmentState.F_VACCINE_QR_CODE)
        }
        ivHelp.setOnClickListener {
            addFragmentInStack<Any>(AppFragmentState.F_HELP)
        }

        viewPager2Adapter.setImages(
            arrayListOf(
                "https://i.ibb.co/s6MgjJR/Mask-Group-2.png",
                "https://i.ibb.co/s6MgjJR/Mask-Group-2.png",
                "https://i.ibb.co/s6MgjJR/Mask-Group-2.png"
            )
        )
        titleList = arrayListOf(
            resources.getString(R.string.label_profile),
            resources.getString(R.string.label_my_vaccine_history),
            resources.getString(R.string.label_my_vaccine_card),
            resources.getString(R.string.label_my_qr_code),
            resources.getString(R.string.label_health_information),
            resources.getString(R.string.label_travel_information)
        )
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        val dashboardAdapter = DashboardAdapter(
            getAppActivity(), titleList, imagesList
        )
        dashboardAdapter.setListener(this)
        recyclerView.adapter = dashboardAdapter

        viewModel.userConfigLiveData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    (activity as HomeActivity).hideProgressBar()
                    Constants.USER = it.data
                    loadData()
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

    override fun onItemClick(position: Int) {
        when (position) {
            0 -> {
                addFragmentInStack<Any>(AppFragmentState.F_PROFILE)
            }
            1 -> {
                addFragmentInStack<Any>(AppFragmentState.F_VACCINE_HISTORY)
            }
            2 -> {
                addFragmentInStack<Any>(AppFragmentState.F_VACCINE_CARD)
            }
            3 -> {
                addFragmentInStack<Any>(AppFragmentState.F_VACCINE_QR_CODE)
            }
            4 -> {
                addFragmentInStack<Any>(AppFragmentState.F_HEALTH_INFORMATION)
            }
            5 -> {
                addFragmentInStack<Any>(AppFragmentState.F_PAYMENT)
            }
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