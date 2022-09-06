package com.global.vtg.appview.config

import com.google.gson.annotations.SerializedName

data class TestInfo(

    @field:SerializedName("result")
    var result: String? = null,

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

    @field:SerializedName("kitName")
    val kit: String? = null,

    @field:SerializedName("addedBy")
    val addedBy: String? = "",

    var testName: String? = "",

    @field:SerializedName("instituteName")
    val insName: String? = ""
)
