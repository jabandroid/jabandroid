package com.global.vtg.appview.home.vaccinehistory

import com.google.gson.annotations.SerializedName

data class VaccineHistory(

    @field:SerializedName("date")
    val date: String? = null,

    @field:SerializedName("srId")
    val srId: String? = null,

    @field:SerializedName("instituteId")
    val instituteId: Int? = null,

    @field:SerializedName("documentLink")
    val documentLink: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("lastModified")
    val lastModified: String? = null,

    @field:SerializedName("type")
    val type: Int? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("dose")
    val dose: String? = null
)
