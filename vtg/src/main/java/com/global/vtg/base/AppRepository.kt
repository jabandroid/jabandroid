package com.global.vtg.base


import android.util.Log
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.model.network.Resource
import com.global.vtg.model.network.result.BaseError
import com.global.vtg.model.network.result.BaseResult
import com.global.vtg.utils.StringUtils
import com.global.vtg.wscoroutine.ApiConstant
import org.json.JSONObject
import retrofit2.Response
import java.net.ConnectException

open class AppRepository {

    suspend fun <T : BaseResult> safeApiCall(call: suspend () -> Response<T>): BaseResult? {
        val result: Resource<T> = safeApiResult(call)
        var data: BaseResult? = null

        when (result) {
            is Resource.Success -> data = result.data
            is Resource.Error -> data = result.error
        }
        return data
    }

    suspend fun <T : BaseResult> safeApiResult(call: suspend () -> Response<T>): Resource<T> {
        try {
            val response = call.invoke()
            Log.e("Response","REsponse"+response.toString())
            if(response.code()==200){
                Log.e("Response123","REsponse123"+response.toString())
            }
            if (response.isSuccessful) {
                if (response.body() != null && response.body() is BaseResult) {
                    Log.e("Response124","REsponse124"+response.toString())


                    val baseResult = response.body() as BaseResult
                    baseResult.code= response.code().toString()

                    return if (baseResult.status == null || baseResult.status.equals("Success", true)) {
                        if(baseResult.errorApi!=null&&!baseResult.errorApi.equals("null")){
                            val baseError = BaseError()
                                baseError.code = response.code().toString()
                                baseError.message = baseResult.errorApi
                            Resource.Error(baseError)
                        }else
                        Resource.Success(response.body()!!)
                    } else {
                        val baseError = BaseError()
                        if (baseResult.error_1 != null) {
                            baseError.code = baseResult.error_1?.code
                            baseError.message = baseResult.error_1?.message
                        } else {
                            baseError.code = ApiConstant.SOMETHING_WRONG_ERROR_STATUS
                            baseError.message = ApiConstant.SOMETHING_WRONG_ERROR
                        }
                        Resource.Error(baseError)
                    }
                } else {

                    Log.e("Response125","REsponse125"+response.toString())

                    if(response.code()==200){
                        val baseError = ResUser()
                        baseError.code = "300"

                        return Resource.Success(response.body()!!)
                    }else {
                        val baseError = BaseError()
                        baseError.code = "101"
                        baseError.message = ApiConstant.SOMETHING_WRONG_ERROR
                        return Resource.Error(baseError)
                    }
                }
            } else {
                if (response.errorBody() != null) {

                    if (response.headers()["Content-Type"]?.contains("application/json")!!) {//check if response is in json format

                        when (response.code()) {

                        }
                        val errorString = response.errorBody()?.string()
                        val baseError = BaseError()
                        if (StringUtils.isNotNullOrNotEmpty(errorString)) {
                            val jsonResponse = JSONObject(errorString)
                            try {
                                baseError.code = response.code().toString()
                                val error = jsonResponse.get("error").toString()
                                val jsonError = JSONObject(error)
                                baseError.message = jsonError.get("message").toString()
                                baseError.message =error
                            } catch (e: Exception) {
                                baseError.code = ApiConstant.SOMETHING_WRONG_ERROR_STATUS
                                baseError.message = ApiConstant.SOMETHING_WRONG_ERROR
                            }
                        } else {
                            baseError.code = ApiConstant.SOMETHING_WRONG_ERROR_STATUS
                            baseError.message = ApiConstant.SOMETHING_WRONG_ERROR
                        }
                        return Resource.Error(baseError)

                    } else {//if response is not in json format than handle it
                        val baseError = BaseError()
                        baseError.code = "101"
                        baseError.message = ApiConstant.SOMETHING_WRONG_ERROR
                        return Resource.Error(baseError)
                    }
                } else {
                    val baseError = BaseError()
                    baseError.code = ApiConstant.SOMETHING_WRONG_ERROR_STATUS
                    baseError.message = ApiConstant.SOMETHING_WRONG_ERROR
                    return Resource.Error(baseError)
                }

            }
        } catch (error: Exception) {
            return when (error) {
                is ConnectException -> {
                    val baseError = BaseError()
                    baseError.code = ApiConstant.TIME_OUT_CONNECTION_STATUS_1
                    baseError.message = ApiConstant.TIME_OUT_CONNECTION
                    Resource.Error(baseError)
                }

                else -> {
                    val baseError = BaseError()
                    baseError.code = ApiConstant.SOMETHING_WRONG_ERROR_STATUS
                    baseError.message = ApiConstant.SOMETHING_WRONG_ERROR
                    Resource.Error(baseError)
                }
            }

        }

    }
}