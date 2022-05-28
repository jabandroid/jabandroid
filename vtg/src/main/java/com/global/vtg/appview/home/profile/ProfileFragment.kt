package com.global.vtg.appview.home.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.global.vtg.imageview.setGlideNormalImage
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
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.AppAlertDialog
import com.global.vtg.utils.Constants.BUNDLE_FROM_PROFILE
import com.global.vtg.utils.Constants.USER
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.SharedPreferenceUtil
import com.global.vtg.utils.baseinrerface.OkCancelNeutralDialogInterface
import com.google.gson.JsonObject
import com.vtg.R
import com.vtg.databinding.FragmentProfileBinding
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_vaccine_history.ivBack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class ProfileFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentProfileBinding
    private val viewModel by viewModel<ProfileViewModel>()
    private var pin: String = ""
    override fun getLayoutId(): Int {
        return R.layout.fragment_profile
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentProfileBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    @SuppressLint("SetTextI18n")
    override fun initializeComponent(view: View?) {
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        tvEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(BUNDLE_FROM_PROFILE, true)
            addFragmentInStack<Any>(AppFragmentState.F_REG_STEP1, bundle)
        }

        tvChangePassword.setOnClickListener {
            addFragmentInStack<Any>(AppFragmentState.F_CHANGE_PASSWORD)
        }


        tvChildLabel.setOnClickListener {
            addFragmentInStack<Any>(AppFragmentState.F_CHILD_LIST)
        }

        tvPinUpdate.setOnClickListener {
            AppAlertDialog().updatePin(
                activity!! as AppCompatActivity,
                object : AppAlertDialog.GetClick {
                    override fun response(type: String) {
                        pin = type
                        val j = JsonObject()
                        j.addProperty("userId", USER?.id)
                        j.addProperty("pin", type)
                        viewModel.updatePin(j)

                    }
                }

            )
        }
        if (SharedPreferenceUtil.getInstance(getAppActivity())
                ?.getData(
                    PreferenceManager.KEY_IS_CHILD,
                    false
                ) == true
        ) {
            mobile.visibility=View.GONE
            mobileView.visibility=View.GONE
            email.visibility=View.GONE
            emailView.visibility=View.GONE
            childAccountView.visibility=View.GONE
            childAccount.visibility=View.GONE
        }

        tvUserName.text = USER?.firstName + " " + USER?.lastName
        tvMobileValue.text = USER?.mobileNo
        tvCityValue.text = USER?.birthCity
        tvStateValue.text = USER?.birthState
        tvCountryValue.text = USER?.birthCountry
        tvEmailValue.text = USER?.email
        tvPin.text = USER?.pin
        tvPinUpdate.setTextColor(ContextCompat.getColor(activity!!, R.color.colorPrimary))
        loadAddress()
        viewModel.getUser()
        if (!USER!!.profileUrl.isNullOrEmpty())
            ivProfilePic.setGlideNormalImage(USER!!.profileUrl)
        viewModel.uploadProfilePic.observe(this, {
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
        })

        viewModel.logout.observe(this, {
            SharedPreferenceUtil.INSTANCE?.removeData(PreferenceManager.KEY_USER_LOGGED_IN)
            SharedPreferenceUtil.INSTANCE?.removeData(PreferenceManager.KEY_REFRESH_TOKEN)
            SharedPreferenceUtil.INSTANCE?.removeData(PreferenceManager.KEY_ACCESS_TOKEN)
            SharedPreferenceUtil.INSTANCE?.deleteAllData()
            val intent = Intent(getAppActivity(), AuthenticationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        })

        viewModel.userLiveData.observe(this, { resources ->
            resources?.let {
                when (it) {
                    is Resource.Success -> {
                        USER?.profileUrl = it.data.profileUrl
//                        if (!it.data.profileUrl.isNullOrEmpty())
//                            ivProfilePic.setGlideNormalImage(it.data.profileUrl)
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

        viewModel.updatePin.observe(this, { resources ->
            resources?.let {
                when (it) {
                    is Resource.Success -> {

                        when (activity) {

                            is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                            is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                            else -> (activity as VendorActivity).hideProgressBar()
                        }

                        tvPin.text = pin


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

    fun loadAddress() {
        if (!USER?.address.isNullOrEmpty()) {
            var index = USER?.address!!.size - 1
            tvCityValue.text = USER?.address?.get(index)?.city ?: ""
            tvStateValue.text = USER?.address?.get(index)?.state ?: ""
            tvPostalCodeValue.text = USER?.address?.get(index)?.zipCode ?: ""
            tvCountryValue.text = USER?.address?.get(index)?.country ?: ""

            if (!USER!!.profileUrl.isNullOrEmpty())
                ivProfilePic.setGlideNormalImage(USER!!.profileUrl)
        }
    }

    override fun pageVisible() {
        loadAddress()
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
        viewModel.uploadProfile(docName)
    }

    override fun onResume() {
        super.onResume()

    }
}