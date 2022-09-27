package com.global.vtg.appview.authentication.registration

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.config.PickMediaExtensions
import com.global.vtg.appview.config.getRealPath
import com.global.vtg.appview.config.getRealPathFromURI
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.appview.home.profile.ProfileFragment
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.*
import com.global.vtg.utils.DateUtils.API_DATE_FORMAT
import com.global.vtg.utils.baseinrerface.OkCancelNeutralDialogInterface
import com.vtg.R
import com.vtg.databinding.FragmentRegStep1Binding
import kotlinx.android.synthetic.main.fragment_reg_step1.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class RegistrationStep1Fragment : AppFragment() {
    private var isVendor: Boolean = false
    private var isClinic: Boolean = false
    private val viewModel by viewModel<RegistrationStep1ViewModel>()
    private lateinit var mFragmentBinding: FragmentRegStep1Binding
    var isFirstTime = true
    private val myCalendar: Calendar = Calendar.getInstance()
    private val currentCalendar: Calendar = Calendar.getInstance()
    var isFromProfile = false
    var date = ""
    var twilioUserId = ""
    var childAccount: Boolean = false
    var callService: Boolean = false
    override fun getLayoutId(): Int {
        return R.layout.fragment_reg_step1
    }

    override fun preDataBinding(arguments: Bundle?) {
        arguments?.let {
            if (arguments.containsKey(Constants.BUNDLE_IS_VENDOR)) {
                isVendor = arguments.getBoolean(Constants.BUNDLE_IS_VENDOR)
            }
            if (arguments.containsKey(Constants.BUNDLE_IS_CLINIC)) {
                isClinic = arguments.getBoolean(Constants.BUNDLE_IS_CLINIC)
            }
            if (arguments.containsKey(Constants.BUNDLE_FROM_PROFILE)) {
                isFromProfile = arguments.getBoolean(Constants.BUNDLE_FROM_PROFILE)
            }

            if (arguments.containsKey(Constants.BUNDLE_DATE)) {
                date = arguments.getString(Constants.BUNDLE_DATE).toString()
            }
            if (arguments.containsKey(Constants.BUNDLE_TWILIO_USER_ID)) {
                twilioUserId = arguments.getString(Constants.BUNDLE_TWILIO_USER_ID, "")
            }
            if (arguments.containsKey(Constants.BUNDLE_CHILD_ACCOUNT)) {
                childAccount = arguments.getBoolean(Constants.BUNDLE_CHILD_ACCOUNT, false)
            }
        }
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentRegStep1Binding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    @SuppressLint("SetTextI18n")
    override fun initializeComponent(view: View?) {

        if (isFromProfile || childAccount) {
            btnSkip.visibility = View.GONE
        } else {

            ivBack.visibility = View.GONE
        }


        var userType = SharedPreferenceUtil.getInstance(getAppActivity())
            ?.getData(
                PreferenceManager.KEY_LOGGED_IN_USER_TYPE,
                ""
            )

//        if (Constants.USER!!.role.equals("ROLE_VENDOR", true)) {
//            tvTitle.text = "Vendor Step 1"
//            tvPlaceOfBirth.visibility = View.GONE
//            etCity.visibility = View.GONE
//            etState.visibility = View.GONE
//            etCountry.visibility = View.GONE
//            sEthnicity.visibility = View.GONE
//            tvEthnicity.visibility = View.GONE
//            edWebsite.visibility = View.VISIBLE
//            tvWebsite.visibility = View.VISIBLE
//        }
        if (userType.equals("Clinic")) {
            tvTitle.text = getString(R.string.lab_Step_1)
            tvPlaceOfBirth.visibility = View.GONE
            etCity.visibility = View.GONE
            etState.visibility = View.GONE
            etCountry.visibility = View.GONE
            sEthnicity.visibility = View.GONE
            tvEthnicity.visibility = View.GONE
//            edWebsite.visibility = View.VISIBLE
//            tvWebsite.visibility = View.VISIBLE
        } else if (userType.equals("Vendor")) {

            tvTitle.text = getString(R.string.vendor_Step_1)
            tvPlaceOfBirth.visibility = View.GONE
            etCity.visibility = View.GONE
            etState.visibility = View.GONE
            etCountry.visibility = View.GONE
            sEthnicity.visibility = View.GONE
            tvEthnicity.visibility = View.GONE
//            edWebsite.visibility = View.VISIBLE
//            tvWebsite.visibility = View.VISIBLE
        }

        if (childAccount) {
            viewModel.childAccount = true
            tvTitle.text = getString(R.string.child_sign_up)
            loadData()
        } else {
            if (Constants.USER != null) {
                loadData()
            } else {
                callService = true
                viewModel.getUser()
            }
        }

        viewModel.userConfigLiveData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }

                    if (viewModel.childAccount) {
                        Constants.USERCHILD = it.data
                        viewModel.childAccountLiveData.postValue(true)
                    } else {
                        Constants.USER = it.data
                        if (callService) {
                            loadData()
                        } else {
                            SharedPreferenceUtil.getInstance(getAppActivity())
                                ?.saveData(
                                    PreferenceManager.KEY_USER_LOGGED_IN,
                                    true
                                )
                            val intent: Intent = if (Constants.USER?.role.equals("ROLE_USER")) {
                                Intent(activity, HomeActivity::class.java)
                            } else {
                                Intent(activity, VendorActivity::class.java)
                            }
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
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
                        is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
                        else -> (activity as VendorActivity).showProgressBar()
                    }
                }
            }
        }

    }

    override fun pageVisible() {

    }

    private fun loadData() {
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        if (!childAccount) {
            if (!Constants.USER!!.profileUrl.isNullOrEmpty()) {
                ivProfilePic.setGlideNormalImage(Constants.USER!!.profileUrl)
                viewModel.documentPath = Constants.USER!!.profileUrl
            }

            if (!Constants.USER?.website.isNullOrEmpty()) {
                viewModel.websiteName.postValue(Constants.USER?.website)
//            etFirstName.isClickable = false
//            etFirstName.isEnabled = false
            }

            if (!Constants.USER?.firstName.isNullOrEmpty()) {
                viewModel.firstName.postValue(Constants.USER?.firstName)
//            etFirstName.isClickable = false
//            etFirstName.isEnabled = false
            }
            if (!Constants.USER?.lastName.isNullOrEmpty()) {
                viewModel.lastName.postValue(Constants.USER?.lastName)
//            etLastName.isClickable = false
//            etLastName.isEnabled = false
            }
            if (!Constants.USER?.birthCity.isNullOrEmpty()) {
                viewModel.city.postValue(Constants.USER?.birthCity)
//            etCity.isClickable = false
//            etCity.isEnabled = false
            }


            if (!Constants.USER?.birthState.isNullOrEmpty()) {
                viewModel.state.postValue(Constants.USER?.birthState)
//            etState.isClickable = false
//            etState.isEnabled = false
            }
            if (!Constants.USER?.birthCountry.isNullOrEmpty()) {
                viewModel.country.postValue(Constants.USER?.birthCountry)
//            etCountry.isClickable = false
//            etCountry.isEnabled = false
            }

            if(TextUtils.isDigitsOnly(Constants.USER?.twilioUserId)) {
                if (isFromProfile || !Constants.USER?.twilioUserId.isNullOrEmpty()) {
                    Constants.twilioUserId = Constants.USER?.twilioUserId?.toInt()
                } else if (twilioUserId.isNotEmpty()) {
                    Constants.twilioUserId = twilioUserId.toInt()
                }
            }

            viewModel.isVendor = isVendor
            viewModel.isClinic = isClinic
        } else {
            viewModel.state

            viewModel.dob.postValue(
                DateUtils.formatDate(
                    date,
                    DateUtils.MMDDYYY
                )
            )

            val date = DateUtils.getDateLocal(
                date,
                DateUtils.MMDDYYY
            )

            viewModel.dob.postValue(
                DateUtils.formatDateTime(
                    date.time,
                    DateUtils.MMDDYYY
                )
            )
            viewModel.apiDob = DateUtils.formatDateTime(date.time, API_DATE_FORMAT)
            var age=viewModel.getAgeString(viewModel.apiDob)
            if(!TextUtils.isEmpty(age)){
                tvAge.visibility=View.VISIBLE
                etAge.visibility=View.VISIBLE
                etAge.text=age
            }
            viewModel.dobDate = date



        }
        val list: Array<out String> = resources.getStringArray(R.array.title)
        val genderList: Array<out String> = resources.getStringArray(R.array.gender)
        val ethnicityList: Array<out String> = resources.getStringArray(R.array.ethnicity)
        Arrays.sort(ethnicityList)

        if (!childAccount) {
            if (!Constants.USER?.title.isNullOrEmpty()) {
                viewModel.title.value = Constants.USER?.title
                for (titleIndex in list.indices) {
                    if (list[titleIndex] == Constants.USER?.title) {
                        sTitleRegister.post { sTitleRegister.setSelection(titleIndex + 1) }
                        sTitleRegister.isClickable = false
                        sTitleRegister.isEnabled = false
                        break
                    }
                }
            }

            if (!Constants.USER?.ethnicity.isNullOrEmpty()) {
                viewModel.ethnicity.value = Constants.USER?.ethnicity
                for (titleIndex in ethnicityList.indices) {
                    if (ethnicityList[titleIndex].equals(Constants.USER?.ethnicity)) {
                        sEthnicity.post { sEthnicity.setSelection(titleIndex + 1) }
                        break
                    }
                }
            }
        }

        sTitleRegister.adapter = TitleSpinnerAdapter(
            getAppActivity(), list
        )
        sGender.adapter = GenderSpinnerAdapter(
            getAppActivity(), genderList
        )

        Arrays.sort(ethnicityList)


        sEthnicity.adapter = EthnicitySpinnerAdapter(
            getAppActivity(), ethnicityList
        )

        if (!childAccount) {
            if (!Constants.USER?.gender.isNullOrEmpty()) {
                viewModel.gender.value = Constants.USER?.gender
                for (index in genderList.indices) {
                    if (genderList[index].equals(Constants.USER?.gender)) {
                        sGender.post { sGender.setSelection(index + 1) }
//                    sGender.isClickable = false
//                    sGender.isEnabled = false
                        break
                    }
                }
            }

            if (!Constants.USER?.citizenship.isNullOrEmpty()) {
                viewModel.citizenship.value = Constants.USER?.citizenship
                ccpCitizenship.setCountryForNameCode(Constants.USER?.citizenship)
//            ccpCitizenship.isClickable = false
//            ccpCitizenship.isEnabled = false
                ccpCitizenship.setCcpClickable(false)
            }
            if (!Constants.USER?.dateOfBirth.isNullOrEmpty()) {
                try {

                    viewModel.dob.postValue(
                        DateUtils.formatDate(
                            Constants.USER?.dateOfBirth!!,
                            DateUtils.API_DATE_FORMAT
                        )
                    )

                    var age=viewModel.getAgeString( Constants.USER?.dateOfBirth!!)
                    if(!TextUtils.isEmpty(age)){
                        tvAge.visibility=View.VISIBLE
                        etAge.visibility=View.VISIBLE
                        etAge.text=age
                    }

                  val date = DateUtils.getDateLocal(
                        Constants.USER?.dateOfBirth!!,
                        DateUtils.API_DATE_FORMAT
                    )

                    viewModel.dob.postValue(
                        DateUtils.formatDateTime(
                            date.time,
                            DateUtils.DDMMYY
                        )
                    )
                    viewModel.apiDob = DateUtils.formatDateTime(date.time, API_DATE_FORMAT)
                    viewModel.dobDate = date


                } catch (e: Exception) {
                    e.printStackTrace()
                }
//            etDob.isClickable = false
//            etDob.isEnabled = false
            }
        }
        sTitleRegister.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                KeyboardUtils.hideKeyboard(view)
                //   if (!isFirstTime)
                try {
                    if ((pos - 1) >= 0)
                        viewModel.title.value = list[pos - 1]
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        sGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                KeyboardUtils.hideKeyboard(view)
                try {
                    if ((pos - 1) >= 0)
                        viewModel.gender.value = genderList[pos - 1]
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        sEthnicity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                KeyboardUtils.hideKeyboard(view)
                try {
                    if ((pos - 1) >= 0)
                        viewModel.ethnicity.value = ethnicityList[pos - 1]
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        ccpCitizenship.setOnCountryChangeListener {
            viewModel.citizenship.value = ccpCitizenship.selectedCountryNameCode
        }

        viewModel.uploadProfilePic.observe(this) {
            DialogUtils.okCancelNeutralDialog(
                context,
                getAppActivity().getString(R.string.app_name),
                getAppActivity().getString(R.string.label_select_image),
                object :
                    OkCancelNeutralDialogInterface {
                    override fun ok() {
                        PickMediaExtensions.instance.pickFromGallery(getAppActivity()) { resultCode: Int, path: String, displayName: String? ->
                            resultMessage(resultCode, path, displayName)
                        }
                    }

                    override fun cancel() {
                        PickMediaExtensions.instance.pickFromCamera(getAppActivity()) { resultCode: Int, path: String, displayName: String? ->
                            resultMessage(resultCode, path, displayName)
                        }
                    }

                    override fun neutral() {

                    }
                })
        }


        val date =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDate()
            }

        etDob.setOnClickListener {
            KeyboardUtils.hideKeyboard(getAppActivity())
            val datePicker = DatePickerDialog(
                getAppActivity(), R.style.DialogTheme, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.maxDate = currentCalendar.timeInMillis
            datePicker.show()
        }

        viewModel.registerStep1LiveData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    if( viewModel.childAccount){
                        Constants.USERCHILD = it.data

                            viewModel.uploadProfileChildPic(it.data.id.toString())


                    }else {


                        Constants.USER = it.data

                        SharedPreferenceUtil.getInstance(getAppActivity())
                            ?.saveData(
                                PreferenceManager.KEY_FIRST_STEP,
                                true
                            )
                        val bundle = Bundle()
                        bundle.putBoolean(Constants.BUNDLE_FROM_PROFILE, isFromProfile)
                        if (it.data.role.equals("ROLE_VENDOR", true))
                            addFragmentInStack<Any>(AppFragmentState.F_VENDOR_STEP2, bundle)
                        else if (it.data.role.equals("ROLE_CLINIC", true)) {
                            addFragmentInStack<Any>(AppFragmentState.F_VENDOR_STEP2, bundle)
                        } else addFragmentInStack<Any>(AppFragmentState.F_REG_STEP2, bundle)
                    }
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
                        is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
                        else -> (activity as VendorActivity).showProgressBar()
                    }
                }
            }

        })

        viewModel.uploadProfilePicStep1.observe(this, {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    if (viewModel.childAccount) {
                        Constants.USERCHILD?.profileUrl = it.data.profileUrl
                        viewModel.childAccountLiveData.postValue(true)

                    } else {
                        Constants.USER?.profileUrl = it.data.profileUrl
                        val fragments = getAppActivity().supportFragmentManager.fragments
                        for (frg in fragments) {
                            if (frg is ProfileFragment) {
                                frg.loadAddress()
                                break
                            }
                        }
                    }

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
                        is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
                        else -> (activity as VendorActivity).showProgressBar()
                    }
                }
            }

        })

        viewModel.childAccountLiveData.observe(this, {
            val bundle = Bundle()
            bundle.putBoolean(Constants.BUNDLE_CHILD_ACCOUNT, true)
            addFragmentInStack<Any>(AppFragmentState.F_REG_STEP2, bundle)

        })


        viewModel.registerStep1LiveDataskip.observe(this) {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    it.data.email?.trim()?.let { it1 ->
                        SharedPreferenceUtil.getInstance(getAppActivity())
                            ?.saveData(
                                PreferenceManager.KEY_USER_NAME,
                                it1
                            )
                    }

                    it.data.email?.trim()?.let { it1 ->
                        SharedPreferenceUtil.getInstance(getAppActivity())
                            ?.saveData(
                                PreferenceManager.KEY_USER_NAME_PARENT,
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

                    it.data.password?.trim()?.let { it1 ->
                        SharedPreferenceUtil.getInstance(getAppActivity())
                            ?.saveData(
                                PreferenceManager.KEY_PASSWORD_PARENT,
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
                        is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
                        else -> (activity as VendorActivity).showProgressBar()
                    }
                }
            }
        }

        etCity.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
        }
        etState.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
        }
        etCountry.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
        }

