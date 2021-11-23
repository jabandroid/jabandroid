package com.global.vtg.appview.config

import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.loader.content.CursorLoader
import java.io.*
import java.nio.charset.Charset
import android.R.id
import java.lang.NumberFormatException


fun Context.requestMediaScanner(url: String) {
    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    val contentUri = Uri.fromFile(File(url))
    mediaScanIntent.data = contentUri
    this.sendBroadcast(mediaScanIntent)
}

fun String.toFile() = File(this)
fun saveFile(fullPath: String, content: String): File =
    fullPath.toFile().apply {
        writeText(content, Charset.defaultCharset())
    }

fun File.readFile(): String = this.readText(Charset.defaultCharset())
@TargetApi(Build.VERSION_CODES.KITKAT)
infix fun Uri.getRealPath(context: Context): String? {
    val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

    if (isKitKat && DocumentsContract.isDocumentUri(context, this)) {
        return checkAuthority(context)
    }

    if ("content".equals(this.scheme, ignoreCase = true)) {
        if ("com.google.android.apps.photos.content" == this.authority)
            return this.lastPathSegment

        return context.getDataColumns(this, null, null)
    }

    if ("file".equals(this.scheme, ignoreCase = true)) {
        return this.path
    }

    return this.path
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
private fun Uri.checkAuthority(context: Context): String? {
    val docId = DocumentsContract.getDocumentId(this)
    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

    if ("com.android.externalstorage.documents" == this.authority) {
        val type = split[0]

        if ("primary".equals(type, ignoreCase = true))
            return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
    } else if ("com.android.providers.downloads.documents" == this.authority) {
        return context.getDataColumns(
            ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                docId.toLong()), null, null)
    } else if ("com.android.providers.media.documents" == this.authority) {
        val contentUri = when (split[0]) {
            "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            else -> {
                val contentUriPrefixesToTry = arrayOf(
                    "content://downloads/public_downloads",
                    "content://downloads/my_downloads"
                )
                for (contentUriPrefix in contentUriPrefixesToTry) {
                    return try {
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse(contentUriPrefix),
                            docId.toLong()
                        )

                        /*   final Uri contentUri = ContentUris.withAppendedId(
                                                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));*/
                        return getDataColumn(context, contentUri, null, null)
                    } catch (e: NumberFormatException) {
                        //In Android 8 and Android P the id is not a number
                        this.path.replaceFirst("^/document/raw:", "").replaceFirst("^raw:", "")
                    }
                }
            }
        }

        return context.getDataColumns(contentUri as Uri?, "_id=?", arrayOf(split[1]))
    }

    return this.path
}


private const val STORAGE_TYPE_PRIMARY = "primary"
private const val STORAGE_TYPE_EXTERNAL = "external"

private const val PREFIX_ID_RAW = "raw:"
private const val PREFIX_ID_MSF = "msf:"

private const val MEDIA_TYPE_IMAGE = "image"
private const val MEDIA_TYPE_VIDEO = "video"
private const val MEDIA_TYPE_AUDIO = "audio"

private const val SCHEME_CONTENT = "content"
private const val SCHEME_FILE = "file"

private const val CONTENT_PATH_DOWNLOAD_PUBLIC = "content://downloads/public_downloads"
private const val CONTENT_PATH_DOWNLOAD_ME = "content://downloads/my_downloads"
private const val CONTENT_PATH_DOWNLOAD_ALL = "content://downloads/all_downloads"

private const val AUTHORITY_DOC_EXTERNAL = "com.android.externalstorage.documents"
private const val AUTHORITY_DOC_DOWNLOAD = "com.android.providers.downloads.documents"
private const val AUTHORITY_DOC_MEDIA = "com.android.providers.media.documents"

private const val DIR_DOCUMENT = "documents"

const val DOWNLOAD_BUFFER_8192 = 8192
const val DOWNLOAD_BUFFER_4096 = 4096
const val DOWNLOAD_BUFFER_1024 = 1024

private const val MSG_FILE_NOT_FOUND = "Could not get file path. Please try again"
private const val MSG_FILE_NOT_SAVED = "File could not be saved."


private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)

    try {
        cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } finally {
        cursor?.close()
    }
    return null

}

