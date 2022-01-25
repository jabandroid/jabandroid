package com.global.vtg.appview.home.testHistory

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import cn.iwgang.countdownview.CountdownView
import cn.iwgang.countdownview.DynamicConfig
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.SimpleTarget
import com.global.vtg.appview.config.TestInfo
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.vtg.R
import com.vtg.databinding.FragmentTestDetailsBinding
import com.vtg.databinding.FragmentTestHistoryBinding
import kotlinx.android.synthetic.main.fragment_test_details.*
import kotlinx.android.synthetic.main.fragment_test_details.countDown
import kotlinx.android.synthetic.main.fragment_test_details.date_test
import kotlinx.android.synthetic.main.fragment_test_details.ivProfilePic
import kotlinx.android.synthetic.main.fragment_test_details.ivStatus_1
import kotlinx.android.synthetic.main.fragment_test_details.test_Root
import kotlinx.android.synthetic.main.fragment_test_details.tvDl
import kotlinx.android.synthetic.main.fragment_test_details.tvDob
import kotlinx.android.synthetic.main.fragment_test_details.tvId
import kotlinx.android.synthetic.main.fragment_test_details.tvName
import kotlinx.android.synthetic.main.fragment_test_details.tvStatus
import kotlinx.android.synthetic.main.fragment_test_details.tvTest
import kotlinx.android.synthetic.main.fragment_test_details.tvType

import kotlinx.android.synthetic.main.fragment_test_history.*
import kotlinx.android.synthetic.main.fragment_vendor_scan_result_cout.*


import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class TestHistoryDetailFragment : AppFragment() {

    private lateinit var mFragmentBinding: FragmentTestDetailsBinding
    private val viewModel by viewModel<TestHistoryViewModel>()
    lateinit var mListner: CountdownView.OnCountdownIntervalListener
    val calendar: Calendar = Calendar.getInstance()

    override fun getLayoutId(): Int {
        return R.layout.fragment_test_details
    }

    override fun preDataBinding(arguments: Bundle?) {
        getAppActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentTestDetailsBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    @SuppressLint("SetTextI18n")
    override fun initializeComponent(view: View?) {

        iv_add.setOnClickListener {
            addFragmentInStack<Any>(AppFragmentState.F_TEST_UPLOAD)
        }

        refreshList()
    }

    override fun pageVisible() {

    }

    fun refreshList() {
        mListner = CountdownView.OnCountdownIntervalListener { dtype, time ->

            val dft =
                SimpleDateFormat(DateUtils.API_DATE_FORMAT_SERVER, Locale.getDefault())
            dft.timeZone = TimeZone.getTimeZone("UTC")
            val currentS = dft.format(Date())
            val currentTime = dft.parse(currentS)


            val diffInMillisec: Long = calendar.timeInMillis - currentTime.time
            val hours: Long = TimeUnit.MILLISECONDS.toHours(diffInMillisec)
            val dynamicConfigBuilder = DynamicConfig.Builder()
            val backgroundInfo = DynamicConfig.BackgroundInfo()
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

        ivProfilePic.setGlideNormalImage(Constants.USER!!.profileUrl)

        if (!Constants.USER?.firstName.isNullOrEmpty()) {
            tvName.text =
                Constants.USER?.firstName + " " + Constants.USER?.lastName
        }


        val list_Doc = Constants.USER?.document

        if (!Constants.USER?.dateOfBirth.isNullOrEmpty()) {
            val str = SpannableStringBuilder(
                "DOB: " + DateUtils.convertDate(
                    DateUtils.API_DATE_FORMAT_VACCINE,
                    DateUtils.API_DATE_FORMAT,
                    Constants.USER?.dateOfBirth!!
                )
            )
            str.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                4,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )


            tvDob.text = str
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
        if (Constants.USER!!.test!!.size > 0) {
            val list = Constants.USER!!.test!!
            Constants.USER?.test?.let {

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

            val institute = list[0].instituteId?.let { Constants.getInstituteName(it) }
            date_test.text = "$institute " + list[0].date?.let {
                DateUtils.formatDateUTCToLocal(
                    it,
                    DateUtils.API_DATE_FORMAT_VACCINE,
                    DateUtils.DDMMYYYY,
                )
            }

            date_test.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.green
                )
            )

            if (list[0].result?.lowercase().equals("Negative".lowercase())) {
                datetimer = list[0].date.toString()
                Glide.with(getAppActivity()).asBitmap().load(R.drawable.ic_drawable_tick)
                    .into(ivStatus_1)
            } else if (list[0].result?.lowercase().equals("Positive".lowercase())) {
                Glide.with(getAppActivity()).asBitmap().load(R.drawable.ic_drawable_cross)
                    .into(ivStatus_1)
            } else if (list[0].result?.lowercase().equals("Invalid".lowercase())) {
                Glide.with(getAppActivity()).asBitmap().load(R.drawable.ic_drawable_pending)
                    .into(ivStatus_1)
            } else if (list[0].result?.lowercase().equals("NA".lowercase())) {
                Glide.with(getAppActivity()).asBitmap().load(R.drawable.ic_drawable_na)
                    .into(ivStatus_1)
            } else {
                Glide.with(getAppActivity()).asBitmap().load(R.drawable.ic_drawable_pending)
                    .into(ivStatus_1)
            }

        }else{
            test_Root.visibility=View.GONE
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
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }
}