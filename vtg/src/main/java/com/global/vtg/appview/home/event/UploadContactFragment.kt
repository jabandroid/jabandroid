package com.global.vtg.appview.home.event


import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.config.PickMediaExtensions
import com.global.vtg.appview.config.getRealPath
import com.global.vtg.appview.config.getRealPathFromURI
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.NetworkUtils
import com.global.vtg.utils.ToastUtils
import com.vtg.R
import com.vtg.databinding.FragmentUploadContactBinding
import kotlinx.android.synthetic.main.fragment_create_event.ivBack
import kotlinx.android.synthetic.main.fragment_upload_contact.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class UploadContactFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentUploadContactBinding
    private val viewModel by viewModel<CreateEventViewModel>()

    companion object {

        lateinit var itemEvent: Event
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_upload_contact
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentUploadContactBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {


        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }


        importIcon.setOnClickListener {
            PickMediaExtensions.instance.PickCsvFile(getAppActivity()) { resultCode: Int, path: String, displayName: String? ->
                resultMessage(resultCode, path, displayName)
            }


        }
    }

    private fun resultMessage(resultCode: Int, path: String, displayName: String?) {
        getAppActivity().getActivityScope(getAppActivity()).launch(Dispatchers.IO) {
            when (resultCode) {
                PickMediaExtensions.PICK_SUCCESS -> {
                    val realPath1 = Uri.parse(path).getRealPathFromURI(getAppActivity())
                    if (realPath1 != null) {
                        if (displayName != null) {
                            if (displayName.contains(".csv")) {

                                updateDocument(displayName, path)
                            } else
                                ToastUtils.shortToast(0, getString(R.string.selectcsvFile))
                        }
                    } else {
                        val realPath = Uri.parse(path).getRealPath(getAppActivity())
                        if (realPath != null) {
                            if (displayName != null) {
                                if (displayName.contains(".csv")) {

                                    updateDocument(displayName, path)
                                } else
                                    ToastUtils.shortToast(0, getString(R.string.selectcsvFile))
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


        viewModel.uplaodAttendees.observe(this, { resources ->
            resources?.let {
                when (it) {
                    is Resource.Success -> {

                        ToastUtils.shortToast(0,"sucess")

                        when (activity) {

                            is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                            is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                            else -> (activity as VendorActivity).hideProgressBar()
                        }
                    }
                    is Resource.Error -> {
                        when (activity) {

                            is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                            is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                            else -> (activity as VendorActivity).hideProgressBar()
                        }
                        it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                    }
                    is Resource.Loading -> {
                        when (activity) {

                            is HomeActivity -> (activity as HomeActivity).showProgressBar()
                            is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
                            else -> (activity as VendorActivity).showProgressBar()
                        }
                    }
                }
            }
        })

    }

    private fun updateDocument(docName: String, path: String) {

        val file: File
        var part: MultipartBody.Part? = null
        file = File(path)
        part = MultipartBody.Part.createFormData(
            "file", file.name,
            file.asRequestBody("image/png".toMediaTypeOrNull())
        )
        val id: RequestBody = CreateEventFragment.itemEvent.eventID!!
            .toRequestBody("text/plain".toMediaTypeOrNull())

        if (NetworkUtils().isNetworkAvailable(activity!!))
            viewModel.uploadAttendees(part, CreateEventFragment.itemEvent.eventID!!)
        else
            ToastUtils.shortToast(0, getString(R.string.error_message_network))



    }

    override fun pageVisible() {

    }


}


