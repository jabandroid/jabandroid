package com.global.vtg.appview.authentication.otp

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragment
import com.global.vtg.base.fragment.replaceWithCurrentFragment
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.Constants.BUNDLE_IS_CLINIC
import com.global.vtg.utils.Constants.BUNDLE_IS_VENDOR
import com.global.vtg.utils.Constants.BUNDLE_REGISTRATION_CODE
import com.global.vtg.utils.Constants.BUNDLE_REGISTRATION_COUNTRY_CODE
import com.global.vtg.utils.Constants.BUNDLE_REGISTRATION_EMAIL
import com.global.vtg.utils.Constants.BUNDLE_REGISTRATION_PASSWORD
import com.global.vtg.utils.Constants.BUNDLE_REGISTRATION_PHONE
import com.global.vtg.utils.Constants.USER
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.SharedPreferenceUtil
import com.vtg.R
import com.vtg.databinding.FragmentOtpBinding
import kotlinx.android.synthetic.main.fragment_otp.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class OtpFragment : AppFragment() {
    private lateinit var editTexts: Array<EditText>
    private lateinit var mFragmentBinding: FragmentOtpBinding
    private val viewModel by viewModel<OtpViewModel>()
    var email: String? = null
    var phone: String? = null
    var password: String? = null
    var code: String? = null
    var countryCode: String? = null
    var isVendor: Boolean? = null
    var isClinic: Boolean? = null
    var isFromForgotPassword: Boolean? = false

    override fun getLayoutId(): Int {
        return R.layout.fragment_otp
    }

    override fun preDataBinding(arguments: Bundle?) {
        if (arguments != null) {
            if (arguments.containsKey(BUNDLE_REGISTRATION_EMAIL)) {
                email = arguments.getString(BUNDLE_REGISTRATION_EMAIL)
            }
            if (arguments.containsKey(BUNDLE_REGISTRATION_PHONE)) {
                phone = arguments.getString(BUNDLE_REGISTRATION_PHONE)
            }
            if (arguments.containsKey(BUNDLE_REGISTRATION_PASSWORD)) {
                password = arguments.getString(BUNDLE_REGISTRATION_PASSWORD)
            }
            if (arguments.containsKey(BUNDLE_REGISTRATION_CODE)) {
                code = arguments.getString(BUNDLE_REGISTRATION_CODE)
            }
            if (arguments.containsKey(BUNDLE_REGISTRATION_COUNTRY_CODE)) {
                countryCode = arguments.getString(BUNDLE_REGISTRATION_COUNTRY_CODE)
            }
            if (arguments.containsKey(BUNDLE_IS_VENDOR)) {
                isVendor = arguments.getBoolean(BUNDLE_IS_VENDOR)
            }
            if (arguments.containsKey(BUNDLE_IS_CLINIC)) {
                isClinic = arguments.getBoolean(BUNDLE_IS_CLINIC)
            }
            if (arguments.containsKey(Constants.BUNDLE_FROM_FORGOT_PASSWORD)) {
                isFromForgotPassword = arguments.getBoolean(Constants.BUNDLE_FROM_FORGOT_PASSWORD)
            }
        }
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentOtpBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        viewModel.context = activity
        code?.let {
            viewModel.code = it
        }
        phone?.let {
            viewModel.phone = it
        }
        if (isFromForgotPassword == false) {
            id.let {
                viewModel.id = it
            }
            email?.let {
                viewModel.email = it
            }
            password?.let {
                viewModel.password = it
            }
            (activity as AuthenticationActivity).showProgressBar()
        } else {
            when (activity) {
                is HomeActivity -> (activity as HomeActivity).showProgressBar()
                is VendorActivity -> (activity as VendorActivity).showProgressBar()
                is ClinicActivity -> (activity as HomeActivity).showProgressBar()
                else -> (activity as AuthenticationActivity).showProgressBar()
            }
        }
        viewModel.createUser()


        editTexts = arrayOf(edOtp1, edOtp2, edOtp3, edOtp4, edOtp5, edOtp6)

        edOtp1.addTextChangedListener(PinTextWatcher(0, editTexts, activity))
        edOtp2.addTextChangedListener(PinTextWatcher(1, editTexts, activity))
        edOtp3.addTextChangedListener(PinTextWatcher(2, editTexts, activity))
        edOtp4.addTextChangedListener(PinTextWatcher(3, editTexts, activity))
        edOtp5.addTextChangedListener(PinTextWatcher(4, editTexts, activity))
        edOtp6.addTextChangedListener(PinTextWatcher(5, editTexts, activity))

        edOtp1.setOnKeyListener(PinOnKeyListener(0, editTexts))
        edOtp2.setOnKeyListener(PinOnKeyListener(1, editTexts))
        edOtp3.setOnKeyListener(PinOnKeyListener(2, editTexts))
        edOtp4.setOnKeyListener(PinOnKeyListener(3, editTexts))
        edOtp5.setOnKeyListener(PinOnKeyListener(4, editTexts))
        edOtp6.setOnKeyListener(PinOnKeyListener(5, editTexts))

        tvResendOtp.setOnClickListener {
            when (activity) {
                is AuthenticationActivity -> (activity as AuthenticationActivity).showProgressBar()
                is HomeActivity -> (activity as HomeActivity).showProgressBar()
                else -> (activity as VendorActivity).showProgressBar()
            }
            viewModel.createUser()
        }

        viewModel.redirectToStep1.observe(this, {
            SharedPreferenceUtil.getInstance(getAppActivity())
                ?.saveData(
                    PreferenceManager.KEY_ROLE,
                    if (isVendor == true) "vendor"
                    else if (isClinic == true) "clinic" else "user"
                )

            SharedPreferenceUtil.getInstance(getAppActivity())
                ?.saveData(
                    PreferenceManager.KEY_LOGGED_IN_USER_TYPE,
                    if (isVendor == true) "vendor"
                    else if (isClinic == true) "clinic" else "user"

                )

            if (isFromForgotPassword == true) {
                val bundle = Bundle()
                bundle.putString(Constants.BUNDLE_TWILIO_USER_ID, viewModel.twilioUserId.toString())
                addFragment<Any>(
                    AppFragmentState.F_FORGOT_CHANGE_PASSWORD,
                    bundle,
                    popFragment = this
                )
            } else {
                email?.let { it1 ->
                    password?.let { it2 ->
                        phone?.let { it3 ->
                            code?.let { it4 ->
                                viewModel.callRegistration(
                                    it1,
                                    it2, it3, it4, viewModel.twilioUserId.toString()
                                )
                            }
                        }
                    }
                }
            }
        })

        viewModel.verifyOtp.observe(this, {
            if (viewModel.verifySuccess) {
                viewModel.redirectToStep1.postValue(true)
            } else {
                viewModel.verifyOTP()
            }
        })

        viewModel.registerLiveData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    USER = it.data
                    if (!it.data.mobileNo.isNullOrEmpty()) {
                        SharedPreferenceUtil.getInstance(getAppActivity())
                            ?.saveData(
                                PreferenceManager.KEY_USER_NAME,
                                it.data.mobileNo.toString().trim()
                            )
                        countryCode?.let { it1 ->
                            SharedPreferenceUtil.getInstance(getAppActivity())
                                ?.saveData(
                                    PreferenceManager.KEY_REMEMBER_ME_COUNTRY_CODE,
                                    it1
                                )
                        }
                    } else {
                        SharedPreferenceUtil.getInstance(getAppActivity())
                            ?.saveData(
                                PreferenceManager.KEY_USER_NAME,
                                it.data.email.toString().trim()
                            )
                    }
                    SharedPreferenceUtil.getInstance(getAppActivity())
                        ?.saveData(
                            PreferenceManager.KEY_PASSWORD,
                            it.data.password.toString()
                        )
                    val bundle = Bundle()
                    bundle.putString(
                        Constants.BUNDLE_TWILIO_USER_ID,
                        viewModel.twilioUserId.toString()
                    )
                    replaceWithCurrentFragment<Any>(AppFragmentState.F_REG_STEP1, bundle)
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

        viewModel.tokenSent.observe(this, {
            when (activity) {
                is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                is ClinicActivity -> (activity as HomeActivity).hideProgressBar()
                else -> (activity as VendorActivity).hideProgressBar()
            }
            DialogUtils.showSnackBar(getAppActivity(), resources.getString(R.string.label_otp_sent))
            btnVerify.isClickable = true
            btnVerify.isEnabled = true
        })

        viewModel.progressBar.observe(this, {
            if (it) {
                when (activity) {
                    is AuthenticationActivity -> (activity as AuthenticationActivity).showProgressBar()
                    is HomeActivity -> (activity as HomeActivity).showProgressBar()
                    is ClinicActivity -> (activity as HomeActivity).showProgressBar()
                    else -> (activity as VendorActivity).showProgressBar()
                }
            } else {
                when (activity) {
                    is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                    is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                    is ClinicActivity -> (activity as HomeActivity).showProgressBar()
                    else -> (activity as VendorActivity).hideProgressBar()
                }
            }
        })

        // Handle Error
        viewModel.showToastError.observe(this, {
            DialogUtils.showSnackBar(context, it)
        })


    }

    class PinTextWatcher(
        private val currentIndex: Int, private val editTexts: Array<EditText>,
        private val activity: FragmentActivity?
    ) : TextWatcher {
        private var isFirst = false
        private var isLast = false
        private var newTypedString = ""
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            newTypedString = s.subSequence(start, start + count).toString().trim { it <= ' ' }
        }

        override fun afterTextChanged(s: Editable) {
            var text = newTypedString

            /* Detect paste event and set first char */
            if (text.length > 1) text = text[0].toString()
            editTexts[currentIndex].removeTextChangedListener(this)
            editTexts[currentIndex].setText(text)
            editTexts[currentIndex].setSelection(text.length)
            editTexts[currentIndex].addTextChangedListener(this)
            if (text.length == 1) {
                editTexts[currentIndex].setBackgroundResource(R.drawable.otp_filled)
                moveToNext()
            } else if (text.isEmpty()) {
                editTexts[currentIndex].setBackgroundResource(R.drawable.otp_blank)
                moveToPrevious()
            }
        }

        private fun moveToNext() {
            if (!isLast) editTexts[currentIndex + 1].requestFocus()
            if (isAllEditTextsFilled && isLast) { // isLast is optional
                editTexts[currentIndex].clearFocus()
                hideKeyboard()
            }
        }

        private fun moveToPrevious() {
            if (!isFirst) editTexts[currentIndex - 1].requestFocus()
        }

        private val isAllEditTextsFilled: Boolean
            get() {
                for (editText in editTexts) if (editText.text.toString()
                        .trim { it <= ' ' }.isEmpty()
                ) return false
                return true
            }

        private fun hideKeyboard() {
            if (activity?.currentFocus != null) {
                val inputMethodManager: InputMethodManager? =
                    activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
                inputMethodManager?.hideSoftInputFromWindow(
                    activity.currentFocus!!.windowToken,
                    0
                )
            }
        }

        init {
            if (currentIndex == 0) isFirst =
                true else if (currentIndex == editTexts.size - 1) isLast = true
        }
    }

    class PinOnKeyListener(private val currentIndex: Int, private val editTexts: Array<EditText>) :
        View.OnKeyListener {
        override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action === KeyEvent.ACTION_DOWN) {
                if (editTexts[currentIndex].text.toString()
                        .isEmpty() && currentIndex != 0
                ) editTexts[currentIndex - 1].requestFocus()
            }
            return false
        }
    }

    override fun pageVisible() {
    }
}