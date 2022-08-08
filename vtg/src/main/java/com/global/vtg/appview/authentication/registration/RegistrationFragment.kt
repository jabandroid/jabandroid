package com.global.vtg.appview.authentication.registration

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.view.View.OnFocusChangeListener
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.base.fragment.replaceAllFragment
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.utils.Constants
import com.global.vtg.utils.Constants.BUNDLE_IS_CLINIC
import com.global.vtg.utils.Constants.BUNDLE_IS_VENDOR
import com.global.vtg.utils.Constants.BUNDLE_REGISTRATION_CODE
import com.global.vtg.utils.Constants.BUNDLE_REGISTRATION_COUNTRY_CODE
import com.global.vtg.utils.Constants.BUNDLE_REGISTRATION_EMAIL
import com.global.vtg.utils.Constants.BUNDLE_REGISTRATION_PASSWORD
import com.global.vtg.utils.Constants.BUNDLE_REGISTRATION_PHONE
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.SharedPreferenceUtil
import com.vtg.R
import com.vtg.databinding.FragmentRegistrationBinding
import kotlinx.android.synthetic.main.fragment_registration.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class RegistrationFragment : AppFragment() {
    private var visibility: Boolean = false
    private lateinit var mFragmentBinding: FragmentRegistrationBinding
    private val viewModel by viewModel<RegistrationViewModel>()
    private var spannableStringPolicies: SpannableString? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_registration
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentRegistrationBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        val clickableSpanTermsAndCondition = object : ClickableSpan() {
            override fun onClick(widget: View) {
                viewHtml(Constants.TERMS_CONDITION)
            }
        }
        val clickableSpanPrivacyPolicy = object : ClickableSpan() {
            override fun onClick(widget: View) {
                viewHtml(Constants.PRIVACY_POLICY)
            }
        }
        val termsLabel = resources.getString(R.string.label_accept_terms)
        val startPosTermsAndCondition = termsLabel.indexOf("Terms")
        val startPosPrivacyPolicy = termsLabel.indexOf("Privacy")

        spannableStringPolicies = SpannableString(resources.getString(R.string.label_accept_terms))


        spannableStringPolicies?.setSpan(UnderlineSpan(), startPosTermsAndCondition, startPosTermsAndCondition + 17, 0)
        spannableStringPolicies?.setSpan(UnderlineSpan(), startPosPrivacyPolicy, startPosPrivacyPolicy + 14, 0)

        spannableStringPolicies?.setSpan(clickableSpanTermsAndCondition, startPosTermsAndCondition, startPosTermsAndCondition + 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableStringPolicies?.setSpan(clickableSpanPrivacyPolicy, startPosPrivacyPolicy, startPosPrivacyPolicy + 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableStringPolicies?.setSpan(ForegroundColorSpan(resources.getColor(R.color.colorPrimary, null)), startPosTermsAndCondition, startPosTermsAndCondition + 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableStringPolicies?.setSpan(ForegroundColorSpan(resources.getColor(R.color.colorPrimary, null)), startPosPrivacyPolicy, startPosPrivacyPolicy + 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        textTerm.text = spannableStringPolicies
        textTerm.movementMethod = LinkMovementMethod.getInstance()

        ivPassword.setOnClickListener {
            var selction=etLoginPassword.selectionStart
            if (visibility) {
                visibility = false
                etLoginPassword.transformationMethod = PasswordTransformationMethod()
                val drawableCompat = ContextCompat.getDrawable(getAppActivity(), R.mipmap.visibility_off)
                ivPassword.setImageDrawable(drawableCompat)
            } else {
                visibility = true
                etLoginPassword.transformationMethod = null
                val drawableCompat = ContextCompat.getDrawable(getAppActivity(), R.mipmap.visibility_on)
                ivPassword.setImageDrawable(drawableCompat)
            }

            etLoginPassword.setSelection(selction)
        }

        etLoginPassword.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            var selction=etLoginPassword.selectionStart
            etLoginPassword.setSelection(selction)
        }
        viewModel.code.value = ccp.defaultCountryCode
        viewModel.region = ccp.defaultCountryNameCode
        tvRegistration.text=getString(R.string.user_Reg)
        viewModel.isVendor.observe(this, {
            swVendor.visibility=View.VISIBLE
            if (it == true) {
                tvRegistration.text=getString(R.string.vendor_reg)
                swClinic.visibility = View.INVISIBLE
            }
            else {
                 tvRegistration.text=getString(R.string.user_Reg)
                swClinic.visibility = View.VISIBLE
            }

//            swIsClinic.visibility = if (it == true) {
//                View.VISIBLE
//            } else {
//                View.GONE
//            }
        })
        viewModel.isClinic.observe(this, {
            swClinic.visibility=View.VISIBLE
            if (it == true) {
                tvRegistration.text=getString(R.string.clinic_reg)
                swVendor.visibility = View.INVISIBLE
            }
            else {
                tvRegistration.text=getString(R.string.user_Reg)
                swVendor.visibility = View.VISIBLE
            }

        })

//        viewModel.isVendor.observe(this, {
//            swClinic.visibility = if (it == true) {
//                View.VISIBLE
//            } else {
//                View.GONE
//            }
//        })


        ccp.setOnCountryChangeListener {
            viewModel.code.value = ccp.selectedCountryCode
            viewModel.region = ccp.selectedCountryNameCode
        }

        viewModel.redirectToSignIn.observe(this, {
            replaceAllFragment<Any>(AppFragmentState.F_SIGN_IN)
        })

        viewModel.redirectToOtp.observe(this, {
            val bundle = Bundle()

            SharedPreferenceUtil.getInstance(getAppActivity())
                ?.saveData(
                    PreferenceManager.KEY_LOGGED_IN_USER_TYPE,
                    if (viewModel.isVendor.value == true) "Vendor"
                    else if (viewModel.isClinic.value == true) "Clinic" else "User"

                )
//            it.data.id?.let { it1 -> bundle.putInt(BUNDLE_REGISTRATION_ID, it1) }
            bundle.putString(BUNDLE_REGISTRATION_PHONE, viewModel.phone.value)
            bundle.putString(BUNDLE_REGISTRATION_CODE, viewModel.code.value)
            bundle.putString(BUNDLE_REGISTRATION_COUNTRY_CODE, ccp.selectedCountryNameCode)
            bundle.putString(BUNDLE_REGISTRATION_EMAIL, viewModel.email.value)
            bundle.putString(BUNDLE_REGISTRATION_PASSWORD, viewModel.password.value)
            bundle.putBoolean(BUNDLE_IS_VENDOR, viewModel.isVendor.value == true)
            bundle.putBoolean(BUNDLE_IS_CLINIC, viewModel.isClinic.value == true)
            addFragmentInStack<Any>(AppFragmentState.F_OTP, keys = bundle)
            //addFragmentInStack<Any>(AppFragmentState.F_REG_STEP1, keys = bundle)
        })

        // Handle Error
        viewModel.showToastError.observe(this, {
            DialogUtils.showSnackBar(context, it)
        })
    }

    override fun pageVisible() {

    }
}