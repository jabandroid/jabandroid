package com.global.vtg.appview.authentication.registration

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.App
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.model.network.result.BaseResult
import com.global.vtg.utils.Constants
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
import java.util.*

class RegistrationStep2ViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {

    var ssn: MutableLiveData<String> = MutableLiveData()
    var ssnFinal: String = ""
    var isChildAccount: Boolean = false
    var id: MutableLiveData<String> = MutableLiveData()
    var idBbEdit: MutableLiveData<String> = MutableLiveData()
    var dln: MutableLiveData<String> = MutableLiveData()
    var dlnState: MutableLiveData<String> = MutableLiveData()
    var dlnCountry: MutableLiveData<String> = MutableLiveData()
    var dlnIssuedDate: MutableLiveData<String> = MutableLiveData()
    var dlnExpiredDate: MutableLiveData<String> = MutableLiveData()
    var passportNumber: MutableLiveData<String> = MutableLiveData()
    var passportState: MutableLiveData<String> = MutableLiveData()
    var passportCountry: MutableLiveData<String> = MutableLiveData()
    var passportIssuedDate: MutableLiveData<String> = MutableLiveData()
    var etBbIssuedDate: MutableLiveData<String> = MutableLiveData()
    var passportExpiredDate: MutableLiveData<String> = MutableLiveData()

    var documentPathId: String? = null
    var idUrl: String? = null
    var idId: Int? = null
    var idBB: Int? = null
    var dlId: Int? = null
    var ssnId: Int? = null
    var ppId: Int? = null
    var dlUrl: String? = null
    var ppUrl: String? = null
    var bbUrl: String? = ""
    var typeOfPic: Int? = null
    var documentPathDl: String? = null
    var documentPathPP: String? = null
    var documentPathBB: String? = null
    var showToastError: MutableLiveData<String> = MutableLiveData()
    var redirectToStep3: MutableLiveData<Boolean> = MutableLiveData()

    val registerLiveData = MutableLiveData<Resource<ResUser>>()
    val uploadPicLiveData = MutableLiveData<Resource<BaseResult>>()


    val userConfigLiveData = MutableLiveData<Resource<ResUser>>()

    private val userObserver = Observer<Resource<ResUser>> {
        userConfigLiveData.postValue(it)
    }
    private val uploadOberver = Observer<Resource<BaseResult>> {
        uploadPicLiveData.postValue(it)
    }


    init {
        userRepository.userPicLiveData.postValue(null)
        userRepository.userPicLiveData.observeForever(uploadOberver)


    }