//        etCity.setDrawableRightTouch {
//            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
//        }
//        etState.setDrawableRightTouch {
//            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
//        }
//        etCountry.setDrawableRightTouch {
//            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
//        }

        // Handle Error
        viewModel.showToastError.observe(this) {
            if (it == "dobchild") {
                AppAlertDialog().showAlert(
                    activity!!,
                    object : AppAlertDialog.GetClick {
                        override fun response(type: String) {
                        }
                    }, getString(R.string.child_valid_date), "Ok", ""
                )
            } else if (it != "dob") {
                DialogUtils.showSnackBar(context, it)
            } else {
                AppAlertDialog().showAlert(
                    activity!!,
                    object : AppAlertDialog.GetClick {
                        override fun response(type: String) {
                        }
                    }, getString(R.string.valid_age), "Ok", ""
                )
            }
        }

        isFirstTime = false
    }

    private fun updateDate() {

        viewModel.dob.postValue(
            DateUtils.formatDate(
                myCalendar.timeInMillis
            )
        )
        viewModel.apiDob = DateUtils.formatDateTime(myCalendar.timeInMillis, API_DATE_FORMAT)

        var age=viewModel.getAgeString( viewModel.apiDob)
        if(!TextUtils.isEmpty(age)){
            tvAge.visibility=View.VISIBLE
            etAge.visibility=View.VISIBLE
            etAge.text=age
        }
        viewModel.dobDate = myCalendar.time
        viewModel.dob.postValue(
            DateUtils.formatDateTime(
                myCalendar.timeInMillis,
                DateUtils.DDMMYY
            )
        )

        if(childAccount){
            if (viewModel.getAge()!! > 13) {
                AppAlertDialog().showAlert(
                    activity!!,
                    object : AppAlertDialog.GetClick {
                        override fun response(type: String) {

                        }
                    }, getString(R.string.child_valid_date), "Ok", ""
                )
            }
        }else

        if (viewModel.getAge()!! < 13) {
            AppAlertDialog().showAlert(
                activity!!,
                object : AppAlertDialog.GetClick {
                    override fun response(type: String) {

                    }
                }, getString(R.string.valid_age), "Ok", ""
            )
        }
    }

    fun updateAddress(city: String, state: String, country: String) {
        etCity.text = city
        etState.text = state
        etCountry.text = country
    }

    private fun resultMessage(
        resultCode: Int,
        path: String,
        displayName: String?,
        fromCamera: Boolean = false
    ) {
        getAppActivity().getActivityScope(getAppActivity()).launch(Dispatchers.IO) {
            when (resultCode) {
                PickMediaExtensions.PICK_SUCCESS -> {
                    val realPath1 = Uri.parse(path).getRealPathFromURI(getAppActivity())
                    if (realPath1 != null) {
                        if (displayName != null) {
                            updateDocument(displayName, path, fromCamera)
                        }
                    } else {
                        val realPath = Uri.parse(path).getRealPath(getAppActivity())
                        if (realPath != null) {
                            if (displayName != null) {
                                updateDocument(displayName, path, fromCamera)
                            }
                        } else {
                            DialogUtils.showSnackBar(
                                getAppActivity(),
                                resources.getString(R.string.error_message_somethingwrong)
                            )
                        }
                    }
                }
                PickMediaExtensions.PICK_CANCELED -> {
                    activity?.runOnUiThread {
                        activity?.onBackPressed()
                    }
                }
                else -> DialogUtils.showSnackBar(
                    getAppActivity(),
                    resources.getString(R.string.error_message_somethingwrong)
                )
            }
        }
    }

    private fun updateDocument(docName: String, path: String, fromCamera: Boolean) {
        MainScope().launch {
            viewModel.documentPath = path
            viewModel.docName = docName
            var bitmap = BitmapFactory.decodeFile(path)
            if (fromCamera) {
                if (bitmap.width > bitmap.height) {
                    val matrix = Matrix()
                    matrix.postRotate(90f)
                    bitmap = Bitmap.createBitmap(
                        bitmap,
                        0,
                        0,
                        bitmap.width,
                        bitmap.height,
                        matrix,
                        true
                    )
                }
                ivProfilePic.setImageBitmap(
                    bitmap
                )
            } else {
                ivProfilePic.setImageBitmap(bitmap)
            }
        }
        // upload profile pic
        if(!childAccount)
        viewModel.uploadProfile(docName)
    }

}