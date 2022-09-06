package com.global.vtg.appview.home.uploaddocument


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.global.vtg.appview.config.Institute
import com.global.vtg.appview.home.health.UploadHealthDocumentFragment
import com.global.vtg.appview.home.testHistory.UploadTestDocumentFragment
import com.global.vtg.appview.home.vaccinehistory.VaccineHistoryFragment
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.AppAlertDialog
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.KeyboardUtils
import com.vtg.R
import com.vtg.databinding.FragmentUploadClinicListBinding
import com.vtg.databinding.FragmentUploadDocumentBinding
import kotlinx.android.synthetic.main.fragment_arrival.*
import kotlinx.android.synthetic.main.fragment_upload_clinic_list.*
import kotlinx.android.synthetic.main.fragment_upload_clinic_list.ivBack
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class ClinicListFragment : AppFragment(), InstituteAdapter.ClickListener {
    private var isSelected: Boolean = false
    var isFirstTime: Boolean = true
    private lateinit var mFragmentBinding: FragmentUploadClinicListBinding
    private val viewModel by viewModel<UploadDocumentViewModel>()
    private val myCalendar: Calendar = Calendar.getInstance()
    private val currentCalendar: Calendar = Calendar.getInstance()
    val doseList: MutableList<String> = ArrayList()
    val vaccineTypeList: ArrayList<String> = ArrayList()
    var values = ArrayList<Institute>()
    lateinit var   instituteAdapter: InstituteAutoCompleteAdapter
    override fun getLayoutId(): Int {
        return R.layout.fragment_upload_clinic_list
    }

    override fun preDataBinding(arguments: Bundle?) {
        getAppActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentUploadClinicListBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
         instituteAdapter =
            InstituteAutoCompleteAdapter(activity, R.layout.recycler_view_institute, values)

        val layoutManager = LinearLayoutManager(getAppActivity())
        rvInstitute.layoutManager = layoutManager
        val adapter = InstituteAdapter(getAppActivity())
        adapter.setListener(this)
        rvInstitute.addItemDecoration(
            DividerItemDecoration(context, layoutManager.orientation)
        )
        rvInstitute.adapter = adapter




        // Handle Error
        viewModel.showToastError.observe(this) {
            DialogUtils.showSnackBar(context, it)
        }

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                viewModel.getDataFromBarcodeId(data!!.getStringExtra("code")!!)
            }
        }




        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        etName.setOnClickListener {
            KeyboardUtils.hideKeyboard(activity!!)

            AppAlertDialog().addClinicName(

                activity!! as AppCompatActivity,
                object : AppAlertDialog.GetClick {
                    override fun response(type: String) {
                        val fragments = getAppActivity().supportFragmentManager.fragments
                        for (frg in fragments) {
                            if (frg is UploadDocumentFragment) {
                                var i=Institute("","","","","","",
                                    type, 0,"","","","")
                                frg.setID(i)
                                break
                            }
                            if (frg is UploadHealthDocumentFragment) {
                                var i=Institute("","","","","","",
                                    type, 0,"","","","")
                                frg.setID(i)
                                break
                            }
                            if (frg is UploadTestDocumentFragment) {
                                var i=Institute("","","","","","",
                                    type, 0,"","","","")
                                frg.setID(i)
                                break
                            }
                        }

                        activity?.onBackPressed()
                    }
                }

            )
          //  addFragmentInStack<Any>(AppFragmentState.F_ADD_CLINIC)
        }



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

        viewModel.instituteLiveData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    it.data.institute?.let { it1 ->
                        adapter.setInstituteList(it1)
                        rvInstitute.visibility = View.VISIBLE
                        etName.visibility = View.VISIBLE
                        if (it1.size == 0) {
                            etName.visibility = View.VISIBLE
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
        }


    }


    override fun pageVisible() {

    }


    override fun onItemClick(institute: Institute) {
        KeyboardUtils.hideKeyboard(getAppActivity())
        isSelected = true
//        etHospitalName.setText(institute.name)
//        viewModel.instituteId = institute.id
//        rvInstitute.visibility = View.GONE

        val fragments = getAppActivity().supportFragmentManager.fragments
        for (frg in fragments) {
            if (frg is UploadDocumentFragment) {
                frg.setID(institute)
                break
            }
            if (frg is UploadHealthDocumentFragment) {
                frg.setID(institute)
                break
            }
            if (frg is UploadTestDocumentFragment) {
                frg.setID(institute)
                break
            }
        }

        activity?.onBackPressed()
    }
}