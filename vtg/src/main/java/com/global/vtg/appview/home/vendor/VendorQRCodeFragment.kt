package com.global.vtg.appview.home.vendor

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragment
import com.global.vtg.permission.PermissionUtils
import com.global.vtg.utils.Constants
import com.global.vtg.utils.Constants.CAMERA_REQUEST_CODE
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
        PermissionUtils.with(
            getAppActivity(),
            true,
            resources.getString(R.string.label_camera_permission)
        )
        if (PermissionUtils.checkPermission(getAppActivity(), Manifest.permission.CAMERA)) {
            mScannerView?.startCamera()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }
    }

    override fun pageVisible() {

    }

    override fun handleResult(rawResult: Result?) {
        val bundle = Bundle()
        bundle.putString(Constants.BUNDLE_BARCODE_ID, rawResult?.text.toString())
        addFragment<Any>(AppFragmentState.F_VENDOR_SCAN_RESULT, bundle, popFragment = this)
    }

    override fun onPause() {
        super.onPause()
        mScannerView?.stopCamera()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        for (i in permissions.indices) {
            val permissionName = permissions[i]
            if (requestCode == CAMERA_REQUEST_CODE) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    mScannerView?.startCamera()
                }
            }
        }
    }
}