package com.global.vtg.appview.authentication.registration

import androidx.annotation.Keep
import com.global.vtg.appview.config.HealthInfo
import com.global.vtg.appview.config.TestInfo
import com.global.vtg.appview.home.vaccinehistory.VaccineHistory
import com.global.vtg.model.network.result.BaseResult
import com.google.gson.annotations.SerializedName

@Keep
data class ResUserMain(
    @field:SerializedName("profileUrl")
    var profileUrl: String? = null,

    @field:SerializedName("ethnicity")
    var ethnicity: String? = null,

    @field:SerializedName("gender")
    var gender: String? = null,

    @field:SerializedName("lastName")
    var lastName: String? = "",

    @field:SerializedName("website")
    var website: String? = null,

    @field:SerializedName("twilioUserId")
    var twilioUserId: String? = null,

    @field:SerializedName("note")
    val note: String? = null,

    @field:SerializedName("birthCity")
    var birthCity: String? = null,

    @field:SerializedName("birthState")
    var birthState: String? = null,

    @field:SerializedName("birthCountry")
    var birthCountry: String? = null,

    @field:SerializedName("address")
    var address: ArrayList<AddressItem?>? = null,

    @field:SerializedName("role")
    var role: String? = null,

    @field:SerializedName("isClinic")
    var isClinic: Boolean? = null,

    @field:SerializedName("productId")
    val productId: Int? = null,

    @field:SerializedName("citizenship")
    var citizenship: String? = null,

    @field:SerializedName("document")
    val document: ArrayList<Document?>? = null,

    @field:SerializedName("extras")
    val extras: List<ExtraItem?>? = null,

    @field:SerializedName("dateOfBirth")
    var dateOfBirth: String? = null,

    @field:SerializedName("mobileNo")
    val mobileNo: String? = null,

    @field:SerializedName("step2Complete")
    var step2Complete: Boolean? = null,

    @field:SerializedName("title")
    var title: String? = null,

    @field:SerializedName("firstName")
    var firstName: String? = "",

    @field:SerializedName("step1Complete")
    var step1Complete: Boolean? = false,

    @field:SerializedName("password")
    var password: String? = null,

    @field:SerializedName("vaccine")
    val vaccine: ArrayList<VaccineHistory>? = null,

    @field:SerializedName("testHistory")
    val test: ArrayList<TestInfo>? = null,

    @field:SerializedName("step3Complete")
    var step3Complete: Boolean? = null,

    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("barcodeUrl")
    val barcodeUrl: String? = null,

    @field:SerializedName("healthInfo")
    val healthInfo: ArrayList<HealthInfo>? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("userPin")
    val pin: String? = null,

    @field:SerializedName("barcodeId")
    val barcodeId: String? = null,

    @field:SerializedName("barcodeUUID")
    val barcodeUUID: String? = null,

    @field:SerializedName("vendorVerify")
    val vendorVerify: String? = null,

    @field:SerializedName("parentId")
    var parentId: String? = null,

    @field:SerializedName("childAccount")
    val childAccount: ArrayList<ResUserMain>? = null,

    ): BaseResult()
