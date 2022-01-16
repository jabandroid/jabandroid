package com.global.vtg.appview.home.testHistory

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.base.AppViewModel
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.vtg.R

class TestHistoryViewModel(application: Application, private val userRepository: UserRepository) : AppViewModel(application) {
    val uploadFile: MutableLiveData<Boolean> = MutableLiveData()
    val uploadProfilePic: MutableLiveData<Boolean> = MutableLiveData()

    fun onClick(view: View) {
        when (view.id) {
            R.id.btnUploadFile -> {
                uploadFile.postValue(true)
            }
            /*R.id.ivProfilePic -> {
                if (isNetworkAvailable(view.context)) {
                    uploadProfilePic.postValue(true)
                } else {
                    DialogUtils.showSnackBar(
                        view.context,
                        view.context.resources.getString(R.string.no_connection)
                    )
                }
            }*/
        }

    }
}