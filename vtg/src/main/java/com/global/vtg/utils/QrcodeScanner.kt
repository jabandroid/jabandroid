package com.global.vtg.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.config.PickMediaExtensions
import com.global.vtg.appview.config.getRealPath
import com.global.vtg.appview.config.getRealPathFromURI
import com.global.vtg.base.AppActivity
import com.global.vtg.permission.getAspectRatio
import com.global.vtg.permission.showToast
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.vtg.R
import kotlinx.android.synthetic.main.activity_bar_code_scanner.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class QrcodeScanner : AppActivity() {

    val TAG = "MainActivity"

    private lateinit var camera: Camera
    private lateinit var cameraProvider: ProcessCameraProvider

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 1003
    }

    private val screenAspectRatio by lazy {


        val metrics = DisplayMetrics().also { previewView12.display.getRealMetrics(it) }
        metrics.getAspectRatio()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_bar_code_scanner
    }

    override fun postDataBinding(binding: ViewDataBinding?) {

    }

    override fun initializeComponent() {

        if (isCameraPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }

        imgFlashLight.setOnClickListener {
            if (camera.cameraInfo.torchState.value == TorchState.ON){
                setFlashOffIcon()
                camera.cameraControl.enableTorch(false)
            }else {
                setFlashOnIcon()
                camera.cameraControl.enableTorch(true)
            }
        }


        imgFlashLight1.visibility=View.GONE
        if(intent.extras!=null){
            if(intent.extras!!.containsKey("child")){
                imgFlashLight1.visibility=View.VISIBLE
            }


        }
        imgFlashLight1.setOnClickListener {
            PickMediaExtensions.instance.pickFromGallery(this) { resultCode: Int, path: String, displayName: String? ->
                resultMessage(resultCode, path, displayName)
            }
        }
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
                    val realPath1 = Uri.parse(path).getRealPathFromURI(this@QrcodeScanner)
                    if (realPath1 != null) {
                        if (displayName != null) {
                            updateDocument(displayName, path, fromCamera)
                        }
                    } else {
                        val realPath = Uri.parse(path).getRealPath(this@QrcodeScanner)
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
                    runOnUiThread {
                      onBackPressed()
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

            var bitmap = BitmapFactory.decodeFile(path)
            scanQRImage(bitmap)
        }

    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun bindPreview(camProvider: ProcessCameraProvider) {
        cameraProvider = camProvider
//        val screen = Size(previewView12.width, previewView12.height) //size of the screen
try {

    val previewUseCase = Preview.Builder()
        // .setTargetRotation(previewView12.display.rotation)
        .setTargetAspectRatio(screenAspectRatio)
        .build().also {
            it.setSurfaceProvider(previewView12.surfaceProvider)
        }
    val barcodeScanner = BarcodeScanning.getClient()
    val analysisUseCase = ImageAnalysis.Builder()
        //  .setTargetRotation(previewView12.display.rotation)
        .setTargetAspectRatio(screenAspectRatio)
        .build().also {
            it.setAnalyzer(
                ContextCompat.getMainExecutor(this),
                { imageProxy ->
                    processImageProxy(barcodeScanner, imageProxy)
                }
            )
        }
    val useCaseGroup = UseCaseGroup.Builder().addUseCase(previewUseCase).addUseCase(
        analysisUseCase
    ).build()

    cameraProvider.unbindAll()
    camera = cameraProvider.bindToLifecycle(
        this,
        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build(),
        useCaseGroup
    )
}catch (e:Exception){
    e.printStackTrace()
}
    }

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    private fun processImageProxy(barcodeScanner: BarcodeScanner, imageProxy: ImageProxy) {

        // This scans the entire screen for barcodes
        imageProxy.image?.let { image ->
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodeList ->
                    if (!barcodeList.isNullOrEmpty()) {
                        if (!barcodeList[0].rawValue.isNullOrEmpty()){
                            Log.e(TAG, "processImageProxy: " + barcodeList[0].rawValue)
                            cameraProvider.unbindAll()
                            setFlashOffIcon()
                            val intent = Intent()
                            intent.putExtra("code",barcodeList[0].rawValue!!)
                            setResult(RESULT_OK, intent)
                            finish()
//                            Snackbar.make(this@QrcodeScanner,binding.clMain,
//                                "${barcodeList[0].rawValue!!}",Snackbar.LENGTH_INDEFINITE)
//                                .setAction("Retry") {
//                                    startCamera()
//                                }
//                                .show()
                        }
                    }
                }.addOnFailureListener {
                    image.close()
                    imageProxy.close()
                    Log.e(TAG, "processImageProxy: ", it)
                }.addOnCompleteListener {
                    image.close()
                    imageProxy.close()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        setFlashOffIcon()
    }

    private fun isCameraPermissionGranted(): Boolean {
        val selfPermission =
            ContextCompat.checkSelfPermission(baseContext, Manifest.permission.CAMERA)
        return selfPermission == PackageManager.PERMISSION_GRANTED
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (isCameraPermissionGranted()) {
                startCamera()
            } else {
                //show custom dialog of camera permission if permission is permanently denied
                showToast("Please allow camera permission!")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (isCameraPermissionGranted()) {
                    startCamera()
                } else {
                    showToast("Please allow camera permission!")
                }
            }
        }
    }

    private fun setFlashOffIcon() {
      imgFlashLight.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_flash_off_24,
                null
            )
        )

        imgFlashLight1.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_picture,
                null
            )
        )
    }

    private fun setFlashOnIcon(){
      imgFlashLight.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_flash_on_24,
                null
            )
        )
    }

    @SuppressLint("RestrictedApi")
    override fun onDestroy() {
        super.onDestroy()
        cameraProvider.shutdown()


    }


    fun scanQRImage(bMap: Bitmap): String? {
        var contents: String? = null
        val intArray = IntArray(bMap.width * bMap.height)
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)
        val source: LuminanceSource = RGBLuminanceSource(bMap.width, bMap.height, intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val reader: Reader = MultiFormatReader()
        try {
            val result = reader.decode(bitmap)
            contents = result.text

            val intent = Intent()
            intent.putExtra("code",contents)
            setResult(RESULT_OK, intent)
            finish()
        } catch (e: Exception) {
            Log.e("QrTest", "Error decoding barcode", e)
        }
        return contents
    }
}
