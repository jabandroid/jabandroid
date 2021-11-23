package com.global.vtg.appview.home.vendor

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.authentication.registration.ResUser
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
}