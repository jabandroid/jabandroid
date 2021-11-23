package com.global.vtg.appview.home.vaccinecard

import android.app.Application
import android.view.View
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.base.AppViewModel
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.vtg.R

class VaccineCardViewModel(application: Application, private val userRepository: UserRepository) : AppViewModel(application) {

    fun onClick(view: View) {
        if (isNetworkAvailable(view.context)) {

        } else {
            DialogUtils.showSnackBar(
                view.context,
                view.context.resources.getString(R.string.no_connection)
            )
        }
    }
}