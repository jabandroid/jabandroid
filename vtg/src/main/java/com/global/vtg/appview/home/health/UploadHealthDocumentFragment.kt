package com.global.vtg.appview.home.health


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.global.vtg.appview.authentication.registration.TestType
import com.global.vtg.appview.authentication.registration.TestTypeResult
import com.global.vtg.appview.config.Institute
import com.global.vtg.appview.config.PickMediaExtensions
import com.global.vtg.appview.config.getRealPath
import com.global.vtg.appview.config.getRealPathFromURI
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.testHistory.TestKit
import com.global.vtg.appview.home.testHistory.TestKitAdapter
import com.global.vtg.appview.home.testHistory.TestKitResult
import com.global.vtg.appview.home.uploaddocument.InstituteAdapter
import com.global.vtg.appview.home.uploaddocument.TestResultSpinnerAdapter
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.*
import com.global.vtg.utils.DateUtils.DDMMYY
import com.global.vtg.utils.DateUtils.appendZero
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.vtg.R
import com.vtg.databinding.FragmentHealthInfoUploadDocumentBinding
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.*
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.cbCertify
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.clForm
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.clThankYou
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.cus_label
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.cvUploadDocument
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.email
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.etFee
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.etHospitalName
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.etLoginPhoneNo
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.groupMobileNo
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.ivBack
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.ivCancel
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.ivUploadDocument
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.or_1
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.or_2
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.rvInstitute
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.sDate
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.sDay
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.sTime
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.s_scan
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.scan
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.tvDocName
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.tvFee
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.tvScan
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.tvSelectDoc
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.tvemail
import kotlinx.android.synthetic.main.fragment_upload_document.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class UploadHealthDocumentFragment : AppFragment(), InstituteAdapter.ClickListener {
    private var isSelected: Boolean = false
    var isFirstTime: Boolean = true
    private lateinit var mFragmentBinding: FragmentHealthInfoUploadDocumentBinding
    private val viewModel by viewModel<UploadHealthDocumentViewModel>()
    private val myCalendar: Calendar = Calendar.getInstance()
    private val currentCalendar: Calendar = Calendar.getInstance()
    private var type: TestType? = null
    private var typeType: String = ""
    var isDob = false
    private var typeKit: TestKit = TestKit()
    private var typeKitId: String = ""
    val resultList: MutableList<String> = ArrayList()
    override fun getLayoutId(): Int {
        return R.layout.fragment_health_info_upload_document
    }

    override fun preDataBinding(arguments: Bundle?) {
        getAppActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentHealthInfoUploadDocumentBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    @SuppressLint("SetTextI18n")
    override fun initializeComponent(view: View?) {

        if (Constants.USER?.role.equals("ROLE_CLINIC")) {
                     cbCertify.visibility = View.GONE
            sStatus.visibility = View.VISIBLE
            s_status.visibility = View.VISIBLE
            sDob.visibility = View.GONE
            dob.visibility = View.GONE
            groupMobileNo.visibility = View.VISIBLE
            tvemail.visibility = View.VISIBLE
            scan.visibility = View.VISIBLE
            or_1.visibility = View.VISIBLE
            or_2.visibility = View.VISIBLE
            cus_label.visibility = View.VISIBLE
            tvScan.visibility = View.GONE

        } else {
            groupMobileNo.visibility = View.GONE
            email.visibility = View.GONE
            tvemail.visibility = View.GONE
            cus_label.visibility = View.GONE
            cbCertify.visibility = View.VISIBLE
            sStatus.visibility = View.VISIBLE
            s_status.visibility = View.VISIBLE
            sDob.visibility = View.GONE
            dob.visibility = View.GONE
            s_scan.visibility = View.GONE
            tvScan.visibility = View.GONE
            scan.visibility = View.GONE
            or_1.visibility = View.GONE
            or_2.visibility = View.GONE

        }
        viewModel.code.value = ccpHealth.defaultCountryCode
        viewModel.region = ccpHealth.defaultCountryNameCode
        ccpHealth.setOnCountryChangeListener {
            viewModel.code.value = ccpHealth.selectedCountryCode
            viewModel.region = ccpHealth.selectedCountryNameCode
        }
        val layoutManager = LinearLayoutManager(getAppActivity())
        rvInstitute.layoutManager = layoutManager
        val adapter = InstituteAdapter(getAppActivity())
        adapter.setListener(this)
        rvInstitute.addItemDecoration(
            DividerItemDecoration(context, layoutManager.orientation)
        )
        rvInstitute.adapter = adapter
        viewModel.email = getString(R.string.scan_qr_code)

        viewModel.chooseFile.observe(this) {
            PickMediaExtensions.instance.pickFromStorage(getAppActivity()) { resultCode: Int, path: String, displayName: String? ->
                resultMessage(resultCode, path, displayName)
            }
        }

        if (isNetworkAvailable(requireActivity())) {
            viewModel.testType()
        } else {
            DialogUtils.showSnackBar(
                requireActivity(),
                requireActivity().resources.getString(R.string.no_connection)
            )
        }

        sTestType.setOnClickListener {
            if (type != null)
                showType(type!!)
        }

        sTestKit.setOnClickListener {

            showKit(typeKit)
        }


        if (isNetworkAvailable(requireActivity())) {
            viewModel.testKit()
        } else {
            DialogUtils.showSnackBar(
                requireActivity(),
                requireActivity().resources.getString(R.string.no_connection)
            )
        }

        viewModel.testDataKit.observe(this, {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                    }
                    typeKit = it.data
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


        etLoginPhoneNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length ?: 0 == 0) {

                } else {
                    viewModel.emailScan.postValue("")
                    viewModel.govId.postValue("")
                }
            }

        })

        tvemail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length ?: 0 == 0) {

                } else {
                    viewModel.emailScan.postValue("")
                    viewModel.phone.postValue("")
                }
            }

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
                    if(TextUtils.isDigitsOnly(it.data.mobileNo))
                    tvScan.text="+"+it.data.mobileNo
                    else
                        tvScan.text=it.data.firstName+" "+ it.data.lastName
                    viewModel.emailScan.postValue(it.data.mobileNo)
                    viewModel.phone.postValue("")
                    viewModel.govId.postValue("")
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
        scan.setOnClickListener {

            val intent = Intent(Intent(activity, QrcodeScanner::class.java))
            resultLauncher.launch(intent)
        }

        tvScan.setOnClickListener {

            val intent = Intent(Intent(activity, QrcodeScanner::class.java))
            resultLauncher.launch(intent)
        }

        viewModel.testData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                    }
                    type = it.data
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

        // Handle Error
        viewModel.showToastError.observe(this, {
            DialogUtils.showSnackBar(context, it)
        })

        viewModel.cancelDoc.observe(this, {
            ivUploadDocument.visibility = View.VISIBLE
            tvSelectDoc.visibility = View.VISIBLE

            tvDocName.visibility = View.GONE
            ivCancel.visibility = View.GONE
            cvUploadDocument.isClickable = true
            viewModel.documentPath = null
        })

        viewModel.isCertify.observe(this, {
            if (it == true) {
                etFee.visibility = View.VISIBLE
                tvFee.visibility = View.VISIBLE
            } else {
                etFee.visibility = View.GONE
                tvFee.visibility = View.GONE
            }
        })
        addResut()

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
        sDob.setOnClickListener {
            isDob = true
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
                if (frg is HealthInformationFragment) {
                    frg.refreshList()
                    break
                }
            }

            Handler().postDelayed({
                activity?.onBackPressed()
            }, 3000)
        })

