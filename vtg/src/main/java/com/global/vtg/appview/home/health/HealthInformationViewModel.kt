package com.global.vtg.appview.home.health

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.base.AppViewModel
import com.vtg.R

class HealthInformationViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {
    val uploadFile: MutableLiveData<Boolean> = MutableLiveData()

    fun onClick(view: View) {
        when (view.id) {
            R.id.btnUploadFile -> {
                uploadFile.postValue(true)
            }
        }
    }
}