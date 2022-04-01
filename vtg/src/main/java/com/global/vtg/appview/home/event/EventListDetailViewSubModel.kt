package com.global.vtg.appview.home.event

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.model.network.result.BaseResult
import kotlinx.coroutines.launch


class EventListDetailViewSubModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {

    val eventDetailSubLiveData = MutableLiveData<Resource<Event>>()
    val eventDeletelLiveData = MutableLiveData<Resource<BaseResult>>()

    var showToastError: MutableLiveData<String> = MutableLiveData()
    private val eventObserver = Observer<Resource<Event>> {
        eventDetailSubLiveData.postValue(it)
    }
    private val eventDelete = Observer<Resource<BaseResult>> {
        eventDeletelLiveData.postValue(it)
    }

    init {

        userRepository.eventLiveData.postValue(null)
        userRepository.eventLiveData.observeForever(eventObserver)

        userRepository.eventDeleteLiveData.postValue(null)
        userRepository.eventDeleteLiveData.observeForever(eventDelete)

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
    fun deleteEvent(obj:String) {
        scope.launch {
            userRepository.DeleteEvent(obj)
        }
    }
}
