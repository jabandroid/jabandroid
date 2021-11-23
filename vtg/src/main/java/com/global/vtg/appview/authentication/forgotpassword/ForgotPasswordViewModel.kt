package com.global.vtg.appview.authentication.forgotpassword

import android.app.Application
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
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {
    var phone: MutableLiveData<String> = MutableLiveData()
    var region: String = "US"
    var code: MutableLiveData<String> = MutableLiveData()
    var redirectToSignIn: MutableLiveData<Boolean> = MutableLiveData()
    var showToastError: MutableLiveData<String> = MutableLiveData()
    var isVendor: MutableLiveData<Boolean> = MutableLiveData(false)

    fun onClick(view: View) {

        when (view.id) {
            R.id.btnSend -> {
                KeyboardUtils.hideKeyboard(view)
                if (isNetworkAvailable(view.context)) {
                    if (validateFields()) {
                        redirectToSignIn.postValue(true)
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

    private fun validateFields(): Boolean {
        var isValidate = true
        when {
            isNullOrEmpty(phone.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_phone))
                isValidate = false
            }
            !Constants.isValidPhoneNumber(
                phone.value.toString().trim(), region
            ) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_phone))
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