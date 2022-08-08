package com.global.vtg.appview.config

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.global.vtg.utils.Constants
import com.global.vtg.utils.ShareUtils
import com.theartofdev.edmodo.cropper.CropImage
import com.vtg.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class PickMediaExtensions {

    fun pickFromCamera(context: Context, callback: (Int, String, String?) -> Unit) =
        requestPhotoPick(
            context,
            PICK_FROM_CAMERA,
            callback
        )

    fun pickFromGallery(context: Context, callback: (Int, String, String?) -> Unit) =
        requestPhotoPick(
            context,
            PICK_FROM_GALLERY,
            callback
        )

    fun pickFromStorage(context: Context, callback: (Int, String, String?) -> Unit) =
        requestPhotoPick(
            context,
            PICK_ANY,
            callback
        )

    fun PickCsvFile(context: Context, callback: (Int, String, String?) -> Unit) =
        requestPhotoPick(
            context,
            PICK_CSV,
            callback
        )



    class ResultFragment() : Fragment() {
        companion object {
            private const val REQUEST_PERMISSION = 1001
            private const val NEEDED_PERMISSIONS = 1002
        }

        var displayName: String? = null
        lateinit var mContext: Context
        var mRequestCode: Int = 0
        private var fm: FragmentManager? = null
        var callback: ((Int, String, String?) -> Unit)? = null

        @SuppressLint("ValidFragment")
        constructor(fm: FragmentManager, callback: (Int, String, String?) -> Unit) : this() {
            this.fm = fm
            this.callback = callback
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setHasOptionsMenu(false)
            activity?.let {
                mContext = it
            }
        }

        override fun onAttach(context: Context) {
            super.onAttach(context)
            mContext = context
        }

        fun checkPermissions(arrays: Array<String>, code: Int) {
            mRequestCode = code
            when {
                Build.VERSION.SDK_INT < 23 -> grantedPermission(mRequestCode)
                mContext.isGranted(arrays).isEmpty() -> grantedPermission(mRequestCode)
                (mContext as Activity).isRationale(arrays).isNotEmpty() -> rationalePermission(
                    arrays
                )
                else -> requestPermissions(
                    arrays,
                    REQUEST_PERMISSION
                )
            }
        }

        private fun rationalPermissions(arrays: Array<String>, code: Int) {
            mRequestCode = code
            requestPermissions(
                arrays,
                NEEDED_PERMISSIONS
            )
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
        ) {
            val notGranted: Array<String> = mContext.isGranted(permissions)
            val notDenied: Array<String> = (mContext as Activity).isRationale(permissions)
            if (requestCode == REQUEST_PERMISSION || requestCode == NEEDED_PERMISSIONS) {
                when {
                    notGranted.isEmpty() -> {
                        grantedPermission(mRequestCode)
                    }
                    notDenied.isEmpty() -> {
                        deniedPermission()
                    }
                    else -> {
                        rationalePermission(notDenied)
                    }
                }
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }

        private fun grantedPermission(requestCode: Int) {
            requestPhotoPick(
                mContext,
                requestCode,
                callback as ((Int, String, String?) -> Unit)
            )
            fm?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()
        }

        private fun rationalePermission(arrays: Array<String>) {
            mContext.alert(
                mContext.getString(R.string.permission_desc_rational_photo),
                mContext.getString(R.string.permission_title_photo),
                mContext.getString(R.string.btn_proceed), false, callback = { _, which ->
                    when (which) {
                        DialogInterface.BUTTON_NEGATIVE -> {
                            failedPermission()
                        }
                        DialogInterface.BUTTON_POSITIVE -> {
                            rationalPermissions(arrays, mRequestCode)
                        }
                        else -> {
                            failedPermission()
                        }
                    }
                })
        }

        private fun deniedPermission() {
            callback?.invoke(PICK_PERMISSION_DENIED, "", null)
            fm?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()
        }

        private fun failedPermission() {
            fm?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            imageUri = data?.data ?: captureUri

            val uriString: String = imageUri.toString()
            val file = File(uriString)
            getOriginalPath(uriString, file)
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    if (resultCode == RESULT_OK) {
                        val resultUri = result.uri
                        val cacheFile = File(context?.cacheDir, resultUri.lastPathSegment)
                        getOriginalPath(resultUri.toString(), cacheFile)
                        callback?.invoke(PICK_SUCCESS, cacheFile.path, displayName)
                    }
                }
                PICK_FROM_CAMERA ->
                    when (resultCode) {
                        RESULT_OK -> {
                            try {
                                currentPhotoPath?.let {
                                    val path = Uri.parse(it).getRealPath(mContext)
                                    currentPhotoPath = path
                                    cropImage()
                                }
                            } catch (e: Exception) {

                                try {
                                    currentPhotoPath?.let {
                                        val path = Uri.parse(it).getRealPathFromURI(mContext)
                                        currentPhotoPath = path
                                        cropImage()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    toast("Fail to get image path, Please try again later.")
                                }
                            }
                        }
                        Activity.RESULT_CANCELED -> {
                            callback?.invoke(PICK_CANCELED, "", displayName)
                        }
                    }
                PICK_FROM_GALLERY ->
                    when (resultCode) {
                        RESULT_OK -> {
                            try {
                                currentPhotoPath = data?.data?.getRealPath((mContext)) as String
                                cropImage()
                            } catch (e: Exception) {
                                try {
                                    val path = data?.data?.getRealPathFromURI(mContext)
                                    currentPhotoPath = path
                                    cropImage()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    toast("Fail to get image path, Please try again later.")
                                }
                            }
                        }
                        Activity.RESULT_CANCELED -> {
                            callback?.invoke(PICK_CANCELED, "", displayName)
                        }
                    }
                PICK_FROM_VIDEO ->
                    if (resultCode == RESULT_OK) {
                        try {
                            currentPhotoPath = data?.data?.getRealPath((mContext)) as String
                            cropImage()
                        } catch (e: Exception) {

                            try {
                                val path = data?.data?.getRealPathFromURI(mContext)
                                currentPhotoPath = path
                                cropImage()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                toast("Fail to get image path, Please try again later.")
                            }
                        }
                    }
                PICK_ANY -> {
                    if (resultCode == RESULT_OK) {
                        try {
                            val path = ShareUtils.getPath(requireContext(), data?.data ?: return)
                            currentPhotoPath = path
                            if (context?.let {
                                    getMimeType(
                                        it,
                                        data.data!!
                                    )?.contains("image/")
                                } == true) {
                                cropImage()
                            } else {
                                getOriginalPath(imageUri.toString(), file)
                                currentPhotoPath = ShareUtils.getPath(requireContext(), data.data ?: return)
                                currentPhotoPath?.let {
                                    callback?.invoke(PICK_SUCCESS,
                                        it,
                                        displayName)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            toast("Fail to get document path, Please try again later.")
                        }
                    }
                }

                PICK_CSV -> {
                    if (resultCode == RESULT_OK) {
                        try {
                            val path = ShareUtils.getPath(requireContext(), data?.data ?: return)
                            currentPhotoPath = path

                                getOriginalPath(imageUri.toString(), file)
                                currentPhotoPath = ShareUtils.getPath(requireContext(), data.data ?: return)
                                currentPhotoPath?.let {
                                    callback?.invoke(PICK_SUCCESS,
                                        it,
                                        displayName)

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            toast("Fail to get document path, Please try again later.")
                        }
                    }
                }
            }
        }

        @SuppressLint("Range")
        fun getOriginalPath(uriString: String, file: File) {
            try {
                if (uriString.startsWith("content://")) {
                    var cursor: Cursor? = null
                    try {
                        cursor =
                            context?.contentResolver?.query(imageUri!!, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName =
                                cursor.getString(
                                    cursor.getColumnIndex(
                                        OpenableColumns.DISPLAY_NAME
                                    )
                                )
                        }
                    } finally {
                        cursor?.close()
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = file.name
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun cropImage() {
            imageUri?.let {
                activity?.let { it1 ->
                    CropImage.activity(it)
                        .start(it1)
                }
            }
        }

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

    companion object {
        @JvmField
        var instance: PickMediaExtensions =
            PickMediaExtensions()
        var IMAGE_CONTENT_URL = MediaStore.Images.Media.EXTERNAL_CONTENT_URI!!
        var VIDEO_CONTENT_URL = MediaStore.Video.Media.EXTERNAL_CONTENT_URI!!
        var currentPhotoPath: String? = null
        var currentVideoPath: String? = null
        var captureUri: Uri? = null
        var imageUri: Uri? = null
        const val PICK_FROM_CAMERA = 0
        const val PICK_FROM_GALLERY = 1
        const val PICK_FROM_VIDEO = 2
        const val PICK_FROM_CAMERA_VIDEO = 3
        const val PICK_ANY = 6
        const val PICK_CSV = 7
        const val PICK_SUCCESS = 3
        const val PICK_PERMISSION_DENIED = 1
        const val PICK_CANCELED = 5
        const val REQUEST_CROP_ICON = 10
    }
}

private fun getActivity(context: Context): AppCompatActivity? {
    var c = context

    while (c is ContextWrapper) {
        if (c is AppCompatActivity) {
            return c
        }
        c = c.baseContext
    }
    return null
}

private fun createImageUri(context: Context): Uri? {
    val contentResolver = context.contentResolver
    val cv = ContentValues()
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    cv.put(MediaStore.Images.Media.TITLE, timeStamp)
    return contentResolver.insert(PickMediaExtensions.IMAGE_CONTENT_URL, cv)
}

private fun createVideoUri(context: Context): Uri? {
    val contentResolver = context.contentResolver
    val cv = ContentValues()
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    cv.put(MediaStore.Images.Media.TITLE, timeStamp)
    return contentResolver.insert(PickMediaExtensions.VIDEO_CONTENT_URL, cv)
}

fun requestPhotoPick(context: Context, pickType: Int, callback: (Int, String, String?) -> Unit) {
    val fm = getActivity(context)?.supportFragmentManager
    val fragment = PickMediaExtensions.ResultFragment(fm as FragmentManager, callback)

    fm.beginTransaction().add(fragment, "FRAGMENT_TAG").commitAllowingStateLoss()
    fm.executePendingTransactions()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
        (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED)
    ) {
        fragment.checkPermissions(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ), pickType
        )
        return
    }
    val intent = Intent()

    when (pickType) {
        PickMediaExtensions.PICK_FROM_CAMERA -> {
            intent.action = MediaStore.ACTION_IMAGE_CAPTURE
            PickMediaExtensions.captureUri = createImageUri(context)
            PickMediaExtensions.currentPhotoPath = PickMediaExtensions.captureUri.toString()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, PickMediaExtensions.captureUri)
        }
        PickMediaExtensions.PICK_FROM_GALLERY -> {
            intent.action = Intent.ACTION_PICK
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "image/*"

        }
        PickMediaExtensions.PICK_FROM_VIDEO -> {
            intent.action = Intent.ACTION_PICK
            intent.type = MediaStore.Video.Media.CONTENT_TYPE
        }
        PickMediaExtensions.PICK_FROM_CAMERA_VIDEO -> {
            intent.action = MediaStore.ACTION_VIDEO_CAPTURE
            val captureUri = createVideoUri(context)
            PickMediaExtensions.currentVideoPath = captureUri.toString()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, captureUri)
        }
        PickMediaExtensions.PICK_CSV -> {

//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            intent.type = "text/csv"
            intent.action = Intent.ACTION_OPEN_DOCUMENT
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            intent.type = "text/*"
            val extraMimeTypes = arrayOf(
                Constants.CSV
            )
            intent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes)
        }

        PickMediaExtensions.PICK_ANY -> {
            intent.action = Intent.ACTION_OPEN_DOCUMENT
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            intent.type = "*/*"
            val extraMimeTypes = arrayOf(
                Constants.IMAGE_PNG,
                Constants.IMAGE_JPEG,
                Constants.IMAGE_JPG,
                Constants.DOC_PDF
            )
            intent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes)
        }
    }

    fragment.startActivityForResult(intent, pickType)
}