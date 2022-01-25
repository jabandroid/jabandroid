package com.global.vtg.appview.home.vendor

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.appview.authentication.registration.TestType
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.vtg.R
import kotlinx.coroutines.launch

class VendorScanResultViewModel(
    application: Application,
    private val userRepository: UserRepository
) :
    AppViewModel(application) {
    val scanBarcodeLiveData = MutableLiveData<Resource<ResUser>>()
    val detailsLiveData = MutableLiveData<Boolean>()

    private val userObserver = Observer<Resource<ResUser>> {
        scanBarcodeLiveData.postValue(it)
    }

    val testDataDetails = MutableLiveData<Resource<TestType>>()

    private val testObserver = Observer<Resource<TestType>> {
        testDataDetails.postValue(it)
    }

    init {


        userRepository.testTypeLiveData.postValue(null)
        userRepository.testTypeLiveData.observeForever(testObserver)
    }

    init {
        userRepository.scanBarcodeLiveData.postValue(null)
        userRepository.scanBarcodeLiveData.observeForever(userObserver)
    }

    fun getDataFromBarcodeId(barcodeId: String) {
        scope.launch {
            userRepository.scanBarcodeId(barcodeId)
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btnShowDetails -> {
                detailsLiveData.postValue(true)
            }
        }
    }


    public fun testHistory() {
        scope.launch {
            userRepository.getTestHistory()
        }
    }
}