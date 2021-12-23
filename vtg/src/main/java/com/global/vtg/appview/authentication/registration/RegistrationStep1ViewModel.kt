package com.global.vtg.appview.authentication.registration

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.App
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.home.profile.ResProfile
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.Constants.USER
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.KeyboardUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.vtg.R
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

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
    var websiteName: MutableLiveData<String> = MutableLiveData()
    var showToastError: MutableLiveData<String> = MutableLiveData()
    var search: MutableLiveData<Boolean> = MutableLiveData()
    var documentPath: String? = null
    val registerStep1LiveData = MutableLiveData<Resource<ResUser>>()
    val registerStep1LiveDataskip = MutableLiveData<Resource<ResUser>>()

    val userConfigLiveData = MutableLiveData<Resource<ResUser>>()
    val uploadProfilePic: MutableLiveData<Boolean> = MutableLiveData()
    private val userObserver = Observer<Resource<ResUser>> {
        userConfigLiveData.postValue(it)

    }

    val uploadProfilePicStep1= MutableLiveData<Resource<ResProfile>>()
    private val userObserver1 = Observer<Resource<ResProfile>> {
        uploadProfilePicStep1.postValue(it)

    }
    init {
        userRepository.userConfigLiveData.postValue(null)
        userRepository.userConfigLiveData.observeForever(userObserver)
        userRepository.userProfilePicLiveDataStep1.postValue(null)
        userRepository.userProfilePicLiveDataStep1.observeForever(userObserver1)

    }


    override fun onCleared() {
        super.onCleared()

        userRepository.userConfigLiveData.removeObserver(userObserver)
    }

    fun onClick(view: View) {

        when (view.id) {
            R.id.ivProfilePic -> {
                if (isNetworkAvailable(view.context)) {
                    uploadProfilePic.postValue(true)
                } else {
                    DialogUtils.showSnackBar(
                        view.context,
                        view.context.resources.getString(R.string.no_connection)
                    )
                }
            }

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

                        USER?.ethnicity =
                            if (ethnicity.value.equals("--Select--")) "" else ethnicity.value
                        USER?.gender = gender.value
                        USER?.twilioUserId = Constants.twilioUserId.toString()
                        if (isVendor)
                            USER?.role = "vendor"
                        else if (isClinic)
                            USER?.role = "clinic"
                        else "user"

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

            R.id.btnSkip -> {
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

                        USER?.ethnicity =
                            if (ethnicity.value.equals("--Select--")) "" else ethnicity.value
                        USER?.gender = gender.value
                        USER?.twilioUserId = Constants.twilioUserId.toString()
                        if (isVendor)
                            USER?.role = "vendor"
                        else if (isClinic)
                            USER?.role = "clinic"
                        else "user"
                        USER?.website = websiteName.value

                        USER?.isClinic = isClinic == true
                        USER?.step1Complete = true
                        callRegisterskipStep()
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

    fun getUser() {
        scope.launch {
            userRepository.getUser()
        }
    }

    private fun callRegisterStep() {
        scope.launch {
            USER?.let { userRepository.registerStep(registerStep1LiveData, it) }
        }
    }

    private fun callRegisterskipStep() {
        scope.launch {
            USER?.let { userRepository.registerStep(registerStep1LiveDataskip, it) }
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

    fun uploadProfile(path: String) {
        scope.launch {
            val file: File
            var part: MultipartBody.Part? = null
            if (documentPath != null) {
                file = File(documentPath)
                part = MultipartBody.Part.createFormData("file", file.name,
                    file.asRequestBody("image/png".toMediaTypeOrNull())
                )
            }
            val id: RequestBody = Constants.USER?.id.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
            userRepository.uploadProfileStep1(part, id)
        }
    }
}