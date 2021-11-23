package com.global.vtg.appview.home.vaccinecard

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.imageview.setGlideNormalImageProgress
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
import com.vtg.databinding.FragmentVaccineCardBinding
import kotlinx.android.synthetic.main.fragment_vaccine_card.*
import kotlinx.android.synthetic.main.fragment_vaccine_card.ivBack
import kotlinx.android.synthetic.main.fragment_vaccine_qr_code.ivQRCode
import org.koin.androidx.viewmodel.ext.android.viewModel

class VaccineCardFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentVaccineCardBinding
    private val viewModel by viewModel<VaccineCardViewModel>()
    var bitmap: Bitmap? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_vaccine_card
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentVaccineCardBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        ivCard.setGlideNormalImageProgress("https://i.ibb.co/Vqzwyf4/Whats-App-Image-2021-08-05-at-10-26-24-PM.png")

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
        renderOption.borderWidth = 0 // width of the empty space around the QR code
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
    }

    override fun pageVisible() {

    }
}