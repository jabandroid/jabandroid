package com.global.vtg.appview.home.qrcode

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.background.StillBackground
import com.github.sumimakito.awesomeqr.option.color.Color
import com.github.sumimakito.awesomeqr.option.logo.Logo
import com.global.vtg.base.AppFragment
import com.global.vtg.utils.Constants
import com.google.zxing.WriterException
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.vtg.R
import com.vtg.databinding.FragmentVaccineQrCodeBinding
import kotlinx.android.synthetic.main.fragment_vaccine_qr_code.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class VaccineQRCodeFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentVaccineQrCodeBinding
    private val viewModel by viewModel<VaccineQRCodeViewModel>()
    var bitmap: Bitmap? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_vaccine_qr_code
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentVaccineQrCodeBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        val color = Color()
        color.light = 0xFFFFFFFF.toInt() // for blank spaces
        color.dark = 0xFF000000.toInt() // for non-blank spaces
        color.background =
            0xFFFFFFFF.toInt() // for the background (will be overriden by background images, if set)
        color.auto = false

        val background = StillBackground()
        background.bitmap = BitmapFactory.decodeResource(context?.resources, R.mipmap.logo_vtg)
        background.clippingRect = Rect(0, 0, 200, 200) // crop the background before applying
        background.alpha = 1f
        val logo = Logo()
        try {
            val bitmap = BitmapFactory.decodeResource(context?.resources, R.mipmap.logo_vtg)
            logo.bitmap = bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        logo.borderRadius = 10 // radius for logo's corners
        logo.borderWidth = 10 // width of the border to be added around the logo
        logo.scale = 0.3f // scale for the logo in the QR code
        logo.clippingRect =
            RectF(0f, 0f, 200f, 200f) // crop the logo image before applying it to the QR code

        val renderOption = RenderOption()
        renderOption.content = Constants.USER?.barcodeId.toString() // content to encode
        renderOption.size = 800 // size of the final QR code image
        renderOption.borderWidth = 20 // width of the empty space around the QR code
        renderOption.ecl = ErrorCorrectionLevel.M // (optional) specify an error correction level
        renderOption.patternScale = 0.35f // (optional) specify a scale for patterns
        renderOption.roundedPatterns =
            true // (optional) if true, blocks will be drawn as dots instead
        renderOption.clearBorder =
            true // if set to true, the background will NOT be drawn on the border area
        renderOption.color = color // set a color palette for the QR code
        renderOption.background = background// set a background, keep reading to find more about it
        renderOption.logo = logo

        try {
            val result = AwesomeQrRenderer.render(renderOption)
            when {
                result.bitmap != null -> {
                    bitmap = result.bitmap!!
                    // play with the bitmap
                    ivQRCode.setImageBitmap(result.bitmap)
                }
                else -> {
                    // Oops, something gone wrong.
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Oops, something gone wrong.
        }

        /*viewModel.shareIntent.observe(this, {
            if (bitmap != null) {
                // save bitmap to cache directory
                // save bitmap to cache directory
                try {
                    val cachePath = File(context?.cacheDir, "images")
                    cachePath.mkdirs() // don't forget to make the directory
                    val stream =
                        FileOutputStream("$cachePath/qr.png") // overwrites this image every time
                    bitmap?.compress(CompressFormat.PNG, 100, stream)
                    stream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val imagePath = File(context?.cacheDir, "images")
                val newFile = File(imagePath, "qr.png")
                val contentUri =
                    FileProvider.getUriForFile(getAppActivity(), "com.vtg.global.fileprovider", newFile)

                if (contentUri != null) {
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
                    shareIntent.setDataAndType(
                        contentUri,
                        getAppActivity().contentResolver.getType(contentUri)
                    )
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                    startActivity(Intent.createChooser(shareIntent, "Choose an app"))
                }
            }
        })*/
    }

    override fun pageVisible() {

    }
}