    fun onClick(view: View) {

        when (view.id) {
            R.id.btnNext -> {
                KeyboardUtils.hideKeyboard(view)
                if (isNetworkAvailable(view.context)) {
                    if (validateFields()) {
                        KeyboardUtils.hideKeyboard(view)
                        if (isChildAccount) {
                            if (Constants.USERCHILD == null)
                                Constants.USERCHILD = ResUser()
                            Constants.USERCHILD?.step2Complete = true
                            val document = ArrayList<Document>()
                            if (!isNullOrEmpty(id.value?.trim())) {
                                document.add(
                                    Document(
                                        id=idId,
                                        type = "ID",
                                        identity = id.value,
                                        url = idUrl
                                    )
                                )
                            }

                            if (!isNullOrEmpty(bbUrl!!.trim())||!isNullOrEmpty(idBbEdit.value)) {
                                document.add(
                                    Document(
                                        id=idBB,
                                        type = "BirthCertificate",
                                        identity=idBbEdit.value,
                                        issueDate=etBbIssuedDate.value,

                                        url = bbUrl
                                    )
                                )
                            }
                            if (!isNullOrEmpty(
                                    ssn
                                        .value?.trim()
                                )
                            ) {
                                document.add(
                                    Document(
                                        id=ssnId,
                                        type = "SSN",
                                        identity = if (ssn.value!!.contains("XX")) ssnFinal else ssn.value
                                    )
                                )
                            }
                            if (!isNullOrEmpty(dln.value)
                                && !isNullOrEmpty(dlnState.value)
                                && !isNullOrEmpty(dlnCountry.value)
                                && !isNullOrEmpty(dlnIssuedDate.value)
                                && !isNullOrEmpty(dlnExpiredDate.value)
                            ) {
                                document.add(
                                    Document(
                                        id=dlId,
                                        type = "DL",
                                        identity = dln.value,
                                        issueDate = dlnIssuedDate.value,
                                        expireDate = dlnExpiredDate.value,
                                        state = dlnState.value,
                                        country = dlnCountry.value,
                                        url = dlUrl

                                    )
                                )
                            }
                            if (!isNullOrEmpty(passportNumber.value)
                                && !isNullOrEmpty(passportState.value)
                                && !isNullOrEmpty(passportCountry.value)
                                && !isNullOrEmpty(passportIssuedDate.value)
                                && !isNullOrEmpty(passportExpiredDate.value)
                            ) {
                                document.add(
                                    Document(
                                        id=ppId,
                                        type = "Passport",
                                        identity = passportNumber.value,
                                        issueDate = passportIssuedDate.value,
                                        expireDate = passportExpiredDate.value,
                                        state = passportState.value,
                                        country = passportCountry.value,
                                        url = ppUrl
                                    )
                                )
                            }
                            if (!document.isNullOrEmpty()) {
                                Constants.USERCHILD?.document?.clear()
                                Constants.USERCHILD?.document?.addAll(document)
                            }
                            Constants.USERCHILD?.parentId = Constants.USER!!.id.toString()
                            callRegisterStepChild()
                        } else {

                            Constants.USER?.step2Complete = true
                            val document = ArrayList<Document>()
                            if (!isNullOrEmpty(id.value?.trim())) {
                                document.add(
                                    Document(
                                        id=idId,
                                        type = "ID",
                                        identity = id.value,
                                        url = idUrl
                                    )
                                )
                            }

                            if (!isNullOrEmpty(bbUrl!!.trim())||!isNullOrEmpty(idBbEdit.value)) {
                                document.add(
                                    Document(
                                        id=idBB,
                                        type = "BirthCertificate",
                                        identity=idBbEdit.value,
                                        issueDate=etBbIssuedDate.value,

                                        url = bbUrl
                                    )
                                )
                            }
                            if (!isNullOrEmpty(
                                    ssn
                                        .value?.trim()
                                )
                            ) {
                                document.add(
                                    Document(
                                        id=ssnId,
                                        type = "SSN",
                                        identity = if (ssn.value!!.contains("XX")) ssnFinal else ssn.value
                                    )
                                )
                            }
                            if (!isNullOrEmpty(dln.value)
                                && !isNullOrEmpty(dlnState.value)
                                && !isNullOrEmpty(dlnCountry.value)
                                && !isNullOrEmpty(dlnIssuedDate.value)
                                && !isNullOrEmpty(dlnExpiredDate.value)
                            ) {
                                document.add(
                                    Document(
                                        id=dlId,
                                        type = "DL",
                                        identity = dln.value,
                                        issueDate = dlnIssuedDate.value,
                                        expireDate = dlnExpiredDate.value,
                                        state = dlnState.value,
                                        country = dlnCountry.value,
                                        url = dlUrl
                                    )
                                )
                            }
                            if (!isNullOrEmpty(passportNumber.value)
                                && !isNullOrEmpty(passportState.value)
                                && !isNullOrEmpty(passportCountry.value)
                                && !isNullOrEmpty(passportIssuedDate.value)
                                && !isNullOrEmpty(passportExpiredDate.value)
                            ) {
                                document.add(
                                    Document(
                                        id=ppId,
                                        type = "Passport",
                                        identity = passportNumber.value,
                                        issueDate = passportIssuedDate.value,
                                        expireDate = passportExpiredDate.value,
                                        state = passportState.value,
                                        country = passportCountry.value,
                                        url = ppUrl
                                    )
                                )
                            }
                            if (!document.isNullOrEmpty()) {
                                Constants.USER?.document?.clear()
                                Constants.USER?.document?.addAll(document)
                            }

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
        }
    }

    //validate login fields
    private fun validateFields(): Boolean {
        var isValidate = true



        when {
//            isNullOrEmpty(ssn.value) -> {
//                showToastError.postValue(App.instance?.getString(R.string.empty_ssn))
//                isValidate = false
//            }
            (isNullOrEmpty(id.value) && !isNullOrEmpty(dln.value)) -> {
//                    showToastError.postValue(App.instance?.getString(R.string.empty_id_number))
//                    isValidate = false
                when {
                    isNullOrEmpty(dln.value) -> {
                        showToastError.postValue(App.instance?.getString(R.string.empty_dln))
                        isValidate = false
                    }
                    isNullOrEmpty(dlnState.value) -> {
                        showToastError.postValue(App.instance?.getString(R.string.empty_dln_state))
                        isValidate = false
                    }
                    isNullOrEmpty(dlnCountry.value) -> {
                        showToastError.postValue(App.instance?.getString(R.string.empty_dln_country))
                        isValidate = false
                    }
                    isNullOrEmpty(dlnIssuedDate.value) -> {
                        showToastError.postValue(App.instance?.getString(R.string.empty_dln_issued_date))
                        isValidate = false
                    }
                    isNullOrEmpty(dlnExpiredDate.value) -> {
                        showToastError.postValue(App.instance?.getString(R.string.empty_dln_expiry_date))
                        isValidate = false
                    }
                }
            }

            (!isNullOrEmpty(id.value) && isNullOrEmpty(dln.value)) -> {

            }
            (isNullOrEmpty(id.value) && isNullOrEmpty(dln.value)) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_id_number))
                isValidate = false
            }

            !isNullOrEmpty(passportNumber.value) -> {
                when {
                    isNullOrEmpty(passportNumber.value) -> {
                        showToastError.postValue(App.instance?.getString(R.string.empty_passport))
                        isValidate = false
                    }
                    isNullOrEmpty(passportState.value) -> {
                        showToastError.postValue(App.instance?.getString(R.string.empty_passport_state))
                        isValidate = false
                    }
                    isNullOrEmpty(passportCountry.value) -> {
                        showToastError.postValue(App.instance?.getString(R.string.empty_passport_country))
                        isValidate = false
                    }
                    isNullOrEmpty(passportIssuedDate.value) -> {
                        showToastError.postValue(App.instance?.getString(R.string.empty_passport_issued_date))
                        isValidate = false
                    }
                    isNullOrEmpty(passportExpiredDate.value) -> {
                        showToastError.postValue(App.instance?.getString(R.string.empty_passport_expiry_date))
                        isValidate = false
                    }
                }
            }
//
            else -> {
                showToastError.postValue("")
            }
        }


        if (!isValidate)
            return false

        return isValidate
    }

    private fun callRegisterStep() {
        scope.launch {
            Constants.USER?.let { userRepository.registerStep(registerLiveData, it) }
        }
    }

    private fun callRegisterStepChild() {
        scope.launch {
            Constants.USERCHILD?.let { userRepository.registerStep(registerLiveData, it) }
        }
    }


    fun getUser() {
        scope.launch {
            userRepository.getUser()
        }
    }


    public fun getAge(mill: Long): Int? {
        var ageInt = 0
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        try {

            dob.timeInMillis = mill!!
            var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
            if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
                age--
            }
            ageInt = age
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ageInt
    }


    fun uploadProfile() {
        scope.launch {
            var path = ""
            when (typeOfPic) {
                1 -> {
                    path = documentPathId.toString()

                }
                2 -> {
                    path = documentPathDl.toString()
                }
                3 -> {
                    path = documentPathPP.toString()
                }
                4 -> {
                    path = documentPathBB.toString()
                }
            }
            val file: File
            var part: MultipartBody.Part? = null
            if (path != null) {
                file = File(path)
                part = MultipartBody.Part.createFormData(
                    "file", file.name,
                    file.asRequestBody("image/png".toMediaTypeOrNull())
                )
            }
            val id: RequestBody = Constants.USER?.id.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
            userRepository.uploadPic(part, id)
        }
    }

}