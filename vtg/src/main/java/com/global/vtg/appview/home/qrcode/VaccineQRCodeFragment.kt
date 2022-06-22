package com.global.vtg.appview.home.qrcode


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.background.StillBackground
import com.github.sumimakito.awesomeqr.option.color.Color
import com.github.sumimakito.awesomeqr.option.logo.Logo
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.base.AppFragment
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.zxing.WriterException
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.vtg.R
import com.vtg.databinding.FragmentVaccineQrCodeBinding
import kotlinx.android.synthetic.main.bottom_chid_qr_code.view.*
import kotlinx.android.synthetic.main.bottom_sheet_child.view.ivProfilePic
import kotlinx.android.synthetic.main.bottom_sheet_child.view.tvName
import kotlinx.android.synthetic.main.card.*
import kotlinx.android.synthetic.main.fragment_vaccine_qr_code.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream


class VaccineQRCodeFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentVaccineQrCodeBinding
    private val viewModel by viewModel<VaccineQRCodeViewModel>()
    var bitmap: Bitmap? = null
    var count : Int=0
    var namePerson : String=""
    var profile : String=""
    var barcodeId : String=""
    lateinit var viewExpand: ImageView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    override fun getLayoutId(): Int {
        return R.layout.fragment_vaccine_qr_code
    }

    override fun preDataBinding(arguments: Bundle?) {
        arguments?.let {
            if (arguments.containsKey("name")) {
                namePerson = arguments.getString("name").toString()
            }
            if (arguments.containsKey("barcodeId")) {
                barcodeId = arguments.getString("barcodeId").toString()
            }
            if (arguments.containsKey("profile")) {
                profile = arguments.getString("profile").toString()
            }

        }
    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentVaccineQrCodeBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun initializeComponent(view: View?) {
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }





        QrCode(-1)

        setTitle(-1)

        ivQRCode.setOnClickListener {
            bottomSheetLayout.toggle()
        }

        btnShare.setOnClickListener{
            val pm = context!!.packageManager
            try {
                val bytes = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val path = MediaStore.Images.Media.insertImage(
                    context!!.contentResolver,
                    bitmap,
                    "Title",
                    null
                )
                val imageUri: Uri = Uri.parse(path)
              //  val info: PackageInfo = pm.getPackageInfo(pack, PackageManager.GET_META_DATA)
                val waIntent = Intent(Intent.ACTION_SEND)
                waIntent.type = "image/*"

                waIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
              //  waIntent.putExtra(Intent.EXTRA_TEXT, pack)
                context!!.startActivity(Intent.createChooser(waIntent, "Share with"))
            } catch (e: java.lang.Exception) {
                Log.e("Error on sharing", "$e ")
                Toast.makeText(context, "App not Installed", Toast.LENGTH_SHORT).show()
            }
        }

        bottomSheetLayout.setOnProgressListener { progress ->
            if(progress==0f)
                viewExpand.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            else
                viewExpand.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)

        }

    }

    fun setTitle(position: Int){
        parent.removeAllViews()

        if(!TextUtils.isEmpty(namePerson)){
            val inflater =
                activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val viewChild = inflater.inflate(R.layout.bottom_chid_qr_code, null)
            viewChild.tvName.text = namePerson
            if (!TextUtils.isEmpty(profile))
                viewChild.ivProfilePic.setGlideNormalImage(profile)
            else
                viewChild.ivProfilePic.setImageResource(R.drawable.user)
            parent.addView(viewChild)

            viewChild.ivslide.visibility = View.GONE

            viewExpand = viewChild.ivslide
            viewChild.ivslide.setOnClickListener {
                bottomSheetLayout.toggle()
            }
            viewChild.setOnClickListener {
                QrCode(-1)
                setTitle(-1)
                bottomSheetLayout.collapse()
            }
        }else {
            if (position == -1) {
                val inflater =
                    activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val viewChild = inflater.inflate(R.layout.bottom_chid_qr_code, null)
                viewChild.tvName.text = Constants.USER?.firstName + " " + Constants.USER?.lastName
                if (!TextUtils.isEmpty(Constants.USER?.profileUrl!!))
                    viewChild.ivProfilePic.setGlideNormalImage(Constants.USER?.profileUrl!!)
                else
                    viewChild.ivProfilePic.setImageResource(R.drawable.user)
                parent.addView(viewChild)

                if (Constants.USER!!.childAccount!!.size > 0)
                    viewChild.ivslide.visibility = View.VISIBLE

                viewExpand = viewChild.ivslide
                viewChild.ivslide.setOnClickListener {
                    bottomSheetLayout.toggle()
                }
                viewChild.setOnClickListener {
                    QrCode(-1)
                    setTitle(-1)
                    bottomSheetLayout.collapse()
                }
                count = 0
                for (item in Constants.USER?.childAccount!!) {

                    val inflater =
                        activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val viewChild = inflater.inflate(R.layout.bottom_chid_qr_code, null)
                    viewChild.tvName.text = item.firstName + " " + item.lastName
                    if (!TextUtils.isEmpty(item.profileUrl))
                        viewChild.ivProfilePic.setGlideNormalImage(item.profileUrl!!)
                    else
                        viewChild.ivProfilePic.setImageResource(R.drawable.user)
                    viewChild.tag = count
                    count++
                    parent.addView(viewChild)
                    viewChild.setOnClickListener {
                        QrCode(it.tag as Int)
                        setTitle(it.tag as Int)
                        bottomSheetLayout.collapse()
                    }
                }
            } else {

                var child = Constants.USER?.childAccount?.get(position)
                var inflater =
                    activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                var viewChild = inflater.inflate(R.layout.bottom_chid_qr_code, null)
                viewChild.tvName.text = child!!.firstName + " " + child!!.lastName
                if (!TextUtils.isEmpty(child.profileUrl))
                    viewChild.ivProfilePic.setGlideNormalImage(child!!.profileUrl!!)
                else
                    viewChild.ivProfilePic.setImageResource(R.drawable.user)
                viewChild.tag = position
                if (Constants.USER!!.childAccount!!.size > 0)
                    viewChild.ivslide.visibility = View.VISIBLE
                viewExpand = viewChild.ivslide
                viewChild.ivslide.setOnClickListener {
                    bottomSheetLayout.toggle()
                }
                parent.addView(viewChild)
                viewChild.setOnClickListener {
                    QrCode(it.tag as Int)
                    setTitle(it.tag as Int)
                    bottomSheetLayout.collapse()
                }

                count = 0
                if (Constants.USER?.childAccount!!.size > 0)
                    for (item in Constants.USER?.childAccount!!) {
                        if (count != position) {
                            val inflater =
                                activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                            val viewChild = inflater.inflate(R.layout.bottom_chid_qr_code, null)
                            viewChild.tvName.text = item.firstName + " " + item.lastName
                            if (!TextUtils.isEmpty(item.profileUrl))
                                viewChild.ivProfilePic.setGlideNormalImage(item.profileUrl!!)
                            else
                                viewChild.ivProfilePic.setImageResource(R.drawable.user)
                            viewChild.tag = count
                            count++
                            parent.addView(viewChild)
                            viewChild.setOnClickListener {
                                QrCode(it.tag as Int)
                                setTitle(it.tag as Int)
                                bottomSheetLayout.collapse()
                            }
                        } else
                            count++
                    }

                inflater =
                    activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                viewChild = inflater.inflate(R.layout.bottom_chid_qr_code, null)
                viewChild.tvName.text = Constants.USER?.firstName + " " + Constants.USER?.lastName
                if (!TextUtils.isEmpty(Constants.USER?.profileUrl))
                    viewChild.ivProfilePic.setGlideNormalImage(Constants.USER?.profileUrl!!)
                else
                    viewChild.ivProfilePic.setImageResource(R.drawable.user)
                parent.addView(viewChild)

                viewChild.setOnClickListener {
                    QrCode(-1)
                    setTitle(-1)
                    bottomSheetLayout.collapse()
                }

            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun QrCode(position: Int){

        if(position!=-1)
            btnShare.visibility=View.VISIBLE
        else
            btnShare.visibility=View.GONE
        ivQRCode.setImageResource(0)
        val color = Color()
        color.light = 0xFFFFFFFF.toInt() // for blank spaces
        color.dark = 0xFF000000.toInt() // for non-blank spaces
        color.background =
            0xFFFFFFFF.toInt() // for the background (will be overriden by background images, if set)
        color.auto = false
//
        val background = StillBackground()
        background.bitmap = BitmapFactory.decodeResource(context?.resources, R.mipmap.qr_code)
        background.clippingRect = Rect(0, 0, 150,150) // crop the background before applying
        background.alpha = 1f
        val logo = Logo()
        try {
            val bitmap = BitmapFactory.decodeResource(context?.resources, R.mipmap.qr_code)
            logo.bitmap = bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        logo.borderRadius = 10 // radius for logo's corners
        logo.borderWidth = 10 // width of the border to be added around the logo
        logo.scale = 0.3f // scale for the logo in the QR code
        logo.clippingRect =
            RectF(0f, 0f, 150f, 150f) // crop the logo image before applying it to the QR code
//
        val renderOption = RenderOption()
        if(position==-1) {
            if(!TextUtils.isEmpty(namePerson)){
                btnShare.visibility = View.VISIBLE
                name.text = namePerson
                renderOption.content =barcodeId
            }else {
                name.text = Constants.USER?.firstName + " " + Constants.USER?.lastName
                renderOption.content = Constants.USER?.barcodeId.toString()

                if (!TextUtils.isEmpty(Constants.USER!!.parentId) && Constants.USER!!.parentId != "0")
                    btnShare.visibility = View.VISIBLE
            }
        }// content to encode
        else{
            val child=Constants.USER?.childAccount
            name.text=  child!![position].firstName+" "+  child[position].lastName
            renderOption.content = child[position].barcodeId.toString()
        }
        renderOption.size = 800 * 3 / 4// size of the final QR code image
        renderOption.borderWidth = 20 // width of the empty space around the QR code
        renderOption.ecl = ErrorCorrectionLevel.M // (optional) specify an error correction level
        renderOption.patternScale = 0.33f // (optional) specify a scale for patterns
        renderOption.roundedPatterns =
            true // (optional) if true, blocks will be drawn as dots instead
        renderOption.clearBorder =
            true // if set to true, the background will NOT be drawn on the border area
        renderOption.color = color // set a color palette for the QR code
        renderOption.background = background// set a background, keep reading to find more about it
        renderOption.logo = logo

        try {
            val result = AwesomeQrRenderer.render(renderOption)
            when {
                result.bitmap != null -> {
                    bitmap = result.bitmap!!
                    // play with the bitmap
                    ivQRCode.setImageBitmap(result.bitmap)
                }
                else -> {
                    // Oops, something gone wrong.
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Oops, something gone wrong.
        }

    }

    override fun pageVisible() {

    }


    class BottomSheetDialog : BottomSheetDialogFragment() {
        private lateinit var listener: ClickListener
        private lateinit var c: Context
        private lateinit var user: ResUser
        private  var count: Int=0

        @SuppressLint("SetTextI18n")
        override fun onCreateView(
            inflater: LayoutInflater,
            @Nullable container: ViewGroup?,
            @Nullable savedInstanceState: Bundle?
        ): View {
            val v: View = inflater.inflate(
                R.layout.bottom_sheet_child_parent,
                container, false
            )


            var parent = v.findViewById<LinearLayout>(R.id.parent)
            val inflater =
                c.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.bottom_sheet_child, null)
            view.tvName.text = user.firstName + " " + user.lastName
            view.ivProfilePic.setGlideNormalImage(user.profileUrl!!)
            parent.addView(view)

            view.setOnClickListener{
                listener.onItemClick(-1)
            }
            count=0
            for (item in user.childAccount!!) {
                val inflater =
                    c.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.bottom_sheet_child, null)
                view.tvName.text = item.firstName + " " + item.lastName
                view.ivProfilePic.setGlideNormalImage(item.profileUrl!!)
                view.tag = count
                count++
                parent.addView(view)
                view.setOnClickListener{
                    listener.onItemClick(view.getTag() as Int)
                }
            }


            return v
        }

        fun setListener(l: ClickListener) {
            this.listener = l
        }

        fun setUser(l: ResUser) {
            this.user = l
        }

        fun setContext(con: Context) {
            this.c = con
        }

        interface ClickListener {
            fun onItemClick(position: Int)
        }
    }

}



