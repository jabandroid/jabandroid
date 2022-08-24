package com.global.vtg.appview.home.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.appview.home.HomeActivity
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.model.network.Resource
import com.global.vtg.utils.*
import com.global.vtg.utils.Constants.USERMain
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.vtg.R
import com.vtg.databinding.FragmentDashboardBinding
import kotlinx.android.synthetic.main.adapter_child_list.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.ext.android.viewModel


class DashboardFragment : AppFragment(), ViewPagerDashAdapter.ClickListener {
    private lateinit var mFragmentBinding: FragmentDashboardBinding
    private val viewModel by viewModel<DashboardViewModel>()
    private lateinit var viewPager2Adapter: ViewPager2Adapter
    private lateinit var viewPagerDash: ViewPagerDashAdapter

    private var clickedPosition: Int = -1
    private val client = OkHttpClient()


    override fun getLayoutId(): Int {
        return R.layout.fragment_dashboard
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentDashboardBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    @SuppressLint("HardwareIds")
    override fun initializeComponent(view: View?) {
        loadData()

        viewModel.getUser()
        viewPager2Adapter = ViewPager2Adapter(getAppActivity())
        viewPager.adapter = viewPager2Adapter

        viewPagerDash = ViewPagerDashAdapter(getAppActivity())
        viewPager_dash.adapter = viewPagerDash
        TabLayoutMediator(vpDots, viewPager_dash) { _, _ ->
            //Some implementation
        }.attach()

        viewPagerDash.setImages(
            arrayListOf(
                "1", "2"
            )
        )

        viewPagerDash.setListenerPager(this)
        ivQrCode.setOnClickListener {
            addFragmentInStack<Any>(AppFragmentState.F_VACCINE_QR_CODE)
        }
        ivHelp.setOnClickListener {
            addFragmentInStack<Any>(AppFragmentState.F_HELP)
        }

        viewPager2Adapter.setImages(
            arrayListOf(
                "https://i.ibb.co/s6MgjJR/Mask-Group-2.png"
            )
        )

        ivProfilePic.setOnClickListener {
            if (USERMain!!.childAccount!!.size > 0) {
                multiple.visibility = View.VISIBLE
                val bottomSheet = BottomSheetDialog()
                bottomSheet.setContext(activity!!)
                bottomSheet.setUser(USERMain!!)
                bottomSheet.setListener(object :
                    BottomSheetDialog.ClickListener {
                    override fun onItemClick(position: Int) {
                        if (position == -1) {

                            SharedPreferenceUtil.getInstance(getAppActivity())
                                ?.saveData(
                                    PreferenceManager.KEY_USER_NAME,
                                    SharedPreferenceUtil.INSTANCE?.getData(
                                        PreferenceManager.KEY_USER_NAME_PARENT,
                                        ""
                                    ).toString()
                                )

                            SharedPreferenceUtil.getInstance(getAppActivity())
                                ?.saveData(
                                    PreferenceManager.KEY_PASSWORD,
                                    SharedPreferenceUtil.INSTANCE?.getData(
                                        PreferenceManager.KEY_PASSWORD_PARENT,
                                        ""
                                    ).toString()
                                )

                            SharedPreferenceUtil.getInstance(getAppActivity())
                                ?.saveData(
                                    PreferenceManager.KEY_IS_CHILD,
                                    false
                                )
                            val intent = Intent(getAppActivity(), HomeActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK


                            startActivity(intent)

                        } else {


                            SharedPreferenceUtil.getInstance(getAppActivity())
                                ?.saveData(
                                    PreferenceManager.KEY_USER_NAME,
                                    USERMain!!.childAccount?.get(position )!!.email.toString()
                                )


                            SharedPreferenceUtil.getInstance(getAppActivity())
                                ?.saveData(
                                    PreferenceManager.KEY_PASSWORD,
                                    USERMain!!.childAccount?.get(position )!!.password.toString()
                                )

                            SharedPreferenceUtil.getInstance(getAppActivity())
                                ?.saveData(
                                    PreferenceManager.KEY_IS_CHILD,
                                    true
                                )
                            val intent = Intent(getAppActivity(), HomeActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }

                    }
                })
                bottomSheet.show(
                    childFragmentManager,
                    "ModalBottomSheet"
                )
            }
        }

        viewModel.userConfigLiveData1.observe(this) {
            when (it) {
                is Resource.Success -> {
                    (activity as HomeActivity).hideProgressBar()
                    Constants.USER = it.data
                    val gson = Gson()
                    if (SharedPreferenceUtil.getInstance(getAppActivity())
                            ?.getData(
                                PreferenceManager.KEY_IS_CHILD,
                                false
                            ) == false
                    ) {
                        USERMain = it.data

//                        USERMain!!.vaccine=ArrayList<VaccineHistory>()
//                        USERMain!!.healthInfo=ArrayList<HealthInfo>()
//                        USERMain!!.test=ArrayList<TestInfo>()


                        val request: Any = gson.toJson(USERMain)
                        SharedPreferenceUtil.getInstance(getAppActivity())
                            ?.saveData(
                                PreferenceManager.KEY_USER_MAIN,
                                request.toString()
                            )
                    }

                    if (USERMain == null) {
                        val k = SharedPreferenceUtil.getInstance(getAppActivity())
                            ?.getData(
                                PreferenceManager.KEY_USER_MAIN,
                                ""
                            )

                        if (k != "") {
                            USERMain = gson.fromJson(k, ResUser::class.java)
                            if (USERMain!!.childAccount!!.size > 0) {
                                multiple.visibility = View.VISIBLE
                            }
                        }
                    } else


                        if (USERMain!!.childAccount!!.size > 0) {
                            multiple.visibility = View.VISIBLE
                        }
                    loadData()

                    if (!TextUtils.isEmpty(
                            SharedPreferenceUtil.getInstance(getAppActivity())
                                ?.getData(PreferenceManager.KEY_TOKEN, "")
                        )
                    ) {
                        val v = JsonObject()
                        v.addProperty("userId", Constants.USER!!.id.toString())
                        v.addProperty(
                            "deviceId", Settings.Secure.getString(
                                activity!!.contentResolver,
                                Settings.Secure.ANDROID_ID
                            )
                        )
                        v.addProperty(
                            "token", SharedPreferenceUtil.getInstance(getAppActivity())
                                ?.getData(PreferenceManager.KEY_TOKEN, "")
                        )
                        v.addProperty("deviceType", "android")
                        viewModel.updateToken(v)
                    }
                    if (clickedPosition != -1) {
                        onItemClickMain(clickedPosition)
                    }

                    if (SharedPreferenceUtil.getInstance(getAppActivity())
                            ?.getData(
                                PreferenceManager.KEY_USER_REG,
                                false
                            ) == true
                    ) {
                        AppAlertDialog().showRegMessage(
                            activity!! as AppCompatActivity,
                            Constants.USER!!.pin!!

                        )
                    }
                }
                is Resource.Error -> {
                    (activity as HomeActivity).hideProgressBar()
                    it.error.message?.let { it1 -> DialogUtils.showSnackBar(context, it1) }
                }
                is Resource.Loading -> {
                    (activity as HomeActivity).showProgressBar()
                }
            }
        }
    }

    override fun pageVisible() {

    }


    private fun loadData() {
        if (!Constants.USER?.address.isNullOrEmpty()) {
            tvState.text = Constants.USER?.address?.get(0)?.state
            tvCountry.text = Constants.USER?.address?.get(0)?.country
            var country = Constants.USER?.address?.get(0)?.country
            country = country?.let { getCountryCode(it) }
            if (country.isNullOrEmpty()) {
                ivCountry.visibility = View.GONE
            } else {
                ivCountry.visibility = View.VISIBLE
                country.let { ivCountry.setCountryForNameCode(it) }
            }
        }

        ivProfilePic.setGlideNormalImage(Constants.USER?.profileUrl)
    }

    override fun onResume() {
        super.onResume()
        if (!Constants.USER?.profileUrl.isNullOrEmpty())
            ivProfilePic.setGlideNormalImage(Constants.USER?.profileUrl)
    }

    override fun onItemClickMain(position: Int) {

        if (Constants.USER != null) {
            clickedPosition = -1
            when (position) {
                1 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_PROFILE)
                }
                2 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_VACCINE_HISTORY)
                }
                3 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_VACCINE_QR_CODE)

                }
                4 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_TEST_BASE)

                }
                5 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_VACCINE_CARD)
                }
                6 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_HEALTH_DASHBOARD_INFORMATION)
                }

                7 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_CHILD_LIST)
                }
                8 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_TRaVEL_INFO)
                }
                10 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_TRaVEL_FROM)
                }
                11 -> {
                    addFragmentInStack<Any>(AppFragmentState.F_EVENT_LIST)
                }
                else -> {
                    ToastUtils.shortToast(0, "Coming soon")
                }

//            5 -> {
//                addFragmentInStack<Any>(AppFragmentState.F_PAYMENT)
//            }
            }
        } else {
            clickedPosition = position
            viewModel.getUser()
        }
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
            if(!TextUtils.isEmpty(user.profileUrl))
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
                if(!TextUtils.isEmpty(item.profileUrl))
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