package com.global.vtg.appview.home.testHistory

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppFragment
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.vtg.R
import com.vtg.databinding.FragmentTestHistoryBinding

import kotlinx.android.synthetic.main.fragment_test_history.*

import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class TestHistoryDetailFragment : AppFragment() {

     private lateinit var mFragmentBinding: FragmentTestHistoryBinding
       private val viewModel by viewModel<TestHistoryViewModel>()


       override fun getLayoutId(): Int {
           return R.layout.fragment_test_history
       }

       override fun preDataBinding(arguments: Bundle?) {
           getAppActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
       }

       override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
           mFragmentBinding = binding as FragmentTestHistoryBinding
           mFragmentBinding.viewModel = viewModel
           mFragmentBinding.lifecycleOwner = this
           return mFragmentBinding
       }

       @SuppressLint("SetTextI18n")
       override fun initializeComponent(view: View?) {

//           ivBack.setOnClickListener {
//               activity?.onBackPressed()
//           }
       }

       override fun pageVisible() {

       }

}