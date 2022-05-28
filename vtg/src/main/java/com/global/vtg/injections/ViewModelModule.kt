package com.global.vtg.injections

import android.content.res.Resources
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.authentication.forgotpassword.ForgotChangePasswordViewModel
import com.global.vtg.appview.authentication.forgotpassword.ForgotPasswordViewModel
import com.global.vtg.appview.authentication.login.SignInViewModel
import com.global.vtg.appview.authentication.otp.OtpViewModel
import com.global.vtg.appview.authentication.registration.*
import com.global.vtg.appview.home.changepassword.ChangePasswordViewModel
import com.global.vtg.appview.home.dashboard.DashboardViewModel
import com.global.vtg.appview.home.event.*
import com.global.vtg.appview.home.health.HealthInformationViewModel
import com.global.vtg.appview.home.health.UploadHealthDocumentViewModel
import com.global.vtg.appview.home.help.HelpViewModel
import com.global.vtg.appview.home.parentchild.ChildListViewModel
import com.global.vtg.appview.home.parentchild.ChildRegistrationModel
import com.global.vtg.appview.home.profile.ProfileViewModel
import com.global.vtg.appview.home.qrcode.VaccineQRCodeViewModel
import com.global.vtg.appview.home.testHistory.TestHistoryViewModel
import com.global.vtg.appview.home.testHistory.TestViewModel
import com.global.vtg.appview.home.testHistory.UploadTestDocumentViewModel
import com.global.vtg.appview.home.uploaddocument.UploadDocumentViewModel
import com.global.vtg.appview.home.vaccinecard.VaccineCardViewModel
import com.global.vtg.appview.home.vaccinehistory.VaccineHistoryViewModel
import com.global.vtg.appview.home.vendor.VendorDashboardViewModel
import com.global.vtg.appview.home.vendor.VendorQRViewModel
import com.global.vtg.appview.home.vendor.VendorResultViewModel
import com.global.vtg.appview.home.vendor.VendorScanResultViewModel
import com.global.vtg.appview.payment.PaymentViewModel
import com.global.vtg.appview.permissiondemo.viewmodel.PermissionDemoViewModel
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.factory.PreferenceManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val appViewModelModule = module {
    //api, preference and database dependency

    //Resource
    single<Resources> {
        androidContext().resources
    }

    factory {

    }

    // Dependency: PreferenceManger
    single {
        PreferenceManager(get())//it will take one argument i.e SharePreference from BaseAppModule.kt
    }

    //add all repo dependency
    single {
        UserRepository(get(), get())//it will take two argument ApiInterface and PreferenceManager
    }

    //add all viewmodel dependency using repo dependency
    //1. Login viewmodel
    viewModel {
        AppViewModel(get())
    }
    viewModel {
        SignInViewModel(get(), get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        RegistrationViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        OtpViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        ForgotPasswordViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        DashboardViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        VaccineHistoryViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        VaccineCardViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        VaccineQRCodeViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        UploadDocumentViewModel(get(), get())//it will take one argument i.e. UserRepository
    }

    viewModel {
        TestViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        RegistrationStep1ViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        RegistrationStep2ViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        RegistrationStep3ViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        VendorDashboardViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        VendorQRViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        VendorResultViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        ProfileViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        HealthInformationViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        ChangePasswordViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        UploadHealthDocumentViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        UploadTestDocumentViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        ForgotChangePasswordViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        HelpViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        TestHistoryViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        VendorRegistrationStep2ViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        PaymentViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        VendorScanResultViewModel(get(), get())//it will take one argument i.e. UserRepository
    }

    viewModel {
        CreateEventViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        CreateEventLocationViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        CreatEventReviewViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        EventListDetailViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        CreateEventSubViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        CreatEventReviewSubViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        EventListDetailViewSubModel(get(), get())//it will take one argument i.e. UserRepository
    }
    viewModel {
        ChildListViewModel(get(), get())//it will take one argument i.e. UserRepository
    }
    //2. Permission viewmodel
    viewModel {
        PermissionDemoViewModel(get())
    }
    viewModel {
        EventListViewModel(get(), get())
    }
  viewModel {
      ContactListViewModel(get(), get())
    }
    viewModel {
        ChildRegistrationModel(get(), get())
    }


}