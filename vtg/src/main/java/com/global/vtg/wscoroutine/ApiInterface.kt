package com.global.vtg.wscoroutine

import android.app.usage.UsageEvents
import com.global.vtg.appview.authentication.forgotpassword.ReqForgotPasswordModel
import com.global.vtg.appview.authentication.registration.ReqRegistration
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.appview.authentication.registration.TestType
import com.global.vtg.appview.config.ResConfig
import com.global.vtg.appview.config.ResInstitute
import com.global.vtg.appview.home.event.Attendees
import com.global.vtg.appview.home.event.Event
import com.global.vtg.appview.home.event.EventArray
import com.global.vtg.appview.home.profile.ResProfile
import com.global.vtg.appview.home.testHistory.TestKit
import com.global.vtg.appview.payment.ReqPayment
import com.global.vtg.model.network.result.BaseResult
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport
import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


/**
 * Created by sa on 14/06/16.
 *
 * api interface for generating request of webservice call.
 *
 */

interface ApiInterface {

    /*@POST("getItems")
    fun getNewsSource(@Body request: ListRequest): Deferred<Response<ListDataModel>>

    @POST("api/register")
    fun signup(@Body reqSingup: ReqSingup): Deferred<Response<Response<ResSingup>>>*/

    @GET("api/v1/user")
    fun loginAsync(): Deferred<Response<ResUser>>

    @GET("api/v1/config")
    fun getConfigAsync(): Deferred<Response<ResConfig>>

    @POST("api/v1/user")
    fun registerAsync(@Body reqRegister: ReqRegistration): Deferred<Response<ResUser>>

    @PUT("api/v1/user")
    fun registerStep1Async(@Body reqRegister: ResUser): Deferred<Response<ResUser>>

    @POST("api/payment/user")
    fun makePaymentAsync(@Body reqPayment: ReqPayment): Deferred<Response<BaseResult>>

    @POST("api/v1/event")
    fun createEvent(@Body reqPayment: Event): Deferred<Response<Event>>

    @PUT("api/v1/user/password")
    fun forgotPasswordAsync(@Body req: ReqRegistration): Deferred<Response<BaseResult>>

    @PUT("api/v1/user")
    fun changePasswordAsync(@Body modelReq: ResUser): Deferred<Response<ResUser>>

    @GET("api/v1/user")
    fun getUserAsync(): Deferred<Response<ResUser>>

    @POST("api/v1/pin")
    fun updatePinAsync(@Body j:JsonObject): Deferred<Response<BaseResult>>
    @POST("api/v1/pin/validate")
    fun validatePinAsync(@Body j:JsonObject): Deferred<Response<BaseResult>>

    @GET("api/v1/barcode/{barcodeId}")
    fun scanBarcodeIdAsync(@Path("barcodeId") barcodeId: String): Deferred<Response<ResUser>>


    @GET("api/v1/institute/{name}")
    fun searchInstituteAsync(@Path("name") name: String): Deferred<Response<ResInstitute>>


    @GET("api/v1/testList")
    fun testType(): Deferred<Response<TestType>>

    @GET("api/v1/rapidKitList")
    fun testkit(): Deferred<Response<TestKit>>

    @Multipart
    @POST("api/v1/vaccine/upload")
    fun uploadVaccineAsync(
        @Part file: MultipartBody.Part,
        @Part("type") type: RequestBody?,
        @Part("userId") userId: RequestBody?,
        @Part("instituteId") instituteId: RequestBody?,
        @Part("status") status: RequestBody?,
        @Part("certified") certified: RequestBody?,
        @Part("date") date: RequestBody?,
        @Part("srId") batchNo: RequestBody?,
        @Part("dose") dose: RequestBody?,
        @Part("username") username: RequestBody?
    ): Deferred<Response<ResUser>>

    @Multipart
    @POST("api/v1/healthinfo/upload")
    fun uploadHealthInfoAsync(
        @Part file: MultipartBody.Part?,
        @Part("userId") userId: RequestBody?,
        @Part("instituteId") instituteId: RequestBody?,
        @Part("status") status: RequestBody?,
        @Part("certified") certified: RequestBody?,
        @Part("date") date: RequestBody?,
        @Part("srId") batchNo: RequestBody?,
        @Part("result") result: RequestBody?,
        @Part("testId") testId: RequestBody?,
        @Part("username") username: RequestBody?
    ): Deferred<Response<ResUser>>

    @Multipart
    @POST("api/v1/testHistory/upload")
    fun uploadTestInfoAsync(
        @Part file: MultipartBody.Part?,
        @Part("userId") userId: RequestBody?,
        @Part("instituteId") instituteId: RequestBody?,
        @Part("status") status: RequestBody?,
        @Part("certified") certified: RequestBody?,
        @Part("date") date: RequestBody?,
        @Part("srId") batchNo: RequestBody?,
        @Part("result") result: RequestBody?,
        @Part("testId") testId: RequestBody?,
        @Part("username") username: RequestBody?,
        @Part("kitId") kitID: RequestBody?
    ): Deferred<Response<ResUser>>

    @Multipart
    @PUT("api/v1/user/profile")
    fun uploadProfileAsync(
        @Part file: MultipartBody.Part?,
        @Part("userId") userId: RequestBody?
    ): Deferred<Response<ResProfile>>

    @Multipart
    @PUT("api/v1/vendor/upload")
    fun uploadVendorStep2Async(
        @Part file: MultipartBody.Part?,
        @Part("vendorId") vendorId: RequestBody?,
        @Part("buisnessName") buisnessName: RequestBody?,
        @Part("buisnessId") buisnessId: RequestBody?,
        @Part("employeeId") employeeId: RequestBody?,
        @Part("certificateExpDate") date: RequestBody?,
        @Part("vat") vat: RequestBody?
    ): Deferred<Response<ResUser>>


    @POST("api/v1/event/profile")
    fun uploadEventImages(
        @Body  file : RequestBody
    ): Deferred<Response<BaseResult>>

    @GET("api/v1/events")
    fun getAllEvents(): Deferred<Response<EventArray>>

    @GET("api/v1/events/user/{userId}")
    fun getMyEvents(@Path("userId") barcodeId: String): Deferred<Response<EventArray>>

    @GET("api/v1/event/{eventId}")
    fun getEventId(@Path("eventId") barcodeId: String): Deferred<Response<Event>>

    @DELETE("api/v1/event/{eventId}")
    fun deleteEvent(@Path("eventId") barcodeId: String): Deferred<Response<BaseResult>>

    @DELETE("api/v1/event/profile/{id}")
    fun deleteEventPic(@Path("id") barcodeId: String): Deferred<Response<BaseResult>>

    @GET("api/v1/userSearch/{id}")
    fun searchUser(@Path("id") barcodeId: String): Deferred<Response<ResUser>>

    @POST("api/v1/eventUser")
    fun addUser(@Body barcodeId: JsonObject): Deferred<Response<BaseResult>>

    @GET("api/v1/eventUser/{id}")
    fun getUsers(@Path("id") barcodeId: String): Deferred<Response<Attendees>>

    @DELETE("api/v1/eventUser/{id}")
    fun deleteUser(@Path("id") barcodeId: String): Deferred<Response<BaseResult>>

    @GET("api/v1/eventUser/{eventid}/{id}")
    fun checkStatus(@Path("eventid") barcodeId: String,@Path("id") id: String): Deferred<Response<BaseResult>>

    @POST("api/v1/token")
    fun updateToken(@Body j: JsonObject): Deferred<Response<BaseResult>>

}
