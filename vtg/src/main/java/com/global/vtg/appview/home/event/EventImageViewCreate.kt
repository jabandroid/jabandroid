package com.global.vtg.appview.home.event

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.global.vtg.appview.config.ResConfig
import com.global.vtg.test.Const
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DialogUtils
import com.global.vtg.wscoroutine.ApiInterface
import com.google.gson.Gson
import com.tslogistics.util.AppAlertDialog
import com.vtg.R
import kotlinx.android.synthetic.main.include_image.view.*
import okhttp3.*
import org.koin.androidx.viewmodel.ext.android.viewModel

import retrofit2.http.Url
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


@SuppressLint("ViewConstructor")
class EventImageViewCreate(context: Context?, name: Bitmap?, url: String?,id:String,count:Int, isnew :Boolean, completedTripDetailActivity: CreateEventReviewFragment, v: LinearLayout) : RelativeLayout(context) {
    private val client = OkHttpClient()
    init {
      val view=  View.inflate(context, R.layout.include_image, this)
       val upload= view.findViewById<ImageView>(R.id.upload)
       val actual= view.findViewById<ImageView>(R.id.actual)
       val ic_delete= view.findViewById<ImageView>(R.id.ic_delete)
       val fl_actual= view.findViewById<FrameLayout>(R.id.fl_actual)
        upload.visibility = View.GONE

        if(isnew)
            upload.visibility = View.VISIBLE

        fl_actual.visibility = View.VISIBLE
       // listOfImages.add(obj.attributes.signed_url)
        if(name!=null) {
            ic_delete.visibility=View.VISIBLE

            actual.setImageBitmap(name)


        } else if(!TextUtils.isEmpty(url!!.toString())){
            ic_delete.visibility=View.VISIBLE
            Glide.with(context!!)
                .load(url)
                .apply(
                    RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(actual)

        }else
            fl_actual.visibility = View.GONE

        actual.setOnClickListener {
            completedTripDetailActivity.onViewClick()
        }
        upload.setOnClickListener {
            completedTripDetailActivity.onImageClick()
        }
        v.tag=count

        ic_delete.tag=count
        ic_delete.setOnClickListener {
            AppAlertDialog().showAlert(
                context!! as FragmentActivity,
                object : AppAlertDialog.GetClick {
                    override fun response(type: String) {
                        if(TextUtils.isEmpty(id)) {
                            completedTripDetailActivity.onDeleteClick(it.tag as Int, v, id)
                        }else{
                            completedTripDetailActivity.onDeleteClick(it.tag as Int, v, id)
                            deletePic(id,it.tag as Int, v,completedTripDetailActivity)
                        }

                    }
                }, context.getString(R.string.delete_image), "Yes", "No"

            )


        }

    }

    public fun loadImage(url: String, actualimg : ImageView,context: Context?){

    }



    interface OnDeleteImageClick{
        fun onDeleteClick(item: Int, ll:LinearLayout,url:String)

        fun onImageClick()
        fun onViewClick()
    }


  public  fun hideAdd(v :View){
        v.visibility=View.INVISIBLE
    }
   public fun showAdd(v :View){
        v.visibility=View.VISIBLE
    }


    fun deletePic(id:String,item: Int, ll:LinearLayout,completedTripDetailActivity: CreateEventReviewFragment) {

        var apiServiceInterface: ApiInterface

        val request = Request.Builder()
            .url(
                "${Const.BASE_URL}/api/v1/event/profile/"+id
            )
            .delete()
            .build()

        getClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
//                progressBar.postValue(false)
                Log.e("",""+e)
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body?.string()
                if(response.isSuccessful)
                 //   completedTripDetailActivity.onDeleteClick(item,ll,id)
                else{
                    //DialogUtils.toast(context,"Error in image delete")
                }
            }
        })
    }

    private fun getClient() : OkHttpClient {
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)


            builder.sslSocketFactory(
                sslSocketFactory,
                trustAllCerts[0] as X509TrustManager
            )
            builder.hostnameVerifier(HostnameVerifier { hostname, session -> true })

            return builder.build()
        } catch (e: java.lang.Exception) {
            throw RuntimeException(e)
        }

    }


}
