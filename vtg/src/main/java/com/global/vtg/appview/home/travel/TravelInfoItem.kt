package com.global.vtg.appview.home.travel

import android.os.Parcel
import android.os.Parcelable


class TravelInfoItem() :Parcelable{

   var flightNumber:String=""
   var departureCode:String=""
   var departureCountryCode:String=""
   var departureName:String=""
   var arrivalCode:String=""
   var arrivalCountryCode:String=""
   var arrivalName:String=""

   constructor(parcel: Parcel) : this() {
     flightNumber = parcel.readString().toString()
     departureCode = parcel.readString().toString()
     arrivalCode = parcel.readString().toString()
     departureName = parcel.readString().toString()
     departureCountryCode = parcel.readString().toString()
     arrivalCountryCode = parcel.readString().toString()
     arrivalName = parcel.readString().toString()
   }

   override fun writeToParcel(parcel: Parcel, flags: Int) {
     parcel.writeString(flightNumber)
     parcel.writeString(departureCode)
     parcel.writeString(arrivalCode)
     parcel.writeString(departureName)
     parcel.writeString(arrivalName)
     parcel.writeString(departureCountryCode)
     parcel.writeString(arrivalCountryCode)
   }

   override fun describeContents(): Int {
     return 0
   }

   companion object CREATOR : Parcelable.Creator<TravelInfoItem> {
     override fun createFromParcel(parcel: Parcel): TravelInfoItem {
       return TravelInfoItem(parcel)
     }

     override fun newArray(size: Int): Array<TravelInfoItem?> {
       return arrayOfNulls(size)
     }
   }

 }




