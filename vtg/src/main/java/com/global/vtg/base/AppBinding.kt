package com.global.vtg.base

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

class AppBinding {

    companion object {

        @BindingAdapter("setErrorMessage")
        @JvmStatic
        fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
            errorMessage?.let {
                view.error = it
            }
        }
    }
}