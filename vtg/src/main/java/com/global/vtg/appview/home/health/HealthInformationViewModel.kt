package com.global.vtg.appview.home.health

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

class HealthInformationViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {
    val uploadFile: MutableLiveData<Boolean> = MutableLiveData()
    val testData = MutableLiveData<Resource<TestType>>()

    private val testObserver = Observer<Resource<TestType>> {
        testData.postValue(it)
    }

    init {


        userRepository.testTypeLiveData.postValue(null)
        userRepository.testTypeLiveData.observeForever(testObserver)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btnUploadFile -> {
                uploadFile.postValue(true)
            }
        }
    }

    public fun testHistory() {
        scope.launch {
            userRepository.getTestHistory()
        }
    }
}