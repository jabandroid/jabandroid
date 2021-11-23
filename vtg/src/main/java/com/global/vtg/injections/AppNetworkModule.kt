package com.global.vtg.injections

import android.util.Log
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.test.Const
import com.global.vtg.utils.Constants.IS_SIGN_IN
import com.global.vtg.utils.Constants.tempPassword
import com.global.vtg.utils.Constants.tempUsername
import com.global.vtg.utils.SharedPreferenceUtil
import com.global.vtg.wscoroutine.ApiConstant
import com.global.vtg.wscoroutine.ApiInterface
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

//import timber.log.Timber

val appNetworkModule = module {
    // Dependency: Retrofit
    single<Retrofit>(name = "main") {
        Retrofit.Builder()
            .baseUrl(Const.BASE_URL)
            .client(get())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Dependency: ApiService
    single {
        val retrofit: Retrofit = get("main")
        retrofit.create(ApiInterface::class.java)
    }

    // Dependency: HttpLoggingInterceptor
    single<Interceptor>(name = "LOGGING_INTERCEPTOR") {
        HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.tag("OkHttp").d(message)
            }
        }).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single(name = "OK_HTTP_INTERCEPTOR") {
        Interceptor { chain ->
            val prefManger: PreferenceManager = get()
            val token = if (prefManger.getAccessToken() == null) "" else prefManger.getAccessToken()
            val username =
                SharedPreferenceUtil.INSTANCE?.getData(PreferenceManager.KEY_USER_NAME, "")
            val password =
                SharedPreferenceUtil.INSTANCE?.getData(PreferenceManager.KEY_PASSWORD, "")
            val role =
                SharedPreferenceUtil.INSTANCE?.getData(PreferenceManager.KEY_ROLE, "user")

            val builder = chain.request().newBuilder()
                .addHeader(ApiConstant.HEADER_AUTHORIZATION, "bearer $token")

            if (IS_SIGN_IN) {
                builder.addHeader(
                    ApiConstant.HEADER_USERNAME,
                    if (IS_SIGN_IN) tempUsername else username ?: ""
                )
                builder.addHeader(
                    ApiConstant.HEADER_PASSWORD,
                    if (IS_SIGN_IN) tempPassword else password ?: ""
                )
            } else if (username?.isNotEmpty() == true) {
                builder.addHeader(
                    ApiConstant.HEADER_USERNAME,
                    username
                )
                builder.addHeader(
                    ApiConstant.HEADER_PASSWORD,
                    password ?: ""
                )
            }
            if (role?.isNotEmpty() == true) {
                builder.addHeader(
                    ApiConstant.HEADER_ROLE,
                    role
                )
            }
            builder.addHeader(
                "Content-Type",
                "application/json"
            )
            chain.proceed(
                builder.build()
            )
        }
    }

    single(name = "OK_HTTP_INTERCEPTOR_TWILIO") {
        Interceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader("X-Authy-API-Key", "ORdNXMXiNsABeKDoid1jvjCw1pWHOmDi")
                    .build()
            )
        }
    }

    // Dependency: OkHttpClient
    single {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(get<Interceptor>("LOGGING_INTERCEPTOR"))
            .addInterceptor(get<Interceptor>("OK_HTTP_INTERCEPTOR"))
            .addInterceptor(get<Interceptor>("OK_HTTP_INTERCEPTOR_TWILIO"))
            .build()
    }

}