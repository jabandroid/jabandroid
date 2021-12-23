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






class TestHistoryFragment : AppFragment() {

    private lateinit var mFragmentBinding: FragmentTestHistoryBinding
    private val viewModel by viewModel<TestViewModel>()


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
        if(!TextUtils.isEmpty(Constants.USER?.firstName!!)&&!TextUtils.isEmpty(Constants.USER?.lastName!!))
            tvName.text = Constants.USER?.firstName!!.replace("null","") + " " + Constants.USER?.lastName!!.replace("null","")
        if (!Constants.USER?.dateOfBirth.isNullOrEmpty()) {
            val dob = SimpleDateFormat(DateUtils.API_DATE_FORMAT_VACCINE, Locale.getDefault())
            val date = dob.parse(Constants.USER?.dateOfBirth)
            val cal = Calendar.getInstance()
            cal.time = date
            val sdf = SimpleDateFormat(DateUtils.DDMMYYYY, Locale.US)
            val date1 = sdf.format(cal.time)
            tvDob.text ="DOB: "+ date1
        }

        countDown.start(995550000) // Millisecond

        for (time in 0..999) {
            countDown.updateShow(time.toLong())
        }

        if (!Constants.USER?.profileUrl.isNullOrEmpty())
            ivProfilePic.setGlideNormalImage(Constants.USER?.profileUrl)
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun pageVisible() {

    }

}