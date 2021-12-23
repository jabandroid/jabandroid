package com.global.vtg.appview.home.health

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.App
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.appview.config.ResInstitute
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.vtg.R
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class UploadHealthDocumentViewModel(
    application: Application,
    private val userRepository: UserRepository
) :
    AppViewModel(application) {
    var instituteId: Int? = null
    var documentPath: String? = null
    var file: File? = null
    var result: String? = null
    var day: String? = null
    var date: String? = null
    var time: String? = null
    var dob: String? = null
    var region: String = "US"
    var code: MutableLiveData<String> = MutableLiveData()
    var phone: MutableLiveData<String> = MutableLiveData()
    val cancelDoc: MutableLiveData<Boolean> = MutableLiveData()
    val chooseFile: MutableLiveData<Boolean> = MutableLiveData()
    val isCertify: MutableLiveData<Boolean> = MutableLiveData(false)
    val fee: MutableLiveData<String> = MutableLiveData()
    val hospitalName: MutableLiveData<String> = MutableLiveData()
    var showToastError: MutableLiveData<String> = MutableLiveData()
    var saveSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var batchNo: MutableLiveData<String> = MutableLiveData()


    val instituteLiveData = MutableLiveData<Resource<ResInstitute>>()

    private val observer = Observer<Resource<ResInstitute>> {
        instituteLiveData.postValue(it)
    }

    val userLiveData = MutableLiveData<Resource<ResUser>>()

    private val userObserver = Observer<Resource<ResUser>> {
        userLiveData.postValue(it)
    }


    init {
        userRepository.searchInstituteLiveData.postValue(null)
        userRepository.searchInstituteLiveData.observeForever(observer)

        userRepository.userLiveData.postValue(null)
        userRepository.userLiveData.observeForever(userObserver)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.cvUploadDocument -> {
                chooseFile.postValue(true)
            }
            R.id.ivCancel -> {
                cancelDoc.postValue(true)
            }
            R.id.btnHealthSaveDocument -> {
                if (isNetworkAvailable(view.context)) {
                    if (validateFields()) {
                        // TODO: Api call
//                        saveSuccess.postValue(true)
                        uploadHealthInfo()
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

    private fun uploadHealthInfo() {
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

            var instituteIdReq: RequestBody? = null
            if (instituteId != null) {
                instituteIdReq = instituteId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            }

            val status: RequestBody = "Pending"
                .toRequestBody("text/plain".toMediaTypeOrNull())

            val srId = batchNo.value.toString().trim()
            var batchNo: RequestBody? = null
            if (!isNullOrEmpty(srId))
                batchNo = srId.toRequestBody("text/plain".toMediaTypeOrNull())


            var resultB: RequestBody? = null
            if (!isNullOrEmpty(result))
                resultB = result!!.toRequestBody("text/plain".toMediaTypeOrNull())


            val certified: RequestBody = if (Constants.USER?.role.equals("ROLE_CLINIC")) {
                "true"
                    .toRequestBody("text/plain".toMediaTypeOrNull())
            } else {

                (if (isCertify.value == false) "false" else "true")
                    .toRequestBody("text/plain".toMediaTypeOrNull())
            }
            var dateReq: RequestBody? = null
            if (date != null) {
                dateReq = date?.toRequestBody("text/plain".toMediaTypeOrNull())
            }
            val username: RequestBody? = if (Constants.USER?.role.equals("ROLE_CLINIC")) {
                "${code.value}${phone.value}".toRequestBody(
                    "text/plain".toMediaTypeOrNull()
                )
            } else {
                Constants.USER?.mobileNo?.toRequestBody(
                    "text/plain".toMediaTypeOrNull())
            }
            userRepository.uploadHealthInfo(
                part,
                id,
                instituteIdReq,
                status,
                certified,
                dateReq,
                batchNo,
                resultB,
                username
            )
        }
    }

    private fun validateFields(): Boolean {
        var isValidate = true
        when {
            isNullOrEmpty(documentPath) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_select_document))
                isValidate = false
            }
            Constants.USER?.role.equals("ROLE_CLINIC") && isNullOrEmpty(phone.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_phone))
                isValidate = false
            }
            Constants.USER?.role.equals("ROLE_CLINIC") && !Constants.isValidPhoneNumber(
                phone.value.toString().trim(), region
            ) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_phone))
                isValidate = false
            }
            isCertify.value == true && isNullOrEmpty(fee.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_fee))
                isValidate = false
            }
            isNullOrEmpty(date) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_select_date))
                isValidate = false
            }
            isNullOrEmpty(time) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_select_time))
                isValidate = false
            }
            instituteId == null -> {
                showToastError.postValue(App.instance?.getString(R.string.error_hospital_name))
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

    fun searchInstitute(text: String) {
        scope.launch {
            userRepository.searchInstitute(text)
        }
    }
}