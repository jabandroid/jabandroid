package com.global.vtg.appview.authentication.registration

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.*
import com.vtg.R
import com.vtg.databinding.FragmentRegStep2Binding
import kotlinx.android.synthetic.main.fragment_reg_step1.*
import kotlinx.android.synthetic.main.fragment_reg_step2.*
import kotlinx.android.synthetic.main.fragment_reg_step2.ivBack
import kotlinx.android.synthetic.main.fragment_reg_step2.tvTitle
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class RegistrationStep2Fragment : AppFragment() {
    private val viewModel by viewModel<RegistrationStep2ViewModel>()
    private lateinit var mFragmentBinding: FragmentRegStep2Binding
    var id: Int? = null
    private val myCalendar: Calendar = Calendar.getInstance()
    private val currentCalendar: Calendar = Calendar.getInstance()
    var isFromProfile = false

    override fun getLayoutId(): Int {
        return R.layout.fragment_reg_step2
    }

    override fun preDataBinding(arguments: Bundle?) {
        if (arguments != null) {
            if (arguments.containsKey(Constants.BUNDLE_REGISTRATION_ID)) {
                id = arguments.getInt(Constants.BUNDLE_REGISTRATION_ID)
            }

            if (arguments.containsKey(Constants.BUNDLE_FROM_PROFILE)) {
                isFromProfile = arguments.getBoolean(Constants.BUNDLE_FROM_PROFILE)
            }
        }
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentRegStep2Binding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        if(isFromProfile)
            tvSkip.visibility=View.GONE
        var userType= SharedPreferenceUtil.getInstance(getAppActivity())
            ?.getData(
                PreferenceManager.KEY_LOGGED_IN_USER_TYPE,
                ""
            )

        if (Constants.USER!!.role.equals("ROLE_VENDOR", true)){
            tvTitle.text = "Vendor Step2"
        }
        if (userType.equals("Clinic")) {
            tvTitle.text = "Lab/Clinic Step 2"
        } else if (userType.equals("Vendor")) {
            tvTitle.text = "Vendor Step 2"
        }


        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        val list = Constants.USER?.document
        if (list != null && list.isNotEmpty()) {
            for (doc in list) {
                when {
                    doc?.type.equals("DL", true) -> {
                        doc?.issueDate?.let { setDate(it, etDlnIssuedDate, ::updateIssuedDate) }
                        doc?.expireDate?.let { setDate(it, etDlnExpiryDate, ::updateExpiryDate) }
                        viewModel.dln.value = doc?.identity
                        viewModel.dlnState.value= doc?.state
                        viewModel.dlnCountry.value= doc?.country
                    }
                    doc?.type.equals("Passport", true) -> {
                        doc?.issueDate?.let {
                            setDate(
                                it,
                                etPassportIssuedDate,
                                ::updatePassportIssuedDate
                            )
                        }
                        doc?.expireDate?.let {
                            setDate(
                                it,
                                etPassportExpiryDate,
                                ::updatePassportExpiryDate
                            )
                        }
                        viewModel.passportNumber.value = doc?.identity
                        viewModel.passportState.value= doc?.state
                        viewModel.passportCountry.value= doc?.country
                    }
                    doc?.type.equals("SSN", true) -> {
                        viewModel.ssn.value = doc?.identity
                        etSsn.isClickable = false
                        etSsn.isEnabled = false
                    }
                    doc?.type.equals("ID", true) -> {
                        viewModel.id.value = doc?.identity
                        etId.isClickable = false
                        etId.isEnabled = false
                    }
                }
            }
        }

        // Handle Error
        viewModel.showToastError.observe(this, {
            DialogUtils.showSnackBar(context, it)
        })

        viewModel.registerLiveData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    Constants.USER = it.data
                    val bundle = Bundle()
                    bundle.putBoolean(Constants.BUNDLE_FROM_PROFILE, isFromProfile)
                    addFragmentInStack<Any>(AppFragmentState.F_REG_STEP3, bundle)
                }
                is Resource.Error -> {
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
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).showProgressBar()
                    }
                }
            }

        })

        val dlnIssuedDate =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateIssuedDate()
            }
        val dlnExpiryDate =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateExpiryDate()
            }
        val passportIssuedDate =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updatePassportIssuedDate()
            }
        val passportExpiryDate =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updatePassportExpiryDate()
            }

        etDlnIssuedDate.setOnClickListener {
            KeyboardUtils.hideKeyboard(getAppActivity())
            val datePicker = DatePickerDialog(
                getAppActivity(), R.style.DialogTheme, dlnIssuedDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.maxDate = currentCalendar.timeInMillis
            datePicker.show()
        }

        etDlnExpiryDate.setOnClickListener {
            KeyboardUtils.hideKeyboard(getAppActivity())
            val datePicker = DatePickerDialog(
                getAppActivity(), R.style.DialogTheme, dlnExpiryDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        tvSkip.setOnClickListener{
            val intent: Intent = if (Constants.USER?.role.equals("ROLE_USER")) {
                Intent(activity, HomeActivity::class.java)
            }else if (Constants.USER?.role.equals("ROLE_CLINIC")) {
                Intent(activity, ClinicActivity::class.java)
            } else {
                Intent(activity, VendorActivity::class.java)
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        etPassportIssuedDate.setOnClickListener {
            KeyboardUtils.hideKeyboard(getAppActivity())
            val datePicker = DatePickerDialog(
                getAppActivity(), R.style.DialogTheme, passportIssuedDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.maxDate = currentCalendar.timeInMillis
            datePicker.show()
        }

        etPassportExpiryDate.setOnClickListener {
            KeyboardUtils.hideKeyboard(getAppActivity())
            val datePicker = DatePickerDialog(
                getAppActivity(), R.style.DialogTheme, passportExpiryDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        sDlnState.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.DLN_AUTOCOMPLETE_REQUEST_CODE)
        }
        sDlnCountry.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.DLN_AUTOCOMPLETE_REQUEST_CODE)
        }
        sPassportState.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.PASSPORT_AUTOCOMPLETE_REQUEST_CODE)
        }
        sPassportCountry.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.PASSPORT_AUTOCOMPLETE_REQUEST_CODE)
        }

    }

    override fun pageVisible() {

    }

    private fun updateIssuedDate() {
        val sdf = SimpleDateFormat(DateUtils.DDMMYYYY, Locale.US)
        val apiSdf = SimpleDateFormat(DateUtils.API_DATE_FORMAT, Locale.US)
        val date = sdf.format(myCalendar.time)
        etDlnIssuedDate.setText(date)

        viewModel.dlnIssuedDate.value = apiSdf.format(myCalendar.time)
    }

    private fun updateExpiryDate() {
        val sdf = SimpleDateFormat(DateUtils.DDMMYYYY, Locale.US)
        val apiSdf = SimpleDateFormat(DateUtils.API_DATE_FORMAT, Locale.US)
        val date = sdf.format(myCalendar.time)
        etDlnExpiryDate.setText(date)

        viewModel.dlnExpiredDate.value = apiSdf.format(myCalendar.time)
    }

    private fun updatePassportIssuedDate() {
        val sdf = SimpleDateFormat(DateUtils.DDMMYYYY, Locale.US)
        val apiSdf = SimpleDateFormat(DateUtils.API_DATE_FORMAT, Locale.US)
        val date = sdf.format(myCalendar.time)
        etPassportIssuedDate.setText(date)

        viewModel.passportIssuedDate.value = apiSdf.format(myCalendar.time)
    }

    private fun updatePassportExpiryDate() {
        val sdf = SimpleDateFormat(DateUtils.DDMMYYYY, Locale.US)
        val apiSdf = SimpleDateFormat(DateUtils.API_DATE_FORMAT, Locale.US)
        val date = sdf.format(myCalendar.time)
        etPassportExpiryDate.setText(date)

        viewModel.passportExpiredDate.value = apiSdf.format(myCalendar.time)
    }

    fun updateDlnAddress(state: String, country: String) {
        sDlnState.setText(state)
        sDlnCountry.setText(country)
        viewModel.dlnState.value = state
        viewModel.dlnCountry.value = country
    }

    fun updatePassportAddress(state: String, country: String) {
        sPassportState.setText(state)
        sPassportCountry.setText(country)
        viewModel.passportState.value = state
        viewModel.passportCountry.value = country
    }

    private fun setDate(parseDate: String, editText: EditText, function: () -> Unit) {
        val dob = SimpleDateFormat(DateUtils.API_DATE_FORMAT_VACCINE, Locale.getDefault())
        val date = dob.parse(parseDate)
        val cal = Calendar.getInstance()
        cal.time = date
        myCalendar.set(Calendar.YEAR, cal.get(Calendar.YEAR))
        myCalendar.set(Calendar.MONTH, cal.get(Calendar.MONTH))
        myCalendar.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH))
        function()
        editText.isClickable = false
        editText.isEnabled = false
    }
}