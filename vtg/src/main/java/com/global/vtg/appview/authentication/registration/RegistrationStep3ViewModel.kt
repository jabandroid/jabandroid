package com.global.vtg.appview.authentication.registration

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.App
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants.USER
import com.global.vtg.utils.Constants.USERCHILD
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.KeyboardUtils
import com.global.vtg.utils.SharedPreferenceUtil
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.vtg.R
import kotlinx.coroutines.launch
import okhttp3.internal.notify

class RegistrationStep3ViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {
    val childAccountLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var firstName: MutableLiveData<String> = MutableLiveData()
    var lastName: MutableLiveData<String> = MutableLiveData()
    var address1: MutableLiveData<String> = MutableLiveData()
    var email: MutableLiveData<String> = MutableLiveData()
   // var phone: MutableLiveData<String> = MutableLiveData()
    var address2: MutableLiveData<String> = MutableLiveData()
    var city: MutableLiveData<String> = MutableLiveData()
    var state: MutableLiveData<String> = MutableLiveData()
    var zip: MutableLiveData<String> = MutableLiveData()

    var country: MutableLiveData<String> = MutableLiveData()
    var isFromProfile: Boolean=false
    var childAccount: Boolean=false
    var isClinicSetUp: Boolean=false
    var id: String?=null

    var showToastError: MutableLiveData<String> = MutableLiveData()
    var redirectToSignIn: MutableLiveData<Boolean> = MutableLiveData()

    val userConfigLiveData = MutableLiveData<Resource<ResUser>>()
    val registerStep1LiveData = MutableLiveData<Resource<ResUser>>()
    private val userObserver = Observer<Resource<ResUser>> {
        userConfigLiveData.postValue(it)
    }

    val registerStep3LiveData = MutableLiveData<Resource<ResUser>>()

    private val registerObserver = Observer<Resource<ResUser>> {
        registerStep3LiveData.postValue(it)
    }

    init {
        userRepository.userConfigLiveData.postValue(null)
        userRepository.userConfigLiveData.observeForever(userObserver)
        userRepository.registerStep3LiveData.postValue(null)
        userRepository.registerStep3LiveData.observeForever(registerObserver)
    }


    fun onClick(view: View) {

        when (view.id) {
            R.id.btnSave -> {
                KeyboardUtils.hideKeyboard(view)
                if (isNetworkAvailable(view.context)) {

                    if (validateFields()) {

                        if(isClinicSetUp){
                            SharedPreferenceUtil.getInstance(view.context)
                                ?.saveData(
                                    PreferenceManager.KEY_USER__ADD_CLINIC,
                                    true
                                )
                        var    USERClinic= ResUser()
                            USERClinic.address = arrayListOf(
                                AddressItem(
                                    id = id,
                                    firstName = firstName.value,
                                    lastName = lastName.value,
                                    addr1 = address1.value,
                                    addr2 = address2.value,
                                    city = city.value,
                                    state = state.value,
                                    zipCode = zip.value,
                                    country = country.value,
                                    shipping = true
                                )


                            )

                            USERClinic.step1Complete = true
                            USERClinic.step3Complete = true
                            USERClinic.firstName =firstName.value

                            USERClinic.email =email.value
                            callRegisterStepPost(USERClinic)
                        }else {
                            if (childAccount) {
                                if (USERCHILD == null)
                                    USERCHILD = ResUser()
                                USERCHILD?.address = arrayListOf(
                                    AddressItem(
                                        id = id,
                                        firstName = firstName.value,
                                        lastName = lastName.value,
                                        addr1 = address1.value,
                                        addr2 = address2.value,
                                        city = city.value,
                                        state = state.value,
                                        zipCode = zip.value,
                                        country = country.value,
                                        shipping = true
                                    )
                                )
                                USERCHILD?.step3Complete = true
                                USERCHILD?.parentId = USER!!.id.toString()
                                callRegisterStepChild()
                            } else {
                                USER?.address = arrayListOf(
                                    AddressItem(
                                        id = id,
                                        firstName = firstName.value,
                                        lastName = lastName.value,
                                        addr1 = address1.value,
                                        addr2 = address2.value,
                                        city = city.value,
                                        state = state.value,
                                        zipCode = zip.value,
                                        country = country.value,
                                        shipping = true
                                    )
                                )
                                USER?.step3Complete = true
                                callRegisterStep()
                            }
                        }
                    }

                } else {
                    DialogUtils.showSnackBar(
                        view.context,
                        view.context.resources.getString(R.string.no_connection)
                    )
                }

            }
            R.id.tvRegistrationSignIn -> {
                KeyboardUtils.hideKeyboard(view)
                redirectToSignIn.postValue(true)
            }
        }
    }

    fun getUser() {
        scope.launch {
            userRepository.getUser()
        }
    }

    //validate login fields
    private fun validateFields(): Boolean {
        var isValidate = true
        when {

            isNullOrEmpty(firstName.value)&&!isClinicSetUp -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_first_name))
                isValidate = false
            }

            isNullOrEmpty(firstName.value)&&isClinicSetUp -> {
                showToastError.postValue(App.instance?.getString(R.string.clinic_name_emoty))
                isValidate = false
            }
            isNullOrEmpty(lastName.value)&&!isClinicSetUp -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_last_name))
                isValidate = false
            }

                isNullOrEmpty(address1.value)&& isFromProfile -> {
                    showToastError.postValue(App.instance?.getString(R.string.empty_address1))
                    isValidate = false
                }

            isNullOrEmpty(city.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_address_city))
                isValidate = false
            }
            isNullOrEmpty(state.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_address_state))
                isValidate = false
            }
//            isNullOrEmpty(zip.value)&& isFromProfile -> {
//                showToastError.postValue(App.instance?.getString(R.string.empty_address_zip))
//                isValidate = false
//            }
            isNullOrEmpty(country.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.empty_address_country))
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

    private fun callRegisterStep() {
        scope.launch {
            USER?.let { userRepository.registerStep3(it) }
        }
    }
    private fun callRegisterStepPost(u:ResUser) {
        scope.launch {
            userRepository.registerStepChild(registerStep1LiveData,u)
        }
    }
    private fun callRegisterStepChild() {
        scope.launch {
            USERCHILD?.let { userRepository.registerStep3(it) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        userRepository.registerStep3LiveData.removeObserver(registerObserver)
        userRepository.userConfigLiveData.removeObserver(userObserver)
    }
}