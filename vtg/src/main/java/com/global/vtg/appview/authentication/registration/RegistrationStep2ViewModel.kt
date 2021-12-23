package com.global.vtg.appview.authentication.registration

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.App
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.KeyboardUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.vtg.R
import kotlinx.coroutines.launch

class RegistrationStep2ViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {

    var ssn: MutableLiveData<String> = MutableLiveData()
    var id: MutableLiveData<String> = MutableLiveData()
    var dln: MutableLiveData<String> = MutableLiveData()
    var dlnState: MutableLiveData<String> = MutableLiveData()
    var dlnCountry: MutableLiveData<String> = MutableLiveData()
    var dlnIssuedDate: MutableLiveData<String> = MutableLiveData()
    var dlnExpiredDate: MutableLiveData<String> = MutableLiveData()
    var passportNumber: MutableLiveData<String> = MutableLiveData()
    var passportState: MutableLiveData<String> = MutableLiveData()
    var passportCountry: MutableLiveData<String> = MutableLiveData()
    var passportIssuedDate: MutableLiveData<String> = MutableLiveData()
    var passportExpiredDate: MutableLiveData<String> = MutableLiveData()

    var showToastError: MutableLiveData<String> = MutableLiveData()
    var redirectToStep3: MutableLiveData<Boolean> = MutableLiveData()

    val registerLiveData = MutableLiveData<Resource<ResUser>>()


    val userConfigLiveData = MutableLiveData<Resource<ResUser>>()

    private val userObserver = Observer<Resource<ResUser>> {
        userConfigLiveData.postValue(it)
    }

    fun onClick(view: View) {

        when (view.id) {
            R.id.btnNext -> {
                KeyboardUtils.hideKeyboard(view)
                if (isNetworkAvailable(view.context)) {
                    if (validateFields()) {
                        KeyboardUtils.hideKeyboard(view)
                        Constants.USER?.step2Complete = true
                        val document = ArrayList<Document>()
                        if (!isNullOrEmpty(id.value?.trim())) {
                            document.add(
                                Document(
                                    type = "ID",
                                    identity = id.value
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
                                    type = "SSN",
                                    identity = ssn.value
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
                                    type = "DL",
                                    identity = dln.value,
                                    issueDate = dlnIssuedDate.value,
                                    expireDate = dlnExpiredDate.value,
                                    state = dlnState.value,
                                    country = dlnCountry.value
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
                                    type = "Passport",
                                    identity = passportNumber.value,
                                    issueDate = passportIssuedDate.value,
                                    expireDate = passportExpiredDate.value,
                                    state = passportState.value,
                                    country = passportCountry.value
                                )
                            )
                        }
                        if (!document.isNullOrEmpty()) {
                            Constants.USER?.document?.clear()
                            Constants.USER?.document?.addAll(document)
                        }

                        callRegisterStep()
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
                ( isNullOrEmpty(id.value)&&!isNullOrEmpty(dln.value)) -> {
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

                (!isNullOrEmpty(id.value)&&isNullOrEmpty(dln.value)) -> {

                }
                (isNullOrEmpty(id.value)&&isNullOrEmpty(dln.value)) -> {
                    showToastError.postValue(App.instance?.getString(R.string.empty_id_number))
                    isValidate = false
                }
//            isNullOrEmpty(passportNumber.value) -> {
//                showToastError.postValue(App.instance?.getString(R.string.empty_passport))
//                isValidate = false
//            }
//            isNullOrEmpty(passportState.value) -> {
//                showToastError.postValue(App.instance?.getString(R.string.empty_passport_state))
//                isValidate = false
//            }
//            isNullOrEmpty(passportCountry.value) -> {
//                showToastError.postValue(App.instance?.getString(R.string.empty_passport_country))
//                isValidate = false
//            }
//            isNullOrEmpty(passportIssuedDate.value) -> {
//                showToastError.postValue(App.instance?.getString(R.string.empty_passport_issued_date))
//                isValidate = false
//            }
//            isNullOrEmpty(passportExpiredDate.value) -> {
//                showToastError.postValue(App.instance?.getString(R.string.empty_passport_expiry_date))
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

    private fun callRegisterStep() {
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