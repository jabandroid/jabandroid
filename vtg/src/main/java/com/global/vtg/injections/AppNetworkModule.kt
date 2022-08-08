package com.global.vtg.injections


import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.test.Const.BASE_URL
import com.global.vtg.utils.Constants.IS_SIGN_IN
import com.global.vtg.utils.Constants.tempPassword
import com.global.vtg.utils.Constants.tempUsername
import com.global.vtg.utils.SharedPreferenceUtil
import com.global.vtg.wscoroutine.ApiConstant
import com.global.vtg.wscoroutine.ApiInterface
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


//import timber.log.Timber

val appNetworkModule = module {
    // Dependency: Retrofit

    val gson = GsonBuilder()
        .setLenient()
        .create()
    single<Retrofit>(name = "main") {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get (  ))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
//
//        Retrofit.Builder()
//            .baseUrl(Constants.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
    }

    // Creating OkHttpclient Object


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



        // Install the all-trusting trust manager

       //  Create an ssl socket factory with our all-trusting manager
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(get<Interceptor>("LOGGING_INTERCEPTOR"))
                .addInterceptor(get<Interceptor>("OK_HTTP_INTERCEPTOR"))
                .addInterceptor(get<Interceptor>("OK_HTTP_INTERCEPTOR_TWILIO"))

            builder.sslSocketFactory(
                sslSocketFactory,
                trustAllCerts[0] as X509TrustManager
            )
            builder.hostnameVerifier(HostnameVerifier { hostname, session -> true })

            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
//
//        val okHttpClient: OkHttpClient = TrustClient().getUnsafeOkHttpClient()
//        okHttpClient.Builder()
//            .connectTimeout(10, TimeUnit.SECONDS)
//            .readTimeout(10, TimeUnit.SECONDS)
//            .writeTimeout(10, TimeUnit.SECONDS)
//            .addInterceptor(get<Interceptor>("LOGGING_INTERCEPTOR"))
//            .addInterceptor(get<Interceptor>("OK_HTTP_INTERCEPTOR"))
//            .addInterceptor(get<Interceptor>("OK_HTTP_INTERCEPTOR_TWILIO"))
//            .build()
    }



}