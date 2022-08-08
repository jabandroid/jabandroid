package com.global.vtg.appview.home.travel

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.config.ResInstitute
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.model.network.result.BaseResult
import kotlinx.coroutines.launch


class TravelViewModel(
    application: Application,
    private val userRepository: UserRepository
) :
    AppViewModel(application) {

    var showToastError: MutableLiveData<String> = MutableLiveData()
    var saveSuccess: MutableLiveData<Boolean> = MutableLiveData()

    val instituteLiveData = MutableLiveData<Resource<ResInstitute>>()

    private val observer = Observer<Resource<ResInstitute>> {
        instituteLiveData.postValue(it)
    }
    val codeLiveData = MutableLiveData<Resource<CitySearchItem>>()

    private val codeLiveObserver = Observer<Resource<CitySearchItem>> {
        codeLiveData.postValue(it)
    }

    val infoLiveData = MutableLiveData<Resource<BaseResult>>()

    private val infoLiveObserver = Observer<Resource<BaseResult>> {
        infoLiveData.postValue(it)
    }




    init {
        userRepository.searchCodeLiveData.postValue(null)
        userRepository.searchCodeLiveData.observeForever(codeLiveObserver)
        userRepository.getInfoLiveData.postValue(null)
        userRepository.getInfoLiveData.observeForever(infoLiveObserver)

    }

    fun onClick(view: View) {
        when (view.id) {

        }
    }




    fun searchCode(text: String) {
        scope.launch {
            userRepository.searchCode(text)
        }
    }


    fun getInfo(cityCode: String,countryCode: String) {
        scope.launch {
            userRepository.getInfo(cityCode,countryCode)
        }
    }



    override fun onCleared() {
        super.onCleared()
        userRepository.searchCodeLiveData.removeObserver(codeLiveObserver)
        userRepository.getInfoLiveData.removeObserver(infoLiveObserver)
    }
}