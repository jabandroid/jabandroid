package com.global.vtg.appview.home.clinic

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.authentication.registration.RegistrationStep3ViewModel
import com.global.vtg.base.AppFragment
import com.global.vtg.base.fragment.popFragment
import com.global.vtg.utils.AppAlertDialog
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.global.vtg.utils.ToastUtils
import com.vtg.R
import com.vtg.databinding.FragmentClinicCardBinding
import com.vtg.databinding.FragmentRegStep3Binding
import kotlinx.android.synthetic.main.fragment_clinic_card.*
import kotlinx.android.synthetic.main.fragment_clinic_card.ivBack

import org.koin.androidx.viewmodel.ext.android.viewModel

import java.util.*


class ClinicCardFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentClinicCardBinding
    private val viewModel by viewModel<RegistrationStep3ViewModel>()
    var bitmap: Bitmap? = null


    override fun getLayoutId(): Int {
        return R.layout.fragment_clinic_card
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentClinicCardBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }


    override fun initializeComponent(view: View?) {
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        val list = Constants.USER?.extras

        if (list != null && list.isNotEmpty()) {
            for (extra in list) {
                when {
                    extra?.K.equals("buisnessName") -> {
                        tvName.text = extra?.V
                    }


                    extra?.K.equals("website") -> {
                        tvweb.text = extra?.V
                    }


                    extra?.K.equals("cPhone") -> {
                        tvPhone.text = extra?.V

                    }
                    extra?.K.equals("cEmail") -> {
                        tvEmail.text = extra?.V

                    }

                }
            }
        }else{
            AppAlertDialog().showAlert(
                activity!!,
                object : AppAlertDialog.GetClick {
                    override fun response(type: String) {
                        popFragment(1)
                    }
                }, getString(R.string.clinic_not_found), "Ok", ""
            )
        }




        if (!Constants.USER?.address.isNullOrEmpty()) {
            val index = Constants.USER?.address!!.size - 1
            val string=Constants.USER?.address?.get(index)?.addr1 +" "+ Constants.USER?.address?.get(index)?.addr2+" "+

                    Constants.USER?.address?.get(index)?.city+ " "+ Constants.USER?.address?.get(index)?.state+" "+
                    Constants.USER?.address?.get(index)?.country+ " "+ Constants.USER?.address?.get(index)?.zipCode
            tvAddress.text=string
        }

    }

    override fun pageVisible() {

    }
}