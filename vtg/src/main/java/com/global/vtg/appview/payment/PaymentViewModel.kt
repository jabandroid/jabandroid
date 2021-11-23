package com.global.vtg.appview.payment

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.global.vtg.App
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.authentication.registration.AddressItem
import com.global.vtg.base.AppViewModel
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.KeyboardUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.vtg.R
import kotlinx.coroutines.launch


class PaymentViewModel(application: Application, private val userRepository: UserRepository) :
    AppViewModel(application) {

    var product: MutableLiveData<String> = MutableLiveData()
    val isDifferentAddress = MutableLiveData(false)
    val makePayment = MutableLiveData(false)
    var firstName: MutableLiveData<String> = MutableLiveData()
    var lastName: MutableLiveData<String> = MutableLiveData()
    var address1: MutableLiveData<String> = MutableLiveData()
    var address2: MutableLiveData<String> = MutableLiveData()
    var city: MutableLiveData<String> = MutableLiveData()
    var state: MutableLiveData<String> = MutableLiveData()
    var zip: MutableLiveData<String> = MutableLiveData()
    var country: MutableLiveData<String> = MutableLiveData()
    var showToastError: MutableLiveData<String> = MutableLiveData()
    var address: AddressItem? = null

    fun onClick(view: View) {
        when (view.id) {
            R.id.btnSave -> {
                KeyboardUtils.hideKeyboard(view)
                if (isNetworkAvailable(view.context)) {
                    if (validateFields()) {
                        if (isDifferentAddress.value == true) {
                            address = AddressItem(
                                firstName = firstName.value,
                                lastName = lastName.value,
                                addr1 = address1.value,
                                addr2 = address2.value,
                                city = city.value,
                                state = state.value,
                                zipCode = zip.value,
                                country = country.value,
                                shipping = false,
                                billing = true
                            )
                            Constants.USER?.address?.add(address)
                        } else {
                            val list = Constants.USER?.address
                            if (list != null && list.isNotEmpty()) {
                                for (add in list) {
                                    if (add?.shipping == true) {
                                        add.billing = true
                                        address = add
                                        break
                                    }
                                }
                            }
                            Constants.USER?.address = list
                        }
                        makePayment.postValue(true)
                    }
                } else {
                    DialogUtils.showSnackBar(
                        view.context,
                        view.context.resources.getString(R.string.no_connection)
                    )
                }
            }
        }
    }

    private fun callRegisterStep() {
        scope.launch {
//            Constants.USER?.let { userRepository.makePayment(it) }
        }
    }

    fun validateFields(): Boolean {
        var isValidate = true
        when {
            isNullOrEmpty(product.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_product))
                isValidate = false
            }
            isDifferentAddress.value == true -> {
                when {
                    isNullOrEmpty(firstName.value) -> {
                        showToastError.postValue(App.instance?.getString(R.string.empty_first_name))
                        isValidate = false
                    }
                    isNullOrEmpty(lastName.value) -> {
                        showToastError.postValue(App.instance?.getString(R.string.empty_last_name))
                        isValidate = false
                    }
                    isNullOrEmpty(address1.value) -> {
                        showToastError.postValue(App.instance?.getString(R.string.empty_address1))
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
                    isNullOrEmpty(zip.value) -> {
                        showToastError.postValue(App.instance?.getString(R.string.empty_address_zip))
                        isValidate = false
                    }
                    isNullOrEmpty(country.value) -> {
                        showToastError.postValue(App.instance?.getString(R.string.empty_address_country))
                        isValidate = false
                    }
                }
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