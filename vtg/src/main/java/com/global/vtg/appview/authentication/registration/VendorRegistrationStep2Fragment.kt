package com.global.vtg.appview.authentication.registration

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.config.PickMediaExtensions
import com.global.vtg.appview.config.getRealPath
import com.global.vtg.appview.config.getRealPathFromURI
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.vtg.R
import com.vtg.databinding.FragmentRegVendorStep2Binding

import kotlinx.android.synthetic.main.fragment_reg_step2.ivBack
import kotlinx.android.synthetic.main.fragment_reg_vendor_step2.*
import kotlinx.android.synthetic.main.fragment_reg_vendor_step2.etId
import kotlinx.android.synthetic.main.fragment_upload_document.*
import kotlinx.android.synthetic.main.fragment_upload_document.cvUploadDocument
import kotlinx.android.synthetic.main.fragment_upload_document.ivCancel
import kotlinx.android.synthetic.main.fragment_upload_document.ivUploadDocument
import kotlinx.android.synthetic.main.fragment_upload_document.tvDocName
import kotlinx.android.synthetic.main.fragment_upload_document.tvSelectDoc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class VendorRegistrationStep2Fragment : AppFragment() {
    private val viewModel by viewModel<VendorRegistrationStep2ViewModel>()
    private lateinit var mFragmentBinding: FragmentRegVendorStep2Binding
    var id: Int? = null
    var isFromProfile = false

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
        }
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        val list = Constants.USER?.extras
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
                    }
                    extra?.K.equals("employeeId") -> {
                        etEmployeeId.isClickable = false
                        etEmployeeId.isEnabled = false
                        viewModel.employeeId.postValue(extra?.V)
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
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).showProgressBar()
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
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    val bundle = Bundle()
                    bundle.putBoolean(Constants.BUNDLE_FROM_PROFILE, isFromProfile)
                    addFragmentInStack<Any>(AppFragmentState.F_REG_STEP3, bundle)
                }
                is Resource.Error -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).showProgressBar()
                        else -> (activity as VendorActivity).showProgressBar()
                    }
                }
            }
        })

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
}