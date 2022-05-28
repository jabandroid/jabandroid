package com.global.vtg.appview.home.event

import androidx.annotation.Keep
import com.global.vtg.model.network.result.BaseResult
import com.google.gson.annotations.SerializedName

@Keep
data class EventArray (
    @field: SerializedName("events")
    var arr : ArrayList<Event>? = null
) :BaseResult()

@Keep
data class Event (
    @field: SerializedName("userId") var userId : String? = null,
    @field: SerializedName("id") var eventID : String? = "",
    @field: SerializedName("parentEvent") var parentEvent : String? = "",
    @field: SerializedName("eventName") var eventName : String ? = null,
    @field: SerializedName("userFirstName") var userFirstName : String ? = null,
    @field: SerializedName("userLastName") var userLastName : String ? = null,
    @field: SerializedName("userProfileUrl") var userProfileUrl : String ? = null,
    @field: SerializedName("startDate") var startDate : String ? = null,
    @field: SerializedName("endDate") var endDate : String ? = null,
    @field: SerializedName("verified") val verified : Boolean ? = null,
    @field: SerializedName("description") var description : String ? = null,
    @field: SerializedName("privateEvent") var privateEvent : Boolean ? = null,
    @field: SerializedName("eventAddress") var eventAddress : ArrayList<EventAddress>? = null,
    @field: SerializedName("eventImage") val eventImage : List<EventImage>? = null,
    @field: SerializedName("guestCount") val guestCount : Int? = null,
    @field: SerializedName("crowdLimit") var crowdLimit : String? = null,
    @field: SerializedName("subEvents")
     var arrSubEvent : ArrayList<Event>? = null
):BaseResult()

@Keep
data class EventAddress (
    @field: SerializedName("id") var addressID : String? = "",
    @field: SerializedName("addr1") val addr1 : String? = "",
     @field: SerializedName("addr2") val addr2 : String? = "",
     @field: SerializedName("addr3") val addr3 : String? = "",
     @field: SerializedName("zipCode") val zipCode : String? = "",
     @field: SerializedName("city") val city : String? = "",
     @field: SerializedName("state") val state : String? = "",
     @field: SerializedName("country") val country : String? = "",
     @field: SerializedName("phoneNo") val phoneNo : String? = "",
     @field: SerializedName("mobileNo") val mobileNo : String? = "",
     @field: SerializedName("fax") val fax : String? = "",
     @field: SerializedName("website") val web : String? = "",
     @field: SerializedName("email") val email : String? = "",
)

@Keep
data class EventImage (

     @field: SerializedName("url") val url : String,
     @field: SerializedName("id") val id : String,
     @field: SerializedName("banner") val banner : Boolean
)