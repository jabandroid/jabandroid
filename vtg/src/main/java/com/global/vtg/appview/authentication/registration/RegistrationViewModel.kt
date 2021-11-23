package com.global.vtg.appview.authentication.registration

import android.app.Application
import android.util.Patterns
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.App
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.KeyboardUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.vtg.R
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class RegistrationViewModel(application: Application, private val userRepository: UserRepository) : AppViewModel(application) {

    var email: MutableLiveData<String> = MutableLiveData()
    var phone: MutableLiveData<String> = MutableLiveData()
    var region: String = "US"
    var password: MutableLiveData<String> = MutableLiveData()
    var code: MutableLiveData<String> = MutableLiveData()
    var isVendor: MutableLiveData<Boolean> = MutableLiveData(false)
    var isClinic: MutableLiveData<Boolean> = MutableLiveData(false)
    var acceptedTerms: MutableLiveData<Boolean> = MutableLiveData(false)

    var showToastError: MutableLiveData<String> = MutableLiveData()
    var redirectToSignIn: MutableLiveData<Boolean> = MutableLiveData()
    var redirectToOtp: MutableLiveData<Boolean> = MutableLiveData()

    fun onClick(view: View) {

        when (view.id) {
            R.id.btnRegister -> {
                KeyboardUtils.hideKeyboard(view)
                if (isNetworkAvailable(view.context)){
                    if (validateFields()) {
                        redirectToOtp.postValue(true)
                    }
                } else {
                    DialogUtils.showSnackBar(view.context, view.context.resources.getString(R.string.no_connection))
                }

            }
            R.id.tvRegistrationSignIn -> {
                KeyboardUtils.hideKeyboard(view)
                redirectToSignIn.postValue(true)
            }
        }
    }

    //validate login fields
    private fun validateFields(): Boolean {
        var isValidate = true
        when {
//            isNullOrEmpty(email.value) -> {
//                showToastError.postValue(App.instance?.getString(R.string.empty_email))
//                isValidate = false
//            }
            isNullOrEmpty(phone.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_phone))
                isValidate = false
            }
            email.value.toString().isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email.value.toString().trim()).matches() -> {
                showToastError.postValue(App.instance?.getString(R.string.error_email))
                isValidate = false
            }
            !isNullOrEmpty(phone.value) && !Constants.isValidPhoneNumber(
                phone.value.toString().trim(), region
            ) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_phone))
                isValidate = false
            }
            isNullOrEmpty(password.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_password))
                isValidate = false
            }
            !(Pattern.compile("^((?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*\\W).{8,32})\$").matcher(password.value.toString()).matches()) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_password))
                isValidate = false
            }
            acceptedTerms.value != true -> {
                showToastError.postValue(App.instance?.getString(R.string.error_terms_condition))
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