package com.global.vtg.appview.home.parentchild

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.appview.home.profile.ProfileViewModel
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.*
import com.global.vtg.utils.Constants.USER
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
    private var pos: Int = 0
    private var barcodeId: String = ""
    private var id: String = ""
    lateinit var adapter: SharedParentListAdapter

    private var positionDelete: Int = -1
    override fun getLayoutId(): Int {
        return R.layout.fragment_child_profile
    }

    override fun preDataBinding(arguments: Bundle?) {
        profile = arguments!!.get("profilePic").toString()
        dob = arguments.get("dob").toString()
        name = arguments.get("name").toString()
        barcodeId = arguments.get("barcodeId").toString()
        id = arguments.get("id").toString()
        pos = arguments.get("pos") as Int
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

        if (!TextUtils.isEmpty(profile))
            ivProfilePic.setGlideNormalImage(profile)

        tvParentIdValue.text = USER!!.email
        tvUserName.text = name

        tvQrCode.setOnClickListener {
            val b = Bundle()
            b.putString("name", name)
            b.putString("barcodeId", barcodeId)
            b.putString("profile", profile)
            addFragmentInStack<Any>(AppFragmentState.F_VACCINE_QR_CODE, b)
        }

        rvChildList.layoutManager = LinearLayoutManager(context)

        adapter =
            SharedParentListAdapter(getAppActivity(), object : SharedParentListAdapter.onItemClick {
                override fun response(item: ParentList, v: View, position: Int) {

                    AppAlertDialog().showAlert(
                        activity!!,
                        object : AppAlertDialog.GetClick {
                            override fun response(type: String) {
                                positionDelete = position
                                if (NetworkUtils().isNetworkAvailable(activity!!))
                                    viewModel.deleteUSer(
                                        id,
                                        item.ParentID.toString()
                                    )
                                else
                                    ToastUtils.shortToast(
                                        0,
                                        getString(R.string.error_message_network)
                                    )

                            }
                        },
                        getString(R.string.remove_parent),
                        getString(R.string.yes),
                        getString(R.string.no)

                    )
                }

            })

        if (Constants.USER!!.childAccount?.get(pos)!!.sharedAccount != null) {
            if (Constants.USER!!.childAccount?.get(pos)!!.sharedAccount!!.size > 0) {
                var k=ArrayList<ParentList>()

                    for (i in 0 until Constants.USER!!.childAccount?.get(pos)!!.sharedAccount!!.size) {
                        if (Constants.USER!!.childAccount?.get(pos)!!.sharedAccount?.get(i)!!.ParentID!!.toInt() != Constants.USER!!.id) {
                          k.add(Constants.USER!!.childAccount?.get(pos)!!.sharedAccount?.get(i)!!)
                        }
                    }
                tvShared.visibility = View.VISIBLE
                k.let { adapter.setList(it) }
            }
        }

        rvChildList.adapter = adapter

        viewModel.deleteUser.observe(this, {
            when (it) {
                is Resource.Success -> {

                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }

                    if(positionDelete!=-1) {
                        val fragments = getAppActivity().supportFragmentManager.fragments
                        for (frg in fragments) {
                            if (frg is ChildListFragment) {
                                frg.refreshList()
                                break
                            }
                        }
                        adapter.remove(positionDelete)
                        positionDelete=-1
                    }


                }
                is Resource.Error -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 ->
                        DialogUtils.showSnackBar(context, it1)
                    }
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

    override fun onResume() {
        super.onResume()

    }
}