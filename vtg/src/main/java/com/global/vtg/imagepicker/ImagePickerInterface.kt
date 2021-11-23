package com.global.vtg.imagepicker

import android.content.Intent

interface ImagePickerInterface {

    fun handleCamera(takePictureIntent: Intent)

    fun handleGallery(galleryPickerIntent: Intent)

}
