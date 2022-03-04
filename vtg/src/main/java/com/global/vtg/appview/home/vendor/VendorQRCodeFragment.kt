package com.global.vtg.appview.home.vendor

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.app.ActivityCompat
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragment
import com.global.vtg.utils.Constants
import com.google.zxing.Result
import com.vtg.R
import com.vtg.databinding.FragmentVendorQrBinding
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.androidx.viewmodel.ext.android.viewModel


class VendorQRCodeFragment : AppFragment(), ZXingScannerView.ResultHandler {
    private lateinit var mFragmentBinding: FragmentVendorQrBinding
    private val viewModel by viewModel<VendorQRViewModel>()
    private var mScannerView: ZXingScannerView? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_vendor_qr
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentVendorQrBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mScannerView = ZXingScannerView(activity)
        return mScannerView
    }

    override fun onResume() {
        super.onResume()
        mScannerView?.setResultHandler(this)
    }

    override fun initializeComponent(view: View?) {

//        mScannerView!! .setAspectTolerance(0.5f);
//        mScannerView!! .setAutoFocus(true);
        Constants.isSpalsh=true
//        val bundle = Bundle()
//        bundle.putString(Constants.BUNDLE_BARCODE_ID, "82847153710119821642744528894")
//        addFragment<Any>(AppFragmentState.F_VENDOR_SCAN_RESULT, bundle, popFragment = this)

        if(checkPermissions()){
            mScannerView?.startCamera()
        }else{
            requestPermissions()
        }

//        PermissionUtils.with(
//            getAppActivity(),
//            true,
//            resources.getString(R.string.label_camera_permission)
//        )
//        if (PermissionUtils.checkPermission(getAppActivity(), Manifest.permission.CAMERA)) {
//            mScannerView?.startCamera()
//
//
//        } else {
//            ActivityCompat.requestPermissions(
//                requireActivity(), arrayOf(
//                    Manifest.permission.CAMERA
//                ), CAMERA_REQUEST_CODE
//            )
         //
//        }


    }

    override fun pageVisible() {

    }

    // method to check for permissions
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    var PERMISSION_ID = 44

    // method to request for permissions
    private fun requestPermissions() {

        val mPermissionResult = registerForActivityResult(
            RequestPermission()
        ) { result ->
            if (result) {
                mScannerView?.startCamera()

            } else {
                val builder = AlertDialog.Builder(requireActivity())
                builder.setTitle("Alert")
                builder.setMessage(getString(R.string.label_camera_permission))
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
            Manifest.permission.CAMERA
        )
    }
//
//    @SuppressLint("MissingPermission")
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        Log.v("asdsad", "asdsadd:" + grantResults.size)
//        when (requestCode) {
//            PERMISSION_ID -> {
//                mScannerView?.startCamera()
//            }
//        }
//    }

    override fun handleResult(rawResult: Result?) {
        val bundle = Bundle()
        bundle.putString(Constants.BUNDLE_BARCODE_ID, rawResult?.text.toString())
        addFragment<Any>(AppFragmentState.F_VENDOR_SCAN_RESULT, bundle, popFragment = this)
    }

    override fun onPause() {
        super.onPause()
        mScannerView?.stopCamera()
    }


}