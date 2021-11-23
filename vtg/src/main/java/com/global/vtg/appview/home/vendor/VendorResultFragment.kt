package com.global.vtg.appview.home.vendor

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.global.vtg.appview.config.HealthInfo
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.appview.home.vaccinehistory.VaccineHistory
import com.global.vtg.appview.home.vaccinehistory.VaccineHistoryAdapter
import com.global.vtg.base.AppFragment
import com.global.vtg.base.fragment.popFragment
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.google.android.material.tabs.TabLayout
import com.vtg.R
import com.vtg.databinding.FragmentVaccineResultBinding
import kotlinx.android.synthetic.main.fragment_vaccine_result.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class VendorResultFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentVaccineResultBinding
    private val viewModel by viewModel<VendorResultViewModel>()

    val vaccineList = ArrayList<VaccineHistory>()
    val healthList = ArrayList<HealthInfo>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_vaccine_result
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentVaccineResultBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        rvVaccine.layoutManager = LinearLayoutManager(context)
        rvHealth.layoutManager = LinearLayoutManager(context)
        val vaccineHistoryAdapter = VaccineHistoryAdapter(getAppActivity())
        val healthAdapter = HealthInformationAdapter(getAppActivity())

        rvVaccine.adapter = vaccineHistoryAdapter
        rvHealth.adapter = healthAdapter

        if (!Constants.SCANNEDUSER?.healthInfo.isNullOrEmpty()) {
            Constants.SCANNEDUSER?.healthInfo?.let { healthList.addAll(it) }
            healthAdapter.setHealthList(healthList)
            tvNoData.visibility = View.GONE
        } else {
            tvNoData.visibility = View.VISIBLE
        }
        if (!Constants.SCANNEDUSER?.vaccine.isNullOrEmpty()) {
            Constants.SCANNEDUSER?.vaccine?.let { vaccineList.addAll(it) }
            vaccineHistoryAdapter.setList(vaccineList)
            tvNoData.visibility = View.GONE
        } else {
            tvNoData.visibility = View.VISIBLE
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                if (p0?.position == 0) {
                    rvVaccine.visibility = View.VISIBLE
                    rvHealth.visibility = View.GONE
                } else {
                    rvVaccine.visibility = View.GONE
                    rvHealth.visibility = View.VISIBLE
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

        })
    }

    override fun pageVisible() {

    }
}