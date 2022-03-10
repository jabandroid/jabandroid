package com.global.vtg.appview.authentication

import android.app.usage.UsageEvents
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import com.global.vtg.appview.authentication.login.ReqLoginModel
import com.global.vtg.appview.authentication.registration.ReqRegistration
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.appview.authentication.registration.TestType
import com.global.vtg.appview.config.ResInstitute
import com.global.vtg.appview.home.event.Event
import com.global.vtg.appview.home.event.EventArray
import com.global.vtg.appview.home.profile.ResProfile
import com.global.vtg.appview.home.testHistory.TestKit
import com.global.vtg.appview.payment.ReqPayment
import com.global.vtg.base.AppRepository
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.EnumLoading
import com.global.vtg.model.network.Resource
import com.global.vtg.model.network.result.BaseError
import com.global.vtg.model.network.result.BaseResult
import com.global.vtg.utils.Constants
import com.global.vtg.wscoroutine.ApiInterface
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository constructor(
    private var apiServiceInterface: ApiInterface,
    private var preferenceManager: PreferenceManager
) : AppRepository() {

    val userLiveData = MutableLiveData<Resource<ResUser>>()
    val userProfilePicLiveData = MutableLiveData<Resource<ResProfile>>()
    val userEventPicData = MutableLiveData<Resource<BaseResult>>()
    val userProfilePicLiveDataStep1 = MutableLiveData<Resource<ResProfile>>()
    val userConfigLiveData = MutableLiveData<Resource<ResUser>>()
    val testTypeLiveData = MutableLiveData<Resource<TestType>>()
    val testTypeKitData = MutableLiveData<Resource<TestKit>>()
    val scanBarcodeLiveData = MutableLiveData<Resource<ResUser>>()
    val searchInstituteLiveData = MutableLiveData<Resource<ResInstitute>>()
    val registerLiveData = MutableLiveData<Resource<ResUser>>()
    val registerStep3LiveData = MutableLiveData<Resource<ResUser>>()
    val createEventLiveData = MutableLiveData<Resource<Event>>()
    val getAllEventsLiveData = MutableLiveData<Resource<EventArray>>()
    val getMyEventLiveData = MutableLiveData<Resource<EventArray>>()
    val eventLiveData = MutableLiveData<Resource<Event>>()
    val eventDeleteLiveData = MutableLiveData<Resource<BaseResult>>()
    val eventDeletePicLiveData = MutableLiveData<Resource<BaseResult>>()
    val paymentLiveData = MutableLiveData<Resource<BaseResult>>()
    val registerVendorStep2LiveData = MutableLiveData<Resource<ResUser>>()

    suspend fun login(modelReq: ReqLoginModel) {
        Constants.tempUsername = modelReq.userName
        Constants.tempPassword = modelReq.password

        userLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result = safeApiCall(call = {
            apiServiceInterface.loginAsync().await()
        })
        Constants.IS_SIGN_IN = false
        Constants.tempUsername = ""
        Constants.tempPassword = ""
        if (result is ResUser) {
            userLiveData.postValue(Resource.Success(result))
        } else if (result is BaseError) {
            userLiveData.postValue(Resource.Error(result))
        }
    }

    suspend fun register(modelReq: ReqRegistration) {
        registerLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result = safeApiCall(call = { apiServiceInterface.registerAsync(modelReq).await() })
        if (result is ResUser) {
            registerLiveData.postValue(Resource.Success(result))
        } else if (result is BaseError) {
            registerLiveData.postValue(Resource.Error(result))
        }
    }

    suspend fun registerStep(liveData: MutableLiveData<Resource<ResUser>>, modelReq: ResUser) {
        liveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result =
            safeApiCall(call = { apiServiceInterface.registerStep1Async(modelReq).await() })
        if (result is ResUser) {
            liveData.postValue(Resource.Success(result))
        } else if (result is BaseError) {
            liveData.postValue(Resource.Error(result))
        }
    }

    suspend fun registerStep3(modelReq: ResUser) {
        registerStep3LiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result =
            safeApiCall(call = { apiServiceInterface.registerStep1Async(modelReq).await() })
        if (result is ResUser) {
            registerStep3LiveData.postValue(Resource.Success(result))
        } else if (result is BaseError) {
            registerStep3LiveData.postValue(Resource.Error(result))
        }
    }

    suspend fun createEvent(modelReq: Event) {
        createEventLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result =
            safeApiCall(call = { apiServiceInterface.createEvent(modelReq).await() })
        if (result is Event && result.code=="200") {
            createEventLiveData.postValue(Resource.Success(result))
        } else if (result is BaseError) {
            createEventLiveData.postValue(Resource.Error(result))
        }
    }


    suspend fun CallEvents(modelReq: JsonObject) {
        getAllEventsLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result =
            safeApiCall(call = { apiServiceInterface.getAllEvents().await() })
        if (result is EventArray && result!!.code=="200") {
            getAllEventsLiveData.postValue(Resource.Success(result))
        } else if (result is BaseError) {
            getAllEventsLiveData.postValue(Resource.Error(result))
        }
    }

    suspend fun CallMyEvents(modelReq: String) {
        getMyEventLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result =
            safeApiCall(call = { apiServiceInterface.getMyEvents(modelReq).await() })
        if (result is EventArray && result!!.code=="200") {
            getMyEventLiveData.postValue(Resource.Success(result))
        } else if (result is BaseError) {
            getMyEventLiveData.postValue(Resource.Error(result))
        }
    }

    suspend fun CallEventID(modelReq: String) {
        eventLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result =
            safeApiCall(call = { apiServiceInterface.getEventId(modelReq).await() })
        if (result is Event && result.code=="200") {
            eventLiveData.postValue(Resource.Success(result))
        } else if (result is BaseError) {
            eventLiveData.postValue(Resource.Error(result))
        }
    }

    suspend fun DeleteEvent(modelReq: String) {
        eventDeleteLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result =
            safeApiCall(call = { apiServiceInterface.deleteEvent(modelReq).await() })
        if (result is BaseResult && result.code=="200") {
            eventDeleteLiveData.postValue(Resource.Success(result))
        } else if (result is BaseError) {
            eventDeleteLiveData.postValue(Resource.Error(result))
        }
    }

    suspend fun deleteEventPic(modelReq: String) {
        eventDeletePicLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result =
            safeApiCall(call = { apiServiceInterface.deleteEventPic(modelReq).await() })
        if (result is Event && result.code=="200") {
            eventDeletePicLiveData.postValue(Resource.Success(result))
        } else if (result is BaseError) {
            eventDeletePicLiveData.postValue(Resource.Error(result))
        }
    }


    suspend fun forgotPassword(modelReq: ReqRegistration) {
        userProfilePicLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result = safeApiCall(call = { apiServiceInterface.forgotPasswordAsync(modelReq).await() })
        if (result is ResProfile) {
            userProfilePicLiveData.postValue(Resource.Success(result))
        } else if (result is BaseError) {
            userProfilePicLiveData.postValue(Resource.Error(result))
        }
    }

    suspend fun changePassword(modelReq: ResUser) {
        userLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result =
            safeApiCall(call = { apiServiceInterface.changePasswordAsync(modelReq).await() })
        if (result is ResUser) {
            userLiveData.postValue(Resource.Success(result))
        } else if (result is BaseError) {
            userLiveData.postValue(Resource.Error(result))
        }
    }

    suspend fun getUser() {
        userConfigLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result = safeApiCall(call = { apiServiceInterface.getUserAsync().await() })
        if (result is ResUser) {
            userConfigLiveData.postValue(Resource.Success(result))
        } else if (result is BaseError) {
            userConfigLiveData.postValue(Resource.Error(result))
        }
    }

    suspend fun scanBarcodeId(barcodeId: String) {
        scanBarcodeLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result =
            safeApiCall(call = { apiServiceInterface.scanBarcodeIdAsync(barcodeId).await() })
        if (result is ResUser) {
            scanBarcodeLiveData.postValue(Resource.Success(result))
            getUser()
        } else if (result is BaseError) {
            scanBarcodeLiveData.postValue(Resource.Error(result))
        }
    }

    suspend fun searchInstitute(text: String) {
        searchInstituteLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result = safeApiCall(call = { apiServiceInterface.searchInstituteAsync(text).await() })
        if (result is ResInstitute) {
            searchInstituteLiveData.postValue(Resource.Success(result))
        } else if (result is BaseError) {
            searchInstituteLiveData.postValue(Resource.Error(result))
        }
    }

    suspend fun uploadVaccine(
        file: MultipartBody.Part?,
        type: RequestBody?,
        userId: RequestBody?,
        instituteId: RequestBody?,
        status: RequestBody?,
        certified: RequestBody?,
        date: RequestBody?,
        batchNo: RequestBody?,
        dose: RequestBody?,
        username: RequestBody?
    ) {
        userLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result = safeApiCall(call = {
            apiServiceInterface.uploadVaccineAsync(
                file!!,
                type,
                userId,
                instituteId,
                status,
                certified,
                date,
                batchNo,
                dose,
                username
            ).await()
        })
        if (result is ResUser) {
            userLiveData.postValue(Resource.Success(result))
            getUser()
        } else if (result is BaseError) {
            userLiveData.postValue(Resource.Error(result))
        }
    }

    suspend fun uploadHealthInfo(
        file: MultipartBody.Part?,
        userId: RequestBody?,
        instituteId: RequestBody?,
        status: RequestBody?,
        certified: RequestBody?,
        date: RequestBody?,
        batchNo: RequestBody?,
        result: RequestBody?,
        test: RequestBody?,
        username: RequestBody?
    ) {
        userLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result = safeApiCall(call = {
            apiServiceInterface.uploadHealthInfoAsync(
                file!!,
                userId,
                instituteId,
                status,
                certified,
                date,
                batchNo,
                result,
                test,
                username
            ).await()
        })
        if (result is ResUser) {
            userLiveData.postValue(Resource.Success(result))
            getUser()
        } else if (result is BaseError) {
            userLiveData.postValue(Resource.Error(result))
        }
    }

    suspend fun uploadTestInfo(
        file: MultipartBody.Part?,
        userId: RequestBody?,
        instituteId: RequestBody?,
        status: RequestBody?,
        certified: RequestBody?,
        date: RequestBody?,
        batchNo: RequestBody?,
        result: RequestBody?,
        test: RequestBody?,
        username: RequestBody?,
        testkit: RequestBody?
    ) {
        userLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result = safeApiCall(call = {
            apiServiceInterface.uploadTestInfoAsync(
                file!!,
                userId,
                instituteId,
                status,
                certified,
                date,
                batchNo,
                result,
                test,
                username,
                testkit
            ).await()
        })
        if (result is ResUser) {
            userLiveData.postValue(Resource.Success(result))
            getUser()
        } else if (result is BaseError) {
            userLiveData.postValue(Resource.Error(result))
        }
    }

    suspend fun uploadProfile(
        file: MultipartBody.Part?,
        userId: RequestBody?
    ) {
        userProfilePicLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result = safeApiCall(call = {
            apiServiceInterface.uploadProfileAsync(
                file!!,
                userId
            ).await()
        })
        if (result is ResProfile) {
            userProfilePicLiveData.postValue(Resource.Success(result))
            getUser()
        } else if (result is BaseError) {
            userProfilePicLiveData.postValue(Resource.Error(result))
        }
    }


    suspend fun uploadEventImages(
        file: RequestBody,
        userId: RequestBody?
    ) {
        userEventPicData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result = safeApiCall(call = {
            apiServiceInterface.uploadEventImages(
                file
            ).await()
        })
        if (result is BaseResult && result.code=="200") {
            userEventPicData.postValue(Resource.Success(result))

        } else if (result is BaseError) {
            userEventPicData.postValue(Resource.Error(result))
        }
    }

    suspend fun uploadProfileStep1(
        file: MultipartBody.Part?,
        userId: RequestBody?
    ) {
        userProfilePicLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result = safeApiCall(call = {
            apiServiceInterface.uploadProfileAsync(
                file!!,
                userId
            ).await()
        })
        if (result is ResProfile) {
            userProfilePicLiveDataStep1.postValue(Resource.Success(result))

        } else if (result is BaseError) {
            userProfilePicLiveDataStep1.postValue(Resource.Error(result))
        }
    }

    suspend fun uploadVendorStep2(
        file: MultipartBody.Part?,
        vendorId: RequestBody?,
        businessName: RequestBody?,
        businessId: RequestBody?,
        employeeId: RequestBody?,
        date: RequestBody?,
        vat: RequestBody?,
    ) {
        registerVendorStep2LiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result = safeApiCall(call = {
            apiServiceInterface.uploadVendorStep2Async(
                file!!,
                vendorId,
                businessName,
                businessId,
                employeeId,date,vat
            ).await()
        })
        if (result is ResUser) {
            registerVendorStep2LiveData.postValue(Resource.Success(result))
        } else if (result is BaseError) {
            registerVendorStep2LiveData.postValue(Resource.Error(result))
        }
    }

    suspend fun makePayment(reqPayment: ReqPayment) {
        paymentLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result =
            safeApiCall(call = { apiServiceInterface.makePaymentAsync(reqPayment).await() })
        if (result is BaseResult) {
            paymentLiveData.postValue(Resource.Success(result))
        } else if (result is BaseError) {
            paymentLiveData.postValue(Resource.Error(result))
        }
    }

    suspend fun getTestHistory() {
        testTypeLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result =
            safeApiCall(call = { apiServiceInterface.testType().await() })
        if (result is TestType) {
            testTypeLiveData.postValue(Resource.Success(result))

        } else if (result is BaseError) {
            testTypeLiveData.postValue(Resource.Error(result))
        }
    }

    suspend fun getTestKit() {
        testTypeLiveData.postValue(Resource.Loading(EnumLoading.LOADING_ALL))
        val result =
            safeApiCall(call = { apiServiceInterface.testkit().await() })
        if (result is TestKit) {
            testTypeKitData.postValue(Resource.Success(result))

        } else if (result is BaseError) {
            testTypeKitData.postValue(Resource.Error(result))
        }
    }

}