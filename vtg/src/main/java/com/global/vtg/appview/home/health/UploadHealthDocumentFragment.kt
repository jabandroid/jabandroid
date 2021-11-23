package com.global.vtg.appview.home.health

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.global.vtg.appview.config.Institute
import com.global.vtg.appview.config.PickMediaExtensions
import com.global.vtg.appview.config.getRealPath
import com.global.vtg.appview.config.getRealPathFromURI
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.uploaddocument.InstituteAdapter
import com.global.vtg.base.AppFragment
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.global.vtg.utils.DateUtils.DDMMYY
import com.global.vtg.utils.DateUtils.appendZero
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.KeyboardUtils
import com.vtg.R
import com.vtg.databinding.FragmentHealthInfoUploadDocumentBinding
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.*
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.clForm
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.clThankYou
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.cvUploadDocument
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.etFee
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.etHospitalName
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.ivBack
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.ivCancel
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.ivUploadDocument
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.rvInstitute
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.sDate
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.sDay
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.sTime
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.tvDocName
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.tvFee
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.tvSelectDoc
import kotlinx.android.synthetic.main.fragment_upload_document.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class UploadHealthDocumentFragment : AppFragment(), InstituteAdapter.ClickListener {
    private var isSelected: Boolean = false
    var isFirstTime: Boolean = true
    private lateinit var mFragmentBinding: FragmentHealthInfoUploadDocumentBinding
    private val viewModel by viewModel<UploadHealthDocumentViewModel>()
    private val myCalendar: Calendar = Calendar.getInstance()
    private val currentCalendar: Calendar = Calendar.getInstance()

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

    override fun initializeComponent(view: View?) {
        if (Constants.USER?.role.equals("ROLE_CLINIC")) {
            groupMobileNoHealth.visibility = View.VISIBLE
        } else {
            groupMobileNoHealth.visibility = View.GONE
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

        viewModel.chooseFile.observe(this, {
            PickMediaExtensions.instance.pickFromStorage(getAppActivity()) { resultCode: Int, path: String, displayName: String? ->
                resultMessage(resultCode, path, displayName)
            }
        })

        viewModel.userLiveData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    (activity as HomeActivity).hideProgressBar()
                    Constants.USER = it.data
                    viewModel.saveSuccess.postValue(true)
                }
                is Resource.Error -> {
                    (activity as HomeActivity).hideProgressBar()
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    (activity as HomeActivity).showProgressBar()
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
                if (frg is HealthInformationFragment) {
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
                if (!isSelected) {
                    viewModel.searchInstitute(s.toString())
                } else {
                    isSelected = false
                }
                if (s?.length ?: 0 == 0) {
                    rvInstitute.visibility = View.GONE
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
        sDate.setText(date)

        val dayOfWeek = SimpleDateFormat("EEEE", Locale.US).format(myCalendar.time)
        sDay.setText(dayOfWeek)

        viewModel.day = dayOfWeek
        viewModel.date = apiSdf.format(myCalendar.time)
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
}