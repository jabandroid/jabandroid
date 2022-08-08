package com.global.vtg.appview.home.event


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.KeyboardUtils
import com.vtg.R
import com.vtg.databinding.FragmentCreateEventSubBinding
import kotlinx.android.synthetic.main.fragment_create_event_sub.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class CreateSubEventFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentCreateEventSubBinding
    private val myCalendar: Calendar = Calendar.getInstance()
    private val currentCalendar: Calendar = Calendar.getInstance()
    private val viewModel by viewModel<CreateEventSubViewModel>()
    private var isStartTime: Boolean = false
private  var addressId:String=""

    companion object {
        lateinit var itemSubEvent: Event
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_create_event_sub
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentCreateEventSubBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    @SuppressLint("SetTextI18n")
    override fun initializeComponent(view: View?) {

        btnNext.setOnClickListener(this)
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

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
                AppFragmentState.F_EVENT_SUB_EVENT_IMAGE
            )

        })

        // Handle Error
        viewModel.showToastError.observe(this, {
            DialogUtils.showSnackBar(context, it)
        })

        sCity.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
        }
        sState.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
        }
        sCountry.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
        }


        if (itemSubEvent.eventName != null)
            viewModel.eventName.postValue(itemSubEvent.eventName)
        if (itemSubEvent.crowdLimit != null)
            viewModel.eventAttendees.postValue(itemSubEvent.crowdLimit)


        if (itemSubEvent.startDate != null) {
            viewModel.startTime = DateUtils.formatDateUTCToLocal(
                itemSubEvent.startDate!!,
                DateUtils.API_DATE_FORMAT_VACCINE,
                true
            )
            tvEventStartTime.text = DateUtils.formatDateUTCToLocal(
                itemSubEvent.startDate!!,
                DateUtils.API_DATE_FORMAT_VACCINE,
                true
            )
        }
        if (itemSubEvent.endDate != null) {
            viewModel.endTime = DateUtils.formatDateUTCToLocal(
                itemSubEvent.endDate!!,
                DateUtils.API_DATE_FORMAT_VACCINE,
                true
            )
            tvEventEndTime.text = DateUtils.formatDateUTCToLocal(
                itemSubEvent.endDate!!,
                DateUtils.API_DATE_FORMAT_VACCINE,
                true
            )
        }
        if (itemSubEvent.eventAddress != null && itemSubEvent.eventAddress!!.isNotEmpty()) {
            addressId=itemSubEvent.eventAddress!![0].addressID.toString()
            viewModel.address1.postValue(itemSubEvent.eventAddress!![0].addr1)
            viewModel.address2.postValue(itemSubEvent.eventAddress!![0].addr2)
            viewModel.address3.postValue(itemSubEvent.eventAddress!![0].addr3)
            viewModel.city.postValue(itemSubEvent.eventAddress!![0].city)
            viewModel.state.postValue(itemSubEvent.eventAddress!![0].state)
            viewModel.country.postValue(itemSubEvent.eventAddress!![0].country)
            viewModel.zip.postValue(itemSubEvent.eventAddress!![0].zipCode)
            viewModel.web.postValue(itemSubEvent.eventAddress!![0].web)
            viewModel.contactNumber.postValue(itemSubEvent.eventAddress!![0].phoneNo)
            viewModel.email.postValue(itemSubEvent.eventAddress!![0].email)

            if (!TextUtils.isEmpty(itemSubEvent.eventAddress!![0].addressID))
                viewModel.id = itemSubEvent.eventAddress!![0].addressID.toString()


        }

        if (CreateEventFragment.itemEvent.eventAddress!!.size > 0) {
            addressId=CreateEventFragment.itemEvent.eventAddress!![0].addressID.toString()
            tvAddress.text = CreateEventFragment.itemEvent.eventAddress!![0].addr1 + " " +
                    CreateEventFragment.itemEvent.eventAddress!![0].addr2 + " " +
                    CreateEventFragment.itemEvent.eventAddress!![0].addr3 + " " +
                    CreateEventFragment.itemEvent.eventAddress!![0].city + " " +
                    CreateEventFragment.itemEvent.eventAddress!![0].state + " "
            CreateEventFragment.itemEvent.eventAddress!![0].country

            val list = ArrayList<EventAddress>()
            list.add(CreateEventFragment.itemEvent.eventAddress!![0])
          itemSubEvent.eventAddress = list
        }



        if (itemSubEvent.description != null)
            viewModel.eventDescription.postValue(itemSubEvent.description)

        tvAddress.setOnClickListener {
            val v = Bundle()
            v.putBoolean("isSubAddress", true)
            v.putString("addressId", addressId)
            addFragmentInStack<Any>(
                AppFragmentState.F_EVENT_ADDRESS, v
            )
        }
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

    fun updateAddress(city: String, state: String, country: String) {
        sCity.text = city
        sState.text = state
        sCountry.text = country
    }

    @SuppressLint("SetTextI18n")
    fun refreshList() {
        if (itemSubEvent.eventAddress!!.size > 0) {
            addressId=itemSubEvent.eventAddress!![0].addressID.toString()
            tvAddress.text = itemSubEvent.eventAddress!![0].addr1 + " " +
                    itemSubEvent.eventAddress!![0].addr2 + " " +
                    itemSubEvent.eventAddress!![0].addr3 + " " +
                    itemSubEvent.eventAddress!![0].city + " " +
                    itemSubEvent.eventAddress!![0].state + " "
            itemSubEvent.eventAddress!![0].country
        }
    }
}