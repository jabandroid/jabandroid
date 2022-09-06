package com.global.vtg.appview.authentication.registration

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.config.Institute
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.appview.home.parentchild.ChildListFragment
import com.global.vtg.appview.home.profile.ProfileFragment
import com.global.vtg.appview.home.uploaddocument.UploadDocumentFragment
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.base.fragment.popFragment
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.Constants.USER
import com.global.vtg.utils.Constants.USERCHILD
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.SharedPreferenceUtil
import com.vtg.R
import com.vtg.databinding.FragmentRegStep3Binding
import kotlinx.android.synthetic.main.fragment_reg_step3.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddClinicFragment : AppFragment() {
    private val viewModel by viewModel<RegistrationStep3ViewModel>()
    private lateinit var mFragmentBinding: FragmentRegStep3Binding
    var isFromProfile = false
    var childAccount = false

    override fun getLayoutId(): Int {
        return R.layout.fragment_reg_step3
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentRegStep3Binding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {


            tvTitle.text = getString(R.string.add_clinic)
        tvFirstName.text = getString(R.string.clinic_name)

viewModel.isClinicSetUp=true

        etEmail.visibility=View.VISIBLE
        tvEmail.visibility=View.VISIBLE
        tvLastName.visibility=View.GONE
        etLastName3.visibility=View.GONE
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        etMailingCity.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
        }
        etMailingState.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
        }
        etMailingCountry.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
        }

        viewModel.redirectToSignIn.observe(this) {
            addFragmentInStack<Any>(AppFragmentState.F_SIGN_IN)
        }

        // Handle Error
        viewModel.showToastError.observe(this) {
            DialogUtils.showSnackBar(context, it)
        }

        viewModel.registerStep1LiveData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }

                    SharedPreferenceUtil.getInstance(getAppActivity())
                        ?.saveData(
                            PreferenceManager.KEY_USER__ADD_CLINIC,
                            false
                        )
                    val fragments = getAppActivity().supportFragmentManager.fragments
                    for (frg in fragments) {
                        if (frg is UploadDocumentFragment) {

                            var i=Institute("","","","","","",
                                it.data.firstName, it.data.id,"","","","")
                            frg.setID(i)
                            break
                        }
                    }
                    popFragment(2)

                }
                is Resource.Error -> {
                    SharedPreferenceUtil.getInstance(getAppActivity())
                        ?.saveData(
                            PreferenceManager.KEY_USER__ADD_CLINIC,
                            false
                        )
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).showProgressBar()
                        is HomeActivity -> (activity as HomeActivity).showProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
                        else -> (activity as VendorActivity).showProgressBar()
                    }
                }
            }

        }
    }

    fun updateAddress(city: String, state: String, country: String) {
        etMailingCity.text = city
        etMailingState.text = state
        etMailingCountry.text = country

        viewModel.city.postValue(city)
        viewModel.state.postValue(state)
        viewModel.country.postValue(country)
    }

    override fun pageVisible() {

    }
}