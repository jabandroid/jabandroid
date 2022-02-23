package com.global.vtg.appview.home.event

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.home.changepassword.ChangePasswordViewModel
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.utils.*
import com.vtg.R

import com.vtg.databinding.FragmentCreateEventBinding

import kotlinx.android.synthetic.main.fragment_create_event.*
import kotlinx.android.synthetic.main.fragment_create_event.ivBack
import kotlinx.android.synthetic.main.fragment_event_list.*


import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class CreateEventFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentCreateEventBinding
    private val myCalendar: Calendar = Calendar.getInstance()
    private val currentCalendar: Calendar = Calendar.getInstance()
    private val viewModel by viewModel<CreateEventViewModel>()
    private var isStartTime: Boolean = false

    companion object {

       lateinit var itemEvent:Event
    }
    override fun getLayoutId(): Int {
        return R.layout.fragment_create_event
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentCreateEventBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        itemEvent= Event()
        viewModel.eventPrivate.postValue(false)
        btnNext.setOnClickListener(this)
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        if (!Constants.USER!!.profileUrl.isNullOrEmpty())
            ivProfilePic.setGlideNormalImage(Constants.USER!!.profileUrl)

        val date =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val mCurrentTime = Calendar.getInstance()
                val hour = mCurrentTime[Calendar.HOUR_OF_DAY]
                val minute = mCurrentTime[Calendar.MINUTE]
                val mTimePicker = TimePickerDialog(
                    getAppActivity(),
                    R.style.DialogTheme,
                    { _, selectedHour, selectedMinute ->
                        run {
                            val time =
                                "${DateUtils.appendZero(selectedHour.toString())}:${
                                    DateUtils.appendZero(
                                        selectedMinute.toString()
                                    )
                                }"

                            myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                            myCalendar.set(Calendar.MINUTE, selectedMinute)
                            updateDate()
                        }

                    },
                    hour,
                    minute,
                    true
                )

                mTimePicker.setTitle(resources.getString(R.string.label_select_time))
                mTimePicker.show()
            }

        tvEventStartTime.setOnClickListener {
            isStartTime = true
            KeyboardUtils.hideKeyboard(getAppActivity())
            val datePicker = DatePickerDialog(
                getAppActivity(), R.style.DialogTheme, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = currentCalendar.timeInMillis
            datePicker.show()
        }

        tvEventEndTime.setOnClickListener {
            isStartTime = false
            KeyboardUtils.hideKeyboard(getAppActivity())
            val datePicker = DatePickerDialog(
                getAppActivity(), R.style.DialogTheme, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = currentCalendar.timeInMillis
            datePicker.show()
        }

        viewModel.redirectToStep2.observe(this, {

            addFragmentInStack<Any>(
                AppFragmentState.F_EVENT_CREATE_LOCATION
            )

        })

        // Handle Error
        viewModel.showToastError.observe(this, {
            DialogUtils.showSnackBar(context, it)
        })
    }

    override fun pageVisible() {

    }

    private fun updateDate() {
        if (isStartTime) {
            val calendar: Calendar = Calendar.getInstance()
//            if(myCalendar.timeInMillis<calendar.timeInMillis){
//                DialogUtils.toast(activity!!,getString(R.string.time))
//            }else {
                viewModel.startTime = DateUtils.formatDateTime(myCalendar.timeInMillis, true)
                tvEventStartTime.text = viewModel.startTime
            //}
        } else {
            viewModel.endTime = DateUtils.formatDateTime(myCalendar.timeInMillis, true)
            tvEventEndTime.text = viewModel.endTime
        }

    }
}