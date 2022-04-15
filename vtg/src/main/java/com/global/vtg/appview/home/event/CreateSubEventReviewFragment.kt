package com.global.vtg.appview.home.event


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.config.PickMediaExtensions
import com.global.vtg.appview.config.getRealPath
import com.global.vtg.appview.config.getRealPathFromURI
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.base.fragment.popFragment
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.AppAlertDialog
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.baseinrerface.OkCancelNeutralDialogInterface
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.vtg.R
import com.vtg.databinding.FragmentCreateEventSubReviewBinding
import kotlinx.android.synthetic.main.fragment_create_event_review.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException


class CreateSubEventReviewFragment : AppFragment(), OnMapReadyCallback,
    EventImageViewCreateSub.OnDeleteImageClick {
    private lateinit var mFragmentBinding: FragmentCreateEventSubReviewBinding
    private val viewModel by viewModel<CreatEventReviewSubViewModel>()
    private var mMap: GoogleMap? = null
    private lateinit var imagetoLoad: ImageView
    private var eventID: String = ""
    private var isBanner: Boolean = false
    var count: Int = 0
    var selectedPosion: Int = 0
    lateinit var selectedLayout: LinearLayout
    var imageServer: HashMap<Int, String> = HashMap()

    override fun getLayoutId(): Int {
        return R.layout.fragment_create_event_sub_review
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentCreateEventSubReviewBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    companion object {
        lateinit var images: HashMap<String, String>
        lateinit var vAddNew: EventImageViewCreateSub
    }

    @SuppressLint("SetTextI18n")
    override fun initializeComponent(view: View?) {
        initMapView()
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        images = HashMap()
        vAddNew =
            EventImageViewCreateSub(
                activity!!,
                null, "", "",
                0,
                true,
                this,
                cvUploadImages
            )
        cvUploadImages.addView(vAddNew)
        selectedLayout = cvUploadImages
        tvDate.text = CreateSubEventFragment.itemSubEvent.startDate
        tvEventName.text = CreateSubEventFragment.itemSubEvent.eventName

        try {
            if (CreateSubEventFragment.itemSubEvent.eventImage!!.isNotEmpty()) {
                for (item in CreateSubEventFragment.itemSubEvent.eventImage!!) {
                    if (item.banner) {
                        viewModel.bannerImage = item.url
                        Glide.with(activity!!)
                            .asBitmap()
                            .load(item.url)
                            .into(doc_img)
                        upload.visibility = View.GONE
                        ivCancel.visibility = View.VISIBLE
                        doc_img.visibility = View.VISIBLE
                        break
                    }
                }

            }

            if (CreateSubEventFragment.itemSubEvent.eventImage!!.isNotEmpty()) {
                for (item in CreateSubEventFragment.itemSubEvent.eventImage!!) {
                    if (!item.banner) {
                        eventAddImages.visibility = View.VISIBLE
                        images_container.visibility = View.VISIBLE
                        val v =
                            EventImageViewCreate(
                                activity!!,
                                null, item.url, item.id,
                                count,
                                false,
                                CreateEventReviewFragment(),
                                cvUploadImages
                            )

                        cvUploadImages.addView(v, 0)

                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (!TextUtils.isEmpty(CreateSubEventFragment.itemSubEvent.eventID)) {
            btnNext.text = getString(R.string.action_save)
            btnCancel.visibility = View.GONE
        }
//
        tvLocation.text = CreateSubEventFragment.itemSubEvent.eventAddress!![0].city + " " +
                CreateSubEventFragment.itemSubEvent.eventAddress!![0].state + " " +
                CreateSubEventFragment.itemSubEvent.eventAddress!![0].country

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
        viewModel.cancelBannerImage.observe(this, {
            images.remove("Banner")
            viewModel.bannerImage = ""
            upload.visibility = View.VISIBLE
            ivCancel.visibility = View.GONE
            doc_img.visibility = View.GONE
        })

        viewModel.showToastError.observe(this, {
            DialogUtils.showSnackBar(context, it)
        })

        viewModel.uploadPics.observe(this, {
            isBanner = it
            if (it)
                imagetoLoad = doc_img

            DialogUtils.okCancelNeutralDialog(
                context,
                getAppActivity().getString(R.string.app_name),
                getAppActivity().getString(R.string.label_select_image),
                object :
                    OkCancelNeutralDialogInterface {
                    override fun ok() {
                        PickMediaExtensions.instance.pickFromGallery(getAppActivity()) { resultCode: Int, path: String, displayName: String? ->
                            resultMessage(resultCode, path, displayName)
                        }
                    }

                    override fun cancel() {
                        PickMediaExtensions.instance.pickFromCamera(getAppActivity()) { resultCode: Int, path: String, displayName: String? ->
                            resultMessage(resultCode, path, displayName)
                        }
                    }

                    override fun neutral() {

                    }
                })
        })
        viewModel.cancelEvent.observe(this, {

            AppAlertDialog().showAlert(
                activity!!,
                object : AppAlertDialog.GetClick {
                    override fun response(type: String) {
                        popFragment(4)

                    }
                }, getString(R.string.cancel_event), "Yes", "No"

            )
        })

        viewModel.createEventLiveData.observe(this, {
            when (it) {
                is Resource.Success -> {
                    eventID = it.data.eventID.toString()
                    CreateSubEventFragment.itemSubEvent.eventID = eventID
                    var part: MutableMap<String, RequestBody> = HashMap()

                    val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
                    builder.addFormDataPart("eventId", eventID)

                    if (images.size > 0) {
                        val file: File
                        if (images.containsKey("Banner")) {
                            count = 0
                            isBanner = true
                            file = File(images["Banner"]!!)
                            builder.addFormDataPart(
                                "banner",
                                file.name,
                                file.asRequestBody(MultipartBody.FORM)
                            )

                        }
                        count = 0
                        isBanner = false
                        val l: List<String> = ArrayList<String>(images.keys)
                        for (item in l) {
                            if (item != "Banner") {

                                isBanner = false
                                val filek = File(images[item]!!)
                                builder.addFormDataPart(
                                    "image" + count,
                                    filek.name,
                                    filek.asRequestBody(MultipartBody.FORM)
                                )
                                count++
                            }
                        }

                        val requestBody: RequestBody = builder.build()
                        viewModel.uploadPic(requestBody, "")


                    } else {
                        val fragments = getAppActivity().supportFragmentManager.fragments
                        for (frg in fragments) {
                            if (frg is EventListFragment) {
                                frg.refreshList()
                                break
                            }

                        }

                        for (frg in fragments) {
                            if (frg is EventListDetailFragment) {
                                frg.refreshList()
                                break
                            }

                        }

                        popFragment(2)
                        addFragmentInStack<Any>(
                            AppFragmentState.F_THANKYOU_EVENT
                        )
                        when (activity) {
                            is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                            is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                            is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                            else -> (activity as VendorActivity).hideProgressBar()
                        }
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


        viewModel.userLiveData.observe(this, { resources ->
            resources?.let {
                when (it) {
                    is Resource.Success -> {

                        when (activity) {
                            is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                            is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                            else -> (activity as VendorActivity).hideProgressBar()
                        }

                        val fragments = getAppActivity().supportFragmentManager.fragments
                        for (frg in fragments) {
                            if (frg is EventListFragment) {
                                frg.refreshList()
                                break
                            }

                        }

                        for (frg in fragments) {
                            if (frg is EventListDetailFragment) {
                                frg.refreshList()
                                break
                            }

                        }

                        popFragment(2)
                        addFragmentInStack<Any>(
                            AppFragmentState.F_THANKYOU_EVENT
                        )

                    }
                    is Resource.Error -> {
                        when (activity) {
                            is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                            is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                            else -> (activity as VendorActivity).hideProgressBar()
                        }
                        it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                    }
                    is Resource.Loading -> {
                        when (activity) {
                            is HomeActivity -> (activity as HomeActivity).showProgressBar()
                            is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
                            else -> (activity as VendorActivity).showProgressBar()
                        }
                    }
                }
            }
        })

        viewModel.deletPicData.observe(this, { resources ->
            resources?.let {
                when (it) {
                    is Resource.Success -> {

                        when (activity) {
                            is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                            is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                            else -> (activity as VendorActivity).hideProgressBar()
                        }
                        selectedLayout.removeViewAt(selectedPosion)

                    }
                    is Resource.Error -> {
                        when (activity) {
                            is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                            is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                            else -> (activity as VendorActivity).hideProgressBar()
                        }
                        it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                    }
                    is Resource.Loading -> {
                        when (activity) {
                            is HomeActivity -> (activity as HomeActivity).showProgressBar()
                            is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
                            else -> (activity as VendorActivity).showProgressBar()
                        }
                    }
                }
            }
        })


    }

    override fun pageVisible() {

    }

    private fun initMapView() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_frag) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
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

            val address = CreateSubEventFragment.itemSubEvent.eventAddress!![0].addr1 + " " +
                    CreateSubEventFragment.itemSubEvent.eventAddress!![0].addr2 + " " +
                    CreateSubEventFragment.itemSubEvent.eventAddress!![0].city + " " +
                    CreateSubEventFragment.itemSubEvent.eventAddress!![0].country + " "


            val location = getLocationFromAddress(activity!!, address)
            val markerOptions = MarkerOptions()
            if (location != null) {
                markerOptions.position(location)
                markerOptions.title(CreateSubEventFragment.itemSubEvent.eventName)

                mMap!!.addMarker(markerOptions)

                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
            }


        }

    }

    @SuppressLint("MissingPermission", "LogNotTimber")
    private fun requestPermissions() {

        val requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach {
                    Log.e("DEBUG", "${it.key} = ${it.value}")

                    mMap!!.isMyLocationEnabled = true

                    val address =
                        CreateSubEventFragment.itemSubEvent.eventAddress!![0].addr1 + " " +
                                CreateSubEventFragment.itemSubEvent.eventAddress!![0].addr2 + " " +
                                CreateSubEventFragment.itemSubEvent.eventAddress!![0].city + " " +
                                CreateSubEventFragment.itemSubEvent.eventAddress!![0].country + " "


                    val location = getLocationFromAddress(activity!!, address)
                    val markerOptions = MarkerOptions()
                    if (location != null) {
                        markerOptions.position(location)
                        markerOptions.title(CreateSubEventFragment.itemSubEvent.eventName)

                        mMap!!.addMarker(markerOptions)

                        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
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

    override fun onDeleteClick(item: Int, ll: LinearLayout, url: String) {
        if (TextUtils.isEmpty(url)) {
            images.remove("image" + item)
            ll.removeViewAt(item)
            count = images.size
            if (images.containsKey("Banner"))
                count--
        } else {
            ll.removeViewAt(item)

        }


        //CreateEventReviewFragment.vAddNew.showAdd(vAddNew)
//        AppAlertDialog().showAlert(
//            activity!!,
//            object : AppAlertDialog.GetClick {
//                override fun response(type: String) {
//
//                }
//            }, getString(R.string.delete_image), "Yes", "No"
//        )

    }

    override fun onImageClick() {
        viewModel.uploadPics.postValue(false)


    }

    private fun resultMessage(
        resultCode: Int,
        path: String,
        displayName: String?,
        fromCamera: Boolean = false
    ) {
        getAppActivity().getActivityScope(getAppActivity()).launch(Dispatchers.IO) {
            when (resultCode) {
                PickMediaExtensions.PICK_SUCCESS -> {
                    val realPath1 = Uri.parse(path).getRealPathFromURI(getAppActivity())
                    if (realPath1 != null) {
                        if (displayName != null) {
                            updateDocument(displayName, path, fromCamera)
                        }
                    } else {
                        val realPath = Uri.parse(path).getRealPath(getAppActivity())
                        if (realPath != null) {
                            if (displayName != null) {
                                updateDocument(displayName, path, fromCamera)
                            }
                        } else {
                            DialogUtils.showSnackBar(
                                getAppActivity(),
                                resources.getString(R.string.error_message_somethingwrong)
                            )
                        }
                    }
                }
                PickMediaExtensions.PICK_CANCELED -> {
                    activity?.runOnUiThread {
                        activity?.onBackPressed()
                    }
                }
                else -> DialogUtils.showSnackBar(
                    getAppActivity(),
                    resources.getString(R.string.error_message_somethingwrong)
                )
            }
        }
    }

    private fun updateDocument(docName: String, path: String, fromCamera: Boolean) {
        MainScope().launch {
            //   viewModel.documentPath = path

            var bitmap = BitmapFactory.decodeFile(path)
            if (fromCamera) {
                if (bitmap.width > bitmap.height) {
                    val matrix = Matrix()
                    matrix.postRotate(90f)
                    bitmap = Bitmap.createBitmap(
                        bitmap,
                        0,
                        0,
                        bitmap.width,
                        bitmap.height,
                        matrix,
                        true
                    )
                }

            }

            if (isBanner) {
                images["Banner"] = path
                viewModel.bannerImage = path
                upload.visibility = View.GONE
                ivCancel.visibility = View.VISIBLE
                doc_img.visibility = View.VISIBLE
                imagetoLoad.setImageBitmap(bitmap)
            } else {
                images["image" + count] = path

                val v =
                    EventImageViewCreateSub(
                        activity!!,
                        bitmap, "", "",
                        count,
                        false,
                        CreateSubEventReviewFragment(),
                        cvUploadImages
                    )
                cvUploadImages.addView(v, 0)
                count++
                if (count == 4) {
                    vAddNew.hideAdd(vAddNew)
                }
            }
        }
        // upload profile pic
        // viewModel.uploadProfile(docName)
    }

    override fun onViewClick() {

    }


}