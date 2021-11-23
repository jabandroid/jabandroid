package com.global.vtg

import android.annotation.SuppressLint
import android.app.Application
import com.global.vtg.injections.appModule
import com.global.vtg.injections.appNetworkModule
import com.global.vtg.injections.appViewModelModule
import com.global.vtg.injections.databaseModule
import com.vtg.BuildConfig
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import org.koin.android.ext.android.startKoin
import timber.log.Timber


/**
 * Created by sa on 29/03/17.
 *
 */

@SuppressLint("Registered")
class App : Application() {
    lateinit var util: PhoneNumberUtil

    override fun onCreate() {
        super.onCreate()
        instance = this
        util = PhoneNumberUtil.createInstance(applicationContext)
        initializeTimber()
        initializeKoin()
    }

    override fun onTerminate() {
        super.onTerminate()
        if (instance != null) {
            instance = null
        }
    }

    companion object {
        var instance: App? = null
    }

    private fun initializeTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initializeKoin() {
        startKoin(this, listOf(appViewModelModule, appModule, appNetworkModule, databaseModule))
    }

}
