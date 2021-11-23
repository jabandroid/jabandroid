package com.global.vtg.appview.home.vaccinehistory

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.utils.Constants
import com.vtg.R
import com.vtg.databinding.FragmentVaccineHistoryBinding
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_vaccine_history.*
import kotlinx.android.synthetic.main.fragment_vaccine_history.ivProfilePic
import org.koin.androidx.viewmodel.ext.android.viewModel


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

    override fun initializeComponent(view: View?) {
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        if (!Constants.USER?.profileUrl.isNullOrEmpty())
            ivProfilePic.setGlideNormalImage(Constants.USER?.profileUrl)

        tvUserName.text = Constants.USER?.firstName + " " + Constants.USER?.lastName
        rvVaccineList.layoutManager = LinearLayoutManager(context)
        adapter = VaccineHistoryAdapter(getAppActivity())
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
        adapter.setList(Constants.USER?.vaccine as ArrayList<VaccineHistory>)
        updateNoData()
    }

    private fun updateNoData() {
        if (!Constants.USER?.vaccine.isNullOrEmpty()) {
            adapter.setList(Constants.USER?.vaccine as ArrayList<VaccineHistory>)
            layoutNoData.visibility = View.GONE
        } else {
            layoutNoData.visibility = View.VISIBLE
        }
    }
}