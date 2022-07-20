package com.global.vtg.appview.home.travel

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.appview.home.parentchild.ChildListAdapter
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.vtg.R
import com.vtg.databinding.*
import kotlinx.android.synthetic.main.fragment_arrival.*


class ArrivalFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentArrivalBinding
    lateinit var adapter: FlightStatusAdapter
    override fun getLayoutId(): Int {
        return R.layout.fragment_arrival
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentArrivalBinding
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {

        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        flight_info.setOnClickListener{
            expandable_layout.toggle()
            expandable_layout_2.collapse()
            expandable_layout_3.collapse()
            expandable_layout_4.collapse()
            if(expandable_layout.isExpanded)
                expand_1.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
            else
                expand_1.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
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


//        btnNext.setOnClickListener{
//            addFragmentInStack<Any>(AppFragmentState.F_ENTER_EXIT_FORM)
//        }
    }

    override fun pageVisible() {

    }
}