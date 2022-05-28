package com.global.vtg.appview.home.parentchild


import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
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
import com.vtg.databinding.FragmentSetPasswordBinding
import kotlinx.android.synthetic.main.fragment_birthday_verification.*
import kotlinx.android.synthetic.main.fragment_birthday_verification.btnNext
import kotlinx.android.synthetic.main.fragment_child_list.*
import kotlinx.android.synthetic.main.fragment_child_list.ivBack
import kotlinx.android.synthetic.main.fragment_set_password.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.Comparator


class SetPasswordFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentSetPasswordBinding
    private val viewModel by viewModel<ChildRegistrationModel>()
    lateinit var adapter: ChildListAdapter

    override fun getLayoutId(): Int {
        return R.layout.fragment_set_password
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentSetPasswordBinding
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


        password_1.setTextColor(ContextCompat.getColor(activity!!,R.color.green))
        password_2.setTextColor(ContextCompat.getColor(activity!!,R.color.green))


    }


    override fun pageVisible() {

    }




}