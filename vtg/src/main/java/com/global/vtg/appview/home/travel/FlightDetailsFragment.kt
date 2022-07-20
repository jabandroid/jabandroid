package com.global.vtg.appview.home.travel

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.utils.DateUtils
import com.global.vtg.utils.KeyboardUtils
import com.vtg.R
import com.vtg.databinding.FragmentChildProfileBinding
import com.vtg.databinding.FragmentFlightDetailsBinding
import com.vtg.databinding.FragmentTravelInformatonBinding
import com.vtg.databinding.FragmentTravelProfileBinding
import kotlinx.android.synthetic.main.fragment_flight_details.*
import kotlinx.android.synthetic.main.fragment_travel_informaton.*
import kotlinx.android.synthetic.main.fragment_travel_informaton.btnNext
import kotlinx.android.synthetic.main.fragment_travel_informaton.ivBack
import kotlinx.android.synthetic.main.fragment_upload_document.*
import kotlinx.android.synthetic.main.fragment_vaccine_qr_code.*
import java.util.*

class FlightDetailsFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentFlightDetailsBinding
    private val myCalendar: Calendar = Calendar.getInstance()
    private val currentCalendar: Calendar = Calendar.getInstance()
    override fun getLayoutId(): Int {
        return R.layout.fragment_flight_details
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentFlightDetailsBinding
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {

        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        btnNext.setOnClickListener {
            addFragmentInStack<Any>(AppFragmentState.F_TRaVEL_SUMMARY)
        }


        val date =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                tvDepartureDate.text=                DateUtils.formatDate(
                        myCalendar.timeInMillis
                    )

//                viewModel.apiDob = DateUtils.formatDateTime(myCalendar.timeInMillis,
//                    DateUtils.API_DATE_FORMAT
//                )
//                viewModel.dobDate = myCalendar.time
            }

        tvDepartureDate.setOnClickListener{
            KeyboardUtils.hideKeyboard(getAppActivity())
            val datePicker = DatePickerDialog(
                getAppActivity(), R.style.DialogTheme, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.maxDate = currentCalendar.timeInMillis
            datePicker.show()
        }


        tvDepartureTime.setOnClickListener {
            KeyboardUtils.hideKeyboard(getAppActivity())
            val mCurrentTime = Calendar.getInstance()
            val hour = mCurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mCurrentTime[Calendar.MINUTE]
            val mTimePicker = TimePickerDialog(
                getAppActivity(),
                R.style.DialogTheme,
                { _, selectedHour, selectedMinute ->
                    run {
                        val time =
                            "${DateUtils.appendZero(selectedHour.toString())}:${DateUtils.appendZero(selectedMinute.toString())}"

                        tvDepartureTime.text = time
                        myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                        myCalendar.set(Calendar.MINUTE, selectedMinute)

                    }

                },
                hour,
                minute,
                true
            ) //Yes 24 hour time

            mTimePicker.setTitle(resources.getString(R.string.label_select_time))
            mTimePicker.show()
        }

        tvArrivalTime.setOnClickListener {
            KeyboardUtils.hideKeyboard(getAppActivity())
            val mCurrentTime = Calendar.getInstance()
            val hour = mCurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mCurrentTime[Calendar.MINUTE]
            val mTimePicker = TimePickerDialog(
                getAppActivity(),
                R.style.DialogTheme,
                { _, selectedHour, selectedMinute ->
                    run {
                        val time =
                            "${DateUtils.appendZero(selectedHour.toString())}:${DateUtils.appendZero(selectedMinute.toString())}"

                        tvArrivalTime.text = time
                        myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                        myCalendar.set(Calendar.MINUTE, selectedMinute)

                    }

                },
                hour,
                minute,
                true
            ) //Yes 24 hour time

            mTimePicker.setTitle(resources.getString(R.string.label_select_time))
            mTimePicker.show()
        }
    }

    override fun pageVisible() {

    }
}