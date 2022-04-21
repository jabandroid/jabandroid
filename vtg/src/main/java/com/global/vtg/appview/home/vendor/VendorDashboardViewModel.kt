package com.global.vtg.appview.home.vendor

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.vtg.R
import kotlinx.coroutines.launch

class VendorDashboardViewModel (application: Application, private val userRepository: UserRepository) : AppViewModel(application) {

    val userConfigLiveData = MutableLiveData<Resource<ResUser>>()

    private val userObserver = Observer<Resource<ResUser>> {
        userConfigLiveData.postValue(it)
    }

    init {
        userRepository.userConfigLiveData.postValue(null)
        userRepository.userConfigLiveData.observeForever(userObserver)
    }

    fun onClick(view: View) {

        if (isNetworkAvailable(view.context)) {

        } else {
            DialogUtils.showSnackBar(
                view.context,
                view.context.resources.getString(R.string.no_connection)
            )
        }
    }

    fun getUser() {
        scope.launch {
            userRepository.getUser()
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
    }
}