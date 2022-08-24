package com.global.vtg.appview.home.travel

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import com.global.vtg.appview.home.dashboard.DashboardAdapter
import com.global.vtg.appview.home.event.Event
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack

import com.vtg.R
import com.vtg.databinding.FragmentEnterExitFormBinding
import kotlinx.android.synthetic.main.fragment_enter_exit_form.*

import org.koin.androidx.viewmodel.ext.android.viewModel

class TravelFormEntryCountryFragment : AppFragment(), TravelFormAdapter.ClickListener {
    private lateinit var mFragmentBinding: FragmentEnterExitFormBinding
    private val viewModel by viewModel<TravelFormViewModel>()
    val titles: ArrayList<FormMain> = ArrayList()

    override fun getLayoutId(): Int {
        return R.layout.fragment_enter_exit_form
    }


    companion object {

        lateinit var itemForm: TravelForm
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentEnterExitFormBinding
        mFragmentBinding.viewModel=viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {

       ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        titles.add(FormMain("Antigua and Barbuda",R.drawable.ic_antiqua,"ATG"))
        titles.add(FormMain("Barbados",R.drawable.ic_barbo,"BRB"))
        titles.add(FormMain("Saint Kitts and Nevis",R.drawable.flag_saint_kitts_and_nevis,"KNA"))
        main_controller.layoutManager = GridLayoutManager(activity, 2)
        val dashboardAdapter = TravelFormAdapter(
            activity!!, titles
        )
        dashboardAdapter.setListener(this)
        main_controller.adapter = dashboardAdapter
    }

    override fun pageVisible() {

    }

    override fun onItemClick(position: Int) {
        itemForm=TravelForm()
        val b =Bundle()
        b.putString("country",titles[position].isoCode)
        b.putString("name",titles[position].data)
        b.putInt("image",titles[position].image)
        addFragmentInStack<Any>(AppFragmentState.F_ENTER_EXIT_FORM,b)
    }
}
