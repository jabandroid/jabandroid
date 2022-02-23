package com.global.vtg.appview.home.event

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.AppViewModel
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.model.network.Resource
import com.global.vtg.model.network.result.BaseResult
import com.google.gson.JsonObject
import com.vtg.R
import kotlinx.coroutines.launch

class EventListViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {


    val getAllEvents = MutableLiveData<Resource<EventArray>>()
    private val eventObserver = Observer<Resource<EventArray>> {
        getAllEvents.postValue(it)
    }

    val getMyEvents = MutableLiveData<Resource<EventArray>>()
    private val eventMyObserver = Observer<Resource<EventArray>> {
        getMyEvents.postValue(it)
    }



    init {

        userRepository.getMyEventLiveData.postValue(null)
        userRepository.getMyEventLiveData.observeForever(eventMyObserver)
        userRepository.getAllEventsLiveData.postValue(null)
        userRepository.getAllEventsLiveData.observeForever(eventObserver)
    }

    var redirectToCreate: MutableLiveData<Boolean> = MutableLiveData()
    fun onClick(view: View) {
        when (view.id) {
            R.id.add -> {
                redirectToCreate.postValue(true)
            }
        }


    }

    fun callEventsApi(obj:JsonObject) {
        scope.launch {

            userRepository.CallEvents(obj)
        }
    }

    fun callMyEventsApi(obj:String) {
        scope.launch {

            userRepository.CallMyEvents(obj)
        }
    }
}