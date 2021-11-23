package com.global.vtg.appview.authentication.otp

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ResToken(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("cellphone")
	val cellphone: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
