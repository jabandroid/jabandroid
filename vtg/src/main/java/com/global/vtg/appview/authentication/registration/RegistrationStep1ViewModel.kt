package com.global.vtg.appview.authentication.registration

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.global.vtg.App
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.Constants.USER
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.KeyboardUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.vtg.R
import kotlinx.coroutines.launch

class RegistrationStep1ViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {

    var isVendor: Boolean = false
    var isClinic: Boolean = false
    var title: MutableLiveData<String> = MutableLiveData()
    var firstName: MutableLiveData<String> = MutableLiveData()
    var lastName: MutableLiveData<String> = MutableLiveData()
    var dob: MutableLiveData<String> = MutableLiveData()
    var apiDob: String = ""
    var city: MutableLiveData<String> = MutableLiveData()
    var state: MutableLiveData<String> = MutableLiveData()
    var country: MutableLiveData<String> = MutableLiveData()
    var citizenship: MutableLiveData<String> = MutableLiveData()
    var gender: MutableLiveData<String> = MutableLiveData()
    var ethnicity: MutableLiveData<String> = MutableLiveData()

    var showToastError: MutableLiveData<String> = MutableLiveData()
    var search: MutableLiveData<Boolean> = MutableLiveData()

    val registerStep1LiveData = MutableLiveData<Resource<ResUser>>()

    fun onClick(view: View) {

        when (view.id) {
            R.id.btnNext -> {
                KeyboardUtils.hideKeyboard(view)
                if (isNetworkAvailable(view.context)) {
                    if (validateFields()) {
                        KeyboardUtils.hideKeyboard(view)
                        USER?.title = title.value
                        USER?.firstName = firstName.value
                        USER?.lastName = lastName.value
                        USER?.citizenship = citizenship.value
                        USER?.dateOfBirth = apiDob
                        USER?.birthCity = city.value
                        USER?.birthState = state.value
                        USER?.birthCountry = country.value
                        USER?.ethnicity = ethnicity.value
                        USER?.gender = gender.value
                        USER?.twilioUserId = Constants.twilioUserId.toString()
                        USER?.role = if (isVendor) "vendor" else "user"
                        USER?.isClinic = isClinic == true
                        USER?.step1Complete = true
                        callRegisterStep()
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
            USER?.let { userRepository.registerStep(registerStep1LiveData, it) }
        }
    }

    //validate fields
    private fun validateFields(): Boolean {
        var isValidate = true
        when {
            isNullOrEmpty(title.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_title))
                isValidate = false
            }
            isNullOrEmpty(firstName.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_given_name))
                isValidate = false
            }
            isNullOrEmpty(lastName.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_last_name))
                isValidate = false
            }
//            isNullOrEmpty(gender.value) -> {
//                showToastError.postValue(App.instance?.getString(R.string.empty_gender))
//                isValidate = false
//            }
            else -> {
                showToastError.postValue("")
            }
        }

        if (!isValidate)
            return false

        return isValidate
    }
}