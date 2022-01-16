package com.global.vtg.appview.home.vendor

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.authentication.registration.TestType
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.vtg.R
import kotlinx.coroutines.launch

class VendorResultViewModel(application: Application, private val userRepository: UserRepository) :
    AppViewModel(application) {

    val testData = MutableLiveData<Resource<TestType>>()

    private val testObserver = Observer<Resource<TestType>> {
        testData.postValue(it)
    }

    init {


        userRepository.testTypeLiveData.postValue(null)
        userRepository.testTypeLiveData.observeForever(testObserver)
    }

    public fun testHistory() {
        scope.launch {
            userRepository.getTestHistory()
        }
    }

}