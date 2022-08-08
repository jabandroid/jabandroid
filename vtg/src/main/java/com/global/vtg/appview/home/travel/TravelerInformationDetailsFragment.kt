package com.global.vtg.appview.home.travel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.model.network.Resource
import com.global.vtg.test.Const
import com.global.vtg.utils.DialogUtils
import com.global.vtg.utils.NetworkUtils
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.vtg.R
import com.vtg.databinding.FragmentTravelInformatondetailsBinding
import kotlinx.android.synthetic.main.fragment_travel_informaton.ivBack
import kotlinx.android.synthetic.main.fragment_travel_informatondetails.*
import kotlinx.android.synthetic.main.inlcude_travel_info.view.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException


class TravelerInformationDetailsFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentTravelInformatondetailsBinding
    private var arrCode: ArrayList<TravelInfoItem> = ArrayList()
    private var count: Int = 0
    private val viewModel by viewModel<TravelViewModel>()
    override fun getLayoutId(): Int {
        return R.layout.fragment_travel_informatondetails
    }

    override fun preDataBinding(arguments: Bundle?) {
        arrCode = arguments!!.getParcelableArrayList<TravelInfoItem>("data")!!
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentTravelInformatondetailsBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {

        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        calApi()

        viewModel.codeLiveData.observe(this, {
            when (it) {
                is Resource.Success -> {

                }
                is Resource.Error -> {
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                }
            }
        })


//
    }

    private fun calApi() {
        if (arrCode.size > count) {

            when (activity) {
                is HomeActivity -> (activity as HomeActivity).showProgressBar()
                is ClinicActivity -> (activity as ClinicActivity).showProgressBar()
            }
            var country = arrCode[count].departureCountryCode.toString()
            country = getCountryCode(country).toString()
            val request = Request.Builder()
                .url(
                    "${Const.BASE_URL}/api/v1/covid/${arrCode[count].departureCode}/${country}"
//                    "${Const.BASE_URL}/api/v1/covid/AMD/IN"
                )
                .get()
                .build()

            NetworkUtils().getClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    when (activity) {
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                    }
                    DialogUtils.showSnackBar(context, e.toString())
                }

                @SuppressLint("LogNotTimber", "SetJavaScriptEnabled")
                override fun onResponse(call: Call, response: Response) {
                    val res = response.body?.string()
                    if (response.isSuccessful) {
                        val jsonParser = JsonParser()
                        val jo = jsonParser.parse(res) as JsonObject

                        if (!jo.has("errors")) {

                            activity!!.runOnUiThread(Runnable {

                                val inflater =
                                    activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                                val viewChild =
                                    inflater.inflate(R.layout.inlcude_travel_info, null)

                                viewChild.main_title.text =Html.fromHtml( arrCode[count].flightNumber +" "+arrCode[count].departureCode+" &rarr; "+arrCode[count].arrivalCode,Html.FROM_HTML_MODE_LEGACY)
                                viewChild.summary.isVerticalScrollBarEnabled = false
                                viewChild.summary.isHorizontalScrollBarEnabled = false
                                viewChild.summary.settings.useWideViewPort = false
                                viewChild.summary.settings.javaScriptEnabled=true
                                viewChild.summary.settings.setLoadWithOverviewMode(false)
                                viewChild.summary.webViewClient = object : WebViewClient() {
                                    override fun shouldOverrideUrlLoading(
                                        view: WebView,
                                        urlNewString: String
                                    ): Boolean {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(urlNewString)
                                        )
                                        startActivity(intent)
                                        return true
                                    }

                                    fun onPageStarted(view: WebView?, url: String?) {

                                    }

                                    override fun onPageFinished(view: WebView, url: String) {

                                    }
                                }
                                var string = ""
                                try {
                                    var data = jo.get("data").asJsonObject
                                    if (data.has("areaAccessRestriction")) {
                                        var obj = data.get("areaAccessRestriction").asJsonObject
                                        if (obj.has("entry")) {
                                            string =
                                                "<b> Entry </b> <br>" + obj.get("entry").asJsonObject.get("date")
                                                    .asString.replace(" \"", "") + "<br>"
                                            string += obj.get("entry").asJsonObject.get("text")
                                                .asString.replace(" \"", "")



                                            if (obj.get("entry").asJsonObject.get("rules")
                                                    .toString()
                                                    .contains("https")
                                            )
                                                string += "<br> <a href=" +
                                                        obj.get("entry").asJsonObject.get("rules")
                                                            .toString() + ">" + obj.get("entry").asJsonObject.get(
                                                    "rules"
                                                ).asString+"</a>"

                                        }

                                        if (obj.has("diseaseTesting")) {
                                            string +=
                                                "<br><br> <b> Disease Testing</b> <br>" + obj.get("diseaseTesting").asJsonObject.get(
                                                    "date"
                                                )
                                                    .asString.replace(" \"", "") + "<br>"
                                            string += obj.get("diseaseTesting").asJsonObject.get("text")
                                                .asString.replace(" \"", "")


//                                            if (obj.get("diseaseTesting").asJsonObject.has("isRequired"))
//                                                string += " <br> isRequired " + obj.get("diseaseTesting").asJsonObject.get(
//                                                    "isRequired"
//                                                )
//                                            if (obj.get("diseaseTesting").asJsonObject.has("when"))
//                                                string += "when  " + obj.get("diseaseTesting").asJsonObject.get(
//                                                    "when"
//                                                )

                                            if (obj.get("diseaseTesting").asJsonObject.get("rules")
                                                    .toString()
                                                    .contains("https")
                                            )
                                                string += "<br> <a href=" +
                                                        obj.get("diseaseTesting").asJsonObject.get("rules")
                                                            .asString + ">" + obj.get("entry").asJsonObject.get(
                                                    "rules"
                                                ).asString+"</a>"


                                        }
                                        if (obj.has("tracingApplication")) {
                                            string +=
                                                "<br><br><b> Tracing Application</b> <br>" + obj.get("tracingApplication").asJsonObject.get(
                                                    "date"
                                                )
                                                    .asString.replace(" \"", "") + "<br>"
                                            string += obj.get("tracingApplication").asJsonObject.get(
                                                "text"
                                            )
                                                .asString.replace(" \"", "")





                                            if (obj.get("tracingApplication").asJsonObject.has("iosUrl")

                                            ) {

                                                var k= obj.get("tracingApplication").asJsonObject.get(
                                                    "iosUrl"
                                                ).asJsonArray

                                                var t=k.get(0).asString
                                                string += "Ios Link :"
                                                string += "<br> <a href=$t>$t</a>"

                                            }

                                            if (obj.get("tracingApplication").asJsonObject.has("androidUrl")

                                            ) {

                                                var k= obj.get("tracingApplication").asJsonObject.get(
                                                    "androidUrl"
                                                ).asJsonArray

                                                var t=k.get(0).asString
                                                string += "<br>Android Link :"
                                                string += "<br> <a href=$t>$t</a>"

                                            }



                                        }
                                    }
//                                    viewChild.summary.text =
//                                        (Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY))

                                    string="<![CDATA[<body style=\"text-align:justify; \">"+string+   "</body>"
                                    viewChild.summary.loadData(
                                        string.replace("\\n",""),
                                        "text/html; charset=UTF-8",
                                        null
                                    )
                                    main.addView(viewChild)
                                    count++

                                    calApi()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    string="<![CDATA[<body style=\"text-align:justify; \">"+string+   "</body>"
                                    viewChild.summary.loadData(
                                        string.replace("\\n",""),

                                        "text/html; charset=UTF-8",
                                        null
                                    )
                                    main.addView(viewChild)
                                    count++

                                    calApi()
                                }
                            })



                        } else {


                            Handler().postDelayed({
                                activity!!.runOnUiThread(Runnable {
                                    when (activity) {
                                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                                    }
                                    DialogUtils.showSnackBar(
                                        context,
                                        jo.get("errors").asJsonArray.get(0).asJsonObject.get("detail")
                                            .toString()
                                    )
                                })
                            }, 3000)

                        }

                    } else {
                        Handler().postDelayed({
                            activity!!.runOnUiThread(Runnable {
                                activity!!.runOnUiThread(Runnable {
                                    when (activity) {
                                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                                    }
                                })
                            })
                        }, 3000)
                    }
                }
            })
        } else {

            Handler().postDelayed({
                activity!!.runOnUiThread(Runnable {
                    activity!!.runOnUiThread(Runnable {
                        when (activity) {
                            is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                            is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        }
                    })
                })
            }, 4000)

        }
    }

    override fun pageVisible() {

    }
}



