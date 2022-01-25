package com.global.vtg.appview.home.vendor

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.global.vtg.appview.config.HealthInfo
import com.global.vtg.appview.config.TestInfo
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.appview.home.testHistory.TestInformationAdapter
import com.global.vtg.appview.home.vaccinehistory.VaccineHistory
import com.global.vtg.appview.home.vaccinehistory.VaccineHistoryAdapter
import com.global.vtg.base.AppFragment
import com.global.vtg.base.fragment.popFragment
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.google.android.material.tabs.TabLayout
import com.vtg.R
import com.vtg.databinding.FragmentVaccineResultBinding
import kotlinx.android.synthetic.main.fragment_health_information.*
import kotlinx.android.synthetic.main.fragment_vaccine_result.*
import kotlinx.android.synthetic.main.fragment_vaccine_result.rvHealth
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class VendorResultFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentVaccineResultBinding
    private val viewModel by viewModel<VendorResultViewModel>()

    val vaccineList = ArrayList<VaccineHistory>()
    val testList = ArrayList<TestInfo>()
    var healthList = ArrayList<HealthInfo>()
    var name: String = ""

    override fun getLayoutId(): Int {
        return R.layout.fragment_vaccine_result
    }

    override fun preDataBinding(arguments: Bundle?) {
        name = requireArguments().getString("name").toString()
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentVaccineResultBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        /*ivBack.setOnClickListener {
            activity?.onBackPressed()
        }*/
        rvVaccine.layoutManager = LinearLayoutManager(context)
        rvHealth.layoutManager = LinearLayoutManager(context)
        rvTest.layoutManager = LinearLayoutManager(context)
        val vaccineHistoryAdapter = VaccineHistoryAdapter(getAppActivity())
        val testHistoryAdapter = TestInformationAdapter(getAppActivity())
        val healthAdapter = HealthInformationAdapter(getAppActivity())

        rvVaccine.adapter = vaccineHistoryAdapter
        rvHealth.adapter = healthAdapter
        rvTest.adapter = testHistoryAdapter


        if (isNetworkAvailable(requireActivity())) {
            viewModel.testHistory()
        } else {
            DialogUtils.showSnackBar(
                requireActivity(),
                requireActivity().resources.getString(R.string.no_connection)
            )
        }

        viewModel.testData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        is VendorActivity -> (activity as VendorActivity).hideProgressBar()
                    }


                    healthAdapter.setTestName(it.data)

                    if (!TextUtils.isEmpty(name)) {
                        // tvTitle.text = name
                        when (name) {
                            getString(R.string.label_health_info_V) -> {
                                rvVaccine.visibility = View.GONE
                                rvTest.visibility = View.GONE
                                rvHealth.visibility = View.VISIBLE
                                if (!Constants.SCANNEDUSER?.healthInfo.isNullOrEmpty()) {
                                    Constants.SCANNEDUSER?.healthInfo?.let {
                                        healthList = Constants.SCANNEDUSER?.healthInfo!!


                                        Collections.sort(healthList, Comparator<HealthInfo?> { obj1, obj2 ->
                                            return@Comparator obj2!!.id!!.compareTo(obj1!!.id!!)
                                        })


                                    }
                                    healthAdapter.setHealthList(healthList)
                                    tvNoData.visibility = View.GONE
                                } else {
                                    tvNoData.visibility = View.VISIBLE
                                }
                            }

                            getString(R.string.label_vaccine_taken_list) -> {
                                rvVaccine.visibility = View.VISIBLE
                                rvHealth.visibility = View.GONE
                                rvTest.visibility = View.GONE

                                if (!Constants.SCANNEDUSER?.vaccine.isNullOrEmpty()) {

                                    Constants.SCANNEDUSER?.vaccine?.let {
                                        val list = it
                                        Collections.sort(list, Comparator<VaccineHistory?> { obj1, obj2 ->
                                            val d1= DateUtils.getDate(  obj1!!.date!!,
                                                DateUtils.API_DATE_FORMAT_VACCINE)
                                            val d2= DateUtils.getDate(  obj2!!.date!!,
                                                DateUtils.API_DATE_FORMAT_VACCINE)
                                            return@Comparator d2.compareTo(d1)
                                        })
                                        vaccineList.addAll(list) }
                                    vaccineHistoryAdapter.setList(vaccineList)
                                    tvNoData.visibility = View.GONE
                                } else {
                                    tvNoData.visibility = View.VISIBLE
                                }
                            }

                            getString(R.string.label_test_history_covid) -> {
                                rvVaccine.visibility = View.GONE
                                rvHealth.visibility = View.GONE
                                rvTest.visibility = View.VISIBLE
                                if (!Constants.SCANNEDUSER?.test.isNullOrEmpty()) {
                                    Constants.SCANNEDUSER?.test?.let {
                                        val list = it
                                        Collections.sort(list, Comparator<TestInfo?> { obj1, obj2 ->
                                            val d1= DateUtils.getDate(  obj1!!.date!!,
                                                DateUtils.API_DATE_FORMAT_VACCINE)
                                            val d2= DateUtils.getDate(  obj2!!.date!!,
                                                DateUtils.API_DATE_FORMAT_VACCINE)
                                            return@Comparator d2.compareTo(d1)
                                        })
                                        testList.addAll(list) }
                                    testHistoryAdapter.setHealthList(testList)
                                    tvNoData.visibility = View.GONE
                                } else {
                                    tvNoData.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        is VendorActivity -> (activity as VendorActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).showProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
                        is VendorActivity -> (activity as VendorActivity).showProgressBar()
                    }
                }
            }
        })



//        if (!Constants.SCANNEDUSER?.healthInfo.isNullOrEmpty()) {
//            Constants.SCANNEDUSER?.healthInfo?.let { healthList.addAll(it) }
//            healthAdapter.setHealthList(healthList)
//            tvNoData.visibility = View.GONE
//        } else {
//            tvNoData.visibility = View.VISIBLE
//        }
//        if (!Constants.SCANNEDUSER?.vaccine.isNullOrEmpty()) {
//            Constants.SCANNEDUSER?.vaccine?.let { vaccineList.addAll(it) }
//            vaccineHistoryAdapter.setList(vaccineList)
//            tvNoData.visibility = View.GONE
//        } else {
//            tvNoData.visibility = View.VISIBLE
//        }
        tabLayout.visibility = View.GONE
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