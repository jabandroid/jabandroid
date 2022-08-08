package com.global.vtg.appview.home.event

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.App
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.model.network.result.BaseResult
import com.vtg.R
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class CreateEventViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {

    var eventName: MutableLiveData<String> = MutableLiveData()
    var eventDescription: MutableLiveData<String> = MutableLiveData()
    var eventPrivate: MutableLiveData<Boolean> = MutableLiveData()
    var eventAttendees: MutableLiveData<String> = MutableLiveData()
    var startTime: String =""
    var endTime: String =""
    var showToastError: MutableLiveData<String> = MutableLiveData()
    var redirectToStep2: MutableLiveData<Boolean> = MutableLiveData()


    val uplaodAttendees = MutableLiveData<Resource<BaseResult>>()

    private val userPinObserver = Observer<Resource<BaseResult>> {
        uplaodAttendees.postValue(it)
    }

    init {
        userRepository.uploadContactLiveData.postValue(null)
        userRepository.uploadContactLiveData.observeForever(userPinObserver)

    }
        fun onClick(view: View) {

        when (view.id) {
            R.id.btnNext->{
                if(validateFields()) {
                    CreateEventFragment.itemEvent.eventName = eventName.value
                    CreateEventFragment.itemEvent.startDate = startTime
                    CreateEventFragment.itemEvent.endDate = endTime
                    CreateEventFragment.itemEvent.description = eventDescription.value
                    CreateEventFragment.itemEvent.privateEvent = eventPrivate.value
                    CreateEventFragment.itemEvent.crowdLimit = eventAttendees.value
                    redirectToStep2.postValue(true)
                }
            }

        }
    }


    private fun validateFields(): Boolean {
        var isValidate = true
        when {
            isNullOrEmpty(eventName.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_event_name))
                isValidate = false
            }
            isNullOrEmpty(startTime) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_start_time))
                isValidate = false
            }
            isNullOrEmpty(endTime) -> {
                showToastError.postValue(App.instance?.getString(R.string.error_end_time))
                isValidate = false
            }

            isNullOrEmpty(eventAttendees.value) -> {
                showToastError.postValue(App.instance?.getString(R.string.enter_attendees))
                isValidate = false
            }

            else -> {
                showToastError.postValue("")
            }
        }

        if (!isValidate)
            return false

        return isValidate
    }


    fun uploadAttendees(file: MultipartBody.Part?, eventId: String) {
        scope.launch {
            val eventID = eventId.toRequestBody("text/plain".toMediaTypeOrNull())
            userRepository.uploadContactList(file, eventID)
        }
    }

}