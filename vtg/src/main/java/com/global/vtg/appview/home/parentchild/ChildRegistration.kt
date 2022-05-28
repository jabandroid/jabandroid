package com.global.vtg.appview.home.parentchild


import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.global.vtg.utils.SharedPreferenceUtil
import com.vtg.R
import com.vtg.databinding.FragmentChildListBinding
import com.vtg.databinding.FragmentChildRegistrationBinding
import kotlinx.android.synthetic.main.fragment_birthday_verification.*
import kotlinx.android.synthetic.main.fragment_child_list.*
import kotlinx.android.synthetic.main.fragment_child_list.ivBack
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.Comparator


class ChildRegistration : AppFragment() {
    private lateinit var mFragmentBinding: FragmentChildRegistrationBinding
    private val viewModel by viewModel<ChildRegistrationModel>()
    lateinit var adapter: ChildListAdapter

    override fun getLayoutId(): Int {
        return R.layout.fragment_child_registration
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentChildRegistrationBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    @SuppressLint("SetTextI18n")
    override fun initializeComponent(view: View?) {
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        btnNext.setOnClickListener{
            addFragmentInStack<Any>(AppFragmentState.F_CHILD_BIRTH)
        }





    }


    override fun pageVisible() {

    }




}