package com.global.vtg.appview.home.vaccinecard

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.global.vtg.imageview.setGlideNormalImageProgress
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.background.StillBackground
import com.github.sumimakito.awesomeqr.option.color.Color
import com.github.sumimakito.awesomeqr.option.logo.Logo
import com.global.vtg.base.AppFragment
import com.global.vtg.imageview.defaultLoader
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.global.vtg.utils.ToastUtils
import com.google.zxing.WriterException
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.vtg.R
import com.vtg.databinding.FragmentVaccineCardBinding
import kotlinx.android.synthetic.main.fragment_reg_step2.*
import kotlinx.android.synthetic.main.fragment_reg_step3.*
import kotlinx.android.synthetic.main.fragment_vaccine_card.*
import kotlinx.android.synthetic.main.fragment_vaccine_card.ivBack
import kotlinx.android.synthetic.main.fragment_vaccine_qr_code.ivQRCode
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import io.michaelrocks.libphonenumber.android.NumberParseException

import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil


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


        ivCard.setCountryImage("https://flagcdn.com/32x24/us.png")

        ivCard.setPersonImage(Constants.USER?.profileUrl)
        ivCard.setLastName(Constants.USER?.lastName)
        ivCard.setFirstName(Constants.USER?.firstName)

        val list = Constants.USER?.document

        var pp: String = ""
        var id: String = ""
        var dl: String = ""

        if (list != null && list.isNotEmpty()) {
            for (doc in list) {
                when {

                    doc?.type.equals("Passport", true) -> {
                        if (!TextUtils.isEmpty(doc?.identity))
                            pp = "PP " + doc?.identity


                    }

                    doc?.type.equals("DL", true) -> {
                        if (!TextUtils.isEmpty(doc?.identity)) {
                            dl = "DL " + doc?.identity
                            //   ivCard.setDL("DL " + doc?.identity)
                        }

                    }


                    doc?.type.equals("ID", true) -> {
                        if (!TextUtils.isEmpty(doc?.identity))
                            id = "ID " + doc?.identity

                    }
                }
            }
        }

        if (!TextUtils.isEmpty(pp)) {
            ivCard.setPP(pp)
            if (!TextUtils.isEmpty(id)) {
                ivCard.setDL(id)
            } else {
                ivCard.setDL(dl)
            }
        } else if (!TextUtils.isEmpty(id)) {
            ivCard.setPP(id)
            if (!TextUtils.isEmpty(dl)) {
                ivCard.setDL(dl)
            }
        } else {
            ivCard.setDL(dl)
        }

        if (!Constants.USER?.address.isNullOrEmpty()) {
            var index = Constants.USER?.address!!.size - 1
            ivCard.setAddressline1(Constants.USER?.address?.get(index)?.addr1)
            if (!TextUtils.isEmpty(Constants.USER?.address?.get(index)?.addr2))
                ivCard.setAddressline2(Constants.USER?.address?.get(index)?.addr2)
            ivCard.setAddressline3(
                Constants.USER?.address?.get(index)?.city + "\n" +
                        Constants.USER?.address?.get(index)?.state + "\n" +
                        Constants.USER?.address?.get(index)?.zipCode + " " + Constants.USER?.address?.get(
                    index
                )?.country
            )

        }

        if (!Constants.USER?.dateOfBirth.isNullOrEmpty()) {
            try {

                var d = DateUtils.formatDate(
                    Constants.USER?.dateOfBirth!!,
                    DateUtils.API_DATE_FORMAT,
                    DateUtils.API_DATE_FORMAT
                )
                ivCard.setDob("DOB $d")
            } catch (e: Exception) {
                e.printStackTrace()
            }
//            etDob.isClickable = false
//            etDob.isEnabled = false
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
                    ivCard.setQrCode(bitmap)
                    // play with the bitmap
                    //ivQRCode.setImageBitmap(result.bitmap)
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