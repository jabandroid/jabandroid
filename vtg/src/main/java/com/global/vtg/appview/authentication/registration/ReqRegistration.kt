package com.global.vtg.appview.authentication.registration

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ReqRegistration(

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("mobileNo")
	val mobileNo: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("twilioUserId")
	var twilioUserId: String? = null
)
