package com.global.vtg.appview.authentication.registration

import com.google.gson.annotations.SerializedName

data class AddressItem(

	@field:SerializedName("lastName")
	val lastName: String? = null,

	@field:SerializedName("zipCode")
	var zipCode: String? = null,

	@field:SerializedName("country")
	var country: String? = null,

	@field:SerializedName("addr2")
	var addr2: String? = null,

	@field:SerializedName("addr1")
    var addr1: String? = null,

	@field:SerializedName("city")
	var city: String? = null,

	@field:SerializedName("addr3")
	val addr3: Any? = null,

	@field:SerializedName("mobileNo")
	val mobileNo: String? = null,

	@field:SerializedName("phoneNo")
	val phoneNo: Any? = null,

	@field:SerializedName("billing")
    var billing: Boolean? = null,

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("shipping")
	val shipping: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("state")
	var state: String? = null,

	@field:SerializedName("fax")
	val fax: Any? = null
)
