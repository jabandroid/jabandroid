package com.global.vtg.appview.home.vendor


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import cn.iwgang.countdownview.CountdownView
import cn.iwgang.countdownview.DynamicConfig
import com.bumptech.glide.Glide
import com.global.vtg.appview.config.HealthInfo
import com.global.vtg.appview.config.TestInfo
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.fragment.popFragment
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.global.vtg.utils.DialogUtils
import com.vtg.R
import com.vtg.databinding.FragmentVendorScanResultCoutBinding
import kotlinx.android.synthetic.main.fragment_vendor_scan_result_cout.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class VendorScanResultQuickFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentVendorScanResultCoutBinding
    private val viewModel by viewModel<VendorScanResultViewModel>()
    private lateinit var barcodeId: String
    var dynamicConfigBuilder = DynamicConfig.Builder()
    lateinit var mListner: CountdownView.OnCountdownIntervalListener
    val calendar: Calendar = Calendar.getInstance()
    var strVaccine:String=""
    var strTest:String=""
    var listHealth = ArrayList<HealthInfo>()
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

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun initializeComponent(view: View?) {
        /*  ivBackScan.setOnClickListener {
              activity?.onBackPressed()
          }*/
        val f: Fragment? =
            activity?.supportFragmentManager?.findFragmentById(R.id.container)
        if (f is VendorQRCodeFragment) {
            f.popFragment()
        }

       // if (Constants.isSpalsh)
            splash.visibility = View.VISIBLE
     //   else main.visibility = View.VISIBLE



//        view!!.setOnTouchListener { v, event ->
//            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
//                splash.visibility = View.GONE
//                main.visibility = View.VISIBLE
//            }
//            true
//        }



        var datetimer = ""
        if (Constants.SCANNEDUSER!!.test!=null&&Constants.SCANNEDUSER!!.test!!.size > 0) {
            val list = Constants.SCANNEDUSER!!.test!!
            Constants.SCANNEDUSER?.test?.let {
                Collections.sort(list, Comparator<TestInfo?> { obj1, obj2 ->
                    val d1 = DateUtils.getDate(
                        obj1!!.date!!,
                        DateUtils.API_DATE_FORMAT_VACCINE
                    )
                    val d2 = DateUtils.getDate(
                        obj2!!.date!!,
                        DateUtils.API_DATE_FORMAT_VACCINE
                    )
                    return@Comparator d2.compareTo(d1)
                })
            }


            if (list[0].result?.lowercase().equals("Negative".lowercase())) {



                Glide.with(getAppActivity())
                    .asGif()
                    .load(R.mipmap.gif_verified)
                    .into(iv_status_splash)
            } else if (list[0].result?.lowercase().equals("Positive".lowercase())) {

                Glide.with(getAppActivity())
                    .asBitmap()
                    .load(R.drawable.ic_drawable_cross)
                    .into(iv_status_splash)
            } else if (list[0].result?.lowercase().equals("Invalid".lowercase())) {

                Glide.with(getAppActivity())
                    .asBitmap()
                    .load(R.drawable.ic_drawable_pending)
                    .into(iv_status_splash)
            } else if (list[0].result?.lowercase().equals("NA".lowercase())) {

                Glide.with(getAppActivity())
                    .asBitmap()
                    .load(R.drawable.ic_drawable_na)
                    .into(iv_status_splash)
            } else {

                Glide.with(getAppActivity())
                    .asBitmap()
                    .load(R.drawable.ic_drawable_pending)
                    .into(iv_status_splash)
            }


        } else {

            if (Constants.SCANNEDUSER!!.healthInfo!=null&&listHealth.size > 0) {
                listHealth = Constants.SCANNEDUSER!!.healthInfo!!
                Constants.SCANNEDUSER?.healthInfo?.let {
                    Collections.sort(listHealth, Comparator<HealthInfo?> { obj1, obj2 ->

                        val d1 = DateUtils.getDate(
                            obj1!!.date!!,
                            DateUtils.API_DATE_FORMAT_VACCINE
                        )
                        val d2 = DateUtils.getDate(
                            obj2!!.date!!,
                            DateUtils.API_DATE_FORMAT_VACCINE
                        )
                        return@Comparator d2.compareTo(d1)
                    })


                    if (listHealth[0].result?.lowercase().equals("Negative".lowercase())) {

                        Glide.with(getAppActivity())
                            .asGif()
                            .load(R.mipmap.gif_verified)
                            .into(iv_status_splash)
                    } else if (listHealth[0].result?.lowercase().equals("Positive".lowercase())) {

                        Glide.with(getAppActivity())
                            .asBitmap()
                            .load(R.drawable.ic_drawable_cross)
                            .into(iv_status_splash)
                    } else if (listHealth[0].result?.lowercase().equals("Invalid".lowercase())) {

                        Glide.with(getAppActivity())
                            .asBitmap()
                            .load(R.drawable.ic_drawable_pending)
                            .into(iv_status_splash)
                    } else if (listHealth[0].result?.lowercase().equals("NA".lowercase())) {

                        Glide.with(getAppActivity())
                            .asBitmap()
                            .load(R.drawable.ic_drawable_na)
                            .into(iv_status_splash)
                    } else {

                        Glide.with(getAppActivity())
                            .asBitmap()
                            .load(R.drawable.ic_drawable_pending)
                            .into(iv_status_splash)
                    }
                }
            }
        }

        viewModel.testDataDetails.observe(this, {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        is VendorActivity -> (activity as VendorActivity).hideProgressBar()
                    }

                    if (listHealth.size > 0)
                        if (!TextUtils.isEmpty(listHealth[0].test)) {
                            tvTest.text = "Test |"
                            for (item in it.data.tests!!) {
                                if (item.id!!.equals(listHealth[0].test)) {
                                    tvType.text = item.name
                                    break
                                }
                            }

                            tvTest.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.green
                                )
                            )

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

//        if (Constants.isSpalsh) {
//            Handler().postDelayed({
//                Constants.isSpalsh=false
//                    splash.visibility = View.GONE
//                 main.visibility = View.VISIBLE
//            }, 3500)
//        }


    }


    override fun pageVisible() {

    }

}