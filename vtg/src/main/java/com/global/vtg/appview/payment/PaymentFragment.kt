package com.global.vtg.appview.payment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.databinding.ViewDataBinding
import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.models.ThreeDSecurePostalAddress
import com.braintreepayments.api.models.ThreeDSecureRequest
import com.global.vtg.base.AppFragment
import com.global.vtg.utils.Constants
import com.global.vtg.utils.KeyboardUtils
import com.vtg.R
import com.vtg.databinding.FragmentPaymentBinding
import kotlinx.android.synthetic.main.fragment_payment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.braintreepayments.api.models.ThreeDSecureAdditionalInformation




class PaymentFragment : AppFragment() {
    private val viewModel by viewModel<PaymentViewModel>()
    private lateinit var mFragmentBinding: FragmentPaymentBinding
    var isFirstTime = true
    var productAmount = 0.0

    override fun getLayoutId(): Int {
        return R.layout.fragment_payment
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentPaymentBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        val productList = Constants.CONFIG?.productList
        val list = ArrayList<String>()
        if (productList != null && productList.isNotEmpty()) {
            for (product in productList) {
                product?.name?.let { list.add(it) }
            }
        }

        sProduct.adapter = ProductSpinnerAdapter(
            getAppActivity(), list
        )

        sProduct.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                KeyboardUtils.hideKeyboard(view)
                if (!isFirstTime) {
                    viewModel.product.value = list[pos - 1]
                    productAmount = productList?.get(pos - 1)?.cost ?: 0.0
                } else {
                    isFirstTime = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        viewModel.isDifferentAddress.observe(this, {
            clBillingAddress.visibility = if(it == true) View.VISIBLE else View.GONE
        })

        viewModel.makePayment.observe(this, {
            if (it == true)
                makePayment()
        })
    }

    override fun pageVisible() {

    }

    private fun makePayment() {
        val billingAddress = ThreeDSecurePostalAddress()
            .givenName(Constants.USER?.firstName)
            .surname(Constants.USER?.lastName)
            .phoneNumber(Constants.USER?.mobileNo)
            .streetAddress(viewModel.address?.addr1)
            .extendedAddress(viewModel.address?.addr2)
            .locality(viewModel.address?.city)
            .region(viewModel.address?.state)
            .postalCode(viewModel.address?.zipCode)
            .countryCodeAlpha2(viewModel.address?.country?.let { getCountryCode(it) })

        val additionalInformation = ThreeDSecureAdditionalInformation()
            .accountId("account-id")
        val threeDSecureRequest = ThreeDSecureRequest()
            .amount(productAmount.toString())
            .versionRequested(ThreeDSecureRequest.VERSION_2)
            .email(Constants.USER?.email)
            .mobilePhoneNumber(Constants.USER?.mobileNo)
            .billingAddress(billingAddress)
            .additionalInformation(additionalInformation)


// Optional additional information.
// For best results, provide as many of these elements as possible.


// Optional additional information.
// For best results, provide as many of these elements as possible.

        val dropInRequest = DropInRequest()
            .clientToken(Constants.USER?.barcodeId)
            .requestThreeDSecureVerification(true)
            .threeDSecureRequest(threeDSecureRequest)

        startActivityForResult(dropInRequest.getIntent(context), Constants.DROP_IN_REQUEST)
    }
}