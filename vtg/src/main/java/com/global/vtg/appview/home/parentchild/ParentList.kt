package com.global.vtg.appview.home.parentchild

import androidx.annotation.Keep
import com.global.vtg.model.network.result.BaseResult
import com.google.gson.annotations.SerializedName

@Keep
data class ParentList(

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("child_id")
    val childId: String? = null,

    @field:SerializedName("parent_id")
    val ParentID: String? = null,

    @field:SerializedName("parentFirstName")
    var firstName: String? = null,
    @field:SerializedName("parentLastName")
    var lastName: String? = null,

    @field:SerializedName("parentProfile")
    val profilePic: String? = null
) : BaseResult()
