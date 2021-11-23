package com.global.vtg.appview.authentication.otp

import androidx.annotation.Keep
import com.global.vtg.model.network.result.BaseResult
import com.google.gson.annotations.SerializedName

@Keep
data class ResCreateTwilioUser(

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("user")
    val user: TwilioUser? = null
) : BaseResult()

@Keep
data class TwilioUser(

    @field:SerializedName("id")
    val id: Int? = null
)
