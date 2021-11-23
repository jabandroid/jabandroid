package com.global.vtg.utils

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.annotation.NonNull
import com.global.vtg.appview.config.ImageFilePath.isDownloadsDocument
import com.global.vtg.appview.config.ImageFilePath.isExternalStorageDocument
import com.global.vtg.appview.config.ImageFilePath.isMediaDocument
import java.io.*


object ShareUtils {
    private const val STORAGE_TYPE_PRIMARY = "primary"
    private const val STORAGE_TYPE_EXTERNAL = "external"
    private const val SCHEME_CONTENT = "content"
    private const val SCHEME_FILE = "file"
    private const val MSG_FILE_NOT_FOUND = "Could not get file path. Please try again"
    private const val MSG_FILE_NOT_SAVED = "File could not be saved."
    private const val CONTENT_PATH_DOWNLOAD_PUBLIC = "content://downloads/public_downloads"
    private const val CONTENT_PATH_DOWNLOAD_ME = "content://downloads/my_downloads"
    private const val CONTENT_PATH_DOWNLOAD_ALL = "content://downloads/all_downloads"
    private const val PREFIX_ID_RAW = "raw:"
    private const val PREFIX_ID_MSF = "msf:"
    private const val MEDIA_TYPE_IMAGE = "image"
    private const val MEDIA_TYPE_VIDEO = "video"
    private const val MEDIA_TYPE_AUDIO = "audio"
    private const val DOWNLOAD_BUFFER_1024 = 1024
    private const val DIR_DOCUMENT = "documents"

    fun share(context: Context, share_text: String) {
        share(context, share_text)
    }


    fun shareImage(context: Context, uri: Uri, title: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = "image/jpeg"
        context.startActivity(Intent.createChooser(shareIntent, title))
    }


    fun share(context: Context, extraText: String, subject: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, extraText)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(
                Intent.createChooser(intent, "Sent"))
    }

    fun getPath(context: Context, uri: Uri): String? {

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]


                if (split.size > 1 && STORAGE_TYPE_PRIMARY.equals(type, ignoreCase = true)) {
                    val path = Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    val actualFile = File(path)
                    if (actualFile.canRead()) return path //if app can read path go ahead otherwise

                    // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                    val fileName = getFileName(context, uri)
                    val cacheDir = getDocumentCacheDir(context)
                    val file = generateFileName(fileName, cacheDir)

                    var destinationPath = ""
                    if (file != null) {
                        destinationPath = file.absolutePath
                        saveFileFromUri(context, uri, destinationPath)
                    }
                    return destinationPath

                } else {
                    Toast.makeText(context, MSG_FILE_NOT_FOUND, Toast.LENGTH_SHORT).show()
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                if (id != null) {
                    if (id.startsWith(PREFIX_ID_RAW) || id.startsWith(PREFIX_ID_MSF)) {
                        return id.substring(4)
                    }
                }
                val contentUriPrefixesToTry = arrayOf(
                    CONTENT_PATH_DOWNLOAD_PUBLIC,
                    CONTENT_PATH_DOWNLOAD_ME,
                    CONTENT_PATH_DOWNLOAD_ALL
                )
                for (contentUriPrefix in contentUriPrefixesToTry) {
                    try {
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse(contentUriPrefix),
                            java.lang.Long.valueOf(id)
                        )

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
                val fileName = getFileName(context, uri)
                val cacheDir = getDocumentCacheDir(context)
                val file = generateFileName(fileName, cacheDir)

                var destinationPath = ""
                if (file != null) {
                    destinationPath = file.absolutePath
                    saveFileFromUri(context, uri, destinationPath)
                }
                return destinationPath
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                val contentUri: Uri?
                contentUri = when (type) {
                    MEDIA_TYPE_IMAGE -> {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    MEDIA_TYPE_VIDEO -> {
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    MEDIA_TYPE_AUDIO -> {
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    else -> {
                        MediaStore.Files.getContentUri(STORAGE_TYPE_EXTERNAL)
                    }
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
                val fileName = getFileName(context, uri)
                val cacheDir = getDocumentCacheDir(context)
                val file = generateFileName(fileName, cacheDir)

                var destinationPath = ""
                if (file != null) {
                    destinationPath = file.absolutePath
                    saveFileFromUri(context, uri, destinationPath)
                }
                return destinationPath
            } // MediaProvider
            // DownloadsProvider
        } else if (SCHEME_CONTENT.equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if (SCHEME_FILE.equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        } // File
        // MediaStore (and general)

        return null //otherwise return null
    }

    fun getFileName(@NonNull context: Context, uri: Uri): String {
        val mimeType = context.contentResolver.getType(uri)
        var filename = ""
        if (mimeType == null) {
            val path = getPath(context, uri)
            filename = if (path == null) {
                this.getName(uri.toString())!!
            } else {
                val file = File(path)
                file.name
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

    fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null

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


    fun generateFileName(name: String, directory: File): File? {
        var file = File(directory, name)
        if (file.exists()) {
            var fileName = name
            var extension = ""
            val dotIndex = name.lastIndexOf('.')
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

    fun getDocumentCacheDir(@NonNull context: Context): File {
        val dir = File(context.cacheDir, DIR_DOCUMENT)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun getDocumentDir(@NonNull context: Context): File {
        val dir = File(context.filesDir, DIR_DOCUMENT)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun getImageSizeInMB(uri: Uri, context: Context): Int {
        val returnCursor: Cursor? =
            context.contentResolver.query(uri, null, null, null, null)
        val sizeIndex = returnCursor?.getColumnIndex(OpenableColumns.SIZE)
        returnCursor?.moveToFirst()
        val length = returnCursor?.getLong(sizeIndex ?: 0)?.toInt() ?: 0
        returnCursor?.close()
        return length / 1000000
    }
}
