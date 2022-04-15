package com.global.vtg.appview.home.vaccinehistory

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.home.profile.ResProfile
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.model.network.result.BaseResult
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.google.gson.JsonObject
import com.vtg.R
import kotlinx.coroutines.launch

class VaccineHistoryViewModel(application: Application, private val userRepository: UserRepository) : AppViewModel(application) {
    val uploadFile: MutableLiveData<Boolean> = MutableLiveData()
    val uploadProfilePic: MutableLiveData<Boolean> = MutableLiveData()



    fun onClick(view: View) {
        when (view.id) {
            R.id.btnUploadFile -> {
                uploadFile.postValue(true)
            }
            R.id.iv_add -> {
                uploadFile.postValue(true)
            }

        }

    }


}