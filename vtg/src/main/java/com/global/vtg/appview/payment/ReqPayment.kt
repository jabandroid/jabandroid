package com.global.vtg.appview.payment

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ReqPayment(

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("data")
	var data: String? = null
)
