package com.global.vtg.appview.authentication.registration

import com.google.gson.annotations.SerializedName

data class ExtraItem(

	@field:SerializedName("v")
	val V: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("k")
	val K: String? = null
)
