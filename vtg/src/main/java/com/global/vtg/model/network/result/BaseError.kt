package com.global.vtg.model.network.result

import com.google.gson.annotations.SerializedName

class BaseError : BaseResult() {


    @SerializedName("message")
    var message: String? = null

    @SerializedName("error")
    var error: String? = null
}