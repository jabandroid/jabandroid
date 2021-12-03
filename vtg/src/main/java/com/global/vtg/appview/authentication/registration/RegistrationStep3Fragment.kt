package com.global.vtg.appview.authentication.registration

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.base.fragment.popFragment
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.Constants.USER
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.SharedPreferenceUtil
import com.global.vtg.utils.setDrawableRightTouch
import com.vtg.R
import com.vtg.databinding.FragmentRegStep3Binding
import kotlinx.android.synthetic.main.fragment_reg_step1.*
import kotlinx.android.synthetic.main.fragment_reg_step2.*
import kotlinx.android.synthetic.main.fragment_reg_step3.*
import kotlinx.android.synthetic.main.fragment_reg_step3.ivBack
import kotlinx.android.synthetic.main.fragment_reg_step3.tvTitle
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegistrationStep3Fragment : AppFragment() {
    private val viewModel by viewModel<RegistrationStep3ViewModel>()
    private lateinit var mFragmentBinding: FragmentRegStep3Binding
    var isFromProfile = false

    override fun getLayoutId(): Int {
        return R.layout.fragment_reg_step3
    }

    override fun preDataBinding(arguments: Bundle?) {
        if (arguments != null) {
            if (arguments.containsKey(Constants.BUNDLE_FROM_PROFILE)) {
                isFromProfile = arguments.getBoolean(Constants.BUNDLE_FROM_PROFILE)
            }
        }
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentRegStep3Binding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        var userType= SharedPreferenceUtil.getInstance(getAppActivity())
            ?.getData(
                PreferenceManager.KEY_LOGGED_IN_USER_TYPE,
                ""
            )

        if (Constants.USER!!.role.equals("ROLE_VENDOR", true)){
            tvTitle.text = "Vendor Step 3"
        }
        if (userType.equals("Clinic")) {
            tvTitle.text = "Lab/Clinic Step 3"
        } else if (userType.equals("Vendor")) {
            tvTitle.text = "Vendor Step 3"
        }
        if (!USER?.address.isNullOrEmpty()) {
            viewModel.firstName.postValue(USER?.address?.get(0)?.firstName)
            viewModel.lastName.postValue(USER?.address?.get(0)?.lastName)
            viewModel.address1.postValue(USER?.address?.get(0)?.addr1)
            viewModel.address2.postValue(USER?.address?.get(0)?.addr2)
            viewModel.city.postValue(USER?.address?.get(0)?.city)
            etMailingCity.setText(USER?.address?.get(0)?.city)
            viewModel.state.postValue(USER?.address?.get(0)?.state)
            etMailingState.setText(USER?.address?.get(0)?.state)
            viewModel.zip.postValue(USER?.address?.get(0)?.zipCode)
            viewModel.country.postValue(USER?.address?.get(0)?.country)
            etMailingCountry.setText(USER?.address?.get(0)?.country)
        } else {
            viewModel.firstName.postValue(USER?.firstName)
            viewModel.lastName.postValue(USER?.lastName)
        }
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        etMailingCity.setDrawableRightTouch {
            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
        }
        etMailingState.setDrawableRightTouch {
            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
        }
        etMailingCountry.setDrawableRightTouch {
            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
        }

        viewModel.redirectToSignIn.observe(this, {
            addFragmentInStack<Any>(AppFragmentState.F_SIGN_IN)
        })

        // Handle Error
        viewModel.showToastError.observe(this, {
            DialogUtils.showSnackBar(context, it)
        })

        viewModel.userConfigLiveData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    USER = it.data
                    SharedPreferenceUtil.getInstance(getAppActivity())
                        ?.saveData(
                            PreferenceManager.KEY_USER_LOGGED_IN,
                            true
                        )
                    val intent: Intent = if (USER?.role.equals("ROLE_USER")) {
                        Intent(activity, HomeActivity::class.java)
                    } else {
                        Intent(activity, VendorActivity::class.java)
                    }
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                is Resource.Error -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).showProgressBar()
                        is HomeActivity -> (activity as HomeActivity).showProgressBar()
                        else -> (activity as VendorActivity).showProgressBar()
                    }
                }
            }
        })

        viewModel.registerStep3LiveData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    USER = it.data

                    if (isFromProfile) {
                        popFragment(3)
                    } else {
                        it.data.email?.trim()?.let { it1 ->
                            SharedPreferenceUtil.getInstance(getAppActivity())
                                ?.saveData(
                                    PreferenceManager.KEY_USER_NAME,
                                    it1
                                )
                        }
                        it.data.password?.trim()?.let { it1 ->
                            SharedPreferenceUtil.getInstance(getAppActivity())
                                ?.saveData(
                                    PreferenceManager.KEY_PASSWORD,
                                    it1
                                )
                        }
                        SharedPreferenceUtil.getInstance(getAppActivity())
                            ?.saveData(
                                PreferenceManager.KEY_USER_LOGGED_IN,
                                true
                            )
                        viewModel.getUser()
                    }
                }
                is Resource.Error -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).showProgressBar()
                        is HomeActivity -> (activity as HomeActivity).showProgressBar()
                        else -> (activity as VendorActivity).showProgressBar()
                    }
                }
            }

        })
    }

    fun updateAddress(city: String, state: String, country: String) {
        etMailingCity.setText(city)
        etMailingState.setText(state)
        etMailingCountry.setText(country)

        viewModel.city.postValue(city)
        viewModel.state.postValue(state)
        viewModel.country.postValue(country)
    }

    override fun pageVisible() {

    }
}