//        etHospitalName.onItemClickListener =
//            AdapterView.OnItemClickListener { adapterView, view, i, l ->
//
//                var item : Institute = adapterView.getItemAtPosition(i) as Institute
//
//                KeyboardUtils.hideKeyboard(getAppActivity())
//                isSelected = true
//
//                viewModel.instituteId = item.id
//
//
//            }
//        etHospitalName.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                if (s?.length ?: 0 == 0) {
//                    rvInstitute.visibility = View.GONE
//                } else {
//                    if (!isSelected) {
//                        viewModel.searchInstitute(s.toString())
//                    } else {
//                        isSelected = false
//                    }
//                }
//            }
//
//        })
//
//        viewModel.instituteLiveData.observe(this) {
//            when (it) {
//                is Resource.Success -> {
//                    it.data.institute?.let { it1 ->
//                        adapter.setInstituteList(it1)
//                        rvInstitute.visibility = View.VISIBLE
//                        if (it1.size == 0) {
//                            it1.add(Institute(name = resources.getString(R.string.label_no_result_found)))
//                        } else {
//                            scrollViewHealth.smoothScrollTo(0, etHospitalName.bottom)
//                        }
//                    }
//                }
//                is Resource.Error -> {
//                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
//                }
//                is Resource.Loading -> {
//                }
//            }
//        }

        etHospitalName.setOnClickListener{
            addFragmentInStack<Any>(AppFragmentState.F_UPLOAD_CLINIC)

        }
    }

    private fun addResut() {
        resultList.add("Negative")
        resultList.add("Positive")
        resultList.add("NA")
        resultList.add("Invalid")


        val adapter = ArrayAdapter(
            getAppActivity(),
            R.layout.my_spinner_row, resultList
        )

        // set adapter to the autocomplete tv to the arrayAdapter
        adapter.setDropDownViewResource(R.layout.my_spinner_row)

        sStatus.adapter = TestResultSpinnerAdapter(
            getAppActivity(), resultList
        )

        sStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                KeyboardUtils.hideKeyboard(view)
                if (!isFirstTime)
                    viewModel.result = resultList[pos - 1]
                else {
                    isFirstTime = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }
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

    private fun updateDate() {



        if (isDob) {
//            sDob.text = apiSdf.format(myCalendar.time)
//            viewModel.dob = apiSdf.format(myCalendar.time).toString()
//            isDob = false

        } else {
            sDate.setText(DateUtils.formatDateTime(
                myCalendar.timeInMillis,
                DDMMYY
            ))

            val dayOfWeek = SimpleDateFormat("EEEE", Locale.US).format(myCalendar.time)
            sDay.setText(dayOfWeek)

            viewModel.day = dayOfWeek
            viewModel.date = DateUtils.formatDateTime(myCalendar.timeInMillis,true)
        }
    }

    override fun pageVisible() {

    }

    @SuppressLint("StringFormatInvalid")
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


    fun showType(
        data: TestType,
    ) {
        val builder = AlertDialog.Builder(activity)
        val dialog: AlertDialog = builder.create()

        val wlp: WindowManager.LayoutParams = dialog.window!!.attributes
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        dialog.window!!.attributes = wlp
        dialog.setCancelable(true)
        val inflater = requireActivity().layoutInflater
        val dialogLayout = inflater.inflate(R.layout.popup_test_type, null)
        val list = dialogLayout.findViewById<RecyclerView>(R.id.rv_list)
        val search = dialogLayout.findViewById<EditText>(R.id.search)
        var layoutManager = LinearLayoutManager(activity)
        lateinit var mListner: TestTypeAdapter.OnItemClickListener
        mListner = object :
            TestTypeAdapter.OnItemClickListener {
            @SuppressLint("SimpleDateFormat")
            override fun onItemClick(item: TestTypeResult) {
                sTestType.text = item.name
                typeType = item.id.toString()
                viewModel.type = typeType
// 80 is id of rapid kit
                if (!typeType.equals("80")) {//rapid
                    test_kit.visibility = View.GONE
                    sTestKit.visibility = View.GONE
                    viewModel.typeKitId = "-1"
                } else {
                    test_kit.visibility = View.VISIBLE
                    sTestKit.visibility = View.VISIBLE
                    viewModel.typeKitId = ""
                }
                dialog.dismiss()
            }
        }
        var adapter = TestTypeAdapter(activity, mListner)



        Collections.sort(data.tests!!, Comparator<TestTypeResult?> { obj1, obj2 ->
            return@Comparator obj1!!.name!!.lowercase().compareTo(obj2!!.name!!.lowercase())
        })
        adapter.addAll(data.tests!!)
        list.layoutManager = layoutManager
        list.adapter = adapter

        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(
                s: CharSequence?, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

                adapter?.filter?.filter(s.toString())
            }
        })

        builder.setView(dialogLayout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.setView(dialogLayout)
        dialog.show()
    }

    fun showKit(
        data: TestKit,
    ) {
        val builder = AlertDialog.Builder(activity)
        val dialog: AlertDialog = builder.create()

        val wlp: WindowManager.LayoutParams = dialog.window!!.attributes
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        dialog.window!!.attributes = wlp
        dialog.setCancelable(true)
        val inflater = requireActivity().layoutInflater
        val dialogLayout = inflater.inflate(R.layout.popup_test_type, null)
        val list = dialogLayout.findViewById<RecyclerView>(R.id.rv_list)
        val search = dialogLayout.findViewById<EditText>(R.id.search)
        var layoutManager = LinearLayoutManager(activity)
        lateinit var mListner: TestKitAdapter.OnItemClickListener
        mListner = object :
            TestKitAdapter.OnItemClickListener {
            @SuppressLint("SimpleDateFormat")
            override fun onItemClick(item: TestKitResult) {
                sTestKit.text = item.name
                typeKitId = item.id.toString()
                viewModel.typeKitId = typeKitId
                dialog.dismiss()
            }
        }
        var adapter = TestKitAdapter(activity, mListner)

        Collections.sort(data.tests!!, Comparator<TestKitResult> { obj1, obj2 ->
            return@Comparator obj1!!.name!!.lowercase().compareTo(obj2!!.name!!.lowercase())
        })
        adapter.addAll(data.tests!!)
        list.layoutManager = layoutManager
        list.adapter = adapter

        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(
                s: CharSequence?, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

                adapter?.filter?.filter(s.toString())
            }
        })

        builder.setView(dialogLayout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.setView(dialogLayout)
        dialog.show()
    }

    fun setID(institute: Institute){
        KeyboardUtils.hideKeyboard(getAppActivity())
        etHospitalName.setText(institute.name)
        viewModel.instituteId = institute.id
        if(institute.id!! == 0)
            viewModel.instituteName=institute.name
        rvInstitute.visibility = View.GONE
    }

}