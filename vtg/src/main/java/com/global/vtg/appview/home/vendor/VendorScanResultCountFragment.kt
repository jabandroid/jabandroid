package com.global.vtg.appview.home.vendor


import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.StyleSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import cn.iwgang.countdownview.CountdownView
import cn.iwgang.countdownview.DynamicConfig
import cn.iwgang.countdownview.DynamicConfig.BackgroundInfo
import com.bumptech.glide.Glide
import com.global.vtg.appview.config.HealthInfo
import com.global.vtg.appview.config.TestInfo
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.appview.home.vaccinehistory.VaccineHistory
import com.global.vtg.base.AppFragment
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.*
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.google.gson.JsonObject
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
    var strVaccine: String = ""
    var strTest: String = ""
    var openDoc: String = ""
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
//        val f: Fragment? =
//            activity?.supportFragmentManager?.findFragmentById(R.id.container)
//        if (f is VendorQRCodeFragment) {
//            f.popFragment()
//        }

        if (Constants.isSpalsh)
            splash.visibility = View.VISIBLE
        else main.visibility = View.VISIBLE



        view!!.setOnTouchListener { v, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                splash.visibility = View.GONE
                main.visibility = View.VISIBLE
            }
            true
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
                backgroundInfo.color =
                    ContextCompat.getColor(requireActivity(), R.color.red)
            } else {
                backgroundInfo.color =
                    ContextCompat.getColor(requireActivity(), R.color.green)
            }
            dynamicConfigBuilder.setBackgroundInfo(backgroundInfo)
            countDown.dynamicShow(dynamicConfigBuilder.build())
        }

        ivProfilePic.setGlideNormalImage(Constants.SCANNEDUSER!!.profileUrl)

        if (!Constants.SCANNEDUSER?.firstName.isNullOrEmpty()) {
            tvName.text =
                Constants.SCANNEDUSER?.firstName + " " + Constants.SCANNEDUSER?.lastName
        }


        val list_Doc = Constants.SCANNEDUSER?.document

        if (!Constants.SCANNEDUSER?.dateOfBirth.isNullOrEmpty()) {
            try {
                val str = SpannableStringBuilder(
                    "DOB: " + DateUtils.formatDate(
                        Constants.SCANNEDUSER?.dateOfBirth!!,
                        DateUtils.API_DATE_FORMAT
                    )
                )
                str.setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    4,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )


                tvDob.text = str
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (list_Doc != null && list_Doc.isNotEmpty()) {
            for (doc in list_Doc) {
                when {
                    doc?.type.equals("DL", true) -> {
                        tvDl.visibility = View.VISIBLE

                        val str = SpannableStringBuilder("DL: " + doc?.identity)
                        str.setSpan(
                            StyleSpan(Typeface.BOLD),
                            0,
                            3,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        tvDl.text = str
                    }

                    doc?.type.equals("ID", true) -> {

                        val str = SpannableStringBuilder("UID: " + doc?.identity)
                        str.setSpan(
                            StyleSpan(Typeface.BOLD),
                            0,
                            4,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        tvId.visibility = View.VISIBLE
                        tvId.text = str
                    }
                }
            }
        }


        var datetimer = ""
        iv_status_splash.visibility = View.INVISIBLE
        if (Constants.SCANNEDUSER!!.test != null && Constants.SCANNEDUSER!!.test!!.size > 0) {
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
            tvStatus.text = list[0].result
            if (!TextUtils.isEmpty(list[0].test)) {
                tvTest.text = "COVID Test | "
                if (list[0].test.equals("1")) {
                    tvType.text = getString(R.string.rtpcr)
                } else {
                    tvType.text = getString(R.string.label_rapid)
                }
                tvTest.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.green
                    )
                )
            }
            if (!TextUtils.isEmpty(list[0].kit)) {
                date_kit.visibility = View.VISIBLE
                date_kit.text = list[0].kit
            } else {
                date_kit.visibility = View.GONE
            }

            strTest = list[0].documentLink.toString()
            val institute = list[0].instituteId?.let { Constants.getInstituteName(it) }
            date_test.text = "$institute " + list[0].date?.let {
                DateUtils.formatDateUTCToLocal(
                    it,
                    DateUtils.API_DATE_FORMAT_VACCINE,
                    true
                )
            }

            date_test.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.green
                )
            )


            if (list[0].addedBy!!.contains("clinic"))
                addedBy.setImageResource(R.drawable.ic_clinic)
            else
                addedBy.setImageResource(R.drawable.ic_user)


            var result = list[0].result
            if (TextUtils.isEmpty(list[0].result) || list[0].result.equals("null"))
                result = "Pending"
            tvType.text = tvType.text.toString() + " | " + result
            if (list[0].result?.lowercase().equals("Negative".lowercase())) {

                datetimer = list[0].date.toString()
                Glide.with(getAppActivity()).asBitmap().load(R.drawable.ic_drawable_tick)
                    .into(ivStatus_1)
                Glide.with(getAppActivity())
                    .asBitmap().load(R.drawable.ic_drawable_tick)
                    .into(iv_status_splash)
            } else if (list[0].result?.lowercase().equals("Positive".lowercase())) {
                Glide.with(getAppActivity()).asBitmap().load(R.drawable.ic_drawable_cross)
                    .into(ivStatus_1)
                Glide.with(getAppActivity())
                    .asBitmap()
                    .load(R.drawable.ic_drawable_cross)
                    .into(iv_status_splash)
            } else if (list[0].result?.lowercase().equals("Invalid".lowercase())) {
                Glide.with(getAppActivity()).asBitmap().load(R.drawable.ic_drawable_pending)
                    .into(ivStatus_1)
                Glide.with(getAppActivity())
                    .asBitmap()
                    .load(R.drawable.ic_drawable_pending)
                    .into(iv_status_splash)
            } else if (list[0].result?.lowercase().equals("NA".lowercase())) {
                Glide.with(getAppActivity()).asBitmap().load(R.drawable.ic_drawable_na)
                    .into(ivStatus_1)
                Glide.with(getAppActivity())
                    .asBitmap()
                    .load(R.drawable.ic_drawable_na)
                    .into(iv_status_splash)
            } else {
                Glide.with(getAppActivity()).asBitmap().load(R.drawable.ic_drawable_pending)
                    .into(ivStatus_1)
                Glide.with(getAppActivity())
                    .asBitmap()
                    .load(R.drawable.ic_drawable_pending)
                    .into(iv_status_splash)
            }


        } else {

            if (Constants.SCANNEDUSER!!.healthInfo != null && Constants.SCANNEDUSER!!.healthInfo!!.size > 0) {
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

                    strTest = listHealth[0].documentLink.toString()
                    val institute =
                        listHealth[0].instituteId?.let { Constants.getInstituteName(it) }
                    date_test.text = "$institute " + listHealth[0].date?.let {
                        DateUtils.formatDateUTCToLocal(
                            it,
                            DateUtils.API_DATE_FORMAT_VACCINE,
                            true
                        )
                    }

                    if (!TextUtils.isEmpty(listHealth[0].testName)) {
                        tvTest.text = listHealth[0].testName

                        tvTest.setTextColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.green
                            )
                        )
                    }

                    date_test.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.green
                        )
                    )

                    var result = listHealth[0].result
                    if (TextUtils.isEmpty(listHealth[0].result) || listHealth[0].result.equals("null"))
                        result = "Pending"
                    tvType.text = tvType.text.toString() + " | " + result

                    if (listHealth[0].addedBy!!.contains("clinic"))
                        addedBy.setImageResource(R.drawable.ic_clinic)
                    else
                        addedBy.setImageResource(R.drawable.ic_user)

                    if (listHealth[0].result?.lowercase().equals("Negative".lowercase())) {
                        datetimer = listHealth[0].date.toString()
                        Glide.with(getAppActivity()).asBitmap().load(R.drawable.ic_drawable_tick)
                            .into(ivStatus_1)
                        Glide.with(getAppActivity())
                            .asBitmap().load(R.drawable.ic_drawable_tick)
                            .into(iv_status_splash)
                    } else if (listHealth[0].result?.lowercase().equals("Positive".lowercase())) {
                        Glide.with(getAppActivity()).asBitmap().load(R.drawable.ic_drawable_cross)
                            .into(ivStatus_1)
                        Glide.with(getAppActivity())
                            .asBitmap()
                            .load(R.drawable.ic_drawable_cross)
                            .into(iv_status_splash)
                    } else if (listHealth[0].result?.lowercase().equals("Invalid".lowercase())) {
                        Glide.with(getAppActivity()).asBitmap().load(R.drawable.ic_drawable_pending)
                            .into(ivStatus_1)
                        Glide.with(getAppActivity())
                            .asBitmap()
                            .load(R.drawable.ic_drawable_pending)
                            .into(iv_status_splash)
                    } else if (listHealth[0].result?.lowercase().equals("NA".lowercase())) {
                        Glide.with(getAppActivity()).asBitmap().load(R.drawable.ic_drawable_na)
                            .into(ivStatus_1)
                        Glide.with(getAppActivity())
                            .asBitmap()
                            .load(R.drawable.ic_drawable_na)
                            .into(iv_status_splash)
                    } else {
                        Glide.with(getAppActivity()).asBitmap().load(R.drawable.ic_drawable_pending)
                            .into(ivStatus_1)
                        Glide.with(getAppActivity())
                            .asBitmap()
                            .load(R.drawable.ic_drawable_pending)
                            .into(iv_status_splash)
                    }


                }
            } else {
                test_Root.visibility = View.GONE
                iv_status_splash.visibility = View.GONE
                tvtest.visibility = View.GONE
            }

        }

        if (!TextUtils.isEmpty(datetimer)) {
            val df =
                SimpleDateFormat(DateUtils.API_DATE_FORMAT_VACCINE, Locale.getDefault())
            var date =
                DateUtils.getDate(datetimer, DateUtils.API_DATE_FORMAT_VACCINE)

            calendar.time = date
            calendar.timeZone = TimeZone.getTimeZone("UTC")
            calendar.add(Calendar.HOUR, 72)

            val dft =
                SimpleDateFormat(DateUtils.API_DATE_FORMAT_VACCINE, Locale.getDefault())
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

        iv_status_Vaccine.visibility = View.INVISIBLE
        val list = Constants.SCANNEDUSER!!.vaccine!!
        if (list.size > 0) {
            Constants.SCANNEDUSER?.vaccine?.let {
                Collections.sort(list, Comparator<VaccineHistory?> { obj1, obj2 ->

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

                strVaccine = list[0].documentLink.toString()

                if (list[0].dose != null) {
                    for (data in Constants.CONFIG?.doses!!) {
                        if (data!!.id.equals(list[0].dose)) {
                            tvDose.text = data.name + " | "
                        }
                    }
                }
                tvVaccine.text = list[0].type?.let { getVaccineName(it) }

                val institute = list[0].instituteId?.let { Constants.getInstituteName(it) }
                date.text = "$institute " + list[0].date?.let {
                    DateUtils.formatDateUTCToLocal(
                        it,
                        DateUtils.API_DATE_FORMAT_VACCINE,
                        true
                    )
                }

                date.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.green
                    )
                )
                tvDose.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.green
                    )
                )

                if (list[0].dose != null) {
                    var valid: Int = 0
                    try {
                        for (data in Constants.CONFIG?.doses!!) {
                            if (data!!.id.equals(list[0].dose)) {
                                if (data.name!!.contains("Dose 2")) {
                                    valid = 1

                                    break
                                } else if (data.name.contains("Dose 1")) {

                                    valid = 3
                                    break
                                } else if (data.name.contains("Dose 3")) {

                                    valid = 2
                                    break
                                }
                                else if (data.name.contains("Booster") || data.name.contains("2")) {

                                    valid = 2
                                    break
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                    when (valid) {
                        1 -> {
                            Glide.with(getAppActivity())
                                .asBitmap()
                                .load(R.drawable.ic_check_circle_yellow)
                                .into(iv_status_Vaccine)
                        }
                        2 -> {
                            Glide.with(getAppActivity())
                                .asBitmap().load(R.drawable.ic_drawable_tick)
                                .into(iv_status_Vaccine)
                        }
                        3 -> {
                            Glide.with(getAppActivity())
                                .asBitmap()
                                .load(R.drawable.ic_drawable_pending)
                                .into(iv_status_Vaccine)
                        }
                        else -> {
                            Glide.with(getAppActivity())
                                .asBitmap()
                                .load(R.drawable.ic_drawable_cross)
                                .into(iv_status_Vaccine)
                        }
                    }

                } else {
                    Glide.with(getAppActivity())
                        .asBitmap()
                        .load(R.drawable.ic_drawable_cross)
                        .into(iv_status_Vaccine)
                }


            }
        } else {
            vaccine_Root.visibility = View.GONE
            view_1.visibility = View.GONE
            iv_status_Vaccine.visibility = View.GONE
            tvVacine.visibility = View.GONE
        }


        var listTest = Constants.SCANNEDUSER!!.test!!
        if (listTest.size == 0) {


            if (isNetworkAvailable(requireActivity())) {
                viewModel.testHistory()
            } else {
                DialogUtils.showSnackBar(
                    requireActivity(),
                    requireActivity().resources.getString(R.string.no_connection)
                )
            }
        }

        Handler().postDelayed(Runnable {
            if (iv_status_splash.visibility == View.GONE) {
                val list = Constants.SCANNEDUSER!!.vaccine!!
                if (list.size > 0) {
                    iv_status_Vaccine.visibility = View.VISIBLE
                    val anim: Animation =
                        AnimationUtils.loadAnimation(context, R.anim.fade_in)
                    anim.repeatCount = 0
                    anim.duration = 800
                    iv_status_Vaccine.startAnimation(anim)
                    anim.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(anim: Animation) {}
                        override fun onAnimationRepeat(anim: Animation) {}
                        override fun onAnimationEnd(anim: Animation) {

                        }
                    })
                }
            } else {
                iv_status_splash.visibility = View.VISIBLE
                val anim: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                anim.duration = 2000
                anim.repeatCount = 0
                iv_status_splash.startAnimation(anim)
                anim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(anim: Animation) {}
                    override fun onAnimationRepeat(anim: Animation) {}
                    override fun onAnimationEnd(anim: Animation) {
                        Handler().postDelayed(Runnable {
                            val list = Constants.SCANNEDUSER!!.vaccine!!
                            if (list.size > 0) {
                                iv_status_Vaccine.visibility = View.VISIBLE
                                val anim: Animation =
                                    AnimationUtils.loadAnimation(context, R.anim.fade_in)
                                anim.repeatCount = 0
                                anim.duration = 800
                                iv_status_Vaccine.startAnimation(anim)
                                anim.setAnimationListener(object : Animation.AnimationListener {
                                    override fun onAnimationStart(anim: Animation) {}
                                    override fun onAnimationRepeat(anim: Animation) {}
                                    override fun onAnimationEnd(anim: Animation) {

                                    }
                                })
                            }
                        }, 10)
                    }
                })
            }
        }, 80)

        vaccine_Root.setOnClickListener {

            val role = SharedPreferenceUtil.getInstance(activity!!)
                ?.getData(PreferenceManager.KEY_ROLE, "user")
            if (!strVaccine.isEmpty()) {
                openDoc = strVaccine

                if (role?.equals("user") == true) {

                    Constants.openFile(strVaccine, activity!!)

                } else {
                    AppAlertDialog().validatePin(
                        activity!! as AppCompatActivity,
                        object : AppAlertDialog.GetClick {
                            override fun response(type: String) {
                                val j = JsonObject()
                                j.addProperty("userId", Constants.SCANNEDUSER?.id)
                                j.addProperty("pin", type)
                                viewModel.validatePin(j)

                            }
                        }

                    )
                }
            }

        }
        test_Root.setOnClickListener {
            // openFile(strTest)
            val role = SharedPreferenceUtil.getInstance(activity!!)
                ?.getData(PreferenceManager.KEY_ROLE, "user")
            if (!strTest.isNullOrEmpty()) {
                openDoc = strTest

                if (role?.equals("user") == true) {

                    Constants.openFile(strTest, activity!!)

                } else {
                    AppAlertDialog().validatePin(
                        activity!! as AppCompatActivity,
                        object : AppAlertDialog.GetClick {
                            override fun response(type: String) {
                                val j = JsonObject()
                                j.addProperty("userId", Constants.SCANNEDUSER?.id)
                                j.addProperty("pin", type)
                                viewModel.validatePin(j)

                            }
                        }

                    )
                }
            }
        }


        viewModel.testDataDetails.observe(this) {
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
        }

        viewModel.validatePin.observe(this, {
            when (it) {
                is Resource.Success -> {


                    if (it.data.status == "Success") {
                        Constants.openFile(openDoc, activity!!)
                    } else {
                        ToastUtils.longToast(0, getString(R.string.invalid_pin))
                    }
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        is VendorActivity -> (activity as VendorActivity).hideProgressBar()
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


        if (Constants.isSpalsh) {
            Handler().postDelayed({
                Constants.isSpalsh = false
                splash.visibility = View.GONE
                main.visibility = View.VISIBLE
            }, 4000)
        }


    }

    fun getVaccineName(id: Int): String? {
        val list = Constants.CONFIG?.vaccineType
        if (list?.isNotEmpty() == true) {
            for (vaccine in list) {
                if (vaccine?.id == id) {
                    return vaccine.type
                }
            }
        }
        return ""
    }

    override fun pageVisible() {

    }

    private fun openFile(url: String) {
        try {
            val uri: Uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW)
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword")
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf")
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel")
            } else if (url.toString().contains(".zip")) {
                // ZIP file
                intent.setDataAndType(uri, "application/zip")
            } else if (url.toString().contains(".rar")) {
                // RAR file
                intent.setDataAndType(uri, "application/x-rar-compressed")
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf")
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav")
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif")
            } else if (url.toString().contains(".jpg") || url.toString()
                    .contains(".jpeg") || url.toString().contains(".png")
            ) {
                // JPG file
                intent.setDataAndType(uri, "image/*")
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain")
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") ||
                url.toString().contains(".mpeg") || url.toString()
                    .contains(".mpe") || url.toString().contains(".mp4") || url.toString()
                    .contains(".avi")
            ) {
                // Video files
                intent.setDataAndType(uri, "video/*")
            } else {
                intent.setDataAndType(uri, "*/*")
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "No application found which can open the file",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}