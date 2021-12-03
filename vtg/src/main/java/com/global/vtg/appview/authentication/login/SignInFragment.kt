package com.global.vtg.appview.authentication.login

import android.content.Intent
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
import com.global.vtg.base.fragment.addFragment
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.DialogUtils.showSnackBar
import com.global.vtg.utils.SharedPreferenceUtil
import com.vtg.R
import com.vtg.databinding.FragmentSigninBinding
import kotlinx.android.synthetic.main.fragment_registration.ccp
import kotlinx.android.synthetic.main.fragment_registration.etLoginPassword
import kotlinx.android.synthetic.main.fragment_signin.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SignInFragment : AppFragment() {
    private var visibility: Boolean = false
    private lateinit var mFragmentBinding: FragmentSigninBinding
    private val viewModel by viewModel<SignInViewModel>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_signin
    }

    override fun preDataBinding(arguments: Bundle?) {
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentSigninBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }


    override fun initializeComponent(view: View?) {
        viewModel.code.value = ccp.defaultCountryCode
        viewModel.region = ccp.defaultCountryNameCode
        ivPassword.setOnClickListener {

            var selction=etLoginPassword.selectionStart
            if (visibility) {
                visibility = false
                etLoginPassword.transformationMethod = PasswordTransformationMethod()
                val drawableCompat =
                    ContextCompat.getDrawable(getAppActivity(), R.mipmap.visibility_off)
                ivPassword.setImageDrawable(drawableCompat)
            } else {
                visibility = true
                etLoginPassword.transformationMethod = null

                val drawableCompat =
                    ContextCompat.getDrawable(getAppActivity(), R.mipmap.visibility_on)
                ivPassword.setImageDrawable(drawableCompat)
            }
            etLoginPassword.setSelection(selction)
        }

        etLoginPassword.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            var selction = etLoginPassword.selectionStart
            etLoginPassword.setSelection(selction)
        }
        val username =
            SharedPreferenceUtil.INSTANCE?.getData(PreferenceManager.KEY_REMEMBER_ME_USERNAME, "")
        val phone =
            SharedPreferenceUtil.INSTANCE?.getData(PreferenceManager.KEY_REMEMBER_ME_PHONE, "")
        val code =
            SharedPreferenceUtil.INSTANCE?.getData(PreferenceManager.KEY_REMEMBER_ME_CODE, "")
        val password =
            SharedPreferenceUtil.INSTANCE?.getData(PreferenceManager.KEY_REMEMBER_ME_PASSWORD, "")
        if (!phone.isNullOrEmpty()) {
            viewModel.phone.postValue(phone)
        }
        if (!username.isNullOrEmpty()) {
            viewModel.email.postValue(username)
        }
        if (!password.isNullOrEmpty()) {
            viewModel.password.postValue(password)
            viewModel.isRememberMeChecked.postValue(true)
        }
        if (!code.isNullOrEmpty()) {
            viewModel.code.value = ccp.selectedCountryCode
            viewModel.region = ccp.selectedCountryNameCode
            viewModel.region.let { ccp.setCountryForNameCode(it) }
        }
        ccp.setOnCountryChangeListener {
            viewModel.code.value = ccp.selectedCountryCode
            viewModel.region = ccp.selectedCountryNameCode
        }
        viewModel.isVendor.observe(this, {
            swVendor.visibility=View.VISIBLE

            if (it == true)
                swIsClinic.visibility=View.INVISIBLE
            else
                swIsClinic.visibility=View.VISIBLE

//            swIsClinic.visibility = if (it == true) {
//                View.VISIBLE
//            } else {
//                View.GONE
//            }
        })
        viewModel.isClinic.observe(this, {
            swIsClinic.visibility=View.VISIBLE
            if (it == true)
                swVendor.visibility=View.INVISIBLE
            else
                swVendor.visibility=View.VISIBLE

        })

        viewModel.userLiveData.observe(this, { resources ->
            resources?.let {
                when (it) {
                    is Resource.Success -> {
                        (activity as AuthenticationActivity).hideProgressBar()
                        Constants.USER = it.data

                        SharedPreferenceUtil.getInstance(getAppActivity())
                            ?.saveData(
                                PreferenceManager.KEY_LOGGED_IN_USER_TYPE,
                                if (viewModel.isVendor.value == true) "Vendor" else "user"

                            )
                        SharedPreferenceUtil.getInstance(getAppActivity())
                            ?.saveData(
                                PreferenceManager.KEY_LOGGED_IN_USER_TYPE,
                                if (viewModel.isClinic.value == true) "Clinic" else "user"

                            )

                        when {
                            it.data.step1Complete == false -> {
                                addFragmentInStack<Any>(AppFragmentState.F_REG_STEP1)
                            }
                            it.data.step2Complete == false -> {
                                if (it.data.role.equals("ROLE_VENDOR", true) || it.data.role.equals("ROLE_CLINIC", true))
                                    addFragmentInStack<Any>(AppFragmentState.F_VENDOR_STEP2)
                                else addFragmentInStack<Any>(AppFragmentState.F_REG_STEP2)
                            }
                            it.data.step3Complete == false -> {
                                addFragmentInStack<Any>(AppFragmentState.F_REG_STEP3)
                            }
                            else -> {
                                viewModel.loginLiveData.postValue(true)
                            }
                        }
                    }
                    is Resource.Error -> {
                        (activity as AuthenticationActivity).hideProgressBar()
                        it.error.message?.let { it1 -> showSnackBar(context, it1) }
                    }
                    is Resource.Loading -> {
                        (activity as AuthenticationActivity).showProgressBar()
                    }
                }
            }
        })

        viewModel.showDialog.observe(this, {
            if (it != "") {
                if (activity != null)
                    DialogUtils.dialog(activity, it)
            }
        })

        viewModel.showProgress.observe(this, {
            if (it) {
                (activity as AuthenticationActivity).showProgressBar()
            } else {
                (activity as AuthenticationActivity).hideProgressBar()
            }
        })

        viewModel.addFragment.observe(this, {
            addFragment<Any>(it.first, it.second)
        })

        viewModel.loginLiveData.observe(this, {
            if (viewModel.isRememberMeChecked.value == true) {
                // Store email and password in to the preferences for Remember Me
                SharedPreferenceUtil.getInstance(getAppActivity())
                    ?.saveData(
                        PreferenceManager.KEY_REMEMBER_ME,
                        true
                    )
                if (!viewModel.email.value.isNullOrEmpty()) {
                    SharedPreferenceUtil.getInstance(getAppActivity())
                        ?.saveData(
                            PreferenceManager.KEY_REMEMBER_ME_USERNAME,
                            viewModel.email.value.toString().trim()
                        )
                }
                if (!viewModel.phone.value.isNullOrEmpty()) {
                    SharedPreferenceUtil.getInstance(getAppActivity())
                        ?.saveData(
                            PreferenceManager.KEY_REMEMBER_ME_PHONE,
                            viewModel.phone.value.toString().trim()
                        )
                    SharedPreferenceUtil.getInstance(getAppActivity())
                        ?.saveData(
                            PreferenceManager.KEY_REMEMBER_ME_COUNTRY_CODE,
                            viewModel.code.value.toString().trim()
                        )
                } else {
                    SharedPreferenceUtil.getInstance(getAppActivity())
                        ?.removeData(PreferenceManager.KEY_REMEMBER_ME_COUNTRY_CODE)
                    SharedPreferenceUtil.getInstance(getAppActivity())
                        ?.removeData(PreferenceManager.KEY_REMEMBER_ME_PHONE)
                    SharedPreferenceUtil.getInstance(getAppActivity())
                        ?.removeData(PreferenceManager.KEY_REMEMBER_ME_COUNTRY_CODE)
                }
                if (!viewModel.code.value.isNullOrEmpty()) {
                    SharedPreferenceUtil.getInstance(getAppActivity())
                        ?.saveData(
                            PreferenceManager.KEY_REMEMBER_ME_CODE,
                            viewModel.code.value.toString().trim()
                        )
                }
                SharedPreferenceUtil.getInstance(getAppActivity())
                    ?.saveData(
                        PreferenceManager.KEY_REMEMBER_ME_PASSWORD,
                        viewModel.password.value.toString().trim()
                    )
            } else {
                SharedPreferenceUtil.getInstance(getAppActivity())
                    ?.removeData(PreferenceManager.KEY_REMEMBER_ME_USERNAME)
                SharedPreferenceUtil.getInstance(getAppActivity())
                    ?.removeData(PreferenceManager.KEY_REMEMBER_ME_PHONE)
                SharedPreferenceUtil.getInstance(getAppActivity())
                    ?.removeData(PreferenceManager.KEY_REMEMBER_ME_CODE)
                SharedPreferenceUtil.getInstance(getAppActivity())
                    ?.removeData(PreferenceManager.KEY_REMEMBER_ME_PASSWORD)
            }
            if (!viewModel.email.value.isNullOrEmpty()) {
                SharedPreferenceUtil.getInstance(getAppActivity())
                    ?.saveData(
                        PreferenceManager.KEY_USER_NAME,
                        viewModel.email.value.toString().trim()
                    )
            }
            if (!viewModel.phone.value.isNullOrEmpty()) {
                SharedPreferenceUtil.getInstance(getAppActivity())
                    ?.saveData(
                        PreferenceManager.KEY_USER_NAME,
                        "${viewModel.code.value}${viewModel.phone.value.toString().trim()}"
                    )
                SharedPreferenceUtil.getInstance(getAppActivity())
                    ?.saveData(
                        PreferenceManager.KEY_REMEMBER_ME_COUNTRY_CODE,
                        ccp.selectedCountryNameCode
                    )
            }
            SharedPreferenceUtil.getInstance(getAppActivity())
                ?.saveData(
                    PreferenceManager.KEY_PASSWORD,
                    viewModel.password.value.toString().trim()
                )
            SharedPreferenceUtil.getInstance(getAppActivity())
                ?.saveData(
                    PreferenceManager.KEY_USER_LOGGED_IN,
                    true
                )
            val intent: Intent = if (Constants.USER?.role.equals("ROLE_USER")
                || Constants.USER?.role.equals("ROLE_CLINIC")) {
                Intent(activity, HomeActivity::class.java)
            } else {
                Intent(activity, VendorActivity::class.java)
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        })

        viewModel.redirectToSignUp.observe(this, {
            addFragmentInStack<Any>(AppFragmentState.F_SIGN_UP)
        })

        viewModel.redirectToForgotPassword.observe(this, {
            addFragmentInStack<Any>(AppFragmentState.F_FORGOT_PASSWORD)
        })

        // Handle Error
        viewModel.showToastError.observe(this, {
            showSnackBar(context, it)
        })
    }

    override fun pageVisible() {

    }


}
