package com.global.vtg.appview.home.health

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.global.vtg.appview.config.HealthInfo
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.vaccinehistory.VaccineHistory
import com.global.vtg.appview.home.vendor.HealthInformationAdapter
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.vtg.R
import com.vtg.databinding.FragmentHealthInformationBinding
import kotlinx.android.synthetic.main.fragment_health_information.*
import kotlinx.android.synthetic.main.fragment_vaccine_history.*
import kotlinx.android.synthetic.main.fragment_vaccine_history.ivBack
import kotlinx.android.synthetic.main.fragment_vaccine_result.*
import kotlinx.android.synthetic.main.fragment_vaccine_result.rvHealth
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class HealthInformationFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentHealthInformationBinding
    private val viewModel by viewModel<HealthInformationViewModel>()
    val healthList = ArrayList<HealthInfo>()
    lateinit var healthAdapter: HealthInformationAdapter

    override fun getLayoutId(): Int {
        return R.layout.fragment_health_information
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentHealthInformationBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

//        if (isNetworkAvailable(requireActivity())) {
//            viewModel.testHistory()
//        } else {
//            DialogUtils.showSnackBar(
//                requireActivity(),
//                requireActivity().resources.getString(R.string.no_connection)
//            )
//        }

        rvHealth.layoutManager = LinearLayoutManager(context)
        healthAdapter = HealthInformationAdapter(getAppActivity())
        rvHealth.adapter = healthAdapter
        val list = Constants.USER?.healthInfo
        if (!list.isNullOrEmpty()) {
            Collections.sort(list, Comparator<HealthInfo?> { obj1, obj2 ->
                return@Comparator obj2!!.id!!.compareTo(obj1!!.id!!)
            })
            healthList.addAll(list)
            healthAdapter.setHealthList(healthList)
            tvHealthNoData.visibility = View.GONE
        } else {
            tvHealthNoData.visibility = View.VISIBLE
        }
     //   healthAdapter.setTestName(it.data)

        viewModel.testData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                    }


                }
                is Resource.Error -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).showProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
                    }
                }
            }
        })



        viewModel.uploadFile.observe(this, {
            addFragmentInStack<Any>(AppFragmentState.F_UPLOAD_HEALTH_INFORMATION)
        })

    }

    override fun pageVisible() {

    }

    fun refreshList() {
        healthAdapter.setHealthList(Constants.USER?.healthInfo as ArrayList<HealthInfo>)
        updateNoData()
    }

    private fun updateNoData() {
        if (!Constants.USER?.healthInfo.isNullOrEmpty()) {
            healthAdapter.setHealthList(Constants.USER?.healthInfo as ArrayList<HealthInfo>)
            tvHealthNoData.visibility = View.GONE
        } else {
            tvHealthNoData.visibility = View.VISIBLE
        }
    }
}