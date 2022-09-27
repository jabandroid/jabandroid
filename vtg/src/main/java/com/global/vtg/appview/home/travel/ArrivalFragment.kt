package com.global.vtg.appview.home.travel

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppFragment
import com.global.vtg.utils.AppAlertDialog
import com.global.vtg.utils.Constants
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.vtg.R
import com.vtg.databinding.FragmentArrivalBinding
import kotlinx.android.synthetic.main.fragment_arrival.*
import kotlinx.android.synthetic.main.fragment_arrival.btnNext
import kotlinx.android.synthetic.main.fragment_arrival.ivBack
import kotlinx.android.synthetic.main.include_imegration_header.*
import net.cachapa.expandablelayout.ExpandableLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.*


class ArrivalFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentArrivalBinding
    private val viewModel by viewModel<TravelFormViewModel>()
    lateinit var adapter: FlightStatusAdapter
    lateinit var portAdapter: CitySearchAdapter
    lateinit var boardedAdapter: CitySearchAdapter
    lateinit var rootView :View
    private var arg: Bundle = Bundle()

    override fun getLayoutId(): Int {
        return R.layout.fragment_arrival
    }

    override fun preDataBinding(arguments: Bundle?) {
        arg = Bundle()
        arg= arguments!!
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentArrivalBinding
        mFragmentBinding.viewModel=viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
      rootView= view!!
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        when (arg.getString("country")) {
            "ATG" -> {
                logo1.setImageResource(R.drawable.ic_atiqua_enblem)

            }
            "BRB" -> {
                logo1.setImageResource(R.drawable.ic_barbodosa_emblem)
            }
            "KNA" -> {
                logo1.setImageResource(R.drawable.ic_st_kitt_small)
            }
            "GUY" -> {
                logo1.setImageResource(R.drawable.ic_gayana_amblem)
            }
            "PHL" -> {
                logo1.setImageResource(R.drawable.ic_philli_amblem)
            }
            "NAM" -> {
                logo1.setImageResource(R.drawable.ic_nam_amblem)
            }
        }
        title.text = getString(R.string.welcome_custom, arg.getString("name"))

        tvTitle.text=getString(R.string.arrival_info)

        portAdapter =
            CitySearchAdapter(activity!!, R.layout.adapter_airport_code)

        boardedAdapter =
            CitySearchAdapter(activity!!, R.layout.adapter_airport_code)
        getJsonFile()

        tvPortOfArrival.setAdapter(portAdapter)
        tvBoardedAt.setAdapter(boardedAdapter)
        tvBoardedAt.setOnItemClickListener { _, _, position, _ ->
            val city = boardedAdapter.getItem(position) as Finaldata?
            viewModel.boardedAt.postValue(city!!.code)
        }
        tvPortOfArrival.setOnItemClickListener { _, _, position, _ ->
            val city = portAdapter.getItem(position) as Finaldata?
            viewModel.portOfArrival.postValue(city!!.code)
        }

        expandable_layout.setOnExpansionUpdateListener { expansionFraction, state, ->
            if (state == ExpandableLayout.State.COLLAPSED) {
                if(viewModel.showDoneFlightInfo()){
                    done.visibility=View.VISIBLE
                }else{
                    done.visibility=View.GONE
                }
            }
        }
        expandable_layout_2.setOnExpansionUpdateListener { expansionFraction, state, ->
            if (state == ExpandableLayout.State.COLLAPSED) {
                if(viewModel.showContactInfo()){
                    done_2.visibility=View.VISIBLE
                }else{
                    done_2.visibility=View.GONE
                }
            }
        }
        expandable_layout_4.setOnExpansionUpdateListener { expansionFraction, state, ->
            if (state == ExpandableLayout.State.COLLAPSED) {
                if(viewModel.showNoOfTravelerInfo()){
                    done_4.visibility=View.VISIBLE
                }else{
                    done_4.visibility=View.GONE
                }
            }
        }

        expandable_layout_3.setOnExpansionUpdateListener { expansionFraction, state, ->
            if (state == ExpandableLayout.State.COLLAPSED) {
                if(viewModel.showDestinationInfo()){
                    done_3.visibility=View.VISIBLE
                }else{
                    done_3.visibility=View.GONE
                }
            }
        }

        flight_info.setOnClickListener{

               expandable_layout.toggle()
                expandable_layout_2.collapse()
                expandable_layout_3.collapse()
                expandable_layout_4.collapse()
                if (expandable_layout.isExpanded)
                    expand_1.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
                else {

                    expand_1.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
                }

        }

        contact_info.setOnClickListener{
            expandable_layout_2.toggle()
            expandable_layout.collapse()

            expandable_layout_3.collapse()
            expandable_layout_4.collapse()
            if(expandable_layout.isExpanded)
                expand_2.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
            else
                expand_2.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
        }



        desti_info.setOnClickListener{
            expandable_layout_3.toggle()
            expandable_layout_2.collapse()
            expandable_layout.collapse()
            expandable_layout_4.collapse()
            if(expandable_layout_3.isExpanded)
                expand_3.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
            else
                expand_3.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
        }

        number_of_traveler.setOnClickListener{

            btnNext.visibility=View.VISIBLE
            expandable_layout_4.toggle()
            expandable_layout_3.collapse()
            expandable_layout_2.collapse()
            expandable_layout.collapse()

            if(expandable_layout_4.isExpanded)
                expand_4.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
            else
                expand_4.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)

        }
        val reasons: Array<out String> = activity!!.resources.getStringArray(R.array.reason)
        val accomodation: Array<out String> = activity!!.resources.getStringArray(R.array.accomodation)
        tvPurposeOfVisit.setOnClickListener{
            AppAlertDialog().showVisitReason(
                activity!! as AppCompatActivity,
                object : AppAlertDialog.GetClick {
                    override fun response(type: String) {
                        tvPurposeOfVisit.text=type

                        if(type == reasons[reasons.size-1]){
                            tvPurposeOfVisit_1.visibility=View.VISIBLE
                        }

                    }
                }

            )
        }


        tvAccomodationType.setOnClickListener{
            AppAlertDialog().showVisitAccomodation(
                activity!! as AppCompatActivity,
                object : AppAlertDialog.GetClick {
                    override fun response(type: String) {
                        tvAccomodationType.text=type
                        if(type == accomodation[accomodation.size-1]){
                            tvAccomodationType_1.visibility=View.VISIBLE
                        }
                    }
                }

            )
        }

        viewModel.showToastError.observe(viewLifecycleOwner) {
            expandable_layout_2.collapse()
            expandable_layout_2.collapse()
            expandable_layout_3.collapse()
            expandable_layout_4.collapse()
           when(it[0]){
               R.id.tvPurposeOfVisit->{
                   val v= rootView.findViewById<TextView>(it[0])
                   val vExpand= rootView.findViewById<ExpandableLayout>(it[1])
                   v.error = getString(R.string.enter_details_error)
                   v.isFocusable=true
                   v.requestFocusFromTouch()
                   vExpand.expand()
               }
               R.id.tvAccomodationType->{
                   val v= rootView.findViewById<TextView>(it[0])
                   val vExpand= rootView.findViewById<ExpandableLayout>(it[1])
                   v.error = getString(R.string.enter_details_error)
                   v.isFocusable=true
                   v.requestFocusFromTouch()
                   vExpand.expand()
               }
               else->{
                   val v= rootView.findViewById<EditText>(it[0])
                   val vExpand= rootView.findViewById<ExpandableLayout>(it[1])
                   v.error = getString(R.string.enter_details_error)
                   v.isFocusable=true
                   v.requestFocusFromTouch()
                   vExpand.expand()
               }
           }






//            when(it){
//                R.id.tvAirlineName-
//            }
        }


//        btnNext.setOnClickListener{
//            addFragmentInStack<Any>(AppFragmentState.F_ENTER_EXIT_FORM)
//        }
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

        portAdapter.addAll(data)
        boardedAdapter.addAll(data)
    }

}