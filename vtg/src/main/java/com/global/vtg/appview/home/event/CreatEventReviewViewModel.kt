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




class CreatEventReviewViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {
    var cancelEvent: MutableLiveData<Boolean> = MutableLiveData()
    var cancelBannerImage: MutableLiveData<Boolean> = MutableLiveData()
    var bannerImage: String = ""
    val createEventLiveData = MutableLiveData<Resource<Event>>()
    val uploadPics: MutableLiveData<Boolean> = MutableLiveData()
    var showToastError: MutableLiveData<String> = MutableLiveData()
    private val eventObserver = Observer<Resource<Event>> {
        createEventLiveData.postValue(it)
    }

    val userLiveData = MutableLiveData<Resource<BaseResult>>()

    private val userObserver = Observer<Resource<BaseResult>> {
        userLiveData.postValue(it)
    }
    init {


        userRepository.createEventLiveData.postValue(null)
        userRepository.createEventLiveData.observeForever(eventObserver)
    }

    init {
        userRepository.userEventPicData.postValue(null)
        userRepository.userEventPicData.observeForever(userObserver)
    }



    fun onClick(view: View) {
        when (view.id) {
            R.id.btnNext -> {
                KeyboardUtils.hideKeyboard(view)
                if (!TextUtils.isEmpty(bannerImage)) {
                    if (isNetworkAvailable(view.context)) {
                        CreateEventFragment.itemEvent.userId = Constants.USER!!.id
                        CreateEventFragment.itemEvent.startDate = DateUtils.formatLocalToUtc(
                            CreateEventFragment.itemEvent.startDate!!,
                            true,
                            DateUtils.API_DATE_FORMAT_VACCINE
                        )
                        CreateEventFragment.itemEvent.endDate = DateUtils.formatLocalToUtc(
                            CreateEventFragment.itemEvent.endDate!!,
                            true,
                            DateUtils.API_DATE_FORMAT_VACCINE
                        )
                        val gson = Gson()
                        val request: Any = gson.toJson(CreateEventFragment.itemEvent)
                        Log.e("input","input"+request.toString())

                        callCreateEvent()
                    }
                } else
                    showToastError.postValue(App.instance?.getString(R.string.empty_banner_image))
            }
            R.id.btnCancel -> {
                cancelEvent.postValue(true)
            }
            R.id.ivCancel -> {
                cancelBannerImage.postValue(true)
            }
            R.id.upload -> {
                uploadPics.postValue(true)
            }
        }
    }

    private fun callCreateEvent() {
        scope.launch {
            userRepository.createEvent(CreateEventFragment.itemEvent)
        }
    }


    override fun onCleared() {
        super.onCleared()
        userRepository.createEventLiveData.removeObserver(eventObserver)
    }

    fun uploadPic(part:  RequestBody  , eventId: String) {
        scope.launch {
            val eventID = eventId.toRequestBody("text/plain".toMediaTypeOrNull())
            userRepository.uploadEventImages(part, eventID)
        }
    }
}
