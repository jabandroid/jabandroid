package com.global.vtg.appview.home.event

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.appview.home.event.CreateSubEventFragment.Companion.itemSubEvent
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.NetworkUtils
import com.global.vtg.utils.ToastUtils
import com.google.gson.JsonObject
import com.vtg.R
import com.vtg.databinding.FragmentEventListBinding
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.BranchError
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.fragment_event_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class EventListFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentEventListBinding

    private val viewModel by viewModel<EventListViewModel>()
    private var selectedType = 0
    override fun getLayoutId(): Int {
        return R.layout.fragment_event_list
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentEventListBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {

        CreateEventFragment.itemEvent = Event()
        itemSubEvent = Event()
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        viewModel.redirectToCreate.observe(this, {
            CreateEventFragment.itemEvent = Event()
            addFragmentInStack<Any>(AppFragmentState.F_EVENT_CREATE)
            // addFragmentInStack<Any>(AppFragmentState.F_EVENT_CREATE_REVIEW)
//            addFragmentInStack<Any>(
//                AppFragmentState.F_SUB_EVENT_CREATE
//            )
        })
        selectedType = 0

        setView()
        rvEventList.layoutManager = LinearLayoutManager(context)
        rvEventListMy.layoutManager = LinearLayoutManager(context)
        val eventAdapter = EventAdapter(getAppActivity(), object :
            EventAdapter.onItemClick {

            override fun onClick(item: Event, v: View) {
                val bundle = Bundle()
                bundle.putString(Constants.BUNDLE_ID, item.eventID!!.toString())
                bundle.putString(Constants.BUNDLE_USERID, item.userId!!.toString())
                bundle.putBoolean(Constants.BUNDLE_FROM_PROFILE, false)
                addFragmentInStack<Any>(AppFragmentState.F_EVENT_EVENT_DETAIL, bundle)
            }
        })
        val eventMyAdapter = EventMyAdapter(getAppActivity(), object :
            EventMyAdapter.onItemClick {

            override fun onClick(item: Event, v: View) {
                when (v.id) {
                    R.id.share -> {


                        var url: String = ""
                        if (item.eventImage!!.isNotEmpty()) {
                            for (item1 in item.eventImage!!) {
                                if (item1.banner) {
                                    url = item1.url
                                    break
                                }
                            }
                        }

                        val branchUniversalObject: BranchUniversalObject = BranchUniversalObject()
                            .setCanonicalIdentifier("item/12345")
                            .setTitle("You are invited to join event :" + item.eventName)
                            .setContentDescription("")
                            .setContentImageUrl(url) //.setContentImageUrl(Uri.parse("file://"+downloadedImagePath).toString())
                            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)

                        val linkProperties: LinkProperties = LinkProperties()
                            .addControlParameter("event_id", item.eventID.toString())
                            .setFeature("sharing")

                            .setStage("1")

                        var bundle = Bundle()

                        bundle.putString("event_id", item.eventID.toString())
                        branchUniversalObject.generateShortUrl(
                            activity!!,
                            linkProperties,
                            object : Branch.BranchLinkCreateListener {
                                override fun onLinkCreate(url: String, error: BranchError?) {
                                    if (error == null) {
                                        val sendIntent = Intent()
                                        sendIntent.setAction(Intent.ACTION_SEND)
                                        sendIntent.putExtra(Intent.EXTRA_TEXT, url)
                                        sendIntent.setType("text/plain")
                                        activity!!.startActivity(sendIntent)
                                    }

                                }
                            })

//
                    }


                    else -> {
                        val bundle = Bundle()
                        bundle.putString(Constants.BUNDLE_ID, item.eventID!!.toString())
                        bundle.putString(Constants.BUNDLE_USERID, item.userId!!.toString())
                        bundle.putBoolean(Constants.BUNDLE_FROM_PROFILE, true)
                        addFragmentInStack<Any>(AppFragmentState.F_EVENT_EVENT_DETAIL, bundle)
                    }
                }

            }
        })
        rvEventList.adapter = eventAdapter
        rvEventListMy.adapter = eventMyAdapter
        allEvent.setOnClickListener()
        {
            selectedType = 0
            setView()
        }

        myEvent.setOnClickListener()
        {
            selectedType = 1
            setView()
        }
        if (NetworkUtils().isNetworkAvailable(activity!!)) {
            viewModel.callEventsApi(JsonObject())
            viewModel.callMyEventsApi(Constants.USER!!.id.toString())
        } else
            ToastUtils.shortToast(0, getString(R.string.error_message_network))

        viewModel.getAllEvents.observe(this,
            {
                when (it) {
                    is Resource.Success -> {
                        when (activity) {
                            is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                            is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                            is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                            else -> (activity as VendorActivity).hideProgressBar()
                        }
                        eventAdapter.setList(it.data.arr!!)

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

        viewModel.getMyEvents.observe(this,
            {
                when (it) {
                    is Resource.Success -> {
                        when (activity) {
                            is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                            is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                            is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                            else -> (activity as VendorActivity).hideProgressBar()
                        }
                        eventMyAdapter.setList(it.data.arr!!)

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

    private fun setView() {
        when (selectedType) {
            0 -> {
                allEvent.setBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.colorPrimary
                    )
                )
                myEvent.setBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.primary_light
                    )
                )
                allEvent.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                myEvent.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                rvEventList.visibility = View.VISIBLE
                rvEventListMy.visibility = View.GONE
            }
            1 -> {
                myEvent.setBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.colorPrimary
                    )
                )
                allEvent.setBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.primary_light
                    )
                )
                myEvent.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                allEvent.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                rvEventList.visibility = View.GONE
                rvEventListMy.visibility = View.VISIBLE
            }
        }
    }

    public fun refreshList() {
        viewModel.callEventsApi(JsonObject())
        viewModel.callMyEventsApi(Constants.USER!!.id.toString())
    }
}