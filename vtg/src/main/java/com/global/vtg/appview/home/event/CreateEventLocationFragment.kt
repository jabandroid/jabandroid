package com.global.vtg.appview.home.event


import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.base.fragment.popFragment
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.google.gson.Gson
import com.vtg.R
import com.vtg.databinding.FragmentCreateEventLocationBinding
import kotlinx.android.synthetic.main.fragment_create_event_location.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class CreateEventLocationFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentCreateEventLocationBinding
    private val viewModel by viewModel<CreateEventLocationViewModel>()
    private var isNewAddress: Boolean = false
    private var isSubEvent: Boolean = false
    private var isHeadsetFocus: Boolean = false
    private var itemEdit: EventAddress = EventAddress()
    private var position: Int = -1
    override fun getLayoutId(): Int {
        return R.layout.fragment_create_event_location
    }

    override fun preDataBinding(arguments: Bundle?) {

        if (arguments != null) {
            isNewAddress = arguments.getBoolean("isNew")
            isSubEvent = arguments.getBoolean("isSubEvent")

            if (arguments.containsKey("edit")) {
                position = arguments.getInt("position")

                val gson = Gson()

                itemEdit = gson.fromJson(
                    arguments.getString("edit"),
                    EventAddress::class.java
                )
            }
        }

    }

    fun updateAddress(city: String, state: String, country: String) {
        sCity.text = city
        sState.text = state
        sCountry.text = country
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentCreateEventLocationBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {

        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        sCity.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
        }
        sState.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
        }
        sCountry.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.AUTOCOMPLETE_REQUEST_CODE)
        }
        viewModel.redirectToStep3.observe(this) {

            if (isSubEvent) {
                val fragments = getAppActivity().supportFragmentManager.fragments
                for (frg in fragments) {
                    if (frg is CreateSubEventFragment) {
                        frg.refreshList()
                        break
                    }
                }


                popFragment(2)
            } else {
                if (isNewAddress) {
                    val fragments = getAppActivity().supportFragmentManager.fragments
                    for (frg in fragments) {
                        if (frg is EventAddressFragment) {
                            frg.refreshList()
                            break
                        }
                    }

                    popFragment(1)

                } else
                    addFragmentInStack<Any>(AppFragmentState.F_EVENT_CREATE_REVIEW)
            }
        }

        // Handle Error
        viewModel.showToastError.observe(this, {
            DialogUtils.showSnackBar(context, it)
        })
        viewModel.isNew = isNewAddress
        viewModel.isSubEvent = isSubEvent
        if (!isNewAddress) {

            if (CreateEventFragment.itemEvent.eventAddress != null && CreateEventFragment.itemEvent.eventAddress!!.isNotEmpty()) {
                viewModel.address1.postValue(CreateEventFragment.itemEvent.eventAddress!![0].addr1)
                viewModel.address2.postValue(CreateEventFragment.itemEvent.eventAddress!![0].addr2)
                viewModel.city.postValue(CreateEventFragment.itemEvent.eventAddress!![0].city)
                viewModel.state.postValue(CreateEventFragment.itemEvent.eventAddress!![0].state)
                viewModel.country.postValue(CreateEventFragment.itemEvent.eventAddress!![0].country)
                viewModel.zip.postValue(CreateEventFragment.itemEvent.eventAddress!![0].zipCode)
                if(CreateEventFragment.itemEvent.eventAddress!![0].phoneNo!!.contains("+"))
                viewModel.contactNumber.postValue(CreateEventFragment.itemEvent.eventAddress!![0].phoneNo)
               else viewModel.contactNumber.postValue("+"+CreateEventFragment.itemEvent.eventAddress!![0].phoneNo)
                viewModel.email.postValue(CreateEventFragment.itemEvent.eventAddress!![0].email)
                viewModel.fax.postValue(CreateEventFragment.itemEvent.eventAddress!![0].fax)
                viewModel.web.postValue(CreateEventFragment.itemEvent.eventAddress!![0].web)
                if (!TextUtils.isEmpty(CreateEventFragment.itemEvent.eventAddress!![0].addressID))
                    viewModel.id =
                        CreateEventFragment.itemEvent.eventAddress!![0].addressID.toString()
            } else {
                if (!Constants.USER?.address.isNullOrEmpty()) {
                    val index = Constants.USER?.address!!.size - 1
                    viewModel.address1.postValue(Constants.USER?.address?.get(index)?.addr1 ?: "")
                    viewModel.address2.postValue(Constants.USER?.address?.get(index)?.addr2 ?: "")
                    viewModel.city.postValue(Constants.USER?.address?.get(index)?.city ?: "")
                    viewModel.state.postValue(Constants.USER?.address?.get(index)?.state ?: "")
                    viewModel.country.postValue(Constants.USER?.address?.get(index)?.country ?: "")
                    viewModel.zip.postValue(Constants.USER?.address?.get(index)?.zipCode ?: "")
                    if(Constants.USER?.mobileNo!!.contains("+"))
                    viewModel.contactNumber.postValue(Constants.USER?.mobileNo)
                    else
                        viewModel.contactNumber.postValue("+"+Constants.USER?.mobileNo)
                    viewModel.email.postValue(Constants.USER?.email)


                }
            }
        } else {
            if (CreateEventFragment.itemEvent.eventAddress != null && CreateEventFragment.itemEvent.eventAddress!!.isNotEmpty()) {

                if(CreateEventFragment.itemEvent.eventAddress!![0].phoneNo!!.contains("+"))
                viewModel.contactNumber.postValue(CreateEventFragment.itemEvent.eventAddress!![0].phoneNo)
                else
                    viewModel.contactNumber.postValue("+"+CreateEventFragment.itemEvent.eventAddress!![0].phoneNo)
                viewModel.email.postValue(CreateEventFragment.itemEvent.eventAddress!![0].email)

            } else {
                if (!Constants.USER?.address.isNullOrEmpty()) {
                    if(Constants.USER?.mobileNo!!.contains("+"))
                    viewModel.contactNumber.postValue(Constants.USER?.mobileNo)
                    else
                        viewModel.contactNumber.postValue("+"+Constants.USER?.mobileNo)
                    viewModel.email.postValue(Constants.USER?.email)
                }
            }

            if (position != -1) {
                viewModel.isEdit = true
                viewModel.position = position
                viewModel.address1.postValue(itemEdit.addr1)
                viewModel.address2.postValue(itemEdit.addr2)
                viewModel.city.postValue(itemEdit.city)
                viewModel.state.postValue(itemEdit.state)
                viewModel.country.postValue(itemEdit.country)
                viewModel.zip.postValue(itemEdit.zipCode)
                if(itemEdit.phoneNo!!.contains("+"))
                viewModel.contactNumber.postValue(itemEdit.phoneNo)
                else{
                    viewModel.contactNumber.postValue("+"+itemEdit.phoneNo)
                }
                viewModel.email.postValue(itemEdit.email)
                viewModel.fax.postValue(itemEdit.fax)
                viewModel.web.postValue(itemEdit.web)
                if (!TextUtils.isEmpty(itemEdit.addressID))
                    viewModel.id =
                        itemEdit.addressID.toString()
            }
        }




        tvContactNumber.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if (b) {
                isHeadsetFocus = true
                var k = tvContactNumber.text.toString()
                if (TextUtils.isEmpty(tvContactNumber.text.toString())) {
                    tvContactNumber.setText("+")
                    tvContactNumber.setSelection(1)
                }

            } else {
                isHeadsetFocus = false

                if (tvContactNumber.text.toString() == "+")
                    tvContactNumber.setText("")

            }
        }


        tvContactNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (isHeadsetFocus) {
                    if (!s.toString().startsWith("+")) {

                        if ((s!!.length == 1 && s.toString() == "+")
                            || s.toString() == ""
                            || s.toString() == " "
                        ) {
                            tvContactNumber.setText("+ ")
                            tvContactNumber.setSelection(1)
                            if (!TextUtils.isEmpty(s.toString()))
                                s.clear()

                        } else {
                            if (!TextUtils.isEmpty(s))
                                s.insert(0, "+")
                        }
                    } else if (s.toString() == "+") {
                        tvContactNumber.setText("+")
                        tvContactNumber.setSelection(1)
                    }
                }
                // transmitter.setText("RA"+s.toString().replace("RA","",false))

            }

            override fun beforeTextChanged(
                s: CharSequence?, start: Int,
                count: Int, after: Int
            ) {


            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {


            }
        })
    }

    override fun pageVisible() {

    }

}