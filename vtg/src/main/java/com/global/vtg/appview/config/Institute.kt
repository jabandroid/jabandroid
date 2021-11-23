package com.global.vtg.appview.config

import androidx.annotation.Keep
import com.global.vtg.model.network.result.BaseResult
import com.google.gson.annotations.SerializedName

@Keep
data class ResInstitute(
	@field:SerializedName("institute")
	val institute: ArrayList<Institute>? = null
): BaseResult()

data class Institute(

	@field:SerializedName("zipCode")
	val zipCode: String? = null,

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("addr2")
	val addr2: String? = null,

	@field:SerializedName("addr1")
	val addr1: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("addr3")
	val addr3: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("mobileNo")
	val mobileNo: String? = null,

	@field:SerializedName("fax")
	val fax: String? = null,

	@field:SerializedName("phoneNo")
	val phoneNo: String? = null
)
