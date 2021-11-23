package com.global.vtg.appview.home.vendor

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.SimpleTarget
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragment
import com.global.vtg.base.fragment.popFragment
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.vtg.R
import com.vtg.databinding.FragmentVendorScanResultBinding
import kotlinx.android.synthetic.main.fragment_vendor_scan_result.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class VendorScanResultFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentVendorScanResultBinding
    private val viewModel by viewModel<VendorScanResultViewModel>()
    private lateinit var barcodeId: String

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
        val f: Fragment? =
            activity?.supportFragmentManager?.findFragmentById(R.id.container)
        if (f is VendorQRCodeFragment) {
            f.popFragment()
        }

        viewModel.getDataFromBarcodeId(barcodeId)

        viewModel.detailsLiveData.observe(this, {
            addFragment<Any>(AppFragmentState.F_VENDOR_RESULT)
        })

        viewModel.scanBarcodeLiveData.observe(this, { it ->
            when (it) {
                is Resource.Success -> {
                    (activity as VendorActivity).hideProgressBar()
                    btnShowDetails.visibility = View.VISIBLE
                    // Load data
                    Constants.SCANNEDUSER = it.data
                    if (!Constants.SCANNEDUSER?.firstName.isNullOrEmpty()) {
                        tvName.text =
                            Constants.SCANNEDUSER?.firstName + " " + Constants.SCANNEDUSER?.lastName
                    }
                    val list = Constants.SCANNEDUSER?.document
                    var passportNumber = "-"
                    if (list != null && list.isNotEmpty()) {
                        for (doc in list) {
                            if (doc?.type.equals("Passport")) {
                                passportNumber = if (doc?.identity.isNullOrEmpty()) "-" else doc?.identity?:"-"
                                break
                            }
                        }
                    }
                    tvPassportNumber.text = resources.getString(
                        R.string.label_passport_number,
                        passportNumber
                    )
                    if (it.data.vendorVerify.equals("YES")) {
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
}