package com.global.vtg.appview.home.event


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.base.fragment.popFragment
import com.global.vtg.utils.AppAlertDialog
import com.google.gson.Gson
import com.vtg.R
import com.vtg.databinding.FragmentEventAddressBinding
import kotlinx.android.synthetic.main.fragment_create_event.ivBack
import kotlinx.android.synthetic.main.fragment_event_address.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class EventAddressFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentEventAddressBinding
    private val myCalendar: Calendar = Calendar.getInstance()
    private val currentCalendar: Calendar = Calendar.getInstance()
    private val viewModel by viewModel<CreateEventViewModel>()
    private var isStartTime: Boolean = false
    private lateinit var contactAdapter: AddressAdapter
    private var isSubEvent: Boolean = false
    private var addressId: String = ""

    companion object {

        lateinit var itemEvent: Event
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_event_address
    }

    override fun preDataBinding(arguments: Bundle?) {
        if (arguments != null) {
            isSubEvent = arguments.getBoolean("isSubAddress")
            addressId = arguments.getString("addressId").toString()
        }
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentEventAddressBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {

        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                contactAdapter.filter.filter(s.toString())
            }
        })


        contactAdapter = AddressAdapter(
            activity!!,
            CreateEventFragment.itemEvent.eventAddress!!,
            object : AddressAdapter.onItemClick {
                override fun onClick(item: EventAddress, v: View, position: Int) {
                    when (v.id) {
                        R.id.edit -> {
                            val vk = Bundle()
                            vk.putBoolean("isNew", true)
                            val gson = Gson()
                            val request: Any = gson.toJson(item)
                            vk.putString("edit", request.toString())
                            vk.putInt("position", position)
                            addFragmentInStack<Any>(
                                AppFragmentState.F_EVENT_CREATE_LOCATION, vk
                            )
                        }
                        R.id.remove -> {
                            AppAlertDialog().showAlert(
                                context!! as FragmentActivity,
                                object : AppAlertDialog.GetClick {
                                    override fun response(type: String) {
                                        CreateEventFragment.itemEvent.eventAddress!!.removeAt(
                                            position
                                        )
                                        contactAdapter.remove(position)

                                    }
                                }, getString(R.string.delete_address), "Yes", "No"

                            )
                        }

                        R.id.check -> {
                            val list = ArrayList<EventAddress>()
                            list.add(item)
                            CreateSubEventFragment.itemSubEvent.eventAddress = list

                            val fragments = getAppActivity().supportFragmentManager.fragments
                            for (frg in fragments) {
                                if (frg is CreateSubEventFragment) {
                                    frg.refreshList()
                                    break
                                }
                            }

                            popFragment(1)


                        }
                    }
                }
            })
        contactAdapter.setSelectedID(addressId)
        var eventAddress = CreateEventFragment.itemEvent.eventAddress!!
        var isAdd: Boolean = true
        try {


            if (eventAddress.size > 0) {
                for (item in eventAddress) {
                    if (item == CreateSubEventFragment.itemSubEvent.eventAddress?.get(0)!!) {
                        isAdd = false
                        break
                    }
                }
            }

            if (isAdd) {
                eventAddress.add(CreateSubEventFragment.itemSubEvent.eventAddress?.get(0)!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        contactAdapter.finalList(eventAddress)
        contactAdapter.addAll(eventAddress)
        contactAdapter.isSubEvent(isSubEvent)
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        tvAddNewAddress.setOnClickListener {
            val v = Bundle()
            v.putBoolean("isNew", true)
            v.putBoolean("isSubEvent", isSubEvent)
            addFragmentInStack<Any>(
                AppFragmentState.F_EVENT_CREATE_LOCATION, v
            )
        }

        rvAddress.layoutManager = LinearLayoutManager(activity!!)

        rvAddress.adapter = contactAdapter

        val list = ArrayList<EventAddress>()
        list.add(CreateEventFragment.itemEvent.eventAddress!![0])
        CreateSubEventFragment.itemSubEvent.eventAddress = list

    }

    override fun pageVisible() {

    }


    fun refreshList() {
        rvAddress.layoutManager = LinearLayoutManager(activity!!)

        var eventAddress = CreateEventFragment.itemEvent.eventAddress!!
        var isAdd: Boolean = true
        for (item in eventAddress) {
            if (item == CreateSubEventFragment.itemSubEvent.eventAddress?.get(0)!!) {
                isAdd = false
                break
            }
        }
        if (isAdd) {
            eventAddress.add(CreateSubEventFragment.itemSubEvent.eventAddress?.get(0)!!)
        }
        contactAdapter.addAll(eventAddress)
        contactAdapter.finalList(eventAddress)
        val fragments = getAppActivity().supportFragmentManager.fragments
        for (frg in fragments) {
            if (frg is CreateEventReviewFragment) {
                frg.refreshList()
                break
            }
        }
    }


}