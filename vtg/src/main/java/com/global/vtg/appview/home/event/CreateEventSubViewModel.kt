package com.global.vtg.appview.home.event

import android.app.Application
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.global.vtg.App
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.base.AppViewModel
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.KeyboardUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.vtg.R
import kotlinx.android.synthetic.main.fragment_create_event.*

class CreateEventSubViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {

    var eventName: MutableLiveData<String> = MutableLiveData()
    var eventDescription: MutableLiveData<String> = MutableLiveData()
    var eventPrivate: MutableLiveData<Boolean> = MutableLiveData()
    var startTime: String =""
    var eventAttendees: MutableLiveData<String> = MutableLiveData()
    var endTime: String =""
    var showToastError: MutableLiveData<String> = MutableLiveData()
    var redirectToStep2: MutableLiveData<Boolean> = MutableLiveData()
    var address1: MutableLiveData<String> = MutableLiveData()
    var id: String = ""
    var address2: MutableLiveData<String> = MutableLiveData()
    var address3: MutableLiveData<String> = MutableLiveData()
    var city: MutableLiveData<String> = MutableLiveData()
    var state: MutableLiveData<String> = MutableLiveData()
    var zip: MutableLiveData<String> = MutableLiveData()
    var country: MutableLiveData<String> = MutableLiveData()
    var contactNumber: MutableLiveData<String> = MutableLiveData()
    var fax: MutableLiveData<String> = MutableLiveData()
    var web: MutableLiveData<String> = MutableLiveData()
    var email: MutableLiveData<String> = MutableLiveData()


    fun onClick(view: View) {

        when (view.id) {
            R.id.btnNext->{
                if(validateFields()) {
                    CreateSubEventFragment.itemSubEvent.eventName = eventName.value
                    CreateSubEventFragment.itemSubEvent.startDate = startTime
                    CreateSubEventFragment.itemSubEvent.endDate = endTime
                    CreateSubEventFragment.itemSubEvent.description = eventDescription.value
                    CreateSubEventFragment.itemSubEvent.privateEvent = eventPrivate.value
                    CreateSubEventFragment.itemSubEvent.crowdLimit = eventAttendees.value

//                    val itemAddress = EventAddress(
//                        id,
//                        address1.value!!,
//                        if (!TextUtils.isEmpty(address2.value)) address2.value.toString() else "",
//                        if (!TextUtils.isEmpty(address3.value)) address3.value.toString() else "",
//                        zip.value!!,
//                        city.value!!,
//                        state.value!!,
//                        country.value!!,
//                        if (!TextUtils.isEmpty(contactNumber.value)) contactNumber.value.toString() else "",
//                        if (!TextUtils.isEmpty(contactNumber.value)) contactNumber.value.toString() else "",
//
//                        if (!TextUtils.isEmpty(fax.value)) fax.value.toString() else "",
//                        if (!TextUtils.isEmpty(web.value)) web.value.toString() else "",
//                        if (!TextUtils.isEmpty(email.value)) email.value.toString() else "",
//                    )


                    redirectToStep2.postValue(true)
                }
            }

        }
    }


    private fun validateFields(): Boolean {
        var isValidate = true
        when {
            isNullOrEmpty(eventName.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_event_name))
                isValidate = false
            }
            isNullOrEmpty(startTime) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_start_time))
                isValidate = false
            }
            isNullOrEmpty(endTime) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_end_time))
                isValidate = false
            }
            isNullOrEmpty( CreateSubEventFragment.itemSubEvent.eventAddress!![0].addr1) -> {
                showToastError.postValue(App.instance?.getString(R.string.select_address))
                isValidate = false
            }
//            isNullOrEmpty(city.value) -> {
//                showToastError.postValue(App.instance?.getString(R.string.empty_address_city))
//                isValidate = false
//            }
//            isNullOrEmpty(state.value) -> {
//                showToastError.postValue(App.instance?.getString(R.string.empty_address_state))
//                isValidate = false
//            }
//
//            isNullOrEmpty(country.value) -> {
//                showToastError.postValue(App.instance?.getString(R.string.empty_address_country))
//                isValidate = false
//            }

            isNullOrEmpty(eventAttendees.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.enter_attendees))
                isValidate = false
            }

            else -> {
                showToastError.postValue("")
            }
        }

        if (!isValidate)
            return false

        return isValidate
    }


}