package com.global.vtg.appview.home.qrcode

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.base.AppViewModel
import com.vtg.R

class VaccineQRCodeViewModel(application: Application, private val userRepository: UserRepository) :
    AppViewModel(application) {
    val shareIntent: MutableLiveData<Boolean> = MutableLiveData()

    fun onClick(view: View) {
        when (view.id) {
            R.id.btnShare -> {
                shareIntent.postValue(true)
            }
        }
    }
}