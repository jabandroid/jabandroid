package com.global.vtg.appview.home.profile

import android.app.Application
import android.net.Uri
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.vtg.R
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import android.webkit.MimeTypeMap
import com.global.vtg.model.network.result.BaseResult
import com.google.gson.JsonObject


class ProfileViewModel(application: Application, private val userRepository: UserRepository) :
    AppViewModel(application) {
    val uploadProfilePic: MutableLiveData<Boolean> = MutableLiveData()
    val logout: MutableLiveData<Boolean> = MutableLiveData()
    var documentPath: String? = null
    val userLiveData = MutableLiveData<Resource<ResProfile>>()

    private val userObserver = Observer<Resource<ResProfile>> {
        userLiveData.postValue(it)
    }

    val updatePin = MutableLiveData<Resource<BaseResult>>()

    private val userPinObserver = Observer<Resource<BaseResult>> {
        updatePin.postValue(it)
    }

    init {
        userRepository.userProfilePicLiveData.postValue(null)
        userRepository.userProfilePicLiveData.observeForever(userObserver)

        userRepository.updatePinLiveData.postValue(null)
        userRepository.updatePinLiveData.observeForever(userPinObserver)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.ivProfilePic -> {
                if (isNetworkAvailable(view.context)) {
                    uploadProfilePic.postValue(true)
                } else {
                    DialogUtils.showSnackBar(
                        view.context,
                        view.context.resources.getString(R.string.no_connection)
                    )
                }
            }


            R.id.btnLogout -> {
                logout.postValue(true)
            }
        }
    }

    fun uploadProfile(path: String) {
        scope.launch {
            val file: File
            var part: MultipartBody.Part? = null
            if (documentPath != null) {
                file = File(documentPath)
                part = MultipartBody.Part.createFormData("file", file.name,
                    file.asRequestBody("image/png".toMediaTypeOrNull())
                )
            }
            val id: RequestBody = Constants.USER?.id.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
            userRepository.uploadProfile(part, id)
        }
    }


    fun getUser() {
        scope.launch {
            userRepository.getUser()
        }
    }

    fun updatePin(j:JsonObject) {
        scope.launch {
            userRepository.updatePin(j)
        }
    }
}