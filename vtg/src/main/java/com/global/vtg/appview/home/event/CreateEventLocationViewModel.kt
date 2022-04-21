package com.global.vtg.appview.home.event

import android.app.Application
import android.text.TextUtils
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

class CreateEventLocationViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {

    var address1: MutableLiveData<String> = MutableLiveData()
    var id: String = ""
    var address2: MutableLiveData<String> = MutableLiveData()
    var city: MutableLiveData<String> = MutableLiveData()
    var state: MutableLiveData<String> = MutableLiveData()
    var zip: MutableLiveData<String> = MutableLiveData()
    var country: MutableLiveData<String> = MutableLiveData()
    var contactNumber: MutableLiveData<String> = MutableLiveData()
    var fax: MutableLiveData<String> = MutableLiveData()
    var email: MutableLiveData<String> = MutableLiveData()

    var showToastError: MutableLiveData<String> = MutableLiveData()
    var redirectToStep3: MutableLiveData<Boolean> = MutableLiveData()

    fun onClick(view: View) {

        when (view.id) {
            R.id.btnNext -> {
                if (validateFields()) {
                    val itemAddress = EventAddress(
                        id,
                        address1.value!!,
                        if (!TextUtils.isEmpty(address2.value)) address2.value.toString() else "",
                        "",
                        if (!TextUtils.isEmpty(zip.value)) zip.value.toString() else "",
                        city.value!!,
                        state.value!!,
                        country.value!!,
                        contactNumber.value!!,
                        contactNumber.value!!,

                        if (!TextUtils.isEmpty(fax.value)) fax.value.toString() else "",
                        if (!TextUtils.isEmpty(email.value)) email.value.toString() else "",
                    )
                    val list = ArrayList<EventAddress>()
                    list.add(itemAddress)
                    CreateEventFragment.itemEvent.eventAddress = list
                    redirectToStep3.postValue(true)
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        var isValidate = true
        when {
            isNullOrEmpty(address1.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_address_1))
                isValidate = false
            }
            isNullOrEmpty(city.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_address_city))
                isValidate = false
            }
            isNullOrEmpty(state.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_address_state))
                isValidate = false
            }
//            isNullOrEmpty(zip.value) -> {
//                showToastError.postValue(App.instance?.getString(R.string.empty_address_zip))
//                isValidate = false
//            }
            isNullOrEmpty(country.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_address_country))
                isValidate = false
            }
            isNullOrEmpty(contactNumber.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_phone))
                isValidate = false
            }
            isNullOrEmpty(email.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_email))
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