package com.global.vtg.appview.home.event

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.model.network.result.BaseResult
import com.google.gson.JsonObject
import kotlinx.coroutines.launch


class EventListDetailViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {

    val eventDetailLiveData = MutableLiveData<Resource<Event>>()
    val eventDeletelLiveData = MutableLiveData<Resource<BaseResult>>()

    var showToastError: MutableLiveData<String> = MutableLiveData()
    private val eventObserver = Observer<Resource<Event>> {
        eventDetailLiveData.postValue(it)
    }
    private val eventDelete = Observer<Resource<BaseResult>> {
        eventDeletelLiveData.postValue(it)
    }

    val addUser = MutableLiveData<Resource<BaseResult>>()

    private val addUserLiveData = Observer<Resource<BaseResult>> {
        addUser.postValue(it)
    }

    val checkStatusLive = MutableLiveData<Resource<BaseResult>>()

    private val checkStatus = Observer<Resource<BaseResult>> {
        checkStatusLive.postValue(it)
    }


    init {

        userRepository.eventLiveData.postValue(null)
        userRepository.eventLiveData.observeForever(eventObserver)

        userRepository.eventDeleteLiveData.postValue(null)
        userRepository.eventDeleteLiveData.observeForever(eventDelete)

        userRepository.addUserLiveData.postValue(null)
        userRepository.addUserLiveData.observeForever(addUserLiveData)
        userRepository.checkStatus.postValue(null)
        userRepository.checkStatus.observeForever(checkStatus)

    }


    fun onClick(view: View) {

    }




    override fun onCleared() {
        super.onCleared()
        userRepository.eventLiveData.removeObserver(eventObserver)
        userRepository.checkStatus.removeObserver(checkStatus)
    }

    fun callEventDetails(obj:String) {
        scope.launch {
            userRepository.CallEventID(obj)
        }
    }
    fun deleteEvent(obj:String) {
        scope.launch {
            userRepository.DeleteEvent(obj)
        }
    }


    fun addUSer(id: JsonObject) {
        scope.launch {
            userRepository.addUser(id)
        }
    }

    fun checkStatus(event: String,id:String) {
        scope.launch {
            userRepository.checkStatus(event, id)
        }
    }

}
