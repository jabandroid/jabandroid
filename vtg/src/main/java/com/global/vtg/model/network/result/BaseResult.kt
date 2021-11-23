package com.global.vtg.model.network.result

import androidx.annotation.Keep

@Keep
open class BaseResult {
    var status: String ?= null
    var error: Error? = null
}

data class Error(val code: String?, val message: String?)