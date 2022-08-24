package com.global.vtg.appview.home.travel

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.KeyboardUtils
import com.global.vtg.utils.ToastUtils
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.vtg.R
import com.vtg.databinding.FragmentTravelInformatonBinding
import kotlinx.android.synthetic.main.fragment_travel_informaton.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.*


class TravelInformationFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentTravelInformatonBinding
    private var isSelectedDeparture: Boolean = false
    private var selectedString: String = ""
    private var codeDeparture: String = ""
    private var codeArrival: String = ""
    private var arrCode: ArrayList<TravelInfoItem> = ArrayList()
    private var count: Int = 0
    lateinit var airportAdapter: CitySearchAdapter
    lateinit var airportAdapterArrival: CitySearchAdapter
    private val viewModel by viewModel<TravelViewModel>()
    override fun getLayoutId(): Int {
        return R.layout.fragment_travel_informaton
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentTravelInformatonBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    fun isValidIndex(index: Int): Boolean {
        try {
            val i = arrCode[index]
        } catch (e: IndexOutOfBoundsException) {
            return false
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    override fun initializeComponent(view: View?) {
        airportAdapter =
            CitySearchAdapter(activity!!, R.layout.adapter_airport_code)

        airportAdapterArrival =
            CitySearchAdapter(activity!!, R.layout.adapter_airport_code)
        getJsonFile()

        ivBack.setOnClickListener {
            if (count == 0)
                activity?.onBackPressed()
            else {
                count--
                etFlightNumber.setText(arrCode[count].flightNumber)
                etDepartureCity.setText(arrCode[count].departureName)
                etArrivalCity.setText(arrCode[count].arrivalName)
            }
        }

        getView()!!.isFocusableInTouchMode = true
        getView()!!.requestFocus()
        getView()!!.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                if (count == 0)
                    activity?.onBackPressed()
                else {
                    count--
                    etFlightNumber.setText(arrCode[count].flightNumber)
                    etDepartureCity.setText(arrCode[count].departureName)
                    etArrivalCity.setText(arrCode[count].arrivalName)
                }
                true
            } else false
        }

        btnNext.setOnClickListener {

            if (isValid()) {
                if (isValidIndex(count)) {
                    val item = arrCode[count]
                    item.flightNumber = etFlightNumber.text.toString()
                    arrCode.removeAt(count)
                    arrCode.add(count, item)
                } else {
                    val item = TravelInfoItem()
                    item.flightNumber = etFlightNumber.text.toString()
                    arrCode.add(count, item)
                }

                val bundle = Bundle()
                bundle.putParcelableArrayList("data", arrCode)
                addFragmentInStack<Any>(AppFragmentState.F_TRaVEL_INFO_DETAILS, bundle)
            }
        }



        etDepartureCity.setAdapter(airportAdapter)
        etArrivalCity.setAdapter(airportAdapterArrival)

        etDepartureCity.setOnItemClickListener { _, _, position, _ ->
            val city = airportAdapter.getItem(position) as Finaldata?
            codeDeparture = city!!.code.toString()
            etDepartureCity.setText(city.name)
            //etDepartureCity.setSelection(etDepartureCity.text.length)

            if (isValidIndex(count)) {
                val item = arrCode[count]
                item.departureCode = codeDeparture
                item.departureCountryCode = city!!.country.toString()
                item.departureName = etDepartureCity.text.toString()
                arrCode.removeAt(count)
                arrCode.add(count, item)
            } else {
                val item = TravelInfoItem()
                item.departureCode = codeDeparture
                item.departureCountryCode = city!!.country.toString()
                item.departureName = etDepartureCity.text.toString()
                arrCode.add(count, item)
            }
            //isSelected=true
        }

        imgAdd.setOnClickListener {
            if (!TextUtils.isEmpty(etFlightNumber.text.toString())) {

                if (isValidIndex(count)) {
                    val item = arrCode[count]
                    item.flightNumber = etFlightNumber.text.toString()
                    arrCode.removeAt(count)
                    arrCode.add(count, item)
                } else {
                    val item = TravelInfoItem()
                    item.flightNumber = etFlightNumber.text.toString()
                    arrCode.add(count, item)
                }

                count++
                etFlightNumber.setText("")
                etDepartureCity.setText("")
                etArrivalCity.setText("")

                if (count == 5)
                    imgAdd.visibility = View.GONE
            }
        }


        etDepartureCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length!! > 0) {
                    airportAdapter.filter.filter(selectedString)
                    // if (!isSelected) {
//                    isSelectedDeparture=true
//                        selectedString=s.toString()
//                        viewModel.searchCode(s.toString())
//                    } else {
//                        isSelected = false
//                    }
                }
            }

        })

        etArrivalCity.setOnItemClickListener { _, _, position, _ ->
            val city = airportAdapterArrival.getItem(position) as Finaldata?
            codeArrival = city!!.code.toString()
            etArrivalCity.setText(city.name)
            // etArrivalCity.setSelection(etArrivalCity.text.length)
            KeyboardUtils.hideKeyboard(getAppActivity())

            if (isValidIndex(count)) {
                val item = arrCode[count]
                item.arrivalCode = codeArrival
                item.arrivalCountryCode = city!!.country.toString()
                item.arrivalName = etDepartureCity.text.toString()
                arrCode.removeAt(count)
                arrCode.add(count, item)
            } else {
                val item = TravelInfoItem()
                item.arrivalCode = codeArrival
                item.arrivalCountryCode = city!!.country.toString()
                item.arrivalName = etArrivalCity.text.toString()
                arrCode.add(count, item)
            }
        }


        etArrivalCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length!! > 0) {
                    airportAdapterArrival.filter.filter(selectedString)
                    // if (!isSelected) {
//                    isSelectedDeparture=false
//                        selectedString=s.toString()
//                        viewModel.searchCode(s.toString())
//                    } else {
//                        isSelected = false
//                    }
                }
            }

        })


        viewModel.codeLiveData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    it.data.data.let { it1 ->
//                        if(isSelectedDeparture) {
//                            airportAdapter.addAll(it1)
//                            airportAdapter.filter.filter(selectedString)
//                        }else{
//                            airportAdapterArrival.addAll(it1)
//                            airportAdapterArrival.filter.filter(selectedString)
//                        }
                    }
                }
                is Resource.Error -> {
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                }
            }
        }

    }

    override fun pageVisible() {

    }

    fun getJsonFile() {
        val `is`: InputStream = resources.openRawResource(com.vtg.R.raw.airports)
        val writer: Writer = StringWriter()
        val buffer = CharArray(1024)
        try {
            val reader: Reader = BufferedReader(InputStreamReader(`is`, "UTF-8"))
            var n: Int
            while (reader.read(buffer).also { n = it } != -1) {
                writer.write(buffer, 0, n)
            }
        } finally {
            `is`.close()
        }

        var data: ArrayList<Finaldata> = arrayListOf()
        val jsonString: String = writer.toString()
        val gson = Gson()
        val jsonParser = JsonParser()
        val jsonArray = jsonParser.parse(jsonString) as JsonArray
        for (i in 0 until jsonArray.size()) {
            val codes = gson.fromJson(
                jsonArray[i],
                Finaldata::class.java
            )
            data.add(codes)
        }

        airportAdapterArrival.addAll(data)
        airportAdapter.addAll(data)
    }

    private fun isValid(): Boolean {

        if (!TextUtils.isEmpty(etFlightNumber.text.toString())) {
            if (!TextUtils.isEmpty(etDepartureCity.text.toString())) {
                if (!TextUtils.isEmpty(etArrivalCity.text.toString())) {
                    return true
                } else {
                    ToastUtils.shortToast(0,getString(R.string.enter_arrival_airport))
                    return false
                }
            } else {
                ToastUtils.shortToast(0,getString(R.string.enter_departure_airport))
                return false
            }
        } else {
            ToastUtils.shortToast(0,getString(R.string.enter_flight_number))
            return false
        }

    }
}