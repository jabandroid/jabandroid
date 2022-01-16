package com.global.vtg.appview.home.vendor

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import cn.iwgang.countdownview.CountdownView
import cn.iwgang.countdownview.DynamicConfig
import cn.iwgang.countdownview.DynamicConfig.BackgroundInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.SimpleTarget
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragment
import com.global.vtg.base.fragment.popFragment
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.global.vtg.utils.DialogUtils
import com.vtg.R

import com.vtg.databinding.FragmentVendorScanResultCoutBinding

import kotlinx.android.synthetic.main.fragment_vendor_scan_result_cout.*


import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class VendorScanResultCountFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentVendorScanResultCoutBinding
    private val viewModel by viewModel<VendorScanResultViewModel>()
    private lateinit var barcodeId: String
    var dynamicConfigBuilder = DynamicConfig.Builder()
    lateinit var mListner: CountdownView.OnCountdownIntervalListener
    val calendar: Calendar = Calendar.getInstance()
    override fun getLayoutId(): Int {
        return R.layout.fragment_vendor_scan_result_cout
    }

    override fun preDataBinding(arguments: Bundle?) {
        arguments?.let {
            if (arguments.containsKey(Constants.BUNDLE_BARCODE_ID)) {
                barcodeId = arguments.getString(Constants.BUNDLE_BARCODE_ID, "")
            }
        }
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentVendorScanResultCoutBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
      /*  ivBackScan.setOnClickListener {
            activity?.onBackPressed()
        }*/
        val f: Fragment? =
            activity?.supportFragmentManager?.findFragmentById(R.id.container)
        if (f is VendorQRCodeFragment) {
            f.popFragment()
        }


        mListner = CountdownView.OnCountdownIntervalListener { dtype, time ->

            val dft =
                SimpleDateFormat(DateUtils.API_DATE_FORMAT_SERVER, Locale.getDefault())
            dft.timeZone = TimeZone.getTimeZone("UTC")
            val currentS = dft.format(Date())
            val currentTime = dft.parse(currentS)


            val diffInMillisec: Long = calendar.timeInMillis - currentTime.time
            val hours: Long = TimeUnit.MILLISECONDS.toHours(diffInMillisec)
            val dynamicConfigBuilder = DynamicConfig.Builder()
            val backgroundInfo = BackgroundInfo()
            //  var hours = diffInSec % 24

            Log.e("hr", "hr" + hours)
            if (hours in 2..5) {
                backgroundInfo.color =
                    ContextCompat.getColor(requireActivity(), R.color.til_tint_color)
            } else if (hours < 1) {
                backgroundInfo.color = ContextCompat.getColor(requireActivity(), R.color.red)
            } else {
                backgroundInfo.color = ContextCompat.getColor(requireActivity(), R.color.green)
            }
            dynamicConfigBuilder.setBackgroundInfo(backgroundInfo)
            countDown.dynamicShow(dynamicConfigBuilder.build())
        }

//
//        viewModel.getDataFromBarcodeId(barcodeId)
//
////        viewModel.detailsLiveData.observe(this, {
////            addFragment<Any>(AppFragmentState.F_VENDOR_RESULT)
////        })
//
//        viewModel.scanBarcodeLiveData.observe(this, { it ->
//            when (it) {
//                is Resource.Success -> {
//                    (activity as VendorActivity).hideProgressBar()
                    btnShowDetails.visibility = View.VISIBLE

                    if (!TextUtils.isEmpty( Constants.SCANNEDUSER!!.note)) {
                        val df =
                            SimpleDateFormat(DateUtils.API_DATE_FORMAT_SERVER, Locale.getDefault())
                        var date =
                            DateUtils.getDate(  Constants.SCANNEDUSER!!.note!!, DateUtils.API_DATE_FORMAT_SERVER)

                        calendar.time = date
                        calendar.timeZone = TimeZone.getTimeZone("UTC")
                        calendar.add(Calendar.HOUR, 72)

                        val dft =
                            SimpleDateFormat(DateUtils.API_DATE_FORMAT_SERVER, Locale.getDefault())
                        dft.timeZone = TimeZone.getTimeZone("UTC")
                        val currentS = dft.format(Date())
                        val currentTime = df.parse(currentS)


                        if (currentTime!!.time <= calendar.timeInMillis) {
                            countDown.visibility = View.VISIBLE
                            val diif = calendar.timeInMillis - currentTime.time
                            countDown.start(diif) // Millisecond

                        }

                        countDown.setOnCountdownEndListener {
                            countDown.stop()
                        }



                        countDown.setOnCountdownIntervalListener(
                            1L,
                            mListner
                        )


                    } else
                        countDown.visibility = View.GONE

                    if (!Constants.SCANNEDUSER?.firstName.isNullOrEmpty()) {
                        tvName.text =
                            Constants.SCANNEDUSER?.firstName + " " + Constants.SCANNEDUSER?.lastName
                    }
                    val list = Constants.SCANNEDUSER?.document
                    var passportNumber = "-"
                    if (list != null && list.isNotEmpty()) {
                        for (doc in list) {
                            if (doc?.type.equals("Passport")) {
                                passportNumber =
                                    if (doc?.identity.isNullOrEmpty()) "-" else doc?.identity ?: "-"
                                break
                            }
                        }
                    }
                    tvPassportNumber.text = resources.getString(
                        R.string.label_passport_number,
                        passportNumber
                    )
                    if ( Constants.SCANNEDUSER!!.vendorVerify.equals("YES")) {
                        Glide.with(getAppActivity()).asGif().load(R.mipmap.gif_verified)
                            .into(object :
                                SimpleTarget<GifDrawable?>() {
                                override fun onResourceReady(
                                    resource: GifDrawable,
                                    transition: com.bumptech.glide.request.transition.Transition<in GifDrawable?>?
                                ) {
                                    resource.start()
                                    ivStatus.setImageDrawable(resource)
                                }
                            })
                    } else {
                        Glide.with(getAppActivity()).asGif().load(R.mipmap.gif_error).into(object :
                            SimpleTarget<GifDrawable?>() {
                            override fun onResourceReady(
                                resource: GifDrawable,
                                transition: com.bumptech.glide.request.transition.Transition<in GifDrawable?>?
                            ) {
                                resource.start()
                                ivStatus.setImageDrawable(resource)
                            }
                        })
                    }
//                }
//                is Resource.Error -> {
//                    when (activity) {
//
//
//                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
//                        else -> (activity as VendorActivity).hideProgressBar()
//                    }
//
//                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
//                }
//                is Resource.Loading -> {
//                    when (activity) {
//
//
//                        is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
//                        else -> (activity as VendorActivity).showProgressBar()
//                    }
//                }
//            }
//        })
    }

    override fun pageVisible() {

    }
}