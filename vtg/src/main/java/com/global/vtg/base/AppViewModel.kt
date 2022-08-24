package com.global.vtg.base

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.*
import kotlin.coroutines.CoroutineContext


open class AppViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    val addFragment = MutableLiveData<Pair<AppFragmentState, Bundle?>>()
    val replaceFragment = MutableLiveData<Pair<AppFragmentState, Bundle?>>()
    val showProgress = MutableLiveData<Boolean>()

    val job = Job()

    val scope = CoroutineScope(coroutineContext)


    fun hasContent(s: String?): Boolean {
        return !isNullOrEmpty(s?.trim { it <= ' ' })

    }

    open fun isNullOrEmpty(s: String?): Boolean {
        return s == null || s.isEmpty() || s.trim().isEmpty()
    }

    final override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getCountryCode(countryName: String) =
        Locale.getISOCountries().find { Locale("", it).displayCountry == countryName }

    fun getMimeType(context: Context, uri: Uri): String? {
        var mimeType: String? = null
        mimeType = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cr = context.contentResolver
            cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                uri
                    .toString()
            )
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.lowercase(Locale.getDefault())
            )
        }
        return mimeType
    }
}