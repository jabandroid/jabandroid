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


class TravelerInformationFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentTravelerInfoBinding
    lateinit var adapter: FlightStatusAdapter
    override fun getLayoutId(): Int {
        return R.layout.fragment_traveler_info
    }

    override fun preDataBinding(arguments: Bundle?) {

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