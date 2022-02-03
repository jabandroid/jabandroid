package com.global.vtg.appview.home.vendor

import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.fragment.popFragment
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.google.android.material.tabs.TabLayoutMediator
import com.vtg.R
import com.vtg.databinding.FragmentVendorScanResultBinding
import kotlinx.android.synthetic.main.fragment_vendor_scan_result.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class VendorScanResultFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentVendorScanResultBinding
    private val viewModel by viewModel<VendorScanResultViewModel>()
    private lateinit var barcodeId: String
    private var countView: Int = 1
    private lateinit var listName: ArrayList<String>

    override fun getLayoutId(): Int {
        return R.layout.fragment_vendor_scan_result
    }

    override fun preDataBinding(arguments: Bundle?) {
        arguments?.let {
            if (arguments.containsKey(Constants.BUNDLE_BARCODE_ID)) {
                barcodeId = arguments.getString(Constants.BUNDLE_BARCODE_ID, "")
            }
        }
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentVendorScanResultBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        ivBackScan.setOnClickListener {
            activity?.onBackPressed()
        }
Constants.testData
        val f: Fragment? =
            activity?.supportFragmentManager?.findFragmentById(R.id.container)
        if (f is VendorQRCodeFragment) {
            f.popFragment()
        }

        if (isNetworkAvailable(activity!!)) {

            viewModel.getDataFromBarcodeId(barcodeId)
        } else {
            DialogUtils.showSnackBar(
                activity,
                activity!!.resources.getString(R.string.no_connection)
            )
        }


        viewModel.scanBarcodeLiveData.observe(this, { it ->
            when (it) {
                is Resource.Success -> {
                    when (activity) {


                        is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
                        else -> (activity as VendorActivity).showProgressBar()
                    }
                 //   (activity as VendorActivity).hideProgressBar()
                    // Load data
                    listName = ArrayList()
                    listName.add("")
                    Constants.SCANNEDUSER = it.data
                    if(it.data.test!=null) {
                        if (it.data.test!!.size > 0) {
                            listName.add(getString(R.string.label_test_history_covid))
                            countView++
                        }
                    }

                    if(it.data.vaccine!=null) {
                        if (it.data.vaccine!!.size > 0) {
                            listName.add(getString(R.string.label_vaccine_taken_list))
                            countView++
                        }
                    }
                    if(it.data.healthInfo!=null) {
                        if (it.data.healthInfo!!.size > 0) {
                            listName.add(getString(R.string.label_health_info_V))
                            countView++
                        }
                    }



                    val pagerAdapter = ViewPagerAdapter(activity,barcodeId,countView,listName)

                    vpPager.adapter = pagerAdapter

                    TabLayoutMediator(vpDots, vpPager) { _, _ ->
                        //Some implementation
                    }.attach()

                    vpPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageScrollStateChanged(state: Int) {}
                        override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                        ) {
                        }

                        override fun onPageSelected(position: Int) {
                            when (position) {
                                0 -> {
                                    tvTitle.text=getString(R.string.label_covid_test_status)
                                }

                                else -> {
                                    tvTitle.text=listName.get(position)

                                }
                            }
                        }
                    })
                }
                is Resource.Error -> {
                    when (activity) {


                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }

                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    when (activity) {


                        is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
                        else -> (activity as VendorActivity).showProgressBar()
                    }
                }
            }
        })


    }

    override fun pageVisible() {

    }

    class ViewPagerAdapter(@NonNull fragmentActivity: FragmentActivity?, b:String, count:Int, listName: ArrayList<String>) :

        FragmentStateAdapter(fragmentActivity!!) {
        @NonNull
        override fun createFragment(position: Int): Fragment {

            when (position) {
                0 -> {
                    var f = VendorScanResultCountFragment()
                    val bundle = Bundle()
                    bundle.putString(Constants.BUNDLE_BARCODE_ID, barcodeId)
                    f.arguments = bundle
                    return f
                }

                else -> {
                    var f = VendorResultFragment()
                    val bundle = Bundle()
                    bundle.putString("name", listName.get(position))
                    f.arguments = bundle
                    return f

                }
            }
        }

        override fun getItemCount(): Int {
            return CARD_ITEM_SIZE
        }

        private val barcodeId: String =b
        private val CARD_ITEM_SIZE=count
        private val listName: ArrayList<String> = listName


    }


//    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) :
//        FragmentStatePagerAdapter(fm) {
//        override fun getCount(): Int = countView
//
//        override fun getItem(position: Int): Fragment {
//
//            when (position) {
//                0 -> {
//                    var f = VendorScanResultCountFragment()
//                    val bundle = Bundle()
//                    bundle.putString(Constants.BUNDLE_BARCODE_ID, barcodeId)
//                    f.arguments = bundle
//                    return f
//                }
//
//                else -> {
//                    var f = VendorResultFragment()
//                    val bundle = Bundle()
//                    bundle.putString("name",listName.get(position))
//                    f.arguments = bundle
//                    return f
//
//                }
//            }
//
//
//        }
//    }

}