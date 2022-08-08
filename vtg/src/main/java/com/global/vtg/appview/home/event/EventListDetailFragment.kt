package com.global.vtg.appview.home.event


import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.global.vtg.FragmentReplaceActivity
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.ImageViewActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.base.fragment.popFragment
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import com.vtg.R
import com.vtg.databinding.FragmentEventDetailBinding
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.android.synthetic.main.fragment_event_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException


class EventListDetailFragment : AppFragment(), OnMapReadyCallback,
    EventImageView.OnDeleteImageClick {
    private lateinit var mFragmentBinding: FragmentEventDetailBinding
    private var mMap: GoogleMap? = null
    private val viewModel by viewModel<EventListDetailViewModel>()
    private var eventId: String = ""
    private var userId: String = ""

    private var positionForDelete: Int = -1
    private var eventName: String = ""
    private var mobile: String = ""
    private var bannerUrl: String = ""
    private var isMyEvent: Boolean = false
    private var isSubEvent: Boolean = false
    private var isInterested: String = "0"
    private lateinit var eventAdapter: SubEventAdapter
    private val requestMultiplePermissionsCall =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            repeat(permissions.entries.size) {
                val intent =
                    Intent(Intent.ACTION_CALL, Uri.parse("tel:$mobile"))
                startActivity(intent)
            }
        }

    var listOfImages = ArrayList<String>()
    override fun getLayoutId(): Int {
        return R.layout.fragment_event_detail
    }

    override fun preDataBinding(arguments: Bundle?) {
        if (arguments != null) {
            if (arguments.containsKey(Constants.BUNDLE_ID)) {
                eventId = arguments.getString(Constants.BUNDLE_ID, "")
                userId = arguments.getString(Constants.BUNDLE_USERID, "")
                isMyEvent = arguments.getBoolean(Constants.BUNDLE_FROM_PROFILE, false)
                isSubEvent = arguments.getBoolean(Constants.BUNDLE_IS_SUB_EVENT, false)
            }
        }
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentEventDetailBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    @SuppressLint("SetTextI18n")
    override fun initializeComponent(view: View?) {
        initMapView()

        if(userId== Constants.USER!!.id.toString())
            isMyEvent=true

        getView()!!.isFocusableInTouchMode = true
        getView()!!.requestFocus()
        getView()!!.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                if (isSubEvent) {

                    val bundle = Bundle()
                    bundle.putString(
                        Constants.BUNDLE_ID,
                        CreateEventFragment.itemEvent.parentEvent!!.toString()
                    )
                    bundle.putBoolean(Constants.BUNDLE_FROM_PROFILE, true)
                    bundle.putBoolean(Constants.BUNDLE_IS_SUB_EVENT, false)
                    popFragment(1)
                    addFragmentInStack<Any>(AppFragmentState.F_EVENT_EVENT_DETAIL, bundle)
                } else
                    activity?.onBackPressed()
                true
            } else false
        }

        ivBack.setOnClickListener {
            if (isSubEvent) {
                val bundle = Bundle()
                bundle.putString(
                    Constants.BUNDLE_ID,
                    CreateEventFragment.itemEvent.parentEvent!!.toString()
                )
                bundle.putBoolean(Constants.BUNDLE_FROM_PROFILE, isMyEvent)
                bundle.putBoolean(Constants.BUNDLE_IS_SUB_EVENT, false)
                popFragment(1)
                addFragmentInStack<Any>(AppFragmentState.F_EVENT_EVENT_DETAIL, bundle)
            } else
                activity?.onBackPressed()
        }


        viewModel.callEventDetails(eventId)
        rvEventList.layoutManager = LinearLayoutManager(context)
        eventAdapter = SubEventAdapter(getAppActivity(), object :
            SubEventAdapter.onItemClick {

            override fun onClick(item: Event, v: View, pos: Int) {
                when (v.id) {
                    R.id.more -> {

                        val bottomSheet = BottomSheetDialog()
                        bottomSheet.setIsSubEvent(true)
                        bottomSheet.setListener(object : BottomSheetDialog.ClickListener {
                            override fun onItemClick(position: Int) {
                                when (position) {
                                    0 -> {
                                        positionForDelete = pos
                                        AppAlertDialog().showAlert(
                                            activity!!,
                                            object : AppAlertDialog.GetClick {
                                                override fun response(type: String) {

                                                    viewModel.deleteEvent(item.eventID!!)
                                                }
                                            }, getString(R.string.delete_event), "Yes", "No"

                                        )
                                    }
                                    1 -> {
                                        CreateSubEventFragment.itemSubEvent = item

                                        addFragmentInStack<Any>(AppFragmentState.F_SUB_EVENT_CREATE)
                                    }
                                    2 -> {
                                        var bannerURl = ""
                                        if (item.eventImage!!.isNotEmpty()) {
                                            for (item in item.eventImage) {
                                                if (item.banner) {
                                                    bannerURl = item.url
                                                    break
                                                }
                                            }
                                        }
                                        val branchUniversalObject: BranchUniversalObject =
                                            BranchUniversalObject()
                                                .setCanonicalIdentifier("item/12345")
                                                .setTitle("You are invited to join event :" + item.eventName)
                                                .setContentDescription("")
                                                .setContentImageUrl(bannerURl) //.setContentImageUrl(Uri.parse("file://"+downloadedImagePath).toString())
                                                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)

                                        val linkProperties: LinkProperties = LinkProperties()
                                            .addControlParameter("event_id", item.eventID)
                                            .setFeature("sharing")
                                            .setStage("1")

                                        val bundle = Bundle()

                                        bundle.putString("event_id", item.eventID)
                                        branchUniversalObject.generateShortUrl(
                                            activity!!,
                                            linkProperties
                                        ) { url, error ->
                                            if (error == null) {
                                                val sendIntent = Intent()
                                                sendIntent.action = Intent.ACTION_SEND
                                                sendIntent.putExtra(Intent.EXTRA_TEXT, url)
                                                sendIntent.type = "text/plain"
                                                activity!!.startActivity(sendIntent)
                                            }
                                        }
                                    }
                                }
                            }

                        })
                        bottomSheet.show(
                            childFragmentManager,
                            "ModalBottomSheet"
                        )
                    }
                    else -> {
                        val bundle = Bundle()
                        bundle.putString(Constants.BUNDLE_ID, item.eventID!!.toString())
                        bundle.putBoolean(Constants.BUNDLE_FROM_PROFILE, isMyEvent)
                        bundle.putBoolean(Constants.BUNDLE_IS_SUB_EVENT, true)
                        popFragment(1)
                        addFragmentInStack<Any>(AppFragmentState.F_EVENT_EVENT_DETAIL, bundle)
                    }

                }
            }
        }, isMyEvent)
        rvEventList.adapter = eventAdapter
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions()
        }

        if (isMyEvent) {
            //  share.visibility = View.VISIBLE
            more.visibility = View.VISIBLE

            add_user_root.visibility = View.VISIBLE

        } else {
            add_user_root.visibility = View.VISIBLE
            start.text = getString(R.string.interested)
        }
        public_event.visibility = View.VISIBLE
        more.setOnClickListener {
            val bottomSheet = BottomSheetDialog()
            bottomSheet.setIsSubEvent(isSubEvent)
            bottomSheet.setListener(object : BottomSheetDialog.ClickListener {
                override fun onItemClick(position: Int) {
                    when (position) {
                        0 -> {
                            AppAlertDialog().showAlert(
                                activity!!,
                                object : AppAlertDialog.GetClick {
                                    override fun response(type: String) {

                                        viewModel.deleteEvent(eventId)
                                    }
                                }, getString(R.string.delete_event), "Yes", "No"

                            )
                        }
                        1 -> {
                            if (isSubEvent) {


                                CreateSubEventFragment.itemSubEvent = CreateEventFragment.itemEvent

                                addFragmentInStack<Any>(AppFragmentState.F_SUB_EVENT_CREATE)

                            } else
                                addFragmentInStack<Any>(AppFragmentState.F_EVENT_CREATE)
                        }
                        2 -> {
                            val branchUniversalObject: BranchUniversalObject =
                                BranchUniversalObject()
                                    .setCanonicalIdentifier("item/12345")
                                    .setTitle("You are invited to join event :$eventName")
                                    .setContentDescription("")
                                    .setContentImageUrl(bannerUrl) //.setContentImageUrl(Uri.parse("file://"+downloadedImagePath).toString())
                                    .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)

                            val linkProperties: LinkProperties = LinkProperties()
                                .addControlParameter("event_id", eventId)
                                .setFeature("sharing")

                                .setStage("1")

                            val bundle = Bundle()

                            bundle.putString("event_id", eventId)
                            branchUniversalObject.generateShortUrl(
                                activity!!,
                                linkProperties
                            ) { url, error ->
                                if (error == null) {
                                    val sendIntent = Intent()
                                    sendIntent.action = Intent.ACTION_SEND
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, url)
                                    sendIntent.type = "text/plain"
                                    activity!!.startActivity(sendIntent)
                                }
                            }
                        }
                        4 -> {
                            CreateSubEventFragment.itemSubEvent = Event()
                            addFragmentInStack<Any>(
                                AppFragmentState.F_SUB_EVENT_CREATE
                            )
                        }
                    }
                }

            })
            bottomSheet.show(
                childFragmentManager,
                "ModalBottomSheet"
            )
        }
        share.setOnClickListener {


        }


        viewModel.eventDeletelLiveData.observe(this, { resources ->
            resources?.let {
                when (it) {
                    is Resource.Success -> {


                        when (activity) {
                            is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                            is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                            is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                            is FragmentReplaceActivity -> (activity as FragmentReplaceActivity).hideProgressBar()
                            else -> (activity as VendorActivity).hideProgressBar()
                        }

                        if (positionForDelete == -1) {

                            val fragments = getAppActivity().supportFragmentManager.fragments
                            for (frg in fragments) {
                                if (frg is EventListFragment) {
                                    frg.refreshList()
                                    break
                                }

                            }
                            popFragment(1)
                        } else {
                            eventAdapter.remove(positionForDelete)
                        }
                        positionForDelete = -1

                    }
                    is Resource.Error -> {

                        when (activity) {
                            is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                            is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                            is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                            is FragmentReplaceActivity -> (activity as FragmentReplaceActivity).hideProgressBar()
                            else -> (activity as VendorActivity).hideProgressBar()
                        }
                        it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                    }
                    is Resource.Loading -> {

                        when (activity) {
                            is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                            is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                            is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                            is FragmentReplaceActivity -> (activity as FragmentReplaceActivity).hideProgressBar()
                            else -> (activity as VendorActivity).hideProgressBar()
                        }
                    }
                }
            }
        })

        viewModel.eventDetailLiveData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    CreateEventFragment.itemEvent = it.data

                    if (it.data.eventImage!!.isNotEmpty()) {
                        for (item in it.data.eventImage) {
                            if (item.banner) {
                                bannerUrl = item.url
                                Glide.with(activity!!)
                                    .asBitmap()
                                    .load(item.url)
                                    .into(doc_img)
                                break
                            }
                        }
                    } else {

                        Glide.with(activity!!)
                            .asBitmap()
                            .load(R.drawable.events)
                            .into(doc_img)
                    }

                    doc_img.setOnClickListener {

                        val listOfImages = ArrayList<String>()

                        listOfImages.add(bannerUrl)
                        val intent = Intent(activity!!, ImageViewActivity::class.java)
                        intent.putExtra("IM", listOfImages)
                        startActivity(intent)
                    }

                    try {
                        tvDate.text =
                            DateUtils.formatDateUTCToLocal(
                                it.data.startDate!!,
                                DateUtils.API_DATE_FORMAT_VACCINE,
                                true
                            ) + " \u2192 " + DateUtils.formatDateUTCToLocal(
                                it.data.endDate!!,
                                DateUtils.API_DATE_FORMAT_VACCINE,
                                true
                            )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    tvEventName.text = it.data.eventName

                    eventName = it.data.eventName.toString()
                    tvName.text = it.data.userFirstName + " " + it.data.userLastName
                    if (!TextUtils.isEmpty(it.data.userProfileUrl)) {
                        Glide.with(activity!!)
                            .asBitmap()
                            .load(it.data.userProfileUrl)
                            .into(ivProfilePic)
                    }
                    if (!TextUtils.isEmpty(it.data.description)) {
                        tvEventDescription.visibility = View.VISIBLE
                        tvEventDescription.text = it.data.description
//                        tvEventDescription.text = "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document or a typeface without relying on meaningful content. Lorem ipsum may be used as a placeholder before the final copy is available. It is also used to temporarily replace text in a process called greeking, which allows designers to consider the form of a webpage or publication, without the meaning of the text influencing the design.\n" +
//                                "\n" +
//                                "Lorem ipsum is typically a corrupted version of De finibus bonorum et malorum, a 1st-century BC text by the Roman statesman and philosopher Cicero, with words altered, added, and removed to make it nonsensical and improper Latin."
                    }


                    if (it.data.eventAddress!!.isNotEmpty()) {
                        phone.text = it.data.eventAddress!![0].mobileNo
                        mobile = it.data.eventAddress!![0].mobileNo.toString()
                        tvLocation.text = it.data.eventAddress!![0].addr1 + " " +
                                it.data.eventAddress!![0].addr2 + " " +
                                it.data.eventAddress!![0].city + " " +
                                it.data.eventAddress!![0].country + " "


                        val address = it.data.eventAddress!![0].addr1 + " " +
                                it.data.eventAddress!![0].addr2 + " " +
                                it.data.eventAddress!![0].city + " " +
                                it.data.eventAddress!![0].country + " "

                        if (!TextUtils.isEmpty(it.data.eventAddress!![0].email))
                            tvEmail.text = it.data.eventAddress!![0].email
                        else
                            tvEmail.text = "testevent@gmail.com"
                        val location = getLocationFromAddress(activity!!, address)
                        val markerOptions = MarkerOptions()
                        if (location != null) {
                            markerOptions.position(location)
                            markerOptions.title(it.data.eventName)
                            mMap!!.addMarker(markerOptions)
                            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
                        }

                        direction.setOnClickListener {
                            if (location != null) {
                                val google = StringBuilder()
                                google.append(
                                    "&" +
                                            "daddr="
                                )
                                    .append(location.latitude.toString() + "," + location.longitude)

                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(
                                        "http://maps.google.com/maps?&daddr=" +
                                                google
                                    )
                                )

                                startActivity(intent)
                            } else {
                                DialogUtils.toast(activity!!, getString(R.string.no_location))
                            }

                        }

                    }

                    cvUploadImages.removeAllViews()
                    if (it.data.eventImage.isNotEmpty()) {
                        for (item in it.data.eventImage) {
                            if (!item.banner) {
                                eventAddImages.visibility = View.VISIBLE
                                images_container.visibility = View.VISIBLE
                                val v =
                                    EventImageView(
                                        activity!!,
                                        null, item.url,
                                        0,
                                        false,
                                        this,
                                        cvUploadImages
                                    )

                                listOfImages.add(item.url)
                                cvUploadImages.addView(v)

                            }
                        }
                    }

                    tvEmail.setOnClickListener {
                        val i = Intent(Intent.ACTION_SEND)
                        i.type = "message/rfc822"
                        i.putExtra(Intent.EXTRA_EMAIL, arrayOf(tvEmail.text.toString()))

                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."))
                        } catch (ex: ActivityNotFoundException) {
                            Toast.makeText(
                                activity,
                                "There are no email clients installed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    if (it.data.privateEvent!!) {
                        tvprivateEvent.text = getString(R.string.label_private_event)

                        lock.setBackgroundResource(R.drawable.ic_icon_awesome_lock)
                    } else {
                        lock.setBackgroundResource(R.drawable.ic_icon_awesome_unlock)
                        tvprivateEvent.text = getString(R.string.label_public_event)

                    }


                    phone.setOnClickListener {
                        if (ActivityCompat.checkSelfPermission(
                                requireActivity(),
                                Manifest.permission.CALL_PHONE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            val intent =
                                Intent(Intent.ACTION_CALL, Uri.parse("tel:$mobile"))
                            startActivity(intent)
                        } else {
                            requestPermissionsCall()
                        }

                    }
                    if (it.data.arrSubEvent != null && it.data.arrSubEvent!!.size > 0) {
                        sub_event.visibility = View.VISIBLE
                        eventAdapter.setList(it.data.arrSubEvent!!)
                    }

                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        is FragmentReplaceActivity -> (activity as FragmentReplaceActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }

                }
                is Resource.Error -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        is FragmentReplaceActivity -> (activity as FragmentReplaceActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).showProgressBar()
                        is HomeActivity -> (activity as HomeActivity).showProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
                        is FragmentReplaceActivity -> (activity as FragmentReplaceActivity).showProgressBar()
                        else -> (activity as VendorActivity).showProgressBar()
                    }
                }
            }
        })

        start.setOnClickListener {
            if (isMyEvent) {
                val bundle = Bundle()
                bundle.putString(Constants.BUNDLE_ID, eventId)
                bundle.putString(Constants.BUNDLE_NAME, eventName)
                bundle.putString(Constants.BUNDLE_PIC, bannerUrl)
                addFragmentInStack<Any>(AppFragmentState.F_CONTACT_LIST, bundle)
            } else {
                if (start.text.toString().equals(getString(R.string.decline))) {
                    AppAlertDialog().showAlert(
                        activity!!,
                        object : AppAlertDialog.GetClick {
                            override fun response(type: String) {
                                val j = JsonObject()
                                j.addProperty("eventId", eventId)
                                j.addProperty("userId", Constants.USER!!.id)
                                j.addProperty(
                                    "userName",
                                    SharedPreferenceUtil.INSTANCE?.getData(
                                        PreferenceManager.KEY_USER_NAME,
                                        ""
                                    )
                                )
                                if (start.text.toString().equals(getString(R.string.decline)))
                                    j.addProperty("interested", "0")
                                else
                                    j.addProperty("interested", "1")

                                viewModel.addUSer(j)
                            }
                        },
                        getString(R.string.decline_invitation),
                        getString(R.string.yes),
                        getString(R.string.no)

                    )
                } else {
                    val j = JsonObject()
                    j.addProperty("eventId", eventId)
                    j.addProperty("userId", Constants.USER!!.id)
                    j.addProperty(
                        "userName",
                        SharedPreferenceUtil.INSTANCE?.getData(PreferenceManager.KEY_USER_NAME, "")
                    )
                    if (start.text.toString().equals(getString(R.string.decline)))
                        j.addProperty("interested", "0")
                    else
                        j.addProperty("interested", "1")

                    viewModel.addUSer(j)

                }
                //  {"eventUsers":"{\"status\":\"Success\"}"}

                //  {"eventId":"51","userId":8,"userName":"919855677137","interested":"0"}
            }
        }

        viewModel.checkStatus(eventId, Constants.USER!!.id.toString())

        viewModel.addUser.observe(this, {
            when (it) {
                is Resource.Success -> {

                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    viewModel.checkStatus(eventId, Constants.USER!!.id.toString())
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

        viewModel.checkStatusLive.observe(this, {
            when (it) {
                is Resource.Success -> {
                    isInterested = it.data.interestedEvent.toString()

                    //  share.visibility = View.VISIBLE

                    if (!isMyEvent) {
                        if (isInterested == "1") {
                            start.text = getString(R.string.decline)
                            start.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ff0000"))
                        } else {

                            start.text = getString(R.string.interested)
                            start.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#255CA4"))
                        }
                    }
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                }
                is Resource.Error -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
//                    it.error.message?.let { it1 ->
//                        DialogUtils.showSnackBar(context, it1)
//                    }
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

    private fun initMapView() {

        val mapFragment: WorkaroundMapFragment =
            childFragmentManager.findFragmentById(R.id.map_frag) as WorkaroundMapFragment

        mapFragment.getMapAsync(this)
        mapFragment.setListener {
            WorkaroundMapFragment.OnTouchListener {
                scroll.requestDisallowInterceptTouchEvent(true)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.uiSettings.isMapToolbarEnabled = false
        mMap!!.uiSettings.isZoomControlsEnabled = true
        mMap!!.clear()
        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap!!.isTrafficEnabled = false
        mMap!!.isIndoorEnabled = false
        mMap!!.isBuildingsEnabled = false

        mMap!!.uiSettings.isZoomControlsEnabled = false
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap!!.isMyLocationEnabled = true
        }
    }

    override fun pageVisible() {

    }

    @SuppressLint("MissingPermission", "LogNotTimber")
    private fun requestPermissions() {

        val requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach {
                    Log.e("DEBUG", "${it.key} = ${it.value}")
                    if (ActivityCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        mMap!!.isMyLocationEnabled = true
                    }
                }
            }

        requestMultiplePermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )


    }

    @SuppressLint("MissingPermission", "LogNotTimber")
    private fun requestPermissionsCall() {


        requestMultiplePermissionsCall.launch(
            arrayOf(
                Manifest.permission.CALL_PHONE,

                )
        )


    }


    private fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var p1: LatLng? = null
        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location: Address = address[0]
            p1 = LatLng(location.latitude, location.longitude)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return p1
    }

    override fun onDeleteClick(item: Int, ll: LinearLayout) {

    }

    override fun onImageClick() {

    }

    override fun onViewClick() {
        val intent = Intent(activity!!, ImageViewActivity::class.java)
        intent.putExtra("IM", listOfImages)
        startActivity(intent)
    }


    class BottomSheetDialog : BottomSheetDialogFragment() {
        private lateinit var listener: ClickListener
        private var isSubEvent: Boolean = false
        override fun onCreateView(
            inflater: LayoutInflater,
            @Nullable container: ViewGroup?,
            @Nullable savedInstanceState: Bundle?
        ): View {
            val v: View = inflater.inflate(
                R.layout.bottom_sheet,
                container, false
            )


            if (!isSubEvent) {
                v.addSlot.visibility = View.VISIBLE
                v.add_sub_event.visibility = View.VISIBLE
            }

            v.delete.setOnClickListener {
                listener.onItemClick(0)
                dismiss()
            }
            v.addSlot.setOnClickListener {
                listener.onItemClick(4)
                dismiss()
            }
            v.edit.setOnClickListener {
                listener.onItemClick(1)
                dismiss()

            }
            v.share.setOnClickListener {
                listener.onItemClick(2)
                dismiss()

            }
            return v
        }

        fun setListener(l: ClickListener) {
            this.listener = l
        }

        fun setIsSubEvent(l: Boolean) {
            this.isSubEvent = l
        }

        interface ClickListener {
            fun onItemClick(position: Int)
        }
    }

    fun refreshList() {
        if (!TextUtils.isEmpty(eventId))
            viewModel.callEventDetails(eventId)
    }


}

