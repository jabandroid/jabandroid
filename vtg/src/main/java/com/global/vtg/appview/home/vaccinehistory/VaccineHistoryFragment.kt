package com.global.vtg.appview.home.vaccinehistory

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.global.vtg.appview.config.HealthInfo
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.utils.AppAlertDialog
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.global.vtg.utils.SharedPreferenceUtil
import com.google.gson.JsonObject
import com.vtg.R
import com.vtg.databinding.FragmentVaccineHistoryBinding
import kotlinx.android.synthetic.main.fragment_vaccine_history.*


import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class VaccineHistoryFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentVaccineHistoryBinding
    private val viewModel by viewModel<VaccineHistoryViewModel>()
    lateinit var adapter : VaccineHistoryAdapter

    override fun getLayoutId(): Int {
        return R.layout.fragment_vaccine_history
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentVaccineHistoryBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    @SuppressLint("SetTextI18n")
    override fun initializeComponent(view: View?) {
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        if (!Constants.USER?.profileUrl.isNullOrEmpty())
            ivProfilePic.setGlideNormalImage(Constants.USER?.profileUrl)

        if(!TextUtils.isEmpty(Constants.USER?.firstName!!)&&!TextUtils.isEmpty(Constants.USER?.lastName!!))
        tvUserNam.text = Constants.USER?.firstName!!.replace("null","") + " " + Constants.USER?.lastName!!.replace("null","")

        rvVaccineList.layoutManager = LinearLayoutManager(context)
        adapter = VaccineHistoryAdapter(getAppActivity(), object : VaccineHistoryAdapter.onItemClick{
            override fun response(item: VaccineHistory, v: View, position: Int) {
                val role = SharedPreferenceUtil.getInstance(activity!!)
                    ?.getData(PreferenceManager.KEY_ROLE, "user")
                if (role?.equals("user") == true) {
                    if (!item.documentLink.isNullOrEmpty())
                        Constants.openFile(item.documentLink,activity!!)

                } else {

                }
            }

        })
        rvVaccineList.adapter = adapter

        updateNoData()

        viewModel.uploadFile.observe(this, {
            addFragmentInStack<Any>(AppFragmentState.F_UPLOAD_DOCUMENT)
        })

//        viewModel.uploadProfilePic.observe(this, {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "*/*"
//            getAppActivity().startActivityForResult(intent, Constants.PICK_IMAGE_REQUEST_CODE)
//        })
    }

    fun updateDocument(docName: String, path: String, uri: Uri?) {
        // API Call
    }

    override fun pageVisible() {

    }

    fun refreshList() {
        val list = Constants.USER?.vaccine as ArrayList<VaccineHistory>
        Collections.sort(list, Comparator<VaccineHistory?> { obj1, obj2 ->

            val d1= DateUtils.getDate(  obj1!!.date!!,
                DateUtils.API_DATE_FORMAT_VACCINE)
            val d2= DateUtils.getDate(  obj2!!.date!!,
                DateUtils.API_DATE_FORMAT_VACCINE)
            return@Comparator d2.compareTo(d1)
        })
        adapter.setList(list)
        updateNoData()
    }

    private fun updateNoData() {
        if (!Constants.USER?.vaccine.isNullOrEmpty()) {
            val list = Constants.USER?.vaccine as ArrayList<VaccineHistory>

            Collections.sort(list, Comparator<VaccineHistory?> { obj1, obj2 ->

                val d1= DateUtils.getDate(  obj1!!.date!!,
                    DateUtils.API_DATE_FORMAT_VACCINE)
                val d2= DateUtils.getDate(  obj2!!.date!!,
                    DateUtils.API_DATE_FORMAT_VACCINE)
                return@Comparator d2.compareTo(d1)
            })
            adapter.setList(list)
            layoutNoData.visibility = View.GONE
        } else {
            layoutNoData.visibility = View.VISIBLE
        }
    }
}