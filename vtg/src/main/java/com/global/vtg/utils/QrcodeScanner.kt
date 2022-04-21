package com.global.vtg.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.global.vtg.permission.getAspectRatio
import com.global.vtg.permission.showToast
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.vtg.databinding.ActivityBarCodeScannerBinding

import com.vtg.R
import kotlinx.android.synthetic.main.activity_bar_code_scanner.*

class QrcodeScanner : AppCompatActivity() {

    val TAG = "MainActivity"

    private lateinit var camera: Camera
    private lateinit var cameraProvider: ProcessCameraProvider

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 1003
    }

    private val screenAspectRatio by lazy {
        val metrics = DisplayMetrics().also { previewView.display.getRealMetrics(it) }
        metrics.getAspectRatio()
    }
   // private lateinit var binding: ActivityBarCodeScannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     //   binding = ActivityBarCodeScannerBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_bar_code_scanner)

        if (isCameraPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }

      imgFlashLight.setOnClickListener {
            if (camera != null){
                if (camera.cameraInfo.torchState.value == TorchState.ON){
                    setFlashOffIcon()
                    camera.cameraControl.enableTorch(false)
                }else {
                    setFlashOnIcon()
                    camera.cameraControl.enableTorch(true)
                }
            }
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

        val previewUseCase = Preview.Builder()
            .setTargetRotation(previewView.display.rotation)
            .setTargetAspectRatio(screenAspectRatio)
            .build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
        val barcodeScanner = BarcodeScanning.getClient()
        val analysisUseCase = ImageAnalysis.Builder()
            .setTargetRotation(previewView.display.rotation)
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

        camera = cameraProvider.bindToLifecycle(
            this,
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build(),
            useCaseGroup
        )
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
}
