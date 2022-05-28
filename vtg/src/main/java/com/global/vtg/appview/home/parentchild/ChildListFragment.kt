package com.global.vtg.appview.home.parentchild


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.*
import com.vtg.R
import com.vtg.databinding.FragmentChildListBinding
import kotlinx.android.synthetic.main.fragment_child_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.Comparator


class ChildListFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentChildListBinding
    private val viewModel by viewModel<ChildListViewModel>()
    lateinit var adapter: ChildListAdapter

    override fun getLayoutId(): Int {
        return R.layout.fragment_child_list
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentChildListBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    @SuppressLint("SetTextI18n")
    override fun initializeComponent(view: View?) {
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }



        iv_add.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(Constants.BUNDLE_CHILD_ACCOUNT,true)
            addFragmentInStack<Any>(AppFragmentState.F_OTP, keys = bundle)

//            val bundle = Bundle()
//            bundle.putBoolean(Constants.BUNDLE_CHILD_ACCOUNT, true)
//            addFragmentInStack<Any>(AppFragmentState.F_CHILD_BIRTH, keys = bundle)

        }


        rvVaccineList.layoutManager = LinearLayoutManager(context)
        adapter = ChildListAdapter(getAppActivity(), object : ChildListAdapter.onItemClick {
            override fun response(item: ResUser, v: View, position: Int) {


                SharedPreferenceUtil.getInstance(getAppActivity())
                    ?.saveData(
                        PreferenceManager.KEY_USER_NAME,
                        Constants.USERMain!!.childAccount?.get(position)!!.email.toString()
                    )

                SharedPreferenceUtil.getInstance(getAppActivity())
                    ?.saveData(
                        PreferenceManager.KEY_IS_CHILD,
                        true
                    )

                SharedPreferenceUtil.getInstance(getAppActivity())
                    ?.saveData(
                        PreferenceManager.KEY_PASSWORD,
                        Constants.USERMain!!.childAccount?.get(position)!!.password.toString()
                    )

                val intent = Intent(getAppActivity(), HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK


                startActivity(intent)

            }

        })
        rvVaccineList.adapter = adapter



        viewModel.uploadFile.observe(this, {
            addFragmentInStack<Any>(AppFragmentState.F_UPLOAD_DOCUMENT)
        })
        if (NetworkUtils().isNetworkAvailable(activity!!))
            viewModel.getUser()
        else
            ToastUtils.shortToast(0, getString(R.string.error_message_network))

        viewModel.userConfigLiveData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }

                    if (it.data.childAccount!!.size > 0) {
                        adapter.setList(it.data.childAccount!!)
                        layoutNoData.visibility = View.GONE
                    } else {
                        val bundle = Bundle()
                        bundle.putBoolean(Constants.BUNDLE_CHILD_ACCOUNT, true)
                        addFragmentInStack<Any>(AppFragmentState.F_OTP, keys = bundle)
                    }


                }
                is Resource.Error -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).showProgressBar()
                        is HomeActivity -> (activity as HomeActivity).showProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
                        else -> (activity as VendorActivity).showProgressBar()
                    }
                }
            }
        })

    }


    override fun pageVisible() {

    }

    fun refreshList() {
        if (NetworkUtils().isNetworkAvailable(activity!!))
            viewModel.getUser()
        else
            ToastUtils.shortToast(0, getString(R.string.error_message_network))
    }




}