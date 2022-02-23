package com.global.vtg.appview.home.event


import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.vtg.R
import com.vtg.databinding.FragmentCreateEventLocationBinding
import kotlinx.android.synthetic.main.fragment_create_event_location.*

import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateEventLocationFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentCreateEventLocationBinding
    private val viewModel by viewModel<CreateEventLocationViewModel>()
    override fun getLayoutId(): Int {
        return R.layout.fragment_create_event_location
    }
    override fun preDataBinding(arguments: Bundle?) {

    }
    fun updateAddress(city: String, state: String, country: String) {
        sCity.text = city
        sState.text = state
        sCountry.text = country
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentCreateEventLocationBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {

        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        sCity.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
        }
        sState.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
        }
        sCountry.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
        }
        viewModel.redirectToStep3.observe(this, {
            addFragmentInStack<Any>(AppFragmentState.F_EVENT_CREATE_REVIEW)
        })

        // Handle Error
        viewModel.showToastError.observe(this, {
            DialogUtils.showSnackBar(context, it)
        })

     if(CreateEventFragment.itemEvent.eventAddress!=null&& CreateEventFragment.itemEvent.eventAddress!!.isNotEmpty()){
         viewModel.address1.postValue(CreateEventFragment.itemEvent.eventAddress!![0].addr1)
         viewModel.address2.postValue(CreateEventFragment.itemEvent.eventAddress!![0].addr2)
         viewModel.city.postValue(CreateEventFragment.itemEvent.eventAddress!![0].city)
         viewModel.state.postValue(CreateEventFragment.itemEvent.eventAddress!![0].state)
         viewModel.country.postValue(CreateEventFragment.itemEvent.eventAddress!![0].country)
         viewModel.zip.postValue(CreateEventFragment.itemEvent.eventAddress!![0].zipCode)
         viewModel.contactNumber.postValue(CreateEventFragment.itemEvent.eventAddress!![0].phoneNo)
         viewModel.fax.postValue(CreateEventFragment.itemEvent.eventAddress!![0].fax)
     }else{
         if (!Constants.USER?.address.isNullOrEmpty()) {
             val index= Constants.USER?.address!!.size-1
             viewModel.address1.postValue(Constants.USER?.address?.get(index)?.addr1 ?: "")
             viewModel.address2.postValue(Constants.USER?.address?.get(index)?.addr2 ?: "")
             viewModel.city.postValue(Constants.USER?.address?.get(index)?.city ?: "")
             viewModel.state.postValue(Constants.USER?.address?.get(index)?.state ?: "")
             viewModel.country.postValue(Constants.USER?.address?.get(index)?.country ?: "")
             viewModel.zip.postValue(Constants.USER?.address?.get(index)?.zipCode ?: "")
             viewModel.contactNumber.postValue(Constants.USER?.mobileNo)


         }
     }
    }

    override fun pageVisible() {

    }

}