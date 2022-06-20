package com.global.vtg.appview.home.parentchild

import androidx.annotation.Keep
import com.global.vtg.model.network.result.BaseResult
import com.google.gson.annotations.SerializedName

@Keep
data class AddChild(

	@field:SerializedName("message")
	val message: String? = null
) : BaseResult()
