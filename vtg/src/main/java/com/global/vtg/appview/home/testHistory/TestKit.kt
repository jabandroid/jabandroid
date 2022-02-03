package com.global.vtg.appview.home.testHistory

import androidx.annotation.Keep
import com.global.vtg.appview.config.HealthInfo
import com.global.vtg.model.network.result.BaseResult
import com.google.gson.annotations.SerializedName

@Keep
data class TestKit(

    @field:SerializedName("kitList")
    var tests: ArrayList<TestKitResult>? = null

) : BaseResult()

data class TestKitResult(

    @field:SerializedName("id")
    var id: String? = null,

    @field:SerializedName("name")
    var name: String? = null,

    )
