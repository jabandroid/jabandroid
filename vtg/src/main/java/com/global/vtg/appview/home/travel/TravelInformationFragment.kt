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
import kotlinx.android.synthetic.main.fragment_travel_informaton.*

class TravelInformationFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentTravelInformatonBinding
    override fun getLayoutId(): Int {
        return R.layout.fragment_travel_informaton
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentTravelInformatonBinding
     //   mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        btnNext.setOnClickListener{
            addFragmentInStack<Any>(AppFragmentState.F_TRaVEL_PROFILE)
        }

    }

    override fun pageVisible() {

    }
}