package com.global.vtg.appview.home.parentchild

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.home.profile.ProfileViewModel
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.utils.Constants
import com.global.vtg.utils.Constants.USER
import com.global.vtg.utils.DateUtils
import com.vtg.R
import com.vtg.databinding.FragmentChildProfileBinding
import kotlinx.android.synthetic.main.fragment_child_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChildProfileFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentChildProfileBinding
    private val viewModel by viewModel<ProfileViewModel>()
    private var profile: String = ""
    private var dob: String = ""
    private var name: String = ""
    private var barcodeId: String = ""
    override fun getLayoutId(): Int {
        return R.layout.fragment_child_profile
    }

    override fun preDataBinding(arguments: Bundle?) {
        profile=arguments!!.get("profilePic").toString()
        dob= arguments.get("dob").toString()
        name= arguments.get("name").toString()
        barcodeId= arguments.get("barcodeId").toString()
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentChildProfileBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    @SuppressLint("SetTextI18n")
    override fun initializeComponent(view: View?) {
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        if (dob.isNotEmpty()) {
            try {
                val date = DateUtils.getDateLocal(
                    dob,
                    DateUtils.API_DATE_FORMAT
                )
                tvDobValue.text = DateUtils.formatDateTime(date.time, DateUtils.API_DATE_FORMAT)

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        if(!TextUtils.isEmpty(profile))
            ivProfilePic.setGlideNormalImage(profile)

        tvParentIdValue.text=USER!!.email
        tvUserName.text=name

        tvQrCode.setOnClickListener{
            val b = Bundle()
            b.putString("name", name)
            b.putString("barcodeId", barcodeId)
            b.putString("profile", profile)
            addFragmentInStack<Any>(AppFragmentState.F_VACCINE_QR_CODE,b)
        }


    }

    override fun pageVisible() {

    }

    override fun onResume() {
        super.onResume()

    }
}