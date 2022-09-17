package com.global.vtg.appview.home.changepassword

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.replaceAllFragment
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.SharedPreferenceUtil
import com.vtg.R
import com.vtg.databinding.FragmentChangePasswordBinding
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.fragment_forgot_update_password.*
import kotlinx.android.synthetic.main.fragment_forgot_update_password.etConfirmNewPassword
import kotlinx.android.synthetic.main.fragment_forgot_update_password.ivConfirmPassword
import kotlinx.android.synthetic.main.fragment_reg_step3.ivBack
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangePasswordFragment : AppFragment() {
    private var visibility: Boolean = false
    private var visibilityNew: Boolean = false
    private var visibilityConfirm: Boolean = false
    private lateinit var mFragmentBinding: FragmentChangePasswordBinding

    private val viewModel by viewModel<ChangePasswordViewModel>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_change_password
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentChangePasswordBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        ivOldPassword.setOnClickListener {
            var selction=etOldPassword.selectionStart
            if (visibility) {
                visibility = false
                etOldPassword.transformationMethod = PasswordTransformationMethod()
                val drawableCompat =
                    ContextCompat.getDrawable(getAppActivity(), R.mipmap.visibility_off)
                ivOldPassword.setImageDrawable(drawableCompat)
            } else {
                visibility = true
                etOldPassword.transformationMethod = null
                val drawableCompat =
                    ContextCompat.getDrawable(getAppActivity(), R.mipmap.visibility_on)
                ivOldPassword.setImageDrawable(drawableCompat)
            }
            etOldPassword.setSelection(selction)
        }
        ivChangeNewPassword.setOnClickListener {
            var selction=etChangeNewPassword.selectionStart
            if (visibilityNew) {
                visibilityNew = false
                etChangeNewPassword.transformationMethod = PasswordTransformationMethod()
                val drawableCompat =
                    ContextCompat.getDrawable(getAppActivity(), R.mipmap.visibility_off)
                ivChangeNewPassword.setImageDrawable(drawableCompat)
            } else {
                visibilityNew = true
                etChangeNewPassword.transformationMethod = null
                val drawableCompat =
                    ContextCompat.getDrawable(getAppActivity(), R.mipmap.visibility_on)
                ivChangeNewPassword.setImageDrawable(drawableCompat)
            }
            etChangeNewPassword.setSelection(selction)
        }
        ivConfirmPassword.setOnClickListener {
            var selction=etConfirmNewPassword.selectionStart
            if (visibilityConfirm) {
                visibilityConfirm = false
                etConfirmNewPassword.transformationMethod = PasswordTransformationMethod()
                val drawableCompat =
                    ContextCompat.getDrawable(getAppActivity(), R.mipmap.visibility_off)
                ivConfirmPassword.setImageDrawable(drawableCompat)
            } else {
                visibilityConfirm = true
                etConfirmNewPassword.transformationMethod = null
                val drawableCompat =
                    ContextCompat.getDrawable(getAppActivity(), R.mipmap.visibility_on)
                ivConfirmPassword.setImageDrawable(drawableCompat)
            }
            etConfirmNewPassword.setSelection(selction)
        }
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        // Handle Error
        viewModel.showToastError.observe(this) {
            DialogUtils.showSnackBar(context, it)
        }

        viewModel.redirectToSignIn.observe(this) {
            replaceAllFragment<Any>(AppFragmentState.F_SIGN_IN)
        }

        viewModel.userLiveData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (activity is HomeActivity) {
                        (activity as HomeActivity).hideProgressBar()
                    } else {
                        (activity as VendorActivity).hideProgressBar()
                    }
                    Constants.USER = it.data
                    it.data.password?.let { it1 ->
                        SharedPreferenceUtil.getInstance(getAppActivity())
                            ?.saveData(
                                PreferenceManager.KEY_REMEMBER_ME_PASSWORD,
                                it1
                            )
                        SharedPreferenceUtil.getInstance(getAppActivity())
                            ?.saveData(
                                PreferenceManager.KEY_PASSWORD,
                                it1
                            )
                    }
                    DialogUtils.showSnackBar(
                        context,
                        resources.getString(R.string.label_password_update)
                    )
                    activity?.onBackPressed()
                }
                is Resource.Error -> {
                    if (activity is HomeActivity) {
                        (activity as HomeActivity).hideProgressBar()
                    } else {
                        (activity as VendorActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    if (activity is HomeActivity) {
                        (activity as HomeActivity).showProgressBar()
                    } else {
                        (activity as VendorActivity).showProgressBar()
                    }
                }
            }
        }
    }

    override fun pageVisible() {

    }
}