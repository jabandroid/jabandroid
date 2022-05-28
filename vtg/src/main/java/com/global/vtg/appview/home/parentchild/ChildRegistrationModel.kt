package com.global.vtg.appview.home.parentchild

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.base.AppViewModel
import com.vtg.R

class ChildRegistrationModel(application: Application, private val userRepository: UserRepository) : AppViewModel(application) {
    val email: MutableLiveData<String> = MutableLiveData()
    val phoneNumber: MutableLiveData<String> = MutableLiveData()
    val govermentId: MutableLiveData<String> = MutableLiveData()



    fun onClick(view: View) {
        when (view.id) {


        }

    }


}