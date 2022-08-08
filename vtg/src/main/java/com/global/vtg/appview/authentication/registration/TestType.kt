package com.global.vtg.appview.authentication.registration

import androidx.annotation.Keep
import com.global.vtg.model.network.result.BaseResult
import com.google.gson.annotations.SerializedName

@Keep
data class TestType(

    @field:SerializedName("testList")
    var tests: ArrayList<TestTypeResult>? = null

) : BaseResult()

data class TestTypeResult(

    @field:SerializedName("id")
    var id: String? = null,

    @field:SerializedName("name")
    var name: String? = null,

    )
