package com.global.vtg.appview.home.event


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.*
import com.google.gson.JsonObject
import com.vtg.R
import com.vtg.databinding.FragmentContactlistBinding
import kotlinx.android.synthetic.main.fragment_contactlist.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContactListFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentContactlistBinding

    private val viewModel by viewModel<ContactListViewModel>()
    private var sizeUsers: Int = 0
    private var eventId: String = ""
    private var name: String = ""
    private var nameEvent: String = ""
    private var namePic: String = ""
    private var positionDelete: Int = 0
    var arrUser : ArrayList<EventUser>?=null
    lateinit var contactAdapter:AttendesAdapter
    override fun getLayoutId(): Int {
        return R.layout.fragment_contactlist
    }

    override fun preDataBinding(arguments: Bundle?) {
        eventId = arguments!!.getString(Constants.BUNDLE_ID).toString()
        nameEvent = arguments.getString(Constants.BUNDLE_NAME).toString()
        namePic = arguments.getString(Constants.BUNDLE_PIC).toString()

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentContactlistBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {

        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(rv_contact_list) {
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
        itemTouchHelper.attachToRecyclerView(rv_contact_list)
        rv_contact_list.layoutManager = LinearLayoutManager(activity!!)


        if (checkPermissions()) {
            when (activity) {
                is AuthenticationActivity -> (activity as AuthenticationActivity).showProgressBar()
                is HomeActivity -> (activity as HomeActivity).showProgressBar()
                is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
                else -> (activity as VendorActivity).showProgressBar()
            }
            viewModel.getAllContact(activity!!)
            viewModel.contactLiveDat.observe(this, {
                setUpdata(it)
            })
        } else {
            requestPermissions()
        }

        if (NetworkUtils().isNetworkAvailable(activity!!))
            viewModel.getUsers(eventId)
        else
            ToastUtils.shortToast(0, getString(R.string.error_message_network))


        add.setOnClickListener {
            if(sizeUsers>=5){
                AppAlertDialog().showAlert(
                    activity!!,
                    object : AppAlertDialog.GetClick {
                        override fun response(type: String) {
                        }
                    }
                    ,getString(R.string.purchase_subscription),getString(R.string.subscribe),getString(R.string.cancel)
                )

            }else {
                if (!TextUtils.isEmpty(search.text.toString())) {
                    KeyboardUtils.hideKeyboard(activity!!)
                    val j = JsonObject()

                    var code=search.text.toString()
                    if(android.util.Patterns.PHONE.matcher(code).matches()) {
                        if (code.contains("+"))
                            code = code.replace("+", "")
                        else if (code.length <= 10)
                            code = Constants.USER!!.mobileNo!!.substring(0, 2) + code


                        code=code.replace(Regex("[^0-9]"), "")
                    }
                    j.addProperty("eventId", eventId)
                    j.addProperty("userName", code)
                    j.addProperty("name", name)
                    // j.addProperty("interested","0")
                    if (NetworkUtils().isNetworkAvailable(activity!!))
                        viewModel.addUSer(j)
                    else
                    ToastUtils.shortToast(0, getString(R.string.error_message_network))

                }
            }
        }

        viewModel.addUser.observe(this, {
            when (it) {
                is Resource.Success -> {

                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    search.setText("")
                    if (NetworkUtils().isNetworkAvailable(activity!!))
                        viewModel.getUsers(eventId)
                    else
                        ToastUtils.shortToast(0, getString(R.string.error_message_network))
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

        viewModel.getEventUser.observe(this, {
            when (it) {
                is Resource.Success -> {

                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }

                    sizeUsers=it.data.arrUser!!.size
                    arrUser =ArrayList<EventUser>()
                    arrUser!!.addAll(it.data.arrUser!!)
                    rv_contact_list.layoutManager = LinearLayoutManager(activity!!)
                     contactAdapter = AttendesAdapter(activity!!, it.data.arrUser!!,nameEvent,namePic)
                    rv_contact_list.adapter = contactAdapter
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

        viewModel.deleteUser.observe(this, {
            when (it) {
                is Resource.Success -> {

                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    contactAdapter.remove(positionDelete)

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

        ivImport.setOnClickListener{
            addFragmentInStack<Any>(
                AppFragmentState.F_EVENT_CONTACT_UPLOAD
            )
        }

    }

private fun deleteButton(position: Int) : SwipeHelper.UnderlayButton {
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
                            positionDelete=position
                            if (NetworkUtils().isNetworkAvailable(activity!!))
                                viewModel.deleteUSer(arrUser?.get(positionDelete)!!.id!!)
                            else
                                ToastUtils.shortToast(0, getString(R.string.error_message_network))

                        }
                    }, getString(R.string.delete_user), getString(R.string.yes), getString(R.string.no)

                )
            }
        })
}

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {

        val mPermissionResult = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {

                viewModel.getAllContact(activity!!)
            } else {
                val builder = AlertDialog.Builder(requireActivity())
                builder.setTitle("Alert")
                builder.setMessage(getString(R.string.label_contact_permission))
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

                builder.setPositiveButton("close") { dialog, which ->
                    activity?.onBackPressed()
                }

                builder.setNegativeButton("Settings") { dialog, which ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
                builder.show()
            }
        }
        mPermissionResult.launch(
            Manifest.permission.READ_CONTACTS
        )
    }

    @SuppressLint("SetTextI18n")
    private fun setUpdata(contacts: ArrayList<ContactItem>) {
//        rv_contact_list.layoutManager = LinearLayoutManager(activity!!)
//        var contactAdapter = ContactListAdapter(activity!!, contacts)
//        rv_contact_list.adapter = contactAdapter

        val contactAdapter =
            ContactSearchAdapter(activity!!, R.layout.adapter_contact)
        search.setAdapter(contactAdapter)
        contactAdapter.addAll(contacts)
        search.setOnItemClickListener { parent, _, position, _ ->
            val city = contactAdapter.getItem(position) as ContactItem?
            search.setText(city!!.number)
            name=city!!.name
        }

        when (activity) {
            is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
            is HomeActivity -> (activity as HomeActivity).hideProgressBar()
            is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
            else -> (activity as VendorActivity).hideProgressBar()
        }
    }

    override fun pageVisible() {

    }
}
