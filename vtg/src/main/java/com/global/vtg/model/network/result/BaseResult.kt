package com.global.vtg.model.network.result

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
open class BaseResult {
    var status: String ?= null
    var error_1: Error? = null

    @SerializedName("error")
    var errorApi: String? = null
}

data class Error(val code: String?, val message: String?,val error: String?)