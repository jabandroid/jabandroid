package com.global.vtg.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import com.global.vtg.App
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.appview.config.ResConfig
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

/**
 * Created by sa on 29/03/17.
 *
 * All application constants should be here.
 *
 */
object Constants {


    const val BASE_URL = "https://vaxserver.com/"

    var PREFERENCE_NAME = "pref_app_name"

    const val MAX_CLICK_INTERVAL: Long = 500//Max time interval to prevent double click
    const val REQUEST_CODE_ASK_PERMISSIONS = 10001
    const val NO_INTERNET_REQ_CODE = 2145
    const val PICK_FILE_REQUEST_CODE = 1000
    const val PICK_IMAGE_REQUEST_CODE = 1001
    const val AUTOCOMPLETE_REQUEST_CODE = 1002
    const val DLN_AUTOCOMPLETE_REQUEST_CODE = 1003
    const val PASSPORT_AUTOCOMPLETE_REQUEST_CODE = 1004
    const val CAMERA_REQUEST_CODE = 1005
    const val DROP_IN_REQUEST = 1006
    const val ANIMATION = "Animation"
    const val USER_TYPE = "userType"
    var REQUEST_PICKED = 0

    enum class USERTYPE(val type: Int) {
        USER(1),
        VENDOR(2)
    }

    // PREFERENCES
    const val BUNDLE_REGISTRATION_ID = "BUNDLE_REGISTRATION_ID"
    const val BUNDLE_REGISTRATION_PHONE = "BUNDLE_REGISTRATION_PHONE"
    const val BUNDLE_REGISTRATION_CODE = "BUNDLE_REGISTRATION_CODE"
    const val BUNDLE_REGISTRATION_COUNTRY_CODE = "BUNDLE_REGISTRATION_COUNTRY_CODE"
    const val BUNDLE_REGISTRATION_EMAIL = "BUNDLE_REGISTRATION_EMAIL"
    const val BUNDLE_REGISTRATION_PASSWORD = "BUNDLE_REGISTRATION_PASSWORD"
    const val BUNDLE_IS_VENDOR = "BUNDLE_IS_VENDOR"
    const val BUNDLE_IS_CLINIC = "BUNDLE_IS_CLINIC"
    const val BUNDLE_BARCODE_ID = "BUNDLE_BARCODE_ID"
    const val BUNDLE_FROM_FORGOT_PASSWORD = "BUNDLE_FROM_FORGOT_PASSWORD"
    const val BUNDLE_TWILIO_USER_ID = "BUNDLE_TWILIO_USER_ID"
    const val BUNDLE_FROM_PROFILE = "BUNDLE_FROM_PROFILE"

    // Mime type
    const val IMAGE_PNG = "image/png"
    const val IMAGE_JPEG = "image/jpeg"
    const val IMAGE_JPG = "image/jpg"
    const val DOC_PDF = "application/pdf"
    const val PRIVACY_POLICY = "https://app.termly.io/document/privacy-policy/4c62edb0-8586-4d27-9c33-9dc1bd7782b8"
    const val TERMS_CONDITION = "https://app.termly.io/document/terms-of-use-for-ios-app/1c67aa22-22e4-45ab-b63b-6e574d21ea87"

    var CONFIG: ResConfig? = null
    var USER: ResUser? = null
    var SCANNEDUSER: ResUser? = null

    var IS_SIGN_IN = false
    var tempUsername = ""
    var tempPassword = ""
    var twilioUserId: Int? = null
    var isShowing = false

    fun getInstituteName(id: Int): String? {
        val list = CONFIG?.institute
        if (list?.isNotEmpty() == true) {
            for (vaccine in list) {
                if (vaccine?.id == id) {
                    return vaccine.name
                }
            }
        }
        return ""
    }

    fun assistActivity(activity: Activity) {
        workAround(activity)
    }

    private var mChildOfContent: View? = null
    private var usableHeightPrevious = 0
    private var frameLayoutParams: FrameLayout.LayoutParams? = null

    private fun workAround(activity: Activity) {
        val content = activity.findViewById<View>(android.R.id.content) as FrameLayout
        mChildOfContent = content.getChildAt(0)
        mChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener { possiblyResizeChildOfContent() }
        frameLayoutParams = mChildOfContent?.layoutParams as FrameLayout.LayoutParams?
    }

    private fun possiblyResizeChildOfContent() {
        val usableHeightNow = computeUsableHeight()
        if (usableHeightNow != usableHeightPrevious) {
            val usableHeightSansKeyboard: Int? = mChildOfContent?.rootView?.height
            val heightDifference = usableHeightSansKeyboard?.minus(usableHeightNow)
            if (usableHeightSansKeyboard != null) {
                if (heightDifference != null) {
                    if (heightDifference > usableHeightSansKeyboard / 4) {
                        // keyboard probably just became visible
                        frameLayoutParams?.height = usableHeightSansKeyboard - heightDifference
                    } else {
                        // keyboard probably just became hidden
                        frameLayoutParams?.height = usableHeightSansKeyboard
                    }
                }
            }
            mChildOfContent?.requestLayout()
            usableHeightPrevious = usableHeightNow
        }
    }

    private fun computeUsableHeight(): Int {
        val r = Rect()
        mChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top
    }

    fun isValidPhoneNumber(phoneNumber: String, region: String): Boolean {
        if (phoneNumber.isNotEmpty()) {
            try {
                val phoneNum = App.instance?.util?.parse(phoneNumber, region)
                val formattedNumber =
                    App.instance?.util?.format(phoneNum, PhoneNumberUtil.PhoneNumberFormat.E164)
                val verificationNumber = App.instance?.util?.parse(formattedNumber, region)
                return App.instance?.util?.isValidNumber(verificationNumber) == true
            } catch (e: NumberParseException) {
                e.printStackTrace()
            }

        }
        return false
    }
}

@SuppressLint("ClickableViewAccessibility")
fun EditText.setDrawableRightTouch(setClickListener: () -> Unit) {
    this.setOnTouchListener(View.OnTouchListener { _, event ->
        val right = 2
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= this.right - this.compoundDrawables[right].bounds.width()
            ) {
                setClickListener()
                return@OnTouchListener true
            }
        }
        false
    })
}