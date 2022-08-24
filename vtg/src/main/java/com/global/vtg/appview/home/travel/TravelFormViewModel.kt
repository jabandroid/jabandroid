package com.global.vtg.appview.home.travel

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.App
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.config.ResInstitute
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.model.network.result.BaseResult
import com.vtg.R
import kotlinx.android.synthetic.main.fragment_arrival.*
import kotlinx.coroutines.launch


class TravelFormViewModel(
    application: Application,
    private val userRepository: UserRepository
) :
    AppViewModel(application) {

    var showToastError: MutableLiveData<ArrayList<Int>> = MutableLiveData()
    var saveSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val airlineName: MutableLiveData<String> = MutableLiveData()
    val countryOfEmbaraktion: MutableLiveData<String> = MutableLiveData()
    val boardedAt: MutableLiveData<String> = MutableLiveData()
    val flightNumber: MutableLiveData<String> = MutableLiveData()
    val arrivalTime: MutableLiveData<String> = MutableLiveData()
    val portOfArrival: MutableLiveData<String> = MutableLiveData()
    val statusOnBoard: MutableLiveData<String> = MutableLiveData()
    val email: MutableLiveData<String> = MutableLiveData()
    val phone: MutableLiveData<String> = MutableLiveData()
    val numberofTravelers: MutableLiveData<String> = MutableLiveData()
    val PurposeOfVisit: MutableLiveData<String> = MutableLiveData()
    val PurposeOfVisit_1: MutableLiveData<String> = MutableLiveData()

    val lengthStay: MutableLiveData<String> = MutableLiveData()
    val accomodationType: MutableLiveData<String> = MutableLiveData()
    val accomodationType_1: MutableLiveData<String> = MutableLiveData()
    val address: MutableLiveData<String> = MutableLiveData()
    val island: MutableLiveData<String> = MutableLiveData()
    val village: MutableLiveData<String> = MutableLiveData()
    val quartine: MutableLiveData<String> = MutableLiveData()
    val confirmationNumber: MutableLiveData<String> = MutableLiveData()


    init {


    }

    fun onClick(view: View) {
        validateFieldsFlightInfo()
    }


    override fun isNullOrEmpty(s: String?): Boolean {
        return s == null || s.isEmpty() || s.trim().isEmpty()
    }


    fun getInfo(cityCode: String, countryCode: String) {
        scope.launch {
            userRepository.getInfo(cityCode, countryCode)
        }
    }


    override fun onCleared() {
        super.onCleared()

    }


    public fun validateFieldsFlightInfo(): Boolean {
        var isValidate = true
        when {
            isNullOrEmpty(airlineName.value) -> {
                val arrIds = ArrayList<Int>()
                arrIds.add(R.id.tvAirlineName)
                arrIds.add(R.id.expandable_layout)
                showToastError.postValue(arrIds)
                isValidate = false
            }
            isNullOrEmpty(countryOfEmbaraktion.value) -> {
                val arrIds = ArrayList<Int>()
                arrIds.add(R.id.tvCountryOfEmbaraktion)
                arrIds.add(R.id.expandable_layout)
                showToastError.postValue(arrIds)
                isValidate = false
            }

            isNullOrEmpty(boardedAt.value) -> {
                val arrIds = ArrayList<Int>()
                arrIds.add(R.id.tvBoardedAt)
                arrIds.add(R.id.expandable_layout)
                showToastError.postValue(arrIds)
                isValidate = false
            }
            isNullOrEmpty(flightNumber.value) -> {
                val arrIds = ArrayList<Int>()
                arrIds.add(R.id.tvflightNumber)
                arrIds.add(R.id.expandable_layout)
                showToastError.postValue(arrIds)
                isValidate = false
            }
            isNullOrEmpty(arrivalTime.value) -> {
                val arrIds = ArrayList<Int>()
                arrIds.add(R.id.tvArrivalTime)
                arrIds.add(R.id.expandable_layout)
                showToastError.postValue(arrIds)
                isValidate = false
            }
            isNullOrEmpty(portOfArrival.value) -> {
                val arrIds = ArrayList<Int>()
                arrIds.add(R.id.tvPortOfArrival)
                arrIds.add(R.id.expandable_layout)
                showToastError.postValue(arrIds)
                isValidate = false
            }
            isNullOrEmpty(statusOnBoard.value) -> {
                val arrIds = ArrayList<Int>()
                arrIds.add(R.id.tvStatusOfBoard)
                arrIds.add(R.id.expandable_layout)
                showToastError.postValue(arrIds)
                isValidate = false
            }
            isNullOrEmpty(email.value) -> {
                val arrIds = ArrayList<Int>()
                arrIds.add(R.id.tvEmail)
                arrIds.add(R.id.expandable_layout_2)
                showToastError.postValue(arrIds)
                isValidate = false
            }

            isNullOrEmpty(phone.value) -> {
                val arrIds = ArrayList<Int>()
                arrIds.add(R.id.tvPhone)
                arrIds.add(R.id.expandable_layout_2)
                showToastError.postValue(arrIds)
                isValidate = false
            }
            isNullOrEmpty(numberofTravelers.value) -> {
                val arrIds = ArrayList<Int>()
                arrIds.add(R.id.tvNumberOFTraveler)
                arrIds.add(R.id.expandable_layout_4)
                showToastError.postValue(arrIds)
                isValidate = false
            }

            isNullOrEmpty(PurposeOfVisit.value) || isNullOrEmpty(PurposeOfVisit_1.value) -> {
                val arrIds = ArrayList<Int>()
                arrIds.add(R.id.tvPurposeOfVisit)
                arrIds.add(R.id.expandable_layout_3)
                showToastError.postValue(arrIds)
                isValidate = false
            }
            isNullOrEmpty(lengthStay.value) -> {
                val arrIds = ArrayList<Int>()
                arrIds.add(R.id.tvLengthStay)
                arrIds.add(R.id.expandable_layout_3)
                showToastError.postValue(arrIds)
                isValidate = false
            }
            isNullOrEmpty(accomodationType.value) || isNullOrEmpty(accomodationType_1.value)-> {
                val arrIds = ArrayList<Int>()
                arrIds.add(R.id.tvAccomodationType)
                arrIds.add(R.id.expandable_layout_3)
                showToastError.postValue(arrIds)
                isValidate = false
            }
            isNullOrEmpty(address.value) -> {
                val arrIds = ArrayList<Int>()
                arrIds.add(R.id.tvAddress)
                arrIds.add(R.id.expandable_layout_3)
                showToastError.postValue(arrIds)
                isValidate = false
            }
            isNullOrEmpty(island.value) -> {
                val arrIds = ArrayList<Int>()
                arrIds.add(R.id.tvIsland)
                arrIds.add(R.id.expandable_layout_3)
                showToastError.postValue(arrIds)
                isValidate = false
            }
            isNullOrEmpty(village.value) -> {
                val arrIds = ArrayList<Int>()
                arrIds.add(R.id.tvVillage)
                arrIds.add(R.id.expandable_layout_3)
                showToastError.postValue(arrIds)
                isValidate = false
            }
            isNullOrEmpty(quartine.value) -> {
                val arrIds = ArrayList<Int>()
                arrIds.add(R.id.tvQuartine)
                arrIds.add(R.id.expandable_layout_3)
                showToastError.postValue(arrIds)
                isValidate = false
            }

        }




        if (!isValidate)
            return false

        return isValidate
    }

    public fun showDoneFlightInfo(): Boolean {
        var isValidate = true
        when {
            isNullOrEmpty(airlineName.value) -> {

                isValidate = false
            }
            isNullOrEmpty(countryOfEmbaraktion.value) -> {

                isValidate = false
            }

            isNullOrEmpty(boardedAt.value) -> {

                isValidate = false
            }
            isNullOrEmpty(flightNumber.value) -> {

                isValidate = false
            }
            isNullOrEmpty(arrivalTime.value) -> {

                isValidate = false
            }
            isNullOrEmpty(portOfArrival.value) -> {

                isValidate = false
            }
            isNullOrEmpty(statusOnBoard.value) -> {

                isValidate = false
            }
        }

        if (!isValidate)
            return false

        return isValidate
    }

    public fun showContactInfo(): Boolean {
        var isValidate = true
        when {
            isNullOrEmpty(email.value) -> {
                isValidate = false
            }

            isNullOrEmpty(phone.value) -> {
                isValidate = false
            }
        }

        if (!isValidate)
            return false

        return isValidate
    }

    public fun showNoOfTravelerInfo(): Boolean {
        var isValidate = true
        when {
            isNullOrEmpty(numberofTravelers.value) -> {
                isValidate = false
            }


        }

        if (!isValidate)
            return false

        return isValidate
    }

    public fun showDestinationInfo(): Boolean {
        var isValidate = true
        when {


            isNullOrEmpty(PurposeOfVisit.value) || isNullOrEmpty(PurposeOfVisit_1.value) -> {

                isValidate = false
            }
            isNullOrEmpty(lengthStay.value) -> {

                isValidate = false
            }
            isNullOrEmpty(accomodationType.value) || isNullOrEmpty(accomodationType_1.value) -> {

                isValidate = false
            }
            isNullOrEmpty(address.value) -> {

                isValidate = false
            }
            isNullOrEmpty(island.value) -> {

                isValidate = false
            }
            isNullOrEmpty(village.value) -> {

                isValidate = false
            }
            isNullOrEmpty(quartine.value) -> {

                isValidate = false
            }


        }

        if (!isValidate)
            return false

        return isValidate
    }



}