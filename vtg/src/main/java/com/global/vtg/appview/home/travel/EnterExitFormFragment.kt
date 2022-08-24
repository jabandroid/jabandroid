package com.global.vtg.appview.home.travel

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.vtg.R
import com.vtg.databinding.FragmentEnterExitBinding
import kotlinx.android.synthetic.main.fragment_enter_exit.*
import kotlinx.android.synthetic.main.fragment_travel_informaton.ivBack
import kotlinx.android.synthetic.main.include_imegration_header.*

class EnterExitFormFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentEnterExitBinding
    private lateinit var adapter: EnterExitAdapter
    private var arg: Bundle = Bundle()
    override fun getLayoutId(): Int {
        return R.layout.fragment_enter_exit
    }

    override fun preDataBinding(arguments: Bundle?) {
        arg = Bundle()
        arg= arguments!!
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
        }
        title.text = getString(R.string.welcome_custom, arg.getString("name"))

        rvForms.layoutManager = LinearLayoutManager(context)
        adapter = EnterExitAdapter(getAppActivity(), object : EnterExitAdapter.onItemClick {
            override fun response(item: FormList, v: View, position: Int) {
                when (position) {
                    0 -> {
                        addFragmentInStack<Any>(AppFragmentState.F_ARIVAL_FRAGMENT, arg)
                    }
                    1 -> {
                        addFragmentInStack<Any>(AppFragmentState.F_TRAVEL_INFO_FRAGMENT, arg)
                    }
                    2 -> {
                        addFragmentInStack<Any>(AppFragmentState.F_TRAVEL_INFO_FRAGMENT)
                    }
                }

            }

        })
        val list = ArrayList<FormList>()
        list.add(FormList(getString(R.string.arrival_info), true))
        list.add(FormList(getString(R.string.travel_info), false))
//        list.add(FormList(getString(R.string.cust_info), false))
//        list.add(FormList(getString(R.string.consent), false))
//        list.add(FormList(getString(R.string.immigration_form), false))
        adapter.setList(list)
        rvForms.adapter = adapter
    }

    override fun pageVisible() {

    }
}