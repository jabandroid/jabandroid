package com.global.vtg.appview.home.event

import android.app.Application
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.App
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.appview.home.profile.ResProfile
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.model.network.result.BaseResult
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.global.vtg.utils.KeyboardUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.tslogistics.util.AppAlertDialog
import com.vtg.R
import kotlinx.android.synthetic.main.fragment_create_event_review.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import com.google.gson.Gson




class EventListDetailViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {

    val eventDetailLiveData = MutableLiveData<Resource<Event>>()

    var showToastError: MutableLiveData<String> = MutableLiveData()
    private val eventObserver = Observer<Resource<Event>> {
        eventDetailLiveData.postValue(it)
    }

    init {

        userRepository.eventLiveData.postValue(null)
        userRepository.eventLiveData.observeForever(eventObserver)

    }


    fun onClick(view: View) {

    }




    override fun onCleared() {
        super.onCleared()
        userRepository.eventLiveData.removeObserver(eventObserver)
    }

    fun callEventDetails(obj:String) {
        scope.launch {
            userRepository.CallEventID(obj)
        }
    }
}
