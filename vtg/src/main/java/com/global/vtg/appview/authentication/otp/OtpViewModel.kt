package com.global.vtg.appview.authentication.otp

import android.app.Application
import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.App
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.authentication.registration.ReqRegistration
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.KeyboardUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.google.gson.Gson
import com.vtg.R
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException


class OtpViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {
    var verifySuccess: Boolean = false
    var twilioUserId: Int? = null
    var showToastError: MutableLiveData<String> = MutableLiveData()
    var et1: MutableLiveData<String> = MutableLiveData()
    var et2: MutableLiveData<String> = MutableLiveData()
    var et3: MutableLiveData<String> = MutableLiveData()
    var et4: MutableLiveData<String> = MutableLiveData()
    var et5: MutableLiveData<String> = MutableLiveData()
    var et6: MutableLiveData<String> = MutableLiveData()
    var redirectToStep1: MutableLiveData<Boolean> = MutableLiveData()
    var verifyOtp: MutableLiveData<Boolean> = MutableLiveData()
    var birthChild: MutableLiveData<Boolean> = MutableLiveData()
    var twiloId: MutableLiveData<String> = MutableLiveData()
    var tokenSent: MutableLiveData<Boolean> = MutableLiveData()
    var id: Int = 0
    var email: String = ""
    var phone: String = ""
    var password: String = ""
    var childaccount: Boolean = false
    var code: String = ""
    private val client = OkHttpClient()
    var context: Context? = null
    var progressBar: MutableLiveData<Boolean> = MutableLiveData()

    val registerLiveData = MutableLiveData<Resource<ResUser>>()

    private val registerObserver = Observer<Resource<ResUser>> {
        registerLiveData.postValue(it)
    }

    init {
        userRepository.registerLiveData.postValue(null)
        userRepository.registerLiveData.observeForever(registerObserver)
    }

    fun callRegistration(
        email: String,
        password: String,
        mobileNo: String,
        code: String,
        twilioUserId: String
    ) {
        scope.launch {
            userRepository.register(
                ReqRegistration(
                    email = email,
                    password = password,
                    mobileNo = "$code$mobileNo",
                    twilioUserId = twilioUserId
                )
            )
        }
    }

    fun onClick(view: View) {

        when (view.id) {
            R.id.btnVerify -> {
                KeyboardUtils.hideKeyboard(view)
                if (isNetworkAvailable(view.context)) {
                    if (validateFields()) {
                        twiloId.postValue(twilioUserId.toString())
                        verifyOtp.postValue(true)
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
            isNullOrEmpty(et1.value)
                    and isNullOrEmpty(et2.value)
                    and isNullOrEmpty(et3.value)
                    and isNullOrEmpty(et4.value)
                    and isNullOrEmpty(et5.value)
                    and isNullOrEmpty(et6.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_otp))
                isValidate = false
            }
            isNullOrEmpty(et1.value)
                    or isNullOrEmpty(et2.value)
                    or isNullOrEmpty(et3.value)
                    or isNullOrEmpty(et4.value)
                    or isNullOrEmpty(et5.value)
                    or isNullOrEmpty(et6.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_otp))
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

    fun createUser() {

        val requestBody: RequestBody = FormBody.Builder()
            .add("user[email]", email)
            .add("user[cellphone]", phone)
            .add("user[country_code]", code)
            .build()
        val request = Request.Builder()
            .url("https://api.authy.com/protected/json/users/new")
            .post(requestBody)
            .addHeader("X-Authy-API-Key", "ORdNXMXiNsABeKDoid1jvjCw1pWHOmDi")
            .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressBar.postValue(false)
                showToastError.postValue(e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body?.string()
                val resTwilioUser =
                    Gson().fromJson(res, ResCreateTwilioUser::class.java)
                twilioUserId = resTwilioUser.user?.id
                if (resTwilioUser.success == false) {
                    DialogUtils.showSnackBar(context, resTwilioUser.message.toString())
                    progressBar.postValue(false)
                } else {
                    sendToken()
                }
            }
        })
    }

    fun sendToken() {
        val request = Request.Builder()
            .url("https://api.authy.com/protected/json/sms/$twilioUserId?force=true")
            .get()
            .addHeader("X-Authy-API-Key", "ORdNXMXiNsABeKDoid1jvjCw1pWHOmDi")
            .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressBar.postValue(false)
                showToastError.postValue(e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body?.string()
                val resTwilioToken =
                    Gson().fromJson(res, ResToken::class.java)
                if (resTwilioToken.success == false) {
                    progressBar.postValue(false)
                    DialogUtils.showSnackBar(context, resTwilioToken.message.toString())
                } else {
                    tokenSent.postValue(true)
                }
            }
        })
    }

    fun verifyOTP() {
        val request = Request.Builder()
            .url(
                "https://api.authy.com/protected/json/verify/" +
                        et1.value.toString().trim() +
                        et2.value.toString().trim() +
                        et3.value.toString().trim() +
                        et4.value.toString().trim() +
                        et5.value.toString().trim() +
                        et6.value.toString().trim() +
                        "/$twilioUserId"
            )
            .get()
            .addHeader("X-Authy-API-Key", "ORdNXMXiNsABeKDoid1jvjCw1pWHOmDi")
            .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressBar.postValue(false)
                showToastError.postValue(e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body?.string()
                val resTwilioToken =
                    Gson().fromJson(res, ResToken::class.java)
                if (resTwilioToken.success == false) {
                    progressBar.postValue(false)
                    DialogUtils.showSnackBar(context, resTwilioToken.message.toString())
                } else {
                    verifySuccess = true
                    twiloId.postValue(twilioUserId.toString())
                    if(childaccount)
                        birthChild.postValue(true)
                    else redirectToStep1.postValue(true)

                }
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        userRepository.registerLiveData.removeObserver(registerObserver)
    }
}