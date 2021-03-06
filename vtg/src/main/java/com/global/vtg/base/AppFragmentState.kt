package com.global.vtg.base


import com.global.vtg.appview.authentication.forgotpassword.ForgotChangePasswordFragment
import com.global.vtg.appview.authentication.forgotpassword.ForgotPasswordFragment
import com.global.vtg.appview.authentication.login.SignInFragment
import com.global.vtg.appview.authentication.otp.OtpFragment
import com.global.vtg.appview.authentication.registration.*
import com.global.vtg.appview.home.testHistory.TestHistoryFragment
import com.global.vtg.appview.home.changepassword.ChangePasswordFragment
import com.global.vtg.appview.home.clinic.ClinicHomeFragment
import com.global.vtg.appview.home.dashboard.DashboardFragment
import com.global.vtg.appview.home.health.HealthInformationFragment
import com.global.vtg.appview.home.health.UploadHealthDocumentFragment
import com.global.vtg.appview.home.help.HelpFragment
import com.global.vtg.appview.home.profile.ProfileFragment
import com.global.vtg.appview.home.qrcode.VaccineQRCodeFragment
import com.global.vtg.appview.home.uploaddocument.UploadDocumentFragment
import com.global.vtg.appview.home.vaccinecard.VaccineCardFragment
import com.global.vtg.appview.home.vaccinehistory.VaccineHistoryFragment
import com.global.vtg.appview.home.vendor.VendorHomeFragment
import com.global.vtg.appview.home.vendor.VendorQRCodeFragment
import com.global.vtg.appview.home.vendor.VendorResultFragment
import com.global.vtg.appview.home.vendor.VendorScanResultFragment
import com.global.vtg.appview.payment.PaymentFragment
import com.global.vtg.appview.permissiondemo.view.PermissionDemoFragment

enum class AppFragmentState(var fragment: Class<out AppFragment>) {

    F_SIGN_IN(SignInFragment::class.java),
    F_TEST(TestHistoryFragment::class.java),
    F_OTP(OtpFragment::class.java),
    F_SIGN_UP(RegistrationFragment::class.java),
    F_FORGOT_PASSWORD(ForgotPasswordFragment::class.java),
    F_FORGOT_CHANGE_PASSWORD(ForgotChangePasswordFragment::class.java),
    F_DASHBOARD(DashboardFragment::class.java),
    F_VACCINE_HISTORY(VaccineHistoryFragment::class.java),
    F_VACCINE_CARD(VaccineCardFragment::class.java),
    F_VACCINE_QR_CODE(VaccineQRCodeFragment::class.java),
    F_UPLOAD_DOCUMENT(UploadDocumentFragment::class.java),
    F_REG_STEP1(RegistrationStep1Fragment::class.java),
    F_REG_STEP2(RegistrationStep2Fragment::class.java),
    F_REG_STEP3(RegistrationStep3Fragment::class.java),
    F_VENDOR_DASHBOARD(VendorHomeFragment::class.java),
    F_CLINIC_DASHBOARD(ClinicHomeFragment::class.java),
    F_VENDOR_QR_CODE(VendorQRCodeFragment::class.java),
    F_VENDOR_RESULT(VendorResultFragment::class.java),
    F_PROFILE(ProfileFragment::class.java),
    F_CHANGE_PASSWORD(ChangePasswordFragment::class.java),
    F_HEALTH_INFORMATION(HealthInformationFragment::class.java),
    F_UPLOAD_HEALTH_INFORMATION(UploadHealthDocumentFragment::class.java),
    F_HELP(HelpFragment::class.java),
    F_VENDOR_STEP2(VendorRegistrationStep2Fragment::class.java),
    F_PAYMENT(PaymentFragment::class.java),
    F_VENDOR_SCAN_RESULT(VendorScanResultFragment::class.java),
    F_PERMISSION_DEMO(PermissionDemoFragment::class.java);


    companion object {

        // To get AppFragmentState  enum from class name
        fun getValue(value: Class<*>): AppFragmentState {
            return values().firstOrNull { it.fragment == value }
                ?: F_SIGN_IN// not found
        }
    }

}
