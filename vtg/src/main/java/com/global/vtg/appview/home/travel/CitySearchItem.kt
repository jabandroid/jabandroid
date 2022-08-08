package com.global.vtg.appview.home.travel

import androidx.annotation.Keep
import com.global.vtg.model.network.result.BaseResult
import com.google.gson.annotations.SerializedName

@Keep
data class CitySearchItem (

  @SerializedName("meta" ) var meta : Meta?           = Meta(),
  @SerializedName("data" ) var data : ArrayList<Data> = arrayListOf()

): BaseResult()


data class Meta (

  @SerializedName("count" ) var count : Int?   = null,
  @SerializedName("links" ) var links : Links? = Links()

)



data class Data (

  @SerializedName("type"         ) var type         : String?  = null,
  @SerializedName("subType"      ) var subType      : String?  = null,
  @SerializedName("name"         ) var name         : String?  = null,
  @SerializedName("detailedName" ) var detailedName : String?  = null,
  @SerializedName("id"           ) var id           : String?  = null,
  @SerializedName("self"         ) var self         : Self?    = Self(),
  @SerializedName("iataCode"     ) var iataCode     : String?  = null,
  @SerializedName("address"      ) var address      : Address? = Address()

)

data class Self (

  @SerializedName("href"    ) var href    : String?           = null,
  @SerializedName("methods" ) var methods : ArrayList<String> = arrayListOf()

)

data class Links (

  @SerializedName("self" ) var self : String? = null

)

data class Address (

  @SerializedName("cityName"    ) var cityName    : String? = null,
  @SerializedName("countryName" ) var countryName : String? = null

)