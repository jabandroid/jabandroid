package com.global.vtg.appview.home.help

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.base.AppViewModel
import com.vtg.R

class HelpViewModel(application: Application, private val userRepository: UserRepository) :
    AppViewModel(application) {
    val terms: MutableLiveData<Boolean> = MutableLiveData()
    val policy: MutableLiveData<Boolean> = MutableLiveData()
    val setting: MutableLiveData<String> = MutableLiveData()

    fun onClick(view: View) {
        when (view.id) {
            R.id.tvTerms -> {
                terms.postValue(true)
            }
            R.id.tvPolicy -> {
                policy.postValue(true)
            }
            R.id.tvLanguage -> {
                setting.postValue("language")
            }
            R.id.tvAboutUs -> {
                setting.postValue("about_us")
            }
        }
    }
}