fun getFileName(@NonNull context: Context, uri: Uri): String {
    val mimeType = context.contentResolver.getType(uri)
    var filename: String = ""
    if (mimeType == null && context != null) {
        val path = uri.getPath(context)
        if (path == null) {
            filename = getName(uri.toString())!!
        } else {
            val file = File(path)
            filename = file.name
        }
    } else {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            filename = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
    }
    return filename
}

private fun getName(filename: String): String? {
    val index = filename.lastIndexOf('/')
    return filename.substring(index + 1)
}

private fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String) {
    var inputStream: InputStream? = null
    var bos: BufferedOutputStream? = null
    try {
        inputStream = context.contentResolver.openInputStream(uri)
        bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
        val buf = ByteArray(DOWNLOAD_BUFFER_1024)
        inputStream?.read(buf)
        do {
            bos.write(buf)
        } while (inputStream?.read(buf) != -1)
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            inputStream?.close()
            bos?.close()
        } catch (e: IOException) {
            Toast.makeText(context, MSG_FILE_NOT_SAVED, Toast.LENGTH_SHORT).show()
        }
    }
}


private fun generateFileName(name: String, directory: File): File? {
    var file = File(directory, name)
    if (file.exists()) {
        var fileName = name
        var extension = ""
        var dotIndex = name.lastIndexOf('.')
        if (dotIndex > 0) {
            fileName = name.substring(0, dotIndex)
            extension = name.substring(dotIndex)
        }
        var index = 0
        while (file.exists()) {
            index++
            val name1 = "$fileName($index)$extension"
            file = File(directory, name1)
        }
    }
    try {
        if (!file.createNewFile()) {
            return null
        }
    } catch (e: IOException) {
        return null
    }
    return file
}

private fun getDocumentCacheDir(@NonNull context: Context): File {
    val dir = File(context.cacheDir, DIR_DOCUMENT)
    if (!dir.exists()) {
        dir.mkdirs()
    }
    return dir
}


private fun Uri.getPath(context: Context): String? {

    // DocumentProvider
    if (DocumentsContract.isDocumentUri(context, this)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(this)) {
            val docId = DocumentsContract.getDocumentId(this)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]


            if (split.size > 1 && STORAGE_TYPE_PRIMARY.equals(type, ignoreCase = true)) {
                val path = Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                val actualFile = File(path)
                if (actualFile.canRead()) return path //if app can read path go ahead otherwise

                // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                val fileName = getFileName(context, this)
                val cacheDir = getDocumentCacheDir(context)
                val file = generateFileName(fileName, cacheDir)

                var destinationPath: String = ""
                if (file != null) {
                    destinationPath = file.absolutePath
                    saveFileFromUri(context, this, destinationPath)
                }
                return destinationPath

            } else {
                Toast.makeText(context, MSG_FILE_NOT_FOUND, Toast.LENGTH_SHORT).show()
            }
        } else if (isDownloadsDocument(this)) {
            val id = DocumentsContract.getDocumentId(this)
            if (id != null) {
                if (id.startsWith(PREFIX_ID_RAW) || id.startsWith(PREFIX_ID_MSF)) {
                    return id.substring(4)
                }
            }
            val contentUriPrefixesToTry = arrayOf<String>(CONTENT_PATH_DOWNLOAD_PUBLIC, CONTENT_PATH_DOWNLOAD_ME, CONTENT_PATH_DOWNLOAD_ALL)
            for (contentUriPrefix in contentUriPrefixesToTry) {
                try {
                    val contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), java.lang.Long.valueOf(id))

                    val path = getDataColumn(context, contentUri, null, null)
                    if (path != null) {
                        val file = File(path)
                        if (file.canRead()) return path //if app can read path go ahead otherwise
                        //return path
                    }
                } catch (e: Exception) {
                }
            }
            // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
            val fileName = getFileName(context, this)
            val cacheDir = getDocumentCacheDir(context)
            val file = generateFileName(fileName, cacheDir)

            var destinationPath: String = ""
            if (file != null) {
                destinationPath = file.absolutePath
                saveFileFromUri(context, this, destinationPath)
            }
            return destinationPath
        } else if (isMediaDocument(this)) {
            val docId = DocumentsContract.getDocumentId(this)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            var contentUri: Uri? = null
            if (MEDIA_TYPE_IMAGE == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if (MEDIA_TYPE_VIDEO == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if (MEDIA_TYPE_AUDIO == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            } else {
                contentUri = MediaStore.Files.getContentUri(STORAGE_TYPE_EXTERNAL)
            }

            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])

            val path = getDataColumn(context, contentUri, selection, selectionArgs)
            if (path != null) {
                val file = File(path)
                if (file.canRead()) return path //if app can read path go ahead otherwise
                //return path
            }

            // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
            val fileName = getFileName(context, this)
            val cacheDir = getDocumentCacheDir(context)
            val file = generateFileName(fileName, cacheDir)

            var destinationPath: String = ""
            if (file != null) {
                destinationPath = file.absolutePath
                saveFileFromUri(context, this, destinationPath)
            }
            return destinationPath
        } // MediaProvider
        // DownloadsProvider
    } else if (SCHEME_CONTENT.equals(this.scheme, ignoreCase = true)) {
        return getDataColumn(context, this, null, null)
    } else if (SCHEME_FILE.equals(this.scheme, ignoreCase = true)) {
        return this.path
    } // File
    // MediaStore (and general)

    return null //otherwise return null
}


