package com.global.vtg.appview.home.travel

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.vtg.R
import com.vtg.databinding.FragmentTravelSummaryBinding
import kotlinx.android.synthetic.main.fragment_travel_summary.*

class TravelerSummaryFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentTravelSummaryBinding
    lateinit var adapter: FlightStatusAdapter
    override fun getLayoutId(): Int {
        return R.layout.fragment_travel_summary
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentTravelSummaryBinding
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {

        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        rvFlightList.layoutManager = LinearLayoutManager(context)
        adapter = FlightStatusAdapter(getAppActivity(), object : FlightStatusAdapter.onItemClick {
            override fun response(item: ResUser, v: View, position: Int) {


            }

        })
        rvFlightList.adapter = adapter

        btnNext.setOnClickListener{
            addFragmentInStack<Any>(AppFragmentState.F_ENTER_EXIT_FORM)
        }
    }

    override fun pageVisible() {

    }
}