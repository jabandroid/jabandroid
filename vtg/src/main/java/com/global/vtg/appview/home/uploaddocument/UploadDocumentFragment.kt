package com.global.vtg.appview.home.uploaddocument

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.authentication.registration.TestTypeResult
import com.global.vtg.appview.config.Institute
import com.global.vtg.appview.config.PickMediaExtensions
import com.global.vtg.appview.config.getRealPath
import com.global.vtg.appview.config.getRealPathFromURI
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.appview.home.vaccinehistory.VaccineHistoryFragment
import com.global.vtg.base.AppFragment
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.*
import com.global.vtg.utils.DateUtils.API_DATE_FORMAT
import com.global.vtg.utils.DateUtils.DDMMYY
import com.global.vtg.utils.DateUtils.DDMMYYYY
import com.global.vtg.utils.DateUtils.appendZero
import com.vtg.R
import com.vtg.databinding.FragmentUploadDocumentBinding
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.*

import kotlinx.android.synthetic.main.fragment_registration.*

import kotlinx.android.synthetic.main.fragment_upload_document.*
import kotlinx.android.synthetic.main.fragment_upload_document.cbCertify
import kotlinx.android.synthetic.main.fragment_upload_document.ccp
import kotlinx.android.synthetic.main.fragment_upload_document.clForm
import kotlinx.android.synthetic.main.fragment_upload_document.clThankYou
import kotlinx.android.synthetic.main.fragment_upload_document.cvUploadDocument
import kotlinx.android.synthetic.main.fragment_upload_document.etFee
import kotlinx.android.synthetic.main.fragment_upload_document.etHospitalName
import kotlinx.android.synthetic.main.fragment_upload_document.ivBack
import kotlinx.android.synthetic.main.fragment_upload_document.ivCancel
import kotlinx.android.synthetic.main.fragment_upload_document.ivUploadDocument
import kotlinx.android.synthetic.main.fragment_upload_document.rvInstitute
import kotlinx.android.synthetic.main.fragment_upload_document.sDate
import kotlinx.android.synthetic.main.fragment_upload_document.sDay
import kotlinx.android.synthetic.main.fragment_upload_document.sTime
import kotlinx.android.synthetic.main.fragment_upload_document.s_scan
import kotlinx.android.synthetic.main.fragment_upload_document.tvDocName
import kotlinx.android.synthetic.main.fragment_upload_document.tvFee
import kotlinx.android.synthetic.main.fragment_upload_document.tvScan
import kotlinx.android.synthetic.main.fragment_upload_document.tvSelectDoc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class UploadDocumentFragment : AppFragment(), InstituteAdapter.ClickListener {
    private var isSelected: Boolean = false
    var isFirstTime: Boolean = true
    private lateinit var mFragmentBinding: FragmentUploadDocumentBinding
    private val viewModel by viewModel<UploadDocumentViewModel>()
    private val myCalendar: Calendar = Calendar.getInstance()
    private val currentCalendar: Calendar = Calendar.getInstance()
    val doseList: MutableList<String> = ArrayList()

    val vaccineTypeList: ArrayList<String> = ArrayList()
    var values = ArrayList<Institute>()
  lateinit var   instituteAdapter: InstituteAutoCompleteAdapter
    override fun getLayoutId(): Int {
        return R.layout.fragment_upload_document
    }

    override fun preDataBinding(arguments: Bundle?) {
        getAppActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentUploadDocumentBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        if (Constants.USER?.role.equals("ROLE_CLINIC")) {
            groupMobileNo.visibility = View.GONE
            cbCertify.isChecked=true
            cbCertify.visibility = View.GONE
            s_scan.visibility = View.VISIBLE
            tvScan.visibility = View.VISIBLE
        } else {
            groupMobileNo.visibility = View.GONE
            cbCertify.visibility = View.VISIBLE
            s_scan.visibility = View.GONE
            tvScan.visibility = View.GONE
        }

         instituteAdapter =
            InstituteAutoCompleteAdapter(activity, R.layout.recycler_view_institute, values)
        viewModel.code.value = ccp.defaultCountryCode
        viewModel.region = ccp.defaultCountryNameCode
        ccp.setOnCountryChangeListener {
            viewModel.code.value = ccp.selectedCountryCode
            viewModel.region = ccp.selectedCountryNameCode
        }
        val layoutManager = LinearLayoutManager(getAppActivity())
        rvInstitute.layoutManager = layoutManager
        val adapter = InstituteAdapter(getAppActivity())
        adapter.setListener(this)
        rvInstitute.addItemDecoration(
            DividerItemDecoration(context, layoutManager.orientation)
        )
        rvInstitute.adapter = adapter

        tvCurrentDate.text = SimpleDateFormat(DDMMYYYY, Locale.US).format(myCalendar.timeInMillis)
        addDoses()
        vaccineType()

        viewModel.email = getString(R.string.scan_qr_code)
        sDose.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                KeyboardUtils.hideKeyboard(view)
                if (!isFirstTime)
                    viewModel.dose = getIdFromdose(doseList[pos - 1])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }
        sVaccine.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                KeyboardUtils.hideKeyboard(view)
                if (!isFirstTime) {
                    viewModel.vaccine = getIdFromType(vaccineTypeList[pos - 1])
                } else {
                    isFirstTime = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        viewModel.chooseFile.observe(this, {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "image/*"
//            getAppActivity().startActivityForResult(intent, PICK_FILE_REQUEST_CODE)

            PickMediaExtensions.instance.pickFromStorage(getAppActivity()) { resultCode: Int, path: String, displayName: String? ->
                resultMessage(resultCode, path, displayName)
            }
        })

        // Handle Error
        viewModel.showToastError.observe(this, {
            DialogUtils.showSnackBar(context, it)
        })

        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data


                viewModel.getDataFromBarcodeId(data!!.getStringExtra("code")!!)
            }
        }

        viewModel.scanBarcodeLiveData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                    }
                    tvScan.text="+"+it.data.mobileNo
                    viewModel.phone.postValue(it.data.mobileNo)
                }
                is Resource.Error -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).showProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
                    }
                }
            }
        })

        tvScan.setOnClickListener {

            val intent = Intent(Intent(activity, QrcodeScanner::class.java))
            resultLauncher.launch(intent)
        }

        viewModel.isCertify.observe(this, {
            if (it == true) {
                etFee.visibility = View.VISIBLE
                tvFee.visibility = View.VISIBLE
            } else {
                etFee.visibility = View.GONE
                tvFee.visibility = View.GONE
            }
        })
        viewModel.cancelDoc.observe(this, {
            ivUploadDocument.visibility = View.VISIBLE
            tvSelectDoc.visibility = View.VISIBLE

            tvDocName.visibility = View.GONE
            ivCancel.visibility = View.GONE
            cvUploadDocument.isClickable = true
            viewModel.documentPath = null
        })

        val date =
            OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDate()
            }

        sDate.setOnClickListener {
            KeyboardUtils.hideKeyboard(getAppActivity())
            val datePicker = DatePickerDialog(
                getAppActivity(), R.style.DialogTheme, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.maxDate = currentCalendar.timeInMillis
            datePicker.show()
        }

        sTime.setOnClickListener {
            KeyboardUtils.hideKeyboard(getAppActivity())
            val mCurrentTime = Calendar.getInstance()
            val hour = mCurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mCurrentTime[Calendar.MINUTE]
            val mTimePicker = TimePickerDialog(
                getAppActivity(),
                R.style.DialogTheme,
                { _, selectedHour, selectedMinute ->
                    run {
                        val time =
                            "${appendZero(selectedHour.toString())}:${appendZero(selectedMinute.toString())}"
                        sTime.setText(time)
                        viewModel.time = time
                        myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                        myCalendar.set(Calendar.MINUTE, selectedMinute)
                        updateDate()
                    }

                },
                hour,
                minute,
                true
            ) //Yes 24 hour time

            mTimePicker.setTitle(resources.getString(R.string.label_select_time))
            mTimePicker.show()
        }

        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        viewModel.saveSuccess.observe(this, {
            clForm.visibility = View.GONE
            clThankYou.visibility = View.VISIBLE
            val fragments = getAppActivity().supportFragmentManager.fragments
            for (frg in fragments) {
                if (frg is VaccineHistoryFragment) {
                    frg.refreshList()
                    break
                }
            }

            Handler().postDelayed({
                activity?.onBackPressed()
            }, 3000)
        })

        etHospitalName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length ?: 0 == 0) {
                    rvInstitute.visibility = View.GONE
                } else {
                    if (!isSelected) {
                        viewModel.searchInstitute(s.toString())
                    } else {
                        isSelected = false
                    }
                }
            }

        })

        viewModel.instituteLiveData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    it.data.institute?.let { it1 ->
                        adapter.setInstituteList(it1)
                        rvInstitute.visibility = View.VISIBLE
                        if (it1.size == 0) {
                            it1.add(Institute(name = resources.getString(R.string.label_no_result_found)))
                        } else {
                            scrollView.smoothScrollTo(0, etHospitalName.bottom)
                        }
                    }
                }
                is Resource.Error -> {
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                }
            }
        })

        viewModel.userLiveData.observe(this, {
            when (it) {
                is Resource.Success -> {

                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                    }
                    Constants.USER = it.data
                    viewModel.saveSuccess.postValue(true)
                }
                is Resource.Error -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).showProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
                    }
                }
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
    }

    private fun getIdFromType(s: String): Int? {
        for (data in Constants.CONFIG?.vaccineType!!) {
            if (data.type.equals(s))
                return data.id
        }
        return 0
    }
    private fun getIdFromdose(s: String): String? {
        for (data in Constants.CONFIG?.doses!!) {
            if (data!!.name.equals(s))
                return data!!.id
        }
        return ""
    }

    private fun updateDate() {

        sDate.setText(DateUtils.formatDateTime(
            myCalendar.timeInMillis,
            DDMMYY
        ))
        val dayOfWeek = SimpleDateFormat("EEEE", Locale.US).format(myCalendar.time)
        sDay.setText(dayOfWeek)

        viewModel.day = dayOfWeek
        var time=DateUtils.formatDateTime(myCalendar.timeInMillis,true)
        viewModel.date = DateUtils.formatDateTime(myCalendar.timeInMillis,true)
    }

    override fun pageVisible() {

    }


    private fun addDoses() {
//        doseList.add("Dose 1")
//        doseList.add("Dose 2")
//        doseList.add("Booster 1")
//        doseList.add("Booster 2")
        Constants.CONFIG?.doses?.let {
            for (data in it) {
                data!!.name?.let { it1 -> doseList.add(it1) }
            }
        }

        val adapter = ArrayAdapter(
            getAppActivity(),
            R.layout.my_spinner_row, doseList
        )

        // set adapter to the autocomplete tv to the arrayAdapter
        adapter.setDropDownViewResource(R.layout.my_spinner_row)

        sDose.adapter = VaccineDoseSpinnerAdapter(
            getAppActivity(),doseList
        )
    }

    private fun vaccineType() {

        Constants.CONFIG?.vaccineType?.let {
            for (data in it) {
                data.type?.let { it1 -> vaccineTypeList.add(it1) }
            }
        }

        Collections.sort(vaccineTypeList, Comparator<String?> { obj1, obj2 ->
            return@Comparator obj1!!.compareTo(obj2!!)
        })
        sVaccine.adapter = VaccineTypeSpinnerAdapter(
            getAppActivity(), vaccineTypeList
        )
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

    override fun onItemClick(institute: Institute) {
        KeyboardUtils.hideKeyboard(getAppActivity())
        isSelected = true
        etHospitalName.setText(institute.name)
        viewModel.instituteId = institute.id
        rvInstitute.visibility = View.GONE
    }
}