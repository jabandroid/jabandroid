package com.global.vtg.appview.home.parentchild

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.model.network.result.BaseResult
import com.global.vtg.utils.Constants
import com.vtg.R
import kotlinx.coroutines.launch

class ChildListViewModel(application: Application, private val userRepository: UserRepository) : AppViewModel(application) {
    val uploadFile: MutableLiveData<Boolean> = MutableLiveData()

    val userConfigLiveData = MutableLiveData<Resource<ResUser>>()

    private val userObserver = Observer<Resource<ResUser>> {
        userConfigLiveData.postValue(it)

    }


    val scanBarcodeLiveData = MutableLiveData<Resource<ResUser>>()
    private val userObserver1 = Observer<Resource<ResUser>> {
        scanBarcodeLiveData.postValue(it)
    }

    val addParentLiveData = MutableLiveData<Resource<AddChild>>()
    private val addParent = Observer<Resource<AddChild>> {
        addParentLiveData.postValue(it)
    }

    val deleteUser = MutableLiveData<Resource<BaseResult>>()

    private val deleteUSerLiveData = Observer<Resource<BaseResult>> {
        deleteUser.postValue(it)
    }
    val deleteChildPermanent = MutableLiveData<Resource<BaseResult>>()

    private val registerObserver = Observer<Resource<BaseResult>> {
        deleteChildPermanent.postValue(it)
    }

    init {


        userRepository.deleteChildPermanentLiveData.postValue(null)
        userRepository.deleteChildPermanentLiveData.observeForever(registerObserver)
        userRepository.deleteUserLiveData.postValue(null)
        userRepository.deleteUserLiveData.observeForever(deleteUSerLiveData)
        userRepository.addParentLiveData.postValue(null)
        userRepository.addParentLiveData.observeForever(addParent)

        userRepository.userConfigLiveData.postValue(null)
        userRepository.userConfigLiveData.observeForever(userObserver)
        userRepository.scanBarcodeLiveData.postValue(null)
        userRepository.scanBarcodeLiveData.observeForever(userObserver1)

    }


    fun getDataFromBarcodeId(barcodeId: String) {
        scope.launch {
            userRepository.scanBarcodeINew(barcodeId)
        }
    }

    fun addParent(childId: String,parentId: String) {
        scope.launch {
            userRepository.addParent(childId,parentId)
        }
    }

    fun deleteChild(childId:String) {
        scope.launch {
            Constants.USER?.let { userRepository.deleteChildPermanent(childId) }
        }
    }

    fun deleteUSer(child:String,parent:String) {
        scope.launch {
            userRepository.deleteUserChild(child,parent)
        }
    }


    override fun onCleared() {
        super.onCleared()
        userRepository.addParentLiveData.removeObserver(addParent)
        userRepository.deleteChildPermanentLiveData.removeObserver(registerObserver)
        userRepository.scanBarcodeLiveData.removeObserver(userObserver1)
        userRepository.userConfigLiveData.removeObserver(userObserver)
        userRepository.getEventsUSerLiveData.removeObserver(deleteUSerLiveData)
    }


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

    fun getUser() {
        scope.launch {
            userRepository.getUser()
        }
    }



}