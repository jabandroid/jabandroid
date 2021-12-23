package com.global.vtg.appview.config

import androidx.annotation.Keep
import com.global.vtg.model.network.result.BaseResult
import com.google.gson.annotations.SerializedName

@Keep
data class ResConfig(

	@field:SerializedName("country")
	val country: List<CountryItem?>? = null,

	@field:SerializedName("colorSchema")
	val colorSchema: String? = null,

	@field:SerializedName("configId")
	val configId: Int? = null,

	@field:SerializedName("institute")
	val institute: List<Institute?>? = null,

	@field:SerializedName("vaccineType")
	val vaccineType: ArrayList<VaccineTypeItem>? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("headline")
	val headline: String? = null,

	@field:SerializedName("logoURL")
	val logoURL: String? = null,

	@field:SerializedName("productList")
	val productList: List<ProductListItem?>? = null	,
	@field:SerializedName("dose")
	val doses: List<Doses?>? = null
): BaseResult()

data class CountryItem(

	@field:SerializedName("numcode")
	val numcode: Int? = null,

	@field:SerializedName("iso")
	val iso: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("nicename")
	val nicename: String? = null,

	@field:SerializedName("phonecode")
	val phonecode: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("iso3")
	val iso3: String? = null
)

data class ProductListItem(

	@field:SerializedName("cost")
	val cost: Double? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class Doses(



	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("dose_id")
	val id: String? = null
)

data class VaccineTypeItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: String? = null
)
