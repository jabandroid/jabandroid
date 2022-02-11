package com.global.vtg.appview.config

import com.google.gson.annotations.SerializedName

data class HealthInfo(

    @field:SerializedName("result")
    val result: String? = null,

    @field:SerializedName("srId")
    val srId: String? = "",

    @field:SerializedName("date")
    val date: String? = null,

    @field:SerializedName("instituteId")
    val instituteId: Int? = null,

    @field:SerializedName("documentLink")
    val documentLink: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("lastModified")
    val lastModified: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("testId")
    val test: String? = null,
    @field:SerializedName("testName")
    var testName: String? = null,

    @field:SerializedName("addedBy")
    var addedBy: String? = "",

    //var testName: String? = ""
)
