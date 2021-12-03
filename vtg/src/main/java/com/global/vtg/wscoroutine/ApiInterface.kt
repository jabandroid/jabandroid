package com.global.vtg.wscoroutine

import com.global.vtg.appview.authentication.forgotpassword.ReqForgotPasswordModel
import com.global.vtg.appview.authentication.registration.ReqRegistration
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.appview.config.ResConfig
import com.global.vtg.appview.config.ResInstitute
import com.global.vtg.appview.home.profile.ResProfile
import com.global.vtg.appview.payment.ReqPayment
import com.global.vtg.model.network.result.BaseResult
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

    @PUT("api/v1/user/password")
    fun forgotPasswordAsync(@Body req: ReqRegistration): Deferred<Response<BaseResult>>

    @PUT("api/v1/user")
    fun changePasswordAsync(@Body modelReq: ResUser): Deferred<Response<ResUser>>

    @GET("api/v1/user")
    fun getUserAsync(): Deferred<Response<ResUser>>

    @GET("api/v1/barcode/{barcodeId}")
    fun scanBarcodeIdAsync(@Path("barcodeId") barcodeId: String): Deferred<Response<ResUser>>

    @GET("api/v1/institute/{name}")
    fun searchInstituteAsync(@Path("name") name: String): Deferred<Response<ResInstitute>>

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
        @Part("username") username: RequestBody?
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
        @Part("employeeId") employeeId: RequestBody?
    ): Deferred<Response<ResUser>>
}
