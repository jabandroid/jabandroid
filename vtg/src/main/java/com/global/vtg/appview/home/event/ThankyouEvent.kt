package com.global.vtg.appview.home.event


import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.base.fragment.popFragment
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.vtg.R
import com.vtg.databinding.FragmentCreateEventLocationBinding
import com.vtg.databinding.FragmentThankyouEventBinding
import kotlinx.android.synthetic.main.fragment_create_event_location.*
import kotlinx.android.synthetic.main.fragment_thankyou_event.*

import org.koin.androidx.viewmodel.ext.android.viewModel

class ThankyouEvent : AppFragment() {
    private lateinit var mFragmentBinding: FragmentThankyouEventBinding

    override fun getLayoutId(): Int {
        return R.layout.fragment_thankyou_event
    }

    override fun preDataBinding(arguments: Bundle?) {

    }


    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentThankyouEventBinding


        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        CreateSubEventFragment.itemSubEvent=Event()
        tvUploaded_add.setOnClickListener {
            addFragmentInStack<Any>(
                AppFragmentState.F_SUB_EVENT_CREATE
            )
        }
        event_page.setOnClickListener {
            popFragment(1)
        }

    }

    override fun pageVisible() {

    }

}