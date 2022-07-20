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
import com.vtg.databinding.FragmentChildProfileBinding
import com.vtg.databinding.FragmentEnterExitBinding
import com.vtg.databinding.FragmentTravelInformatonBinding
import com.vtg.databinding.FragmentTravelProfileBinding
import kotlinx.android.synthetic.main.fragment_enter_exit.*
import kotlinx.android.synthetic.main.fragment_travel_informaton.*
import kotlinx.android.synthetic.main.fragment_travel_informaton.ivBack
import kotlinx.android.synthetic.main.fragment_travel_summary.*
import kotlinx.android.synthetic.main.fragment_travel_summary.rvFlightList

class EnterExitFormFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentEnterExitBinding
    private lateinit var adapter: EnterExitAdapter
    override fun getLayoutId(): Int {
        return R.layout.fragment_enter_exit
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentEnterExitBinding
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {

        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        rvForms.layoutManager = LinearLayoutManager(context)
        adapter = EnterExitAdapter(getAppActivity(), object : EnterExitAdapter.onItemClick {
            override fun response(item: FormList, v: View, position: Int) {
                when (position) {
                    0 -> {
                        addFragmentInStack<Any>(AppFragmentState.F_ARIVAL_FRAGMENT)
                    }
                    1->{
                        addFragmentInStack<Any>(AppFragmentState.F_TRAVEL_INFO_FRAGMENT)
                    }
                    2->{
                        addFragmentInStack<Any>(AppFragmentState.F_TRAVEL_INFO_FRAGMENT)
                    }
                }

            }

        })
        var list = ArrayList<FormList>()
        list.add(FormList(getString(R.string.arrival_info), true))
        list.add(FormList(getString(R.string.travel_info), false))
        list.add(FormList(getString(R.string.cust_info), false))
        list.add(FormList(getString(R.string.consent), false))
        list.add(FormList(getString(R.string.immigration_form), false))
        adapter.setList(list)
        rvForms.adapter = adapter
    }

    override fun pageVisible() {

    }
}