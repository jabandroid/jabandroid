package com.global.vtg.appview.home.changepassword

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.App
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.KeyboardUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.vtg.R
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class ChangePasswordViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {
    var oldPassword: MutableLiveData<String> = MutableLiveData()
    var newPassword: MutableLiveData<String> = MutableLiveData()
    var confirmNewPassword: MutableLiveData<String> = MutableLiveData()
    var redirectToSignIn: MutableLiveData<Boolean> = MutableLiveData()
    var showToastError: MutableLiveData<String> = MutableLiveData()
    val userLiveData = MutableLiveData<Resource<ResUser>>()

    private val userObserver = Observer<Resource<ResUser>> {
        userLiveData.postValue(it)
    }

    init {
        userRepository.userLiveData.postValue(null)
        userRepository.userLiveData.observeForever(userObserver)
    }

    fun onClick(view: View) {

        when (view.id) {
            R.id.btnUpdate -> {
                KeyboardUtils.hideKeyboard(view)
                if (isNetworkAvailable(view.context)) {
                    if (validateFields()) {
                        Constants.USER?.password = newPassword.value?.trim()
                        callChangePassword()
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
            isNullOrEmpty(oldPassword.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_old_password))
                isValidate = false
            }
            isNullOrEmpty(newPassword.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_new_password))
                isValidate = false
            }
            isNullOrEmpty(confirmNewPassword.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_confirm_new_password))
                isValidate = false
            }
            !(oldPassword.value.equals(Constants.USER?.password)) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_old_password))
                isValidate = false
            }
            !(Pattern.compile("^((?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*\\W).{8,32})\$")
                .matcher(newPassword.value.toString()).matches()) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_password))
                isValidate = false
            }
            !(newPassword.value.equals(confirmNewPassword.value)) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_confirm_mismatch))
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

    private fun callChangePassword() {
        scope.launch {
            Constants.USER?.let { userRepository.changePassword(it) }
        }
    }
}