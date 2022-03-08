package com.global.vtg.appview.home.dashboard

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.vtg.R
import kotlinx.coroutines.launch

class DashboardViewModel (application: Application, private val userRepository: UserRepository) : AppViewModel(application) {

    val userConfigLiveData1 = MutableLiveData<Resource<ResUser>>()

    private val userObserver = Observer<Resource<ResUser>> {
        userConfigLiveData1.postValue(it)
    }

    init {
        userRepository.userConfigLiveData.postValue(null)
        userRepository.userConfigLiveData.observeForever(userObserver)
    }

    fun onClick(view: View) {

        if (isNetworkAvailable(view.context)) {

        } else {
            DialogUtils.showSnackBar(
                view.context,
                view.context.resources.getString(R.string.no_connection)
            )
        }
    }

    fun getUser() {
        scope.launch {
            userRepository.getUser()
        }
    }

    override fun onCleared() {
        super.onCleared()
        userRepository.userConfigLiveData.removeObserver(userObserver)

    }
}