package com.global.vtg.appview.authentication.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.base.fragment.replaceAllFragment
import com.global.vtg.utils.Constants
import com.global.vtg.utils.Constants.BUNDLE_FROM_FORGOT_PASSWORD
import com.global.vtg.utils.DialogUtils
import com.vtg.R
import com.vtg.databinding.FragmentForgotPasswordBinding
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.android.synthetic.main.fragment_registration.ccp
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgotPasswordFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentForgotPasswordBinding

    private val viewModel by viewModel<ForgotPasswordViewModel>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_forgot_password
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentForgotPasswordBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        viewModel.code.value = ccp.defaultCountryCode
        viewModel.region = ccp.defaultCountryNameCode
        ccp.setOnCountryChangeListener {
            viewModel.code.value = ccp.selectedCountryCode
            viewModel.region = ccp.selectedCountryNameCode
        }
        ivBack.setOnClickListener { activity?.onBackPressed() }

        // Handle Error
        viewModel.showToastError.observe(this, {
            DialogUtils.showSnackBar(context, it)
        })

        viewModel.redirectToSignIn.observe(this, {
            val bundle = Bundle()
            bundle.putBoolean(BUNDLE_FROM_FORGOT_PASSWORD, true)
            bundle.putString(Constants.BUNDLE_REGISTRATION_PHONE, viewModel.phone.value)
            bundle.putString(Constants.BUNDLE_REGISTRATION_CODE, viewModel.code.value)
            addFragmentInStack<Any>(AppFragmentState.F_OTP, bundle)
        })
    }

    override fun pageVisible() {

    }
}