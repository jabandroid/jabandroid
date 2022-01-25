package com.global.vtg.appview.home.testHistory

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.global.vtg.appview.authentication.registration.TestType
import com.global.vtg.appview.authentication.registration.TestTypeResult
import com.global.vtg.appview.config.*
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.health.HealthInformationFragment
import com.global.vtg.appview.home.health.TestTypeAdapter
import com.global.vtg.appview.home.uploaddocument.InstituteAdapter
import com.global.vtg.appview.home.uploaddocument.TestResultSpinnerAdapter
import com.global.vtg.appview.home.uploaddocument.VaccineDoseSpinnerAdapter
import com.global.vtg.base.AppFragment
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.global.vtg.utils.DateUtils.DDMMYY
import com.global.vtg.utils.DateUtils.appendZero
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.KeyboardUtils
import com.global.vtg.utils.broadcasts.isNetworkAvailable
import com.vtg.R

import com.vtg.databinding.FragmentTestInfoUploadDocumentBinding
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.*


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

import com.global.vtg.appview.home.uploaddocument.InstituteAutoCompleteAdapter
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.*
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.cbCertify
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.ccpHealth
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.clForm
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.clThankYou
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.cvUploadDocument
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.dob
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.etFee
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.etHospitalName
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.groupMobileNoHealth
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.ivBack
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.ivCancel
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.ivUploadDocument
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.rvInstitute
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.sDate
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.sDay
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.sDob
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.sStatus
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.sTestType
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.sTime
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.s_status
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.scrollViewHealth
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.tvDocName
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.tvFee
import kotlinx.android.synthetic.main.fragment_test_info_upload_document.tvSelectDoc


class UploadTestDocumentFragment : AppFragment(), InstituteAdapter.ClickListener {
    private var isSelected: Boolean = false
    var isFirstTime: Boolean = true
    private lateinit var mFragmentBinding: FragmentTestInfoUploadDocumentBinding
    private val viewModel by viewModel<UploadTestDocumentViewModel>()
    private val myCalendar: Calendar = Calendar.getInstance()
    private val currentCalendar: Calendar = Calendar.getInstance()
    private  var type:TestType=TestType()
    private  var typeType:String=""
    var isDob=false

    val resultList: MutableList<String> = ArrayList()
    override fun getLayoutId(): Int {
        return R.layout.fragment_test_info_upload_document
    }

    override fun preDataBinding(arguments: Bundle?) {
        getAppActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentTestInfoUploadDocumentBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        if (Constants.USER?.role.equals("ROLE_CLINIC")) {
            groupMobileNoHealth.visibility = View.VISIBLE
            cbCertify.visibility = View.GONE
            sStatus.visibility = View.VISIBLE
            s_status.visibility = View.VISIBLE
            sDob.visibility = View.GONE
            dob.visibility = View.GONE
        } else {
            groupMobileNoHealth.visibility = View.GONE
            cbCertify.visibility = View.VISIBLE
            sStatus.visibility = View.GONE
            s_status.visibility = View.GONE

            sDob.visibility = View.GONE
            dob.visibility = View.GONE
        }
        viewModel.code.value = ccpHealth.defaultCountryCode
        viewModel.region = ccpHealth.defaultCountryNameCode
        ccpHealth.setOnCountryChangeListener {
            viewModel.code.value = ccpHealth.selectedCountryCode
            viewModel.region = ccpHealth.selectedCountryNameCode
        }
        val layoutManager = LinearLayoutManager(getAppActivity())
        rvInstitute.layoutManager = layoutManager
        var adapter = InstituteAdapter(getAppActivity())
        adapter.setListener(this)
        rvInstitute.addItemDecoration(
            DividerItemDecoration(context, layoutManager.orientation)
        )
        rvInstitute.adapter = adapter

        viewModel.chooseFile.observe(this, {
            PickMediaExtensions.instance.pickFromStorage(getAppActivity()) { resultCode: Int, path: String, displayName: String? ->
                resultMessage(resultCode, path, displayName)
            }
        })

        sTestType.setOnClickListener {
            var arr=ArrayList<TestTypeResult>()
            var item=TestTypeResult()
            item.id="1"
            item.name="RT-PCR"
            arr.add(item)
             item=TestTypeResult()
            item.id="2"
            item.name="Rapid Antigen"
            arr.add(item)
            type.tests=arr
            showType(type)
        }

//        if (isNetworkAvailable(requireActivity())) {
//            viewModel.testHistory()
//        } else {
//            DialogUtils.showSnackBar(
//                requireActivity(),
//                requireActivity().resources.getString(R.string.no_connection)
//            )
//        }

        viewModel.testData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                    }
                    type=it.data
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
            isDob=true
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
                        val time = "${appendZero(selectedHour.toString())}:${appendZero(selectedMinute.toString())}"
                        sTime.setText(time)
                        viewModel.time = time
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
                if (frg is TestHistoryDetailFragment) {
                    frg.refreshList()

                }
                if (frg is TestHistoryFragment) {
                    frg.refreshList()

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
                            scrollViewHealth.smoothScrollTo(0, etHospitalName.bottom)
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
            getAppActivity(),resultList
        )

        sStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                KeyboardUtils.hideKeyboard(view)
                if (!isFirstTime)
                    viewModel.result = resultList[pos - 1]
                else{
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

        val sdf = SimpleDateFormat(DDMMYY, Locale.US)
        val apiSdf = SimpleDateFormat(DateUtils.API_DATE_FORMAT, Locale.US)
        val date = sdf.format(myCalendar.time)
        if(isDob){
            sDob.text = apiSdf.format(myCalendar.time)
            viewModel.dob= apiSdf.format(myCalendar.time).toString()
            isDob=false

        }else {
            sDate.setText(date)

            val dayOfWeek = SimpleDateFormat("EEEE", Locale.US).format(myCalendar.time)
            sDay.setText(dayOfWeek)

            viewModel.day = dayOfWeek
            viewModel.date = apiSdf.format(myCalendar.time)
        }
    }

    override fun pageVisible() {

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
                sTestType.text=item.name
                typeType=item.id.toString()
                viewModel.type=typeType
                dialog.dismiss()
            }
        }
        var adapter = TestTypeAdapter(activity, mListner)

        Collections.sort(data.tests!!, Comparator<TestTypeResult?> { obj1, obj2 ->
            return@Comparator obj2!!.id!!.compareTo(obj1!!.id!!)
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




}