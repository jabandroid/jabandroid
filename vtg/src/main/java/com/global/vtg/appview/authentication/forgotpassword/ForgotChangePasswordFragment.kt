package com.global.vtg.appview.authentication.forgotpassword

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.replaceAllFragment
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants.BUNDLE_TWILIO_USER_ID
import com.global.vtg.utils.DialogUtils
import com.vtg.R
import com.vtg.databinding.FragmentForgotUpdatePasswordBinding
import kotlinx.android.synthetic.main.fragment_forgot_update_password.*
import kotlinx.android.synthetic.main.fragment_reg_step3.ivBack
import kotlinx.android.synthetic.main.fragment_registration.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgotChangePasswordFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentForgotUpdatePasswordBinding
    var twilioUserId = ""
    private var visibility: Boolean = false

    private val viewModel by viewModel<ForgotChangePasswordViewModel>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_forgot_update_password
    }

    override fun preDataBinding(arguments: Bundle?) {
        if (arguments != null) {
            if (arguments.containsKey(BUNDLE_TWILIO_USER_ID)) {
                twilioUserId = arguments.getString(BUNDLE_TWILIO_USER_ID, "")
            }
        }

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentForgotUpdatePasswordBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        ivNewPassword.setOnClickListener {
            var selction=etNewPassword.selectionStart
            if (visibility) {
                visibility = false
                etNewPassword.transformationMethod = PasswordTransformationMethod()
                val drawableCompat =
                    ContextCompat.getDrawable(getAppActivity(), R.mipmap.visibility_off)
                ivNewPassword.setImageDrawable(drawableCompat)
            } else {
                visibility = true
                etNewPassword.transformationMethod = null
                val drawableCompat =
                    ContextCompat.getDrawable(getAppActivity(), R.mipmap.visibility_on)
                ivNewPassword.setImageDrawable(drawableCompat)
            }
            etNewPassword.setSelection(selction)
        }
        ivConfirmPassword.setOnClickListener {
            var selction=etConfirmNewPassword.selectionStart
            if (visibility) {
                visibility = false
                etConfirmNewPassword.transformationMethod = PasswordTransformationMethod()
                val drawableCompat =
                    ContextCompat.getDrawable(getAppActivity(), R.mipmap.visibility_off)
                ivConfirmPassword.setImageDrawable(drawableCompat)
            } else {
                visibility = true
                etConfirmNewPassword.transformationMethod = null
                val drawableCompat =
                    ContextCompat.getDrawable(getAppActivity(), R.mipmap.visibility_on)
                ivConfirmPassword.setImageDrawable(drawableCompat)
            }
            etConfirmNewPassword.setSelection(selction)
        }
        twilioUserId.let {
            viewModel.twilioUserId = it
        }
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        // Handle Error
        viewModel.showToastError.observe(this, {
            DialogUtils.showSnackBar(context, it)
        })

        viewModel.redirectToSignIn.observe(this, {
            replaceAllFragment<Any>(AppFragmentState.F_SIGN_IN)
        })

        viewModel.userLiveData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is HomeActivity -> {
                            (activity as HomeActivity).hideProgressBar()
                        }
                        is VendorActivity -> {
                            (activity as VendorActivity).hideProgressBar()
                        }
                        else -> {
                            (activity as AuthenticationActivity).hideProgressBar()
                        }
                    }
                    if (it.data.status.equals("Success", true)) {
                        DialogUtils.showSnackBar(
                            context,
                            resources.getString(R.string.label_password_update)
                        )
                        replaceAllFragment<Any>(AppFragmentState.F_SIGN_IN)
                    } else {
                        DialogUtils.showSnackBar(
                            context,
                            resources.getString(R.string.error_message_somethingwrong)
                        )
                    }
                }
                is Resource.Error -> {
                    when (activity) {
                        is HomeActivity -> {
                            (activity as HomeActivity).hideProgressBar()
                        }
                        is VendorActivity -> {
                            (activity as VendorActivity).hideProgressBar()
                        }
                        else -> {
                            (activity as AuthenticationActivity).hideProgressBar()
                        }
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    if (activity is HomeActivity) {
                        (activity as HomeActivity).showProgressBar()
                    } else if (activity is VendorActivity) {
                        (activity as VendorActivity).showProgressBar()
                    } else {
                        (activity as AuthenticationActivity).showProgressBar()
                    }
                }
            }
        })
    }

    override fun pageVisible() {

    }
}