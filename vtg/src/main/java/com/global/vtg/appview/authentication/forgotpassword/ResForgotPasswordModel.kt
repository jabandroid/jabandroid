package com.global.vtg.appview.authentication.forgotpassword

import com.google.gson.annotations.SerializedName
import com.global.vtg.model.network.result.BaseResult

data class ResForgotPasswordModel(
    @field:SerializedName("access_token")
    val accessToken: String?,
    @field:SerializedName("token_type")
    val tokenType: String?,
    @field:SerializedName("expires_in")
    val expiresIn: Long?,
    @field:SerializedName("refresh_token")
    val refreshToken: String?
) : BaseResult()