package com.global.vtg.appview.home.event

import androidx.annotation.Keep
import com.global.vtg.model.network.result.BaseResult
import com.google.gson.annotations.SerializedName

@Keep
data class Attendees (
    @field: SerializedName("eventUsers")
    var arrUser : ArrayList<EventUser>? = null
) :BaseResult()

@Keep
data class EventUser (
    @field: SerializedName("id") var id : String? = null,
    @field: SerializedName("eventId") var eventId : String? = null,
    @field: SerializedName("userId") var userId : String? = null,
    @field: SerializedName("userName") var userName : String? = null,
    @field: SerializedName("notified") var notified : Boolean? = null,
    @field: SerializedName("name") var firstName : String? = null,
    @field: SerializedName("lastName") var lastName : String? = "",
    @field: SerializedName("profileUrl") var profileUrl : String? = null,
    @field: SerializedName("interested") var interested : String? = null,


)