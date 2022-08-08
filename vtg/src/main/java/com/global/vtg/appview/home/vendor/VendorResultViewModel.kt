package com.global.vtg.appview.home.vendor

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.authentication.registration.TestType
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.model.network.result.BaseResult
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

class VendorResultViewModel(application: Application, private val userRepository: UserRepository) :
    AppViewModel(application) {

    val testData = MutableLiveData<Resource<TestType>>()

    private val testObserver = Observer<Resource<TestType>> {
        testData.postValue(it)
    }

    val validatePin = MutableLiveData<Resource<BaseResult>>()

    private val userPinObserver = Observer<Resource<BaseResult>> {
        validatePin.postValue(it)
    }


    init {
        userRepository.validatePinLiveData.postValue(null)
        userRepository.validatePinLiveData.observeForever(userPinObserver)

        userRepository.testTypeLiveData.postValue(null)
        userRepository.testTypeLiveData.observeForever(testObserver)
    }

    public fun testHistory() {
        scope.launch {
            userRepository.getTestHistory()
        }
    }


    fun validatePin(j: JsonObject) {
        scope.launch {
            userRepository.validatePin(j)
        }
    }

}