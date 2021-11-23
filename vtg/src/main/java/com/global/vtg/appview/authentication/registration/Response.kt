package com.global.vtg.appview.authentication.registration

import com.google.gson.annotations.SerializedName

data class Response(

	@field:SerializedName("profileUrl")
	val profileUrl: String? = null,

	@field:SerializedName("lastName")
	val lastName: String? = null,

	@field:SerializedName("note")
	val note: String? = null,

	@field:SerializedName("address")
	val address: List<Any?>? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("productId")
	val productId: Int? = null,

	@field:SerializedName("citizenship")
	val citizenship: String? = null,

	@field:SerializedName("document")
	val document: List<Any?>? = null,

	@field:SerializedName("extras")
	val extras: List<Any?>? = null,

	@field:SerializedName("dateOfBirth")
	val dateOfBirth: Any? = null,

	@field:SerializedName("mobileNo")
	val mobileNo: String? = null,

	@field:SerializedName("step2Complete")
	val step2Complete: Boolean? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("step1Complete")
	val step1Complete: Boolean? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("vaccine")
	val vaccine: List<Any?>? = null,

	@field:SerializedName("step3Complete")
	val step3Complete: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("barcodeUrl")
	val barcodeUrl: String? = null,

	@field:SerializedName("healthInfo")
	val healthInfo: List<Any?>? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("barcodeId")
	val barcodeId: String? = null
)
