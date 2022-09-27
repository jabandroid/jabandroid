package com.global.vtg.appview.home.travel

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppFragment
import com.vtg.R
import com.vtg.databinding.FragmentTravelerInfoBinding
import kotlinx.android.synthetic.main.fragment_arrival.desti_info
import kotlinx.android.synthetic.main.fragment_arrival.expand_1
import kotlinx.android.synthetic.main.fragment_arrival.expand_2
import kotlinx.android.synthetic.main.fragment_arrival.expand_3
import kotlinx.android.synthetic.main.fragment_arrival.expandable_layout
import kotlinx.android.synthetic.main.fragment_arrival.expandable_layout_2
import kotlinx.android.synthetic.main.fragment_arrival.expandable_layout_3
import kotlinx.android.synthetic.main.fragment_arrival.ivBack
import kotlinx.android.synthetic.main.fragment_traveler_info.*
import kotlinx.android.synthetic.main.include_imegration_header.*


class TravelerInformationFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentTravelerInfoBinding
    lateinit var adapter: FlightStatusAdapter
    private var arg: Bundle = Bundle()
    override fun getLayoutId(): Int {
        return R.layout.fragment_traveler_info
    }

    override fun preDataBinding(arguments: Bundle?) {
        arg = Bundle()
        arg= arguments!!
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentTravelerInfoBinding
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {

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

      personal_info.setOnClickListener{
            expandable_layout.toggle()
          expandable_layout_2.collapse()
          expandable_layout_3.collapse()
            if(expandable_layout.isExpanded)
                expand_1.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
            else
                expand_1.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
        }

        travel_info.setOnClickListener{
            expandable_layout_2.toggle()
            expandable_layout_3.collapse()
            expandable_layout.collapse()
            if(expandable_layout.isExpanded)
                expand_2.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
            else
                expand_2.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
        }



        desti_info.setOnClickListener{
            expandable_layout_3.toggle()

            expandable_layout_2.collapse()
            expandable_layout.collapse()
            if(expandable_layout_3.isExpanded)
                expand_3.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
            else
                expand_3.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
        }




//        btnNext.setOnClickListener{
//            addFragmentInStack<Any>(AppFragmentState.F_ENTER_EXIT_FORM)
//        }
    }

    override fun pageVisible() {

    }
}