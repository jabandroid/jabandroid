package com.global.vtg.appview.home.profile

import androidx.annotation.Keep
import com.global.vtg.model.network.result.BaseResult
import com.google.gson.annotations.SerializedName

@Keep
data class ResProfile(

	@field:SerializedName("profileUrl")
	val profileUrl: String? = null
) : BaseResult()
