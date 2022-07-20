package com.global.vtg.appview.home.travel

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.vtg.R
import com.vtg.databinding.FragmentChildProfileBinding
import com.vtg.databinding.FragmentTravelInformatonBinding
import com.vtg.databinding.FragmentTravelProfileBinding
import kotlinx.android.synthetic.main.fragment_travel_informaton.*

class TravelerProfileFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentTravelProfileBinding
    override fun getLayoutId(): Int {
        return R.layout.fragment_travel_profile
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentTravelProfileBinding
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {

        btnNext.setOnClickListener{
            addFragmentInStack<Any>(AppFragmentState.F_TRVEL_FLIGHT_DETAILS)
        }

        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun pageVisible() {

    }
}