package com.global.vtg.appview.home.uploaddocument

import android.app.Application
import android.text.TextUtils
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
import com.global.vtg.utils.DateUtils
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


class UploadDocumentViewModel(
    application: Application,
    private val userRepository: UserRepository
) :
    AppViewModel(application) {
    var instituteId: Int? = null
    var instituteName: String? = null
    var documentPath: String? = null
    var dose: String? = null
    var vaccine: Int? = null
    var day: String? = null
    var date: String? = null
    var time: String? = null
    var region: String = "US"
    var email:String=""
    var code: MutableLiveData<String> = MutableLiveData()
    var phone: MutableLiveData<String> = MutableLiveData()
    var emailScan: MutableLiveData<String> = MutableLiveData()
    var govId: MutableLiveData<String> = MutableLiveData()
    val cancelDoc: MutableLiveData<Boolean> = MutableLiveData()
    val chooseFile: MutableLiveData<Boolean> = MutableLiveData()
    val isCertify: MutableLiveData<Boolean> = MutableLiveData(false)
    val fee: MutableLiveData<String> = MutableLiveData()
    val hospitalName: MutableLiveData<String> = MutableLiveData()
    val batchNo: MutableLiveData<String> = MutableLiveData()
    var showToastError: MutableLiveData<String> = MutableLiveData()
    var saveSuccess: MutableLiveData<Boolean> = MutableLiveData()

    val instituteLiveData = MutableLiveData<Resource<ResInstitute>>()

    private val observer = Observer<Resource<ResInstitute>> {
        instituteLiveData.postValue(it)
    }
    val userLiveData = MutableLiveData<Resource<ResUser>>()

    private val userObserver = Observer<Resource<ResUser>> {
        userLiveData.postValue(it)
    }

    val scanBarcodeLiveData = MutableLiveData<Resource<ResUser>>()


    private val userObserver1 = Observer<Resource<ResUser>> {
        scanBarcodeLiveData.postValue(it)
    }


    init {
        userRepository.searchInstituteLiveData.postValue(null)
        userRepository.searchInstituteLiveData.observeForever(observer)

        userRepository.userLiveData.postValue(null)
        userRepository.userLiveData.observeForever(userObserver)

        userRepository.scanBarcodeLiveData.postValue(null)
        userRepository.scanBarcodeLiveData.observeForever(userObserver1)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.cvUploadDocument -> {
                chooseFile.postValue(true)
            }
            R.id.ivCancel -> {
                cancelDoc.postValue(true)
            }
            R.id.btnSaveDocument -> {
                if (isNetworkAvailable(view.context)) {
                    if (validateFields()) {
                        // TODO: Api call
//                        saveSuccess.postValue(true)
                        uploadVaccine()
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

    private fun uploadVaccine() {
        scope.launch {

            val file = File(documentPath)
            val part = MultipartBody.Part.createFormData(
                "file", file.name,
                file.asRequestBody("image/png".toMediaTypeOrNull())
            )

            val dose: RequestBody? = dose?.toRequestBody("text/plain".toMediaTypeOrNull())

            val type: RequestBody? =
                vaccine?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            val id: RequestBody = Constants.USER?.id.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())

            val instituteIdReq: RequestBody = instituteId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())

            val status: RequestBody = "Pending"
                .toRequestBody("text/plain".toMediaTypeOrNull())

            val srId = batchNo.value.toString().trim()
            var batchNo: RequestBody? = null
            if (!isNullOrEmpty(srId))
                batchNo = srId.toRequestBody("text/plain".toMediaTypeOrNull())

            val certified: RequestBody = if (Constants.USER?.role.equals("ROLE_CLINIC")) {
                "true"
                    .toRequestBody("text/plain".toMediaTypeOrNull())
            } else {

                (if (isCertify.value == false) "false" else "true")
                    .toRequestBody("text/plain".toMediaTypeOrNull())
            }
            var dateReq: RequestBody? = null
            if (date != null) {
                date= "$date"
                var dateForServer=
                    DateUtils.formatLocalToUtc(date!!, true, DateUtils.API_DATE_FORMAT_TIME)

                dateReq = dateForServer?.toRequestBody("text/plain".toMediaTypeOrNull())
            }

            var insReq: RequestBody? = null
            if (instituteName != null) {

                insReq = instituteName?.toRequestBody("text/plain".toMediaTypeOrNull())
            }
//            var dateForServer=DateUtils.formatLocalToUtc(date!!,DateUtils.API_DATE_FORMAT,DateUtils.API_DATE_FORMAT)
//            val dateReq: RequestBody? = dateForServer?.toRequestBody("text/plain".toMediaTypeOrNull())
//            val username: RequestBody? = if (Constants.USER?.role.equals("ROLE_CLINIC")) {
//                phone.value!!.toRequestBody(
//                    "text/plain".toMediaTypeOrNull()
//                )
//            } else {
//                Constants.USER?.mobileNo?.toRequestBody(
//                    "text/plain".toMediaTypeOrNull()
//                )
//            }

            var username: RequestBody?=null
            if (Constants.USER?.role.equals("ROLE_CLINIC")) {

                if(!TextUtils.isEmpty(phone.value.toString())){
                    username=  "${code.value}${phone.value}".toRequestBody(
                        "text/plain".toMediaTypeOrNull()
                    )
                }else  if(!TextUtils.isEmpty(emailScan.value.toString())){
                    username=    emailScan.value!!.toRequestBody(
                        "text/plain".toMediaTypeOrNull()
                    )
                } else  if(!TextUtils.isEmpty(govId.value.toString())){
                    username=    govId.value!!.toRequestBody(
                        "text/plain".toMediaTypeOrNull()
                    )
                }


            } else {

                username=   Constants.USER?.mobileNo?.toRequestBody(
                    "text/plain".toMediaTypeOrNull()
                )
            }
            userRepository.uploadVaccine(
                part,
                type,
                id,
                instituteIdReq,
                status,
                certified,
                dateReq,
                batchNo,
                dose,
                username,
                insReq
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
            Constants.USER?.role.equals("ROLE_CLINIC") && isNullOrEmpty(phone.value)&& isNullOrEmpty(emailScan.value)&& isNullOrEmpty(govId.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.scan_user_code))
                isValidate = false
            }
//            Constants.USER?.role.equals("ROLE_CLINIC") && !Constants.isValidPhoneNumber(
//                phone.value.toString().trim(), region
//            ) -> {
//                showToastError.postValue(App.instance?.getString(R.string.error_phone))
//                isValidate = false
//            }
            isNullOrEmpty(dose) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_select_dose))
                isValidate = false
            }
            isCertify.value == true && isNullOrEmpty(fee.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_fee))
                isValidate = false
            }
            vaccine == null -> {
                showToastError.postValue(App.instance?.getString(R.string.error_select_type))
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

    fun getDataFromBarcodeId(barcodeId: String) {
        scope.launch {
            userRepository.scanBarcodeId(barcodeId)
        }
    }
    override fun onCleared() {
        super.onCleared()
        userRepository.userConfigLiveData.removeObserver(userObserver)
        userRepository.scanBarcodeLiveData.removeObserver(userObserver1)

    }
}