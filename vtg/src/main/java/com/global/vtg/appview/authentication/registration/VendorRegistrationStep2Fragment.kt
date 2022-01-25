package com.global.vtg.appview.authentication.registration

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.config.PickMediaExtensions
import com.global.vtg.appview.config.getRealPath
import com.global.vtg.appview.config.getRealPathFromURI
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.*
import com.vtg.R
import com.vtg.databinding.FragmentRegVendorStep2Binding
import kotlinx.android.synthetic.main.fragment_dashboard.*

import kotlinx.android.synthetic.main.fragment_reg_vendor_step2.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class VendorRegistrationStep2Fragment : AppFragment() {
    private val viewModel by viewModel<VendorRegistrationStep2ViewModel>()
    private lateinit var mFragmentBinding: FragmentRegVendorStep2Binding
    var id: Int? = null
    var isFromProfile = false
    var businessDate = ""
    private val myCalendar: Calendar = Calendar.getInstance()
    private val currentCalendar: Calendar = Calendar.getInstance()
    override fun getLayoutId(): Int {
        return R.layout.fragment_reg_vendor_step2
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
        mFragmentBinding = binding as FragmentRegVendorStep2Binding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {

        if (Constants.USER!!.role.equals("ROLE_VENDOR", true)){
            tvTitleMain!!.text = "Vendor Step 2"
        }else{
            tvTitleMain!!.text = "Lab/Clinic Step 2"
        }
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

//        if(isFromProfile)
//            tvSkip.visibility=View.GONE

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
        ivCancel.setOnClickListener{
            ivUploadDocument.visibility = View.VISIBLE
            tvSelectDoc.visibility = View.VISIBLE
            cvUploadDocument.isClickable = true
            viewModel.documentPath = ""

            tvDocName.text = resources.getString(R.string.label_upload_text_b)
            tvDocName.visibility = View.GONE
            ivCancel.visibility = View.GONE
        }


        val date =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                updateDate()
            }

        eExpiryDate.setOnClickListener {
            KeyboardUtils.hideKeyboard(getAppActivity())
            val datePicker = DatePickerDialog(
                getAppActivity(), R.style.DialogTheme, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
            datePicker.show()
        }
        viewModel.isDataAvailable=false
        val list = Constants.USER?.extras

        val document= Constants.USER?.document
        if(document!=null&&document.isNotEmpty()){
            for (doc in document) {
                if(doc!!.type!!.lowercase().contains("Vendor/Clinic Certificate".lowercase())){
                    doc_img.setGlideNormalImage(doc!!.identity)
                    ivUploadDocument.visibility=View.GONE
                    tvSelectDoc.visibility=View.GONE
                    tvDocName.visibility=View.GONE
                    ivCancel.visibility=View.GONE
                    viewModel.isDataAvailable=true
                    cvUploadDocument.isClickable = false
                    cvUploadDocument.isEnabled = false

                    businessDate=   DateUtils.formatDateUTCToLocal(doc.expireDate!!,DateUtils.API_DATE_FORMAT_VACCINE,DateUtils.API_DATE_FORMAT)
                    viewModel.expiryDate.postValue(businessDate)

                    break
                }
            }
        }
        if (list != null && list.isNotEmpty()) {
            for (extra in list) {
                when {
                    extra?.K.equals("buisnessName") -> {
                        etBusinessName.isClickable = false
                        etBusinessName.isEnabled = false
                        viewModel.businessName.postValue(extra?.V)
                    }
                    extra?.K.equals("buisnessId") -> {
                        etId.isClickable = false
                        etId.isEnabled = false
                        viewModel.businessId.postValue(extra?.V)
                        viewModel.isDataAvailable=true
                    }
                    extra?.K.equals("employeeId") -> {
                        etEmployeeId.isClickable = false
                        etEmployeeId.isEnabled = false
                        viewModel.employeeId.postValue(extra?.V)
                        viewModel.isDataAvailable=true
                    }

                    extra?.K.equals("vat") -> {
                        etBusinessVat.isClickable = false
                        etBusinessVat.isEnabled = false
                        viewModel.businessVat.postValue(extra?.V)
                        viewModel.isDataAvailable=true
                    }
                    extra?.K.equals("certificateExpDate") -> {
//                         businessDate=apiSdf.format(myCalendar.time)
//
//                        eExpiryDate.text = date
                        businessDate=   DateUtils.formatDateUTCToLocal(extra?.V!!,DateUtils.API_DATE_FORMAT_EXP,DateUtils.API_DATE_FORMAT)
                        viewModel.expiryDate.postValue(businessDate)

                        eExpiryDate.isClickable = false
                        eExpiryDate.isEnabled = false
                        cvUploadDocument.isClickable = false
                        cvUploadDocument.isEnabled = false
                        viewModel.isDataAvailable=true
                      // updateDate()
                    }
                }
            }
        }

        // Handle Error
        viewModel.showToastError.observe(this, {
            DialogUtils.showSnackBar(context, it)
        })

        viewModel.registerVendorStep2LiveData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    Constants.USER = it.data
                    Constants.USER?.step2Complete = true
                    viewModel.completeStep2()
                }
                is Resource.Error -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).showProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).showProgressBar()
                    }
                }
            }

        })

        viewModel.registerLiveData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    Constants.USER = it.data
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    val bundle = Bundle()
                    bundle.putBoolean(Constants.BUNDLE_FROM_PROFILE, isFromProfile)
                    addFragmentInStack<Any>(AppFragmentState.F_REG_STEP3, bundle)
                }
                is Resource.Error -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).showProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).showProgressBar()
                    }
                }
            }
        })
        viewModel.registerLiveDataAlready.observe(this, {
            when (activity) {
                is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                else -> (activity as VendorActivity).hideProgressBar()
            }
            val bundle = Bundle()
            bundle.putBoolean(Constants.BUNDLE_FROM_PROFILE, isFromProfile)
            addFragmentInStack<Any>(AppFragmentState.F_REG_STEP3, bundle)})

        viewModel.chooseFile.observe(this, {
            PickMediaExtensions.instance.pickFromStorage(getAppActivity()) { resultCode: Int, path: String, displayName: String? ->
                resultMessage(resultCode, path, displayName)
            }
        })
    }

    private fun resultMessage(resultCode: Int, path: String, displayName: String?) {
        getAppActivity().getActivityScope(getAppActivity()).launch(Dispatchers.IO) {
            when (resultCode) {
                PickMediaExtensions.PICK_SUCCESS -> {
                    val realPath1 = Uri.parse(path).getRealPathFromURI(getAppActivity())
                    if (realPath1 != null) {
                        if (displayName != null) {
                            updateDocument(displayName, path)
                        }
                    } else {
                        val realPath = Uri.parse(path).getRealPath(getAppActivity())
                        if (realPath != null) {
                            if (displayName != null) {
                                updateDocument(displayName, path)
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

//        viewModel.userConfigLiveData.observe(this, {
//            when (it) {
//                is Resource.Success -> {
//                    when (activity) {
//                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
//                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
//                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
//                        else -> (activity as VendorActivity).hideProgressBar()
//                    }
//                    Constants.USER = it.data
//                    SharedPreferenceUtil.getInstance(getAppActivity())
//                        ?.saveData(
//                            PreferenceManager.KEY_USER_LOGGED_IN,
//                            true
//                        )
//                    val intent: Intent = if (Constants.USER?.role.equals("ROLE_USER")) {
//                        Intent(activity, HomeActivity::class.java)
//                    }else if (Constants.USER?.role.equals("ROLE_CLINIC")) {
//                        Intent(activity, ClinicActivity::class.java)
//                    } else {
//                        Intent(activity, VendorActivity::class.java)
//                    }
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    startActivity(intent)
//                }
//                is Resource.Error -> {
//                    when (activity) {
//                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
//                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
//                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
//                        else -> (activity as VendorActivity).hideProgressBar()
//                    }
//                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
//                }
//                is Resource.Loading -> {
//                    when (activity) {
//                        is AuthenticationActivity -> (activity as AuthenticationActivity).showProgressBar()
//                        is HomeActivity -> (activity as HomeActivity).showProgressBar()
//                        is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
//                        else -> (activity as VendorActivity).showProgressBar()
//                    }
//                }
//            }
//        })
    }

    private fun updateDocument(docName: String, path: String) {
        MainScope().launch {
            ivUploadDocument.visibility = View.GONE
            tvSelectDoc.visibility = View.GONE
            cvUploadDocument.isClickable = false
            viewModel.documentPath = path

            tvDocName.text = resources.getString(R.string.label_doc_name, docName)
            tvDocName.visibility = View.VISIBLE
            ivCancel.visibility = View.VISIBLE

        }
    }

    override fun pageVisible() {

    }

    private fun updateDate() {
        val apiSdf = SimpleDateFormat(DateUtils.API_DATE_FORMAT, Locale.getDefault())
        businessDate=apiSdf.format(myCalendar.time)
        viewModel.expiryDate.postValue(businessDate)



//        viewModel.day = dayOfWeek
//        viewModel.date = apiSdf.format(myCalendar.time)
    }
}