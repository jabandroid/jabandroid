package com.global.vtg.base

//import com.global.vtg.utils.Constants.BASE_URL

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.braintreepayments.api.dropin.DropInActivity
import com.braintreepayments.api.dropin.DropInResult
import com.global.vtg.appview.authentication.registration.*
import com.global.vtg.appview.config.PickMediaExtensions
import com.global.vtg.appview.config.ResConfig
import com.global.vtg.appview.home.event.CreateEventLocationFragment
import com.global.vtg.appview.home.event.CreateSubEventFragment
import com.global.vtg.base.fragment.notifyFragment
import com.global.vtg.test.Const.BASE_URL
import com.global.vtg.utils.Constants
import com.global.vtg.utils.Constants.AUTOCOMPLETE_REQUEST_CODE
import com.global.vtg.utils.Constants.CONFIG
import com.global.vtg.utils.Constants.DLN_AUTOCOMPLETE_REQUEST_CODE
import com.global.vtg.utils.Constants.PASSPORT_AUTOCOMPLETE_REQUEST_CODE
import com.global.vtg.utils.Constants.isShowing
import com.global.vtg.wscoroutine.ApiInterface
import com.global.vtg.wscoroutine.CustomCoroutineScope
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import com.vtg.R
import kotlinx.coroutines.CoroutineScope
import okhttp3.*
import okhttp3.Response
import org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
import java.io.IOException
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


abstract class AppActivity : AppCompatActivity() {

    val stack = Stack<Fragment>()
    var ft: FragmentTransaction? = null
    private val client = OkHttpClient()
    lateinit var placesClient: PlacesClient


    /**
     *To initialize the component you want to initialize before inflating layout
     */
    private fun preInflateInitialization() {
        /*1. Windows transition
        * 2. Permission utils initialization*/
        window.apply {
//            setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = Color.TRANSPARENT
        }

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, resources.getString(R.string.api_key))
        }
        placesClient = Places.createClient(this)
    }

    /**
     * @return layout resource id
     */
    @LayoutRes
    abstract fun getLayoutId(): Int


    abstract fun postDataBinding(binding: ViewDataBinding?)

    /**
     *To initialize the activity components
     */
    protected abstract fun initializeComponent()

    // Common Handling of top bar for all fragments like header name, icon on top bar in case of moving to other fragment and coming back again
//    abstract fun <T> setUpFragmentConfig(currentState: IFragmentState, keys: T?)

    override fun onCreate(savedInstanceState: Bundle?) {
        preInflateInitialization()
        //setContentView(getLayoutId())
        val binding = DataBindingUtil.setContentView(this, getLayoutId()) as ViewDataBinding?
        super.onCreate(savedInstanceState)
        postDataBinding(binding)
        initializeComponent()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    fun getActivityScope(activity: AppCompatActivity): CoroutineScope {
        val localScopeApiHandle = CustomCoroutineScope()
        activity.lifecycle.addObserver(localScopeApiHandle)
        return localScopeApiHandle.getCoroutineScope()
    }

    override fun onBackPressed() {
        if (!isShowing)
            notifyFragment()
    }


    internal fun getAppActivity(): AppActivity {
        return this@AppActivity
    }

    internal fun getFragmentContainerId(): Int {
        return R.id.container
    }

    fun enableFullScreen(isEnable: Boolean) {
        if (isEnable) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val fragments = supportFragmentManager.fragments
            if (requestCode == AUTOCOMPLETE_REQUEST_CODE ||
                requestCode == DLN_AUTOCOMPLETE_REQUEST_CODE ||
                requestCode == PASSPORT_AUTOCOMPLETE_REQUEST_CODE
            ) {
                val place = data?.let { Autocomplete.getPlaceFromIntent(it) }

                try {
                    val components = place?.addressComponents
                    var city = ""
                    var state = ""
                    var country = ""

                    if (components != null) {
                        for (component in components.asList()) {
                            val type: String = component.types[0]
                            val name: String = component.name

                            when (type) {
                                "street_number" -> {
                                }
                                "route" -> {
                                }
                                "postal_code" -> {
                                }
                                "postal_code_suffix" -> {
                                }
                                "locality" -> {
                                    city = name
                                }
                                "sublocality_level_1" -> {
                                    city = name
                                }
                                "administrative_area_level_1" -> {
                                    state = name
                                }
                                "administrative_area_level_2" -> {

                                }
                                "country" -> {
                                    country = name
                                }
                            }
                        }
                    }

                    for (frg in fragments) {
                        if (frg is RegistrationStep1Fragment) {
                            city.let { frg.updateAddress(it, state, country) }
                        } else if (frg is RegistrationStep2Fragment) {
                            if (requestCode == DLN_AUTOCOMPLETE_REQUEST_CODE) {
                                frg.updateDlnAddress(state, country)
                            } else if (requestCode == PASSPORT_AUTOCOMPLETE_REQUEST_CODE) {
                                frg.updatePassportAddress(state, country)
                            }
                        } else if (frg is RegistrationStep3Fragment) {
                            city.let { frg.updateAddress(it, state, country) }
                        }else if (frg is AddClinicFragment) {
                            city.let { frg.updateAddress(it, state, country) }
                        }
                        else if (frg is CreateEventLocationFragment) {
                            city.let { frg.updateAddress(it, state, country) }
                        }
                        else if (frg is CreateSubEventFragment) {
                            city.let { frg.updateAddress(it, state, country) }
                        }
                        else if (frg is VendorRegistrationStep2Fragment) {
                            city.let { frg.updateAddress(it, state, country) }
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } else if (requestCode == Constants.DROP_IN_REQUEST) {
                val result: DropInResult? =
                    data?.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT)
                Log.d("AANAL", "AANAL === ${result?.paymentMethodType}")
            } else {
                if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    for (fragment in supportFragmentManager.fragments) {
                        if (fragment is PickMediaExtensions.ResultFragment) {
                            fragment.onActivityResult(requestCode, resultCode, data)
                            fragment.activity?.supportFragmentManager?.beginTransaction()?.remove(fragment)?.commitAllowingStateLoss()
                        }
                    }
                }
            }
        } else if (resultCode == RESULT_FIRST_USER) {
            val error: Exception = data?.getSerializableExtra(DropInActivity.EXTRA_ERROR) as Exception
            Log.d("AANAL", "AANAL === ${error.message}")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    open fun getImagePath(uri: Uri?): String? {
        // Will return "image:x*"
        // Will return "image:x*"
        val wholeID = DocumentsContract.getDocumentId(uri)

// Split at colon, use second item in the array

// Split at colon, use second item in the array
        val id = wholeID.split(":").toTypedArray()[1]

        val column = arrayOf(MediaStore.Images.Media.DATA)

// where id is equal to

// where id is equal to
        val sel = MediaStore.Images.Media._ID + "=?"

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            column, sel, arrayOf(id), null
        )

        var filePath: String? = ""

        val columnIndex = cursor!!.getColumnIndex(column[0])

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex)
        }

        cursor.close()
        return filePath
    }

    open fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String
        val cursor = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path!!
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    open fun getRealPathFromURI(context: Context, uri: Uri?): String? {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri!!, proj, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(proj[0])
                result = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        if (result == null) {
            result = "Not found"
        }
        return result
    }

    /*override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }*/

    open fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected
    }

    fun onSearchCalled(requestCode: Int) {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields = listOf(
            Place.Field.ID, Place.Field.NAME,
            Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG
        )



        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this)
        startActivityForResult(intent, requestCode)
    }

    fun getConfig() {

         var apiServiceInterface: ApiInterface

        val request = Request.Builder()
            .url(
                "$BASE_URL/api/v1/config/1"
            )
            .get()
            .build()

        getClient() .newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
//                progressBar.postValue(false)
                Log.e("",""+e)
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body?.string()
                if(response.isSuccessful)
                CONFIG = Gson().fromJson(res, ResConfig::class.java)
            }
        })
    }

    private fun getClient() : OkHttpClient {
        try {
            // Create a trust manager that does not validate certificate chains
//            val trustAllCerts = arrayOf<TrustManager>(
//                object : X509TrustManager {
//                    @SuppressLint("TrustAllX509TrustManager")
//                    @Throws(CertificateException::class)
//                    override fun checkClientTrusted(
//                        chain: Array<X509Certificate?>?,
//                        authType: String?
//                    ) {
//                    }
//
//                    @Throws(CertificateException::class)
//                    override fun checkServerTrusted(
//                        chain: Array<X509Certificate?>?,
//                        authType: String?
//                    ) {
//                    }
//
//                    override fun getAcceptedIssuers(): Array<X509Certificate> {
//                        return arrayOf()
//                    }
//                }
//            )
//
//            // Install the all-trusting trust manager
//            val sslContext = SSLContext.getInstance("SSL")
//            sslContext.init(null, trustAllCerts, SecureRandom())
//
//            // Create an ssl socket factory with our all-trusting manager
//            val sslSocketFactory = sslContext.socketFactory
//            val builder = OkHttpClient.Builder()
//            builder
//                .connectTimeout(10, TimeUnit.SECONDS)
//                .readTimeout(10, TimeUnit.SECONDS)
//                .writeTimeout(10, TimeUnit.SECONDS)
//
//
//            builder.sslSocketFactory(
//                sslSocketFactory,
//                trustAllCerts[0] as X509TrustManager
//            )
//
//            val allHostsValid =
//                HostnameVerifier { hostname, session -> true }
//
//             builder.hostnameVerifier(allHostsValid)
//
//            return builder.build()

            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
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

                    override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val trustManagerFactory: TrustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers: Array<TrustManager> =
                trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + trustManagers.contentToString()
            }

            val trustManager =
                trustManagers[0] as X509TrustManager


            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustManager)
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })
            builder
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)

       return     builder.build()
        } catch (e: java.lang.Exception) {
            throw RuntimeException(e)
        }

    }
}
