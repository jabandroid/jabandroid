package com.global.vtg.appview.home.help

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
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
import com.vtg.databinding.FragmentHelpBinding
import com.vtg.databinding.FragmentVaccineQrCodeBinding
import kotlinx.android.synthetic.main.fragment_vaccine_qr_code.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class HelpFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentHelpBinding
    private val viewModel by viewModel<HelpViewModel>()
    var bitmap: Bitmap? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_help
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentHelpBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        viewModel.policy.observe(this, {
            viewHtml(Constants.PRIVACY_POLICY)
        })

        viewModel.terms.observe(this, {
            viewHtml(Constants.TERMS_CONDITION)
        })
    }

    override fun pageVisible() {

    }
}