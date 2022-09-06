package com.global.vtg.appview.authentication.registration

import android.app.Application
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.App
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.home.profile.ResProfile
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.*
import com.global.vtg.utils.Constants.USER
import com.global.vtg.utils.Constants.USERCHILD
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.google.gson.Gson
import com.vtg.R
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*

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
    var dobDate: Date? = null
    var apiDob: String = ""
    var childAccount: Boolean = false
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
    var docName: String? = null
    val registerStep1LiveData = MutableLiveData<Resource<ResUser>>()
    val registerStep1LiveDataskip = MutableLiveData<Resource<ResUser>>()

    val userConfigLiveData = MutableLiveData<Resource<ResUser>>()
    val uploadProfilePic: MutableLiveData<Boolean> = MutableLiveData()
    val childAccountLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val userObserver = Observer<Resource<ResUser>> {
        userConfigLiveData.postValue(it)

    }

    val uploadProfilePicStep1 = MutableLiveData<Resource<ResProfile>>()
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
                    var userType = SharedPreferenceUtil.getInstance(view.context)
                        ?.getData(
                            PreferenceManager.KEY_LOGGED_IN_USER_TYPE,
                            ""
                        )
                    var k = SharedPreferenceUtil.getInstance(view.context)
                        ?.getData(
                            PreferenceManager.KEY_IS_CHILD,
                            false
                        )

                    if (k == true) {
                        userType = "child"
                    }


                    if (childAccount) {
                        if (validateFields("child")) {

                            if (USERCHILD == null)
                                USERCHILD = ResUser()
                            USERCHILD?.title = title.value
                            USERCHILD?.firstName = firstName.value!!.substring(0, 1)
                                .uppercase() + firstName.value!!.substring(1)
                            USERCHILD?.lastName = lastName.value!!.substring(0, 1)
                                .uppercase() + lastName.value!!.substring(1)
                            USERCHILD?.citizenship = citizenship.value
                            USERCHILD?.dateOfBirth = apiDob
                            USERCHILD?.birthCity = city.value
                            USERCHILD?.birthState = state.value
                            USERCHILD?.birthCountry = country.value
                            USERCHILD?.ethnicity =
                                if (ethnicity.value.equals("--Select--")) "" else ethnicity.value
                            USERCHILD?.gender = gender.value
                            USER?.role = "user"
                            USERCHILD?.website = websiteName.value
                            USERCHILD?.isClinic = isClinic == true
                            USERCHILD?.step1Complete = true
                            USERCHILD?.parentId = USER!!.id.toString()

                            val gson = Gson()
                            val request: Any = gson.toJson(USERCHILD)
                            //  USERCHILD!!.id=452
                            if (USERCHILD!!.id != null && USERCHILD!!.id!! > 0) {
                                callRegisterStepChildUpdate()
                            } else
                                callRegisterStepChild()

                            //childAccountLiveData.postValue(true)

                        }
                    } else {

                        if (validateFields(userType!!)) {
                            KeyboardUtils.hideKeyboard(view)

                            USER?.title = title.value
                            USER?.firstName = firstName.value!!.substring(0, 1)
                                .uppercase() + firstName.value!!.substring(1)
                            USER?.lastName = lastName.value!!.substring(0, 1)
                                .uppercase() + lastName.value!!.substring(1)
                            USER?.citizenship = citizenship.value
                            USER?.dateOfBirth = apiDob
                            USER?.birthCity = city.value
                            USER?.birthState = state.value
                            USER?.birthCountry = country.value

                            USER?.ethnicity =
                                if (ethnicity.value.equals("--Select--")) "" else ethnicity.value
                            USER?.gender = gender.value

                            if(userType!="child")
                            USER?.twilioUserId = Constants.twilioUserId.toString()
                            if (isVendor)
                                USER?.role = "vendor"
                            else if (isClinic)
                                USER?.role = "clinic"
                            else "user"
                            USER?.website = websiteName.value
                            USER?.isClinic = isClinic == true
                            USER?.step1Complete = true

                            val gson = Gson()
                            val request: Any = gson.toJson(USER)

                            callRegisterStep()

                        }
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
                    var userType = SharedPreferenceUtil.getInstance(view.context)
                        ?.getData(
                            PreferenceManager.KEY_LOGGED_IN_USER_TYPE,
                            ""
                        )
                    if (validateFields(userType!!)) {
                        KeyboardUtils.hideKeyboard(view)
                        USER?.title = title.value
                        USER?.firstName = firstName.value!!.substring(0, 1)
                            .uppercase() + firstName.value!!.substring(1)
                        USER?.lastName = lastName.value!!.substring(0, 1)
                            .uppercase() + lastName.value!!.substring(1)
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

    private fun callRegisterStepChild() {
        scope.launch {
            USERCHILD?.let { userRepository.registerStepChild(registerStep1LiveData, it) }
        }
    }

    private fun callRegisterStepChildUpdate() {
        scope.launch {
            USERCHILD?.let { userRepository.registerStep(registerStep1LiveData, it) }
        }
    }


    private fun callRegisterskipStep() {
        scope.launch {
            USER?.let { userRepository.registerStep(registerStep1LiveDataskip, it) }
        }
    }

    //validate fields
    private fun validateFields(str: String): Boolean {
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
            isNullOrEmpty(lastName.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_last_name))
                isValidate = false
            }

            isNullOrEmpty(documentPath) -> {
                showToastError.postValue(App.instance?.getString(R.string.upload_image))
                isValidate = false
            }


            str == "User" -> {

                if(TextUtils.isEmpty(apiDob)){
                    showToastError.postValue(App.instance?.getString(R.string.empty_dob))
                    isValidate = false
                }else {
                    if (getAge()!! < 13) {
                        showToastError.postValue("dob")
                        isValidate = false
                    }
                }
            }

            str == "child" -> {
                if (getAge()!! > 18) {
                    showToastError.postValue("dobchild")
                    isValidate = false
                }
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

    public fun getAge(): Int? {
        var ageInt=0
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        try {

            dob.time = dobDate!!
            var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
            if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
                age--
            }
             ageInt = age

        }catch (e:Exception){
            e.printStackTrace()
        }
        return ageInt
    }

    fun getAgeString(date: String): String {


        val date = DateUtils.getDate(
            date,
            DateUtils.API_DATE_FORMAT
        )

        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()

        dob.time = date
        var k = today[Calendar.YEAR]
        var t = dob[Calendar.YEAR]
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        var ageInt = ""

        if (age > 0) {
            if (age == 1)
                ageInt = "$age year"
            else
                ageInt = "$age years"
        } else {
            age = (today[Calendar.MONTH] + 1) - (dob[Calendar.MONTH] + 1)
            if (age > 0) {
                if (age == 1)
                    ageInt = "$age month"
                else
                    ageInt = "$age months"
            } else {
                age = today[Calendar.DAY_OF_YEAR] - dob[Calendar.DAY_OF_YEAR]
                if (age == 0)
                    age = 1
                if (age == 1)
                    ageInt = "$age day"
                else
                    ageInt = "$age Days"
            }
        }
        return ageInt
    }



    fun uploadProfile(path: String) {
        scope.launch {
            val file: File
            var part: MultipartBody.Part? = null
            if (documentPath != null) {
                file = File(documentPath)
                part = MultipartBody.Part.createFormData(
                    "file", file.name,
                    file.asRequestBody("image/png".toMediaTypeOrNull())
                )
            }
            val id: RequestBody = Constants.USER?.id.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
            userRepository.uploadProfileStep1(part, id)
        }
    }

    fun uploadProfileChildPic(id: String) {
        scope.launch {
            val file: File
            var part: MultipartBody.Part? = null
            if (documentPath != null) {
                file = File(documentPath)
                part = MultipartBody.Part.createFormData(
                    "file", file.name,
                    file.asRequestBody("image/png".toMediaTypeOrNull())
                )
            }
            val id: RequestBody = id.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
            userRepository.uploadProfileStep1(part, id)
        }
    }
}