// get Path
@TargetApi(Build.VERSION_CODES.KITKAT)
infix fun Uri.getRealPathFromURI(context: Context): String? {
    val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

    if (isKitKat && DocumentsContract.isDocumentUri(context, this)) {
        if (isExternalStorageDocument(this)) {
            val docId = DocumentsContract.getDocumentId(this)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
        } else if (isDownloadsDocument(this)) {
            val id = DocumentsContract.getDocumentId(this)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), id.toLong())

            return context.getDataColumns(contentUri, null, null)
        } else if (isMediaDocument(this)) {
            val docId = DocumentsContract.getDocumentId(this)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            when (type) {
                "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                "document" -> {
                    val proj = arrayOf(MediaStore.Images.Media.DATA)
                    val loader = CursorLoader(
                        context,
                        this,
                        proj,
                        null,
                        null,
                        null
                    )
                    val cursor: Cursor? = loader.loadInBackground()
                    val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    cursor?.moveToFirst()
                    columnIndex?.let {
                        val result = cursor.getString(columnIndex)
                        cursor.close()
                        return result
                    }
                }
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])

            return context.getDataColumns(contentUri, selection, selectionArgs)
        }
    } else return if ("content".equals(scheme, ignoreCase = true)) {
        if (isGooglePhotosUri(this)) lastPathSegment else context.getDataColumns(this, null, null)
    } else if ("file".equals(scheme, ignoreCase = true)) {
        path
    } else
        context.getRealPathFromURIDB(this)
    return null
}

private fun Context.getRealPathFromURIDB(contentUri: Uri): String {
    val cursor = contentResolver.query(contentUri, null, null, null, null)
    return if (cursor == null) {
        contentUri.path!!
    } else {
        cursor.moveToFirst()
        val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        val realPath = cursor.getString(index)
        cursor.close()
        realPath
    }
}

private fun Context.getDataColumns(uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)

    try {
        uri?.let {
            cursor = contentResolver.query(it, projection, selection, selectionArgs, null)
            cursor?.let {
                it.moveToFirst()
                val columnIndex = it.getColumnIndexOrThrow(column)
                return if (it.isNull(columnIndex)) {
                    null
                } else {
                    it.getString(columnIndex)
                }
            }
        }
    } finally {
        cursor?.close()
    }
    return null
}

private fun isExternalStorageDocument(uri: Uri): Boolean = "com.android.externalstorage.documents" == uri.authority
private fun isDownloadsDocument(uri: Uri): Boolean = "com.android.providers.downloads.documents" == uri.authority
private fun isMediaDocument(uri: Uri): Boolean = "com.android.providers.media.documents" == uri.authority
private fun isGooglePhotosUri(uri: Uri): Boolean = "com.google.android.apps.photos.content" == uri.authority
// Save file
fun saveToInternalStorage(context: Context, bitmapImage: Bitmap, fileName: String): String {
    val path = "/data/data/${context.packageName}/files"
    val myPath = File(path, "$fileName.png")
    if (myPath.exists())
        myPath.delete()
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(myPath)
        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        fos?.close()
    }
    return myPath.path
}
