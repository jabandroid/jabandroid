package com.global.vtg.appview.home.travel

import com.google.gson.annotations.SerializedName


data class TravelForm (

 var booleanStep1: Boolean = true,
 @SerializedName("userId") val userId : Int=0,
 @SerializedName("country") val country : String="",
 @SerializedName("flightInformation") val flightInformation : FlightInformation?=null,
 @SerializedName("contactInformation") val contactInformation : ContactInformation?=null,
 @SerializedName("destinationInformation") val destinationInformation : DestinationInformation?=null,
 @SerializedName("travelInformation") val travelInformation : TravelInformation?=null,
 @SerializedName("numberOfTravellers") val numberOfTravellers : Int=0
)


data class FlightInformation (

 @SerializedName("airlineName") val airlineName : String,
 @SerializedName("countryOfEmbarkation") val countryOfEmbarkation : String,
 @SerializedName("boardedAt") val boardedAt : String,
 @SerializedName("flightNumber") val flightNumber : Int,
 @SerializedName("arrivalTime") val arrivalTime : String,
 @SerializedName("portOfArrival") val portOfArrival : String,
 @SerializedName("statusOnBoard") val statusOnBoard : String,

 var done: Boolean = false,
)

data class ContactInformation (

 @SerializedName("email") val email : String,
 @SerializedName("phoneNumber") val phoneNumber : Int,
 var done: Boolean = false,
)

data class DestinationInformation (

 @SerializedName("purposeOfVisit") val purposeOfVisit : String,
 @SerializedName("lengthOfStay") val lengthOfStay : String,
 @SerializedName("accomodationType") val accomodationType : String,
 @SerializedName("address") val address : String,
 @SerializedName("island") val island : String,
 @SerializedName("town") val town : String,
 @SerializedName("quarantine") val quarantine : String,
 @SerializedName("confirmationNumber") val confirmationNumber : String,
 var done: Boolean = false,
)


data class TravelInformation (

 @SerializedName("travelDocumentType") val travelDocumentType : String,
 @SerializedName("travelDocumentNumber") val travelDocumentNumber : Int,
 @SerializedName("travelDocumentIssueDate") val travelDocumentIssueDate : String,
 @SerializedName("travelDocumentExpDate") val travelDocumentExpDate : String,
 @SerializedName("travelDocumentIssueCountry") val travelDocumentIssueCountry : String,
 var done: Boolean = false,
)