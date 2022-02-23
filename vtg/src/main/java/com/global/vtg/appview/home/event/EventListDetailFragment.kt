package com.global.vtg.appview.home.event


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.global.vtg.FragmentReplaceActivity
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.ImageViewActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.global.vtg.utils.DialogUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.vtg.R
import com.vtg.databinding.FragmentEventDetailBinding
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.BranchError
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.fragment_event_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException


class EventListDetailFragment : AppFragment(), OnMapReadyCallback, EventImageView.OnDeleteImageClick {
    private lateinit var mFragmentBinding: FragmentEventDetailBinding
    private var mMap: GoogleMap? = null
    private val viewModel by viewModel<EventListDetailViewModel>()
    private var eventId: String = ""
    private var eventName: String = ""
    private var mobile: String = ""
    private var bannerUrl: String = ""
    private var isMyEvent: Boolean = false
   val requestMultiplePermissionsCall =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
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
                isMyEvent = arguments.getBoolean(Constants.BUNDLE_FROM_PROFILE, false)
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

        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        viewModel.callEventDetails(eventId)

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            requestPermissions()
        }

        if(isMyEvent)
            share.visibility=View.VISIBLE

        share.setOnClickListener{

            val branchUniversalObject: BranchUniversalObject = BranchUniversalObject()
                .setCanonicalIdentifier("item/12345")
                .setTitle("You are invited to join event :" + eventName)
                .setContentDescription("")
                .setContentImageUrl(bannerUrl) //.setContentImageUrl(Uri.parse("file://"+downloadedImagePath).toString())
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)

            val linkProperties: LinkProperties = LinkProperties()
                .addControlParameter("event_id", eventId)
                .setFeature("sharing")

                .setStage("1")

            var bundle = Bundle()

            bundle.putString("event_id",  eventId)
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

        }


        viewModel.eventDetailLiveData.observe(this, {
            when (it) {
                is Resource.Success -> {

                    if(it.data.eventImage!!.isNotEmpty()) {
                        for ( item in it.data.eventImage){
                            if(item.banner){
                                bannerUrl=item.url
                                Glide.with(activity!!)
                                    .asBitmap()
                                    .load(item.url)
                                    .into(doc_img)
                                break
                            }
                        }
                    }else{

                        Glide.with(activity!!)
                            .asBitmap()
                            .load(R.drawable.events)
                            .into(doc_img)
                    }

                    doc_img.setOnClickListener{

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
                    }catch(e:Exception){
                        e.printStackTrace()
                    }
                    tvEventName.text = it.data.eventName
                    eventName=it.data.eventName.toString()
                    tvName.text=it.data.userFirstName+" "+ it.data.userLastName
                    if(!TextUtils.isEmpty(it.data.userProfileUrl)){
                        Glide.with(activity!!)
                            .asBitmap()
                            .load(it.data.userProfileUrl)
                            .into(ivProfilePic)
                    }
                    if(!TextUtils.isEmpty(it.data.description)){
                        tvEventDescription.visibility=View.VISIBLE
                        tvEventDescription.text = it.data.description
//                        tvEventDescription.text = "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document or a typeface without relying on meaningful content. Lorem ipsum may be used as a placeholder before the final copy is available. It is also used to temporarily replace text in a process called greeking, which allows designers to consider the form of a webpage or publication, without the meaning of the text influencing the design.\n" +
//                                "\n" +
//                                "Lorem ipsum is typically a corrupted version of De finibus bonorum et malorum, a 1st-century BC text by the Roman statesman and philosopher Cicero, with words altered, added, and removed to make it nonsensical and improper Latin."
                    }

                    phone.text= it.data.eventAddress!![0].mobileNo
                    mobile=it.data.eventAddress!![0].mobileNo
                    tvLocation.text = it.data.eventAddress!![0].addr1 + " " +
                            it.data.eventAddress!![0].addr2 + " " +
                            it.data.eventAddress!![0].city + " " +
                            it.data.eventAddress!![0].country + " "


                    val address = it.data.eventAddress!![0].addr1 + " " +
                            it.data.eventAddress!![0].addr2 + " " +
                            it.data.eventAddress!![0].city + " " +
                            it.data.eventAddress!![0].country + " "


                    val location = getLocationFromAddress(activity!!, address)
                    val markerOptions = MarkerOptions()
                    if (location != null) {
                        markerOptions.position(location)
                        markerOptions.title(it.data.eventName)
                        mMap!!.addMarker(markerOptions)
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
                    }
                    if(it.data.eventImage.isNotEmpty()) {
                        for (item in it.data.eventImage) {
                            if (!item.banner) {
                                eventAddImages.visibility=View.VISIBLE
                                images_container.visibility=View.VISIBLE
                                val v =
                                    EventImageView(
                                        activity!!,
                                        null,item.url,
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

                    direction.setOnClickListener{
                        if(location!=null) {
                            val google = StringBuilder()
                            google.append("&daddr=")
                                .append(location.latitude.toString() + "," + location.longitude)

                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                    "http://maps.google.com/maps?&daddr=" +
                                            google
                                )
                            )

                            startActivity(intent)
                        }else{
                            DialogUtils.toast(activity!!,getString(R.string.no_location))
                        }

                    }


                    phone.setOnClickListener{
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
    }

    private fun initMapView() {

        val mapFragment: WorkaroundMapFragment = childFragmentManager.findFragmentById(R.id.map_frag) as WorkaroundMapFragment

        mapFragment!!.getMapAsync(this)
        mapFragment.setListener { WorkaroundMapFragment.OnTouchListener {
            scroll.requestDisallowInterceptTouchEvent(true);

        } }

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

                    mMap!!.isMyLocationEnabled = true

               


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


    fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
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
            p1 = LatLng(location.getLatitude(), location.getLongitude())
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






}

