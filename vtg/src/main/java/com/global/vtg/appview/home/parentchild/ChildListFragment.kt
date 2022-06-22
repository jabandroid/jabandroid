package com.global.vtg.appview.home.parentchild


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ItemTouchHelper
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
import com.google.gson.JsonObject
import com.vtg.R
import com.vtg.databinding.FragmentChildListBinding
import kotlinx.android.synthetic.main.fragment_child_list.*
import kotlinx.android.synthetic.main.fragment_child_list.ivBack
import kotlinx.android.synthetic.main.fragment_contactlist.*
import kotlinx.android.synthetic.main.fragment_health_info_upload_document.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.Comparator


class ChildListFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentChildListBinding
    private val viewModel by viewModel<ChildListViewModel>()
    lateinit var adapter: ChildListAdapter
    private var positionDelete: Int = 0
    private var isChildController: Boolean = false
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
        add_child_controller.visibility = View.GONE
        main_controller.visibility = View.VISIBLE
        ivBack.setOnClickListener {

            if (isChildController) {
                add_child_controller.visibility = View.GONE
                main_controller.visibility = View.VISIBLE
                iv_add.visibility = View.VISIBLE
                isChildController = false
            } else
                activity?.onBackPressed()
        }


        var resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    viewModel.getDataFromBarcodeId(data!!.getStringExtra("code")!!)
                }
            }


        scan.setOnClickListener {
            val intent = Intent(Intent(activity, QrcodeScanner::class.java))
            intent.putExtra("child", true)
            resultLauncher.launch(intent)
        }

        add_child.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(Constants.BUNDLE_CHILD_ACCOUNT, true)
            addFragmentInStack<Any>(AppFragmentState.F_OTP, keys = bundle)
        }
        iv_add.setOnClickListener {
            add_child_controller.visibility = View.VISIBLE
            main_controller.visibility = View.GONE
            iv_add.visibility = View.GONE
            isChildController = true

//            val bundle = Bundle()
//            bundle.putBoolean(Constants.BUNDLE_CHILD_ACCOUNT, true)
//            addFragmentInStack<Any>(AppFragmentState.F_CHILD_BIRTH, keys = bundle)

        }

        rvVaccineList.layoutManager = LinearLayoutManager(context)
        adapter = ChildListAdapter(getAppActivity(), object : ChildListAdapter.onItemClick {
            override fun response(item: ResUser, v: View, position: Int) {

                val b = Bundle()
                b.putString("profilePic", item.profileUrl)
                b.putString("dob", item.dateOfBirth)
                b.putString("name", item.firstName+" "+item.lastName)
                b.putString("barcodeId", item.barcodeId)
                addFragmentInStack<Any>(AppFragmentState.F_PROFILE_CHILD, b)
//
//                SharedPreferenceUtil.getInstance(getAppActivity())
//                    ?.saveData(
//                        PreferenceManager.KEY_USER_NAME,
//                        Constants.USERMain!!.childAccount?.get(position)!!.email.toString()
//                    )
//
//                SharedPreferenceUtil.getInstance(getAppActivity())
//                    ?.saveData(
//                        PreferenceManager.KEY_IS_CHILD,
//                        true
//                    )
//
//                SharedPreferenceUtil.getInstance(getAppActivity())
//                    ?.saveData(
//                        PreferenceManager.KEY_PASSWORD,
//                        Constants.USERMain!!.childAccount?.get(position)!!.password.toString()
//                    )
//
//                val intent = Intent(getAppActivity(), HomeActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(intent)

            }

        })
        rvVaccineList.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(rvVaccineList) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                var buttons = listOf<UnderlayButton>()
                val deleteButton = deleteButton(position)

                when (position) {
                    0 -> buttons = listOf(deleteButton)
                    else -> buttons = listOf(deleteButton)
                }
                return buttons
            }
        })
        itemTouchHelper.attachToRecyclerView(rvVaccineList)


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
                        add_child_controller.visibility = View.VISIBLE
                        main_controller.visibility = View.GONE
                        iv_add.visibility = View.GONE
                        isChildController = true


//                        AppAlertDialog().addChild(
//                            activity!! as AppCompatActivity,
//                            object : AppAlertDialog.GetClick {
//                                override fun response(type: String) {
//
//                                    if (type.equals("add")) {
//                                        val bundle = Bundle()
//                                        bundle.putBoolean(Constants.BUNDLE_CHILD_ACCOUNT, true)
//                                        addFragmentInStack<Any>(AppFragmentState.F_OTP, keys = bundle)
//                                    } else {
//                                        val intent = Intent(Intent(activity, QrcodeScanner::class.java))
//                                        intent.putExtra("child", true)
//                                        resultLauncher.launch(intent)
//                                    }
//
//                                }
//                            }
//
//                        )
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

        viewModel.deleteUser.observe(this, {
            when (it) {
                is Resource.Success -> {

                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    adapter.remove(positionDelete)
                    if (adapter.getSize() == 0) {
                        AppAlertDialog().addChild(
                            activity!! as AppCompatActivity,
                            object : AppAlertDialog.GetClick {
                                override fun response(type: String) {

                                    if (type.equals("add")) {
                                        val bundle = Bundle()
                                        bundle.putBoolean(Constants.BUNDLE_CHILD_ACCOUNT, true)
                                        addFragmentInStack<Any>(
                                            AppFragmentState.F_OTP,
                                            keys = bundle
                                        )
                                    } else {
                                        val intent =
                                            Intent(Intent(activity, QrcodeScanner::class.java))
                                        intent.putExtra("child", true)
                                        resultLauncher.launch(intent)
                                    }

                                }
                            }

                        )
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

        viewModel.scanBarcodeLiveData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                    }


                    viewModel.addParent(it.data.id.toString(), Constants.USER!!.id.toString())
                }
                is Resource.Error -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).showProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
                    }
                }
            }
        })


        viewModel.addParentLiveData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                    }

                    if (it.data.message!!.lowercase().equals("success")) {
                        if (NetworkUtils().isNetworkAvailable(activity!!))
                            viewModel.getUser()
                        else
                            ToastUtils.shortToast(0, getString(R.string.error_message_network))
                    } else if (it.data.message!!.lowercase() == "duplicate") {
                        ToastUtils.shortToast(0, getString(R.string.alrady_child))
                    } else {
                        ToastUtils.shortToast(0, getString(R.string.error_message_somethingwrong))
                    }
                }
                is Resource.Error -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).showProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
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

    private fun deleteButton(position: Int): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            activity!!,
            "Delete",
            14.0f,
            android.R.color.holo_red_light,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    AppAlertDialog().showAlert(
                        activity!!,
                        object : AppAlertDialog.GetClick {
                            override fun response(type: String) {
                                positionDelete = position
                                if (NetworkUtils().isNetworkAvailable(activity!!))
                                    viewModel.deleteUSer(
                                        adapter.getID(position)!!,
                                        Constants.USER!!.id.toString()
                                    )
                                else
                                    ToastUtils.shortToast(
                                        0,
                                        getString(R.string.error_message_network)
                                    )

                            }
                        },
                        getString(R.string.delete_child),
                        getString(R.string.yes),
                        getString(R.string.no)

                    )
                }
            })
    }

}