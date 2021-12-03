package com.global.vtg.appview.authentication.login

import android.app.Application
import android.util.Patterns
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.App
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.Constants.isValidPhoneNumber
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.KeyboardUtils
import com.global.vtg.utils.SharedPreferenceUtil
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.vtg.R
import kotlinx.coroutines.launch


class SignInViewModel(
    application: Application,
    private val userRepository: UserRepository,
    private val preferenceManager: PreferenceManager
) :
    AppViewModel(application) {

    var email: MutableLiveData<String> = MutableLiveData()
    var phone: MutableLiveData<String> = MutableLiveData()
    var region: String = "US"
    var code: MutableLiveData<String> = MutableLiveData()
    var password: MutableLiveData<String> = MutableLiveData()
    var isRememberMeChecked: MutableLiveData<Boolean> = MutableLiveData()
    var isVendor: MutableLiveData<Boolean> = MutableLiveData(false)
    var isClinic: MutableLiveData<Boolean> = MutableLiveData(false)

    var showToastError: MutableLiveData<String> = MutableLiveData()

    val showDialog = MutableLiveData<String>()
    val loginLiveData = MutableLiveData<Boolean>()
    val redirectToSignUp = MutableLiveData<Boolean>()
    val redirectToForgotPassword = MutableLiveData<Boolean>()
    val userLiveData = MutableLiveData<Resource<ResUser>>()

    private val userObserver = Observer<Resource<ResUser>> {
        userLiveData.postValue(it)
    }


    init {
        //set initial value of all mutable
        email.postValue("")
        password.postValue("")

        showToastError.postValue("")

        showToastError.postValue("")
        showDialog.postValue("")

        userRepository.userLiveData.postValue(null)
        userRepository.userLiveData.observeForever(userObserver)

    }

    fun onClick(view: View) {

        when (view.id) {
            R.id.btnLogin -> {
                KeyboardUtils.hideKeyboard(view)
                if (isNetworkAvailable(view.context)) {
                    if (validateFields()) {
                        showProgress.postValue(true)
                        preferenceManager.saveRole(
                            //if (isVendor.value == true && isClinic.value == true) "clinic"
                            if ( isClinic.value == true) "clinic"
                            else if (isVendor.value == true) "vendor"
                            else "user"
                        )

                        preferenceManager.saveIsClinic(
                            (isClinic.value == true)
                        )
                        callLogin()
                    }
                } else {
                    DialogUtils.showSnackBar(
                        view.context,
                        view.context.resources.getString(R.string.no_connection)
                    )
                }
            }
            R.id.tvLoginSignUp -> {
                KeyboardUtils.hideKeyboard(view)
                redirectToSignUp.postValue(true)
            }
            R.id.tvForgotPassword -> {
                KeyboardUtils.hideKeyboard(view)
                redirectToForgotPassword.postValue(true)
            }
        }
    }

    //validate login fields
    private fun validateFields(): Boolean {
        var isValidate = true
        when {
            !isNullOrEmpty(email.value) && !Patterns.EMAIL_ADDRESS.matcher(
                email.value.toString().trim()
            ).matches() -> {
                showToastError.postValue(App.instance?.getString(R.string.error_email))
                isValidate = false
            }
            !isNullOrEmpty(phone.value) && !isValidPhoneNumber(
                phone.value.toString().trim(),
                region
            ) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_phone))
                isValidate = false
            }
            isNullOrEmpty(email.value) && isNullOrEmpty(phone.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_phone_email))
                isValidate = false
            }
            isNullOrEmpty(password.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_password))
                isValidate = false
            }
//            !(Pattern.compile("^((?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*\\W).{8,32})\$")
//                .matcher(password.value.toString()).matches()) -> {
//                showToastError.postValue(App.instance?.getString(R.string.error_password))
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

    private fun callLogin() {
        scope.launch {
            Constants.IS_SIGN_IN = true
            if (!isNullOrEmpty(email.value) || !isNullOrEmpty(phone.value)) {
                password.value?.let { it1 ->
                    val username = if (!isNullOrEmpty(email.value)) {
                        email.value?.trim()
                    } else "${code.value}${phone.value}"

                    val model = username?.let {
                        ReqLoginModel(
                            userName = it,
                            password = it1
                        )
                    }
                    model?.let {
                        userRepository.login(it)
                    }

                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        userRepository.userLiveData.removeObserver(userObserver)
    }
}