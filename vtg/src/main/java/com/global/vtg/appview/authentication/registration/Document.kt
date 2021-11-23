package com.global.vtg.appview.authentication.registration

import com.google.gson.annotations.SerializedName

data class Document(

	@field:SerializedName("identity")
	val identity: String? = null,

	@field:SerializedName("expireDate")
	val expireDate: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("issueDate")
	val issueDate: String? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("country")
	val country: String? = null
)
