package com.global.vtg.appview.authentication.registration

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.authentication.AuthenticationActivity
import com.global.vtg.appview.config.PickMediaExtensions
import com.global.vtg.appview.config.getRealPath
import com.global.vtg.appview.config.getRealPathFromURI
import com.global.vtg.appview.home.ClinicActivity
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.appview.home.ImageViewActivity
import com.global.vtg.appview.home.VendorActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.*
import com.global.vtg.utils.baseinrerface.OkCancelNeutralDialogInterface
import com.vtg.R
import com.vtg.databinding.FragmentRegStep2Binding
import kotlinx.android.synthetic.main.fragment_reg_step2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class RegistrationStep2Fragment : AppFragment() {
    private val viewModel by viewModel<RegistrationStep2ViewModel>()
    private lateinit var mFragmentBinding: FragmentRegStep2Binding
    var id: Int? = null
    private val myCalendar: Calendar = Calendar.getInstance()
    private val currentCalendar: Calendar = Calendar.getInstance()
    var isFromProfile = false
    var isChildAccount = false
    var typeOfPic :Int =0

    override fun getLayoutId(): Int {
        return R.layout.fragment_reg_step2
    }

    override fun preDataBinding(arguments: Bundle?) {
        if (arguments != null) {
            if (arguments.containsKey(Constants.BUNDLE_REGISTRATION_ID)) {
                id = arguments.getInt(Constants.BUNDLE_REGISTRATION_ID)
            }

            if (arguments.containsKey(Constants.BUNDLE_FROM_PROFILE)) {
                isFromProfile = arguments.getBoolean(Constants.BUNDLE_FROM_PROFILE)
            }

            if (arguments.containsKey(Constants.BUNDLE_CHILD_ACCOUNT)) {
                isChildAccount = arguments.getBoolean(Constants.BUNDLE_CHILD_ACCOUNT)
            }
        }
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentRegStep2Binding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    @SuppressLint("SetTextI18n")
    override fun initializeComponent(view: View?) {
        if(isFromProfile||isChildAccount)
            tvSkip.visibility=View.GONE
        var userType= SharedPreferenceUtil.getInstance(getAppActivity())
            ?.getData(
                PreferenceManager.KEY_LOGGED_IN_USER_TYPE,
                ""
            )

        if (Constants.USER!!.role.equals("ROLE_VENDOR", true)){
            tvTitle.text = getString(R.string.vendor_Step_2)
        }
        if (userType.equals("Clinic")) {
            tvTitle.text= getString(R.string.lab_Step_2)
        } else if (userType.equals("Vendor")) {
            tvTitle.text  = getString(com.vtg.R.string.vendor_Step_2)
        }
        tvSsn.text = getString(R.string.digit_pin)

        if(isChildAccount) {
            tvTitle.text = getString(R.string.child_sign_up)

            viewModel.isChildAccount =true
            val date = DateUtils.getDateLocal(
                Constants.USERCHILD?.dateOfBirth!!,
                DateUtils.API_DATE_FORMAT
            )

            if(viewModel.getAge(date.time)!! <15) {
                tvDln.visibility = View.GONE
                etDln.visibility = View.GONE
                sDlnState.visibility = View.GONE
                sDlnCountry.visibility = View.GONE
                etDlnIssuedDate.visibility = View.GONE
                etDlnExpiryDate.visibility = View.GONE
            }
        }

        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }


        imgIdUpload.setOnClickListener{
            typeOfPic=1
            pickImage()
        }
        imgDlUpload.setOnClickListener{
            typeOfPic=2
            pickImage()
        }
        imgPPUpload.setOnClickListener{
            typeOfPic=3
            pickImage()
        }

        imgbbUpload.setOnClickListener{
            typeOfPic=4
            pickImage()
        }

        ig_edit.setOnClickListener{
            typeOfPic=1
            pickImage()
        }
        ig_edit_dl.setOnClickListener{
            typeOfPic=2
            pickImage()
        }
        ig_edit_pp.setOnClickListener{
            typeOfPic=3
            pickImage()
        }

        ig_edit_bb.setOnClickListener{
            typeOfPic=4
            pickImage()
        }

        imgId.isEnabled=false
        imgDl.isEnabled=false
        imgPP.isEnabled=false

        etId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                imgId.isEnabled = s?.length ?: 0 != 0
            }

        })

        etDln.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                imgDl.isEnabled = s?.length ?: 0 != 0
            }

        })
        etPassport.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                imgPP.isEnabled = s?.length ?: 0 != 0
            }

        })

        if(!isChildAccount){
        val list = Constants.USER?.document
            list!!.reverse()
        if (list != null && list.isNotEmpty()) {
            for (doc in list) {
                when {
                    doc?.type.equals("DL", true) -> {

                        if(TextUtils.isEmpty(  viewModel.dln.value)) {
                            if (!TextUtils.isEmpty(doc!!.url)) {
                                viewModel.dlUrl=doc!!.url
                                imgDl.setGlideNormalImage(doc!!.url)
                                imgDlUpload.visibility = View.GONE
                                ig_edit_dl.visibility = View.VISIBLE

                                imgDl.setOnClickListener{
                                    val listOfImages = ArrayList<String>()

                                    listOfImages.add(doc.url.toString())
                                    val intent = Intent(activity!!, ImageViewActivity::class.java)
                                    intent.putExtra("IM", listOfImages)
                                    startActivity(intent)
                                }
                            } else {
                                imgDlUpload.visibility = View.VISIBLE
                                ig_edit_dl.visibility = View.GONE
                            }
                            doc?.issueDate?.let { setDate(it, etDlnIssuedDate, ::updateIssuedDate) }
                            doc?.expireDate?.let {
                                setDate(
                                    it,
                                    etDlnExpiryDate,
                                    ::updateExpiryDate
                                )
                            }
                            viewModel.dln.value = doc?.identity
                            viewModel.dlId = doc.id
                            viewModel.dlnState.value = doc?.state
                            viewModel.dlnCountry.value = doc?.country
                        }
                    }
                    doc?.type.equals("Passport", true) -> {
                        if(TextUtils.isEmpty(  viewModel.passportNumber.value)) {

                            doc?.issueDate?.let {
                                setDate(
                                    it,
                                    etPassportIssuedDate,
                                    ::updatePassportIssuedDate
                                )
                            }
                            doc?.expireDate?.let {
                                setDate(
                                    it,
                                    etPassportExpiryDate,
                                    ::updatePassportExpiryDate
                                )
                            }

                            if (!TextUtils.isEmpty(doc!!.url)) {
                                viewModel.ppUrl=doc!!.url
                                imgPP.setGlideNormalImage(doc!!.url)
                                imgPPUpload.visibility = View.GONE
                                ig_edit_pp.visibility=View.VISIBLE
                                imgPP.setOnClickListener{
                                    val listOfImages = ArrayList<String>()

                                    listOfImages.add(doc.url.toString())
                                    val intent = Intent(activity!!, ImageViewActivity::class.java)
                                    intent.putExtra("IM", listOfImages)
                                    startActivity(intent)
                                }
                            } else {
                                imgPPUpload.visibility = View.VISIBLE
                                ig_edit_pp.visibility = View.GONE
                            }

                            viewModel.ppId = doc!!.id

                            viewModel.passportNumber.value = doc?.identity
                            viewModel.passportState.value = doc?.state
                            viewModel.passportCountry.value = doc?.country
                        }
                    }
                    doc?.type.equals("SSN", true) -> {
                        if(TextUtils.isEmpty(  viewModel.ssn.value)) {
                            viewModel.ssnId = doc!!.id
                            viewModel.ssn.value = doc?.identity!!.substring(0, 2) + "XX"
                            viewModel.ssnFinal = doc?.identity!!
//                            etSsn.isClickable = false
//                            etSsn.isEnabled = false
                        }

                    }
                    doc?.type.equals("ID", true) -> {
                        if(TextUtils.isEmpty(  viewModel.id.value)) {
                            viewModel.id.value = doc?.identity
                            viewModel.idId = doc!!.id
                            if (!TextUtils.isEmpty(doc!!.url)) {
                                viewModel.idUrl=doc!!.url
                                imgId.setGlideNormalImage(doc!!.url)
                                imgIdUpload.visibility = View.GONE
                                ig_edit.visibility = View.VISIBLE

                                imgId.setOnClickListener{
                                    val listOfImages = ArrayList<String>()

                                    listOfImages.add(doc.url.toString())
                                    val intent = Intent(activity!!, ImageViewActivity::class.java)
                                    intent.putExtra("IM", listOfImages)
                                    startActivity(intent)
                                }
                            } else {
                                imgIdUpload.visibility = View.VISIBLE
                                ig_edit.visibility = View.GONE
                            }

//                            etDln.isClickable = false
//                            etDln.isEnabled = false
                        }
                    }
                    doc?.type.equals("BirthCertificate", true) -> {
                        if(TextUtils.isEmpty(  viewModel.id.value)) {

                            viewModel.idBB = doc!!.id
                            if (!TextUtils.isEmpty(doc!!.url)) {
                                viewModel.bbUrl=doc!!.url
                                imgbbId.setGlideNormalImage(doc!!.url)
                                imgbbUpload.visibility = View.GONE
                                ig_edit_bb.visibility = View.VISIBLE

                                imgbbId.setOnClickListener{
                                    val listOfImages = ArrayList<String>()

                                    listOfImages.add(doc.url.toString())
                                    val intent = Intent(activity!!, ImageViewActivity::class.java)
                                    intent.putExtra("IM", listOfImages)
                                    startActivity(intent)
                                }
                            } else {
                                imgIdUpload.visibility = View.VISIBLE
                                ig_edit_bb.visibility = View.GONE
                            }


                        }
                    }
                }
            }
        }
        }

        // Handle Error
        viewModel.showToastError.observe(this) {
            DialogUtils.showSnackBar(context, it)
        }

        viewModel.uploadPicLiveData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }

                    when (typeOfPic) {
                        1 -> {
                            viewModel.idUrl = it.data.url


                        }
                        2 -> {
                            viewModel.dlUrl = it.data.url

                        }
                        3 -> {
                            viewModel.ppUrl = it.data.url

                        }
                        4 -> {
                            viewModel.bbUrl = it.data.url

                        }
                    }
                }
                is Resource.Error -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).showProgressBar()
                        is HomeActivity -> (activity as HomeActivity).showProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).showProgressBar()
                    }
                }
            }

        }

        viewModel.registerLiveData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }

                    if (viewModel.isChildAccount) {
                        Constants.USERCHILD = it.data
                        val bundle = Bundle()
                        bundle.putBoolean(Constants.BUNDLE_CHILD_ACCOUNT, true)
                        addFragmentInStack<Any>(AppFragmentState.F_REG_STEP3, bundle)
                    } else {
                        Constants.USER = it.data
                        val bundle = Bundle()
                        bundle.putBoolean(Constants.BUNDLE_FROM_PROFILE, isFromProfile)
                        addFragmentInStack<Any>(AppFragmentState.F_REG_STEP3, bundle)
                    }
                }
                is Resource.Error -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).hideProgressBar()
                        is HomeActivity -> (activity as HomeActivity).hideProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).hideProgressBar()
                    }
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    when (activity) {
                        is AuthenticationActivity -> (activity as AuthenticationActivity).showProgressBar()
                        is HomeActivity -> (activity as HomeActivity).showProgressBar()
                        is ClinicActivity -> (activity as ClinicActivity).hideProgressBar()
                        else -> (activity as VendorActivity).showProgressBar()
                    }
                }
            }

        }

        val dlnIssuedDate =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateIssuedDate()
            }
        val dlnExpiryDate =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateExpiryDate()
            }
        val passportIssuedDate =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updatePassportIssuedDate()
            }
        val passportExpiryDate =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updatePassportExpiryDate()
            }

        etDlnIssuedDate.setOnClickListener {
            KeyboardUtils.hideKeyboard(getAppActivity())
            val datePicker = DatePickerDialog(
                getAppActivity(), R.style.DialogTheme, dlnIssuedDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.maxDate = currentCalendar.timeInMillis
            datePicker.show()
        }

        etDlnExpiryDate.setOnClickListener {
            KeyboardUtils.hideKeyboard(getAppActivity())
            val datePicker = DatePickerDialog(
                getAppActivity(), R.style.DialogTheme, dlnExpiryDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = currentCalendar.timeInMillis
            datePicker.show()
        }

        tvSkip.setOnClickListener{
            val intent: Intent = if (Constants.USER?.role.equals("ROLE_USER")) {
                Intent(activity, HomeActivity::class.java)
            }else if (Constants.USER?.role.equals("ROLE_CLINIC")) {
                Intent(activity, ClinicActivity::class.java)
            } else {
                Intent(activity, VendorActivity::class.java)
            }

            SharedPreferenceUtil.getInstance(getAppActivity())
                ?.saveData(
                    PreferenceManager.KEY_USER_REG,
                    true
                )

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        etPassportIssuedDate.setOnClickListener {
            KeyboardUtils.hideKeyboard(getAppActivity())
            val datePicker = DatePickerDialog(
                getAppActivity(), R.style.DialogTheme, passportIssuedDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.maxDate = currentCalendar.timeInMillis
            datePicker.show()
        }

        etPassportExpiryDate.setOnClickListener {
            KeyboardUtils.hideKeyboard(getAppActivity())
            val datePicker = DatePickerDialog(
                getAppActivity(), R.style.DialogTheme, passportExpiryDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = currentCalendar.timeInMillis
            datePicker.show()
        }

        sDlnState.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.DLN_AUTOCOMPLETE_REQUEST_CODE)
        }
        sDlnCountry.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.DLN_AUTOCOMPLETE_REQUEST_CODE)
        }
        sPassportState.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.PASSPORT_AUTOCOMPLETE_REQUEST_CODE)
        }
        sPassportCountry.setOnClickListener {
            getAppActivity().onSearchCalled(Constants.PASSPORT_AUTOCOMPLETE_REQUEST_CODE)
        }

    }

    override fun pageVisible() {

    }

    private fun updateIssuedDate() {
        etDlnIssuedDate.setText(DateUtils.formatDate(
            myCalendar.timeInMillis,
            DateUtils.API_DATE_FORMAT_VACCINE
        ))

        viewModel.dlnIssuedDate.value = DateUtils.formatDateTime(myCalendar.timeInMillis, DateUtils.API_DATE_FORMAT)
    }

    private fun updateExpiryDate() {
        etDlnExpiryDate.setText(DateUtils.formatDate(
            myCalendar.timeInMillis,
            DateUtils.API_DATE_FORMAT_VACCINE
        ))

        viewModel.dlnExpiredDate.value = DateUtils.formatDateTime(myCalendar.timeInMillis, DateUtils.API_DATE_FORMAT)
    }

    private fun updatePassportIssuedDate() {

        etPassportIssuedDate.setText(DateUtils.formatDate(
            myCalendar.timeInMillis,
            DateUtils.API_DATE_FORMAT_VACCINE
        ))

        viewModel.passportIssuedDate.value =DateUtils.formatDateTime(myCalendar.timeInMillis, DateUtils.API_DATE_FORMAT)
    }

    private fun updatePassportExpiryDate() {

        etPassportExpiryDate.setText(DateUtils.formatDate(
            myCalendar.timeInMillis,
            DateUtils.API_DATE_FORMAT_VACCINE
        ))

        viewModel.passportExpiredDate.value = DateUtils.formatDateTime(myCalendar.timeInMillis, DateUtils.API_DATE_FORMAT)
    }

    fun updateDlnAddress(state: String, country: String) {
        sDlnState.setText(state)
        sDlnCountry.setText(country)
        viewModel.dlnState.value = state
        viewModel.dlnCountry.value = country
    }

    fun updatePassportAddress(state: String, country: String) {
        sPassportState.setText(state)
        sPassportCountry.setText(country)
        viewModel.passportState.value = state
        viewModel.passportCountry.value = country
    }

    private fun setDate(parseDate: String, editText: EditText, function: () -> Unit) {

        val cal = Calendar.getInstance()
        cal.time = DateUtils.getDate(parseDate, DateUtils.API_DATE_FORMAT_VACCINE)
        myCalendar.set(Calendar.YEAR, cal.get(Calendar.YEAR))
        myCalendar.set(Calendar.MONTH, cal.get(Calendar.MONTH))
        myCalendar.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH))
        function()
        editText.isClickable = false
        editText.isEnabled = false
    }

    private  fun  pickImage(){
        DialogUtils.okCancelNeutralDialog(
            context,
            getAppActivity().getString(R.string.app_name),
            getAppActivity().getString(R.string.label_select_image),
            object :
                OkCancelNeutralDialogInterface {
                override fun ok() {
                    PickMediaExtensions.instance.pickFromGallery(getAppActivity()) { resultCode: Int, path: String, displayName: String? ->
                        resultMessage(resultCode, path, displayName)
                    }
                }

                override fun cancel() {
                    PickMediaExtensions.instance.pickFromCamera(getAppActivity()) { resultCode: Int, path: String, displayName: String? ->
                        resultMessage(resultCode, path, displayName)
                    }
                }

                override fun neutral() {

                }
            })
    }


    private fun resultMessage(
        resultCode: Int,
        path: String,
        displayName: String?,
        fromCamera: Boolean = false
    ) {
        getAppActivity().getActivityScope(getAppActivity()).launch(Dispatchers.IO) {
            when (resultCode) {
                PickMediaExtensions.PICK_SUCCESS -> {
                    val realPath1 = Uri.parse(path).getRealPathFromURI(getAppActivity())
                    if (realPath1 != null) {
                        if (displayName != null) {
                            updateDocument(displayName, path, fromCamera)
                        }
                    } else {
                        val realPath = Uri.parse(path).getRealPath(getAppActivity())
                        if (realPath != null) {
                            if (displayName != null) {
                                updateDocument(displayName, path, fromCamera)
                            }
                        } else {
                            DialogUtils.showSnackBar(
                                getAppActivity(),
                                resources.getString(R.string.error_message_somethingwrong)
                            )
                        }
                    }
                }
                PickMediaExtensions.PICK_CANCELED -> {
                    activity?.runOnUiThread {
                        activity?.onBackPressed()
                    }
                }
                else -> DialogUtils.showSnackBar(
                    getAppActivity(),
                    resources.getString(R.string.error_message_somethingwrong)
                )
            }
        }
    }

    private fun updateDocument(docName: String, path: String, fromCamera: Boolean) {
        MainScope().launch {



            var bitmap = BitmapFactory.decodeFile(path)
            if (fromCamera) {
                if (bitmap.width > bitmap.height) {
                    val matrix = Matrix()
                    matrix.postRotate(90f)
                    bitmap = Bitmap.createBitmap(
                        bitmap,
                        0,
                        0,
                        bitmap.width,
                        bitmap.height,
                        matrix,
                        true
                    )
                }

            }
            viewModel.typeOfPic=typeOfPic
            when(typeOfPic){
                1->{
                    viewModel.documentPathId = path
                    imgId.setImageBitmap(bitmap)
                    imgIdUpload.visibility=View.GONE
                    ig_edit.visibility=View.VISIBLE

                }
                2->{
                    viewModel.documentPathDl = path
                    imgDl.setImageBitmap(bitmap)
                    imgDlUpload.visibility=View.GONE
                    ig_edit_dl.visibility=View.VISIBLE
                }
                3->{
                    viewModel.documentPathPP = path
                    imgPPUpload.visibility=View.GONE
                    imgPP.setImageBitmap(bitmap)
                    ig_edit_pp.visibility=View.VISIBLE
                }
                4->{
                    viewModel.documentPathBB = path
                    imgbbUpload.visibility=View.GONE
                    imgbbId.setImageBitmap(bitmap)
                    ig_edit_bb.visibility=View.VISIBLE
                }
            }

        }

        viewModel.uploadProfile()





    }
}