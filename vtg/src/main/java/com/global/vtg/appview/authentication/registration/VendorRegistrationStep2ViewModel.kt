package com.global.vtg.appview.authentication.registration

import android.app.Application
import android.content.Context
import android.net.Uri
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.App
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
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

class VendorRegistrationStep2ViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {

    var businessName: MutableLiveData<String> = MutableLiveData()
    var businessId: MutableLiveData<String> = MutableLiveData()
    var employeeId: MutableLiveData<String> = MutableLiveData()
    var businessVat: MutableLiveData<String> = MutableLiveData()
    var businessNIS: MutableLiveData<String> = MutableLiveData()
    var businesstin: MutableLiveData<String> = MutableLiveData()
    var state: MutableLiveData<String> = MutableLiveData()
    var country: MutableLiveData<String> = MutableLiveData()
    var zip: MutableLiveData<String> = MutableLiveData()
    var phone: MutableLiveData<String> = MutableLiveData()
    var email: MutableLiveData<String> = MutableLiveData()
    var city: MutableLiveData<String> = MutableLiveData()
    var website: MutableLiveData<String> = MutableLiveData()
    var websiteName: MutableLiveData<String> = MutableLiveData()
    var expiryDate: MutableLiveData<String> = MutableLiveData()
    val chooseFile: MutableLiveData<Boolean> = MutableLiveData()
    var documentPath: String? = null
    val registerLiveData = MutableLiveData<Resource<ResUser>>()
    val registerLiveDataAlready = MutableLiveData<String>()
    var isDataAvailable: Boolean = false
    var showToastError: MutableLiveData<String> = MutableLiveData()

    val registerVendorStep2LiveData = MutableLiveData<Resource<ResUser>>()
   // val userConfigLiveData = MutableLiveData<Resource<ResUser>>()


    private val userObserver = Observer<Resource<ResUser>> {
        registerVendorStep2LiveData.postValue(it)
       // userConfigLiveData.postValue(it)
    }

    init {
        userRepository.registerVendorStep2LiveData.postValue(null)
        userRepository.registerVendorStep2LiveData.observeForever(userObserver)
        userRepository.userConfigLiveData.postValue(null)
        userRepository.userConfigLiveData.observeForever(userObserver)
    }

    fun onClick(view: View) {

        when (view.id) {
            R.id.cvUploadDocument -> {
                chooseFile.postValue(true)
            }
            R.id.btnNext -> {
                KeyboardUtils.hideKeyboard(view)

                if(isDataAvailable){
                    var utcDate=DateUtils.formatLocalToUtc(expiryDate.value!!,false,DateUtils.API_DATE_FORMAT)
                    val date: RequestBody? = utcDate?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

                    registerLiveDataAlready.postValue("")
                }else {
                    if (isNetworkAvailable(view.context)) {
                        if (validateFields()) {
                            KeyboardUtils.hideKeyboard(view)
                            Constants.USER?.step2Complete = true
                            callRegisterStep(view.context)
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
    }

    //validate login fields
    private fun validateFields(): Boolean {
        var isValidate = true
        when {
            isNullOrEmpty(documentPath) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_select_document))
                isValidate = false
            }
            isNullOrEmpty(businessName.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_business_name))
                isValidate = false
            }
            isNullOrEmpty(businessId.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_business_id))
                isValidate = false
            }
            isNullOrEmpty(employeeId.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_employee_id))
                isValidate = false
            }
            isNullOrEmpty(expiryDate.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_expiry_date))
                isValidate = false
            }
            isNullOrEmpty(businessVat.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.enter_vat))
                isValidate = false
            }
            isNullOrEmpty(state.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_address_state))
                isValidate = false
            }
            isNullOrEmpty(country.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_address_country))
                isValidate = false
            }
            isNullOrEmpty(businessNIS.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_nis))
                isValidate = false
            }
            isNullOrEmpty(businesstin.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_tin))
                isValidate = false
            }

            isNullOrEmpty(email.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_email))
                isValidate = false
            }
            isNullOrEmpty(phone.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_phone_1))
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

    private fun callRegisterStep(context: Context) {

        scope.launch {

                val file = File(documentPath)
                val part = MultipartBody.Part.createFormData(
                    "file", file.name,
                    file.asRequestBody(
                        getMimeType(
                            context,
                            Uri.fromFile(file)
                        )?.toMediaTypeOrNull()
                    )
                )

            val vendorId: RequestBody = Constants.USER?.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val businessName: RequestBody? = businessName.value?.toRequestBody("text/plain".toMediaTypeOrNull())
            val businessId: RequestBody? = businessId.value?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val employeeID: RequestBody? = employeeId.value?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val vat: RequestBody? = businessVat.value?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val state: RequestBody? = state.value?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val country: RequestBody? = country.value?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            zip.value="000"

            val zip: RequestBody? = zip.value?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val city: RequestBody? = city.value?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val web: RequestBody? = websiteName.value?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val nis: RequestBody? = businessNIS.value?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val tin: RequestBody? = businesstin.value?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val email: RequestBody? = email.value?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val phone: RequestBody? = phone.value?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            var utcDate=DateUtils.formatLocalToUtc(expiryDate.value!!,false,DateUtils.API_DATE_FORMAT)
            val date: RequestBody? = utcDate?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            userRepository.uploadVendorStep2(
                part, vendorId, businessName, businessId, employeeID
                ,date,vat,state, country,zip,web,city,nis,tin,email,phone
            )
        }
    }

    fun completeStep2() {
        scope.launch {
            Constants.USER?.let { userRepository.registerStep(registerLiveData, it) }
        }
    }

    fun getUser() {
        scope.launch {
            userRepository.getUser()
        }
    }


}