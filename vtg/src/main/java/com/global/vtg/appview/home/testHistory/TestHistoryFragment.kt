package com.global.vtg.appview.home.testHistory

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.global.vtg.appview.config.TestInfo
import com.global.vtg.base.AppFragment
import com.global.vtg.model.factory.PreferenceManager
import com.global.vtg.utils.Constants
import com.global.vtg.utils.DateUtils
import com.global.vtg.utils.SharedPreferenceUtil
import com.vtg.R
import com.vtg.databinding.FragmentTestHistoryBinding

import kotlinx.android.synthetic.main.fragment_test_history.*


import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class TestHistoryFragment : AppFragment() {

    private lateinit var mFragmentBinding: FragmentTestHistoryBinding
    private val viewModel by viewModel<TestHistoryViewModel>()
    lateinit var adapter : TestInformationAdapter

    override fun getLayoutId(): Int {
        return R.layout.fragment_test_history
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentTestHistoryBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    @SuppressLint("SetTextI18n")
    override fun initializeComponent(view: View?) {
//        ivBack.setOnClickListener {
//            activity?.onBackPressed()
//        }

        rvTestList.layoutManager = LinearLayoutManager(context)
        adapter = TestInformationAdapter(getAppActivity(),object : TestInformationAdapter.ClickListener{
            override fun response(item: TestInfo, v: View, position: Int) {
                val role = SharedPreferenceUtil.getInstance(activity!!)
                    ?.getData(PreferenceManager.KEY_ROLE, "user")
                if (!item.documentLink.isNullOrEmpty()) {


                    if (role?.equals("user") == true) {
                        if (!item.documentLink.isNullOrEmpty())
                            Constants.openFile(item.documentLink, activity!!)

                    }
                }
            }


        })
        rvTestList.adapter = adapter

        updateNoData()
//
//        viewModel.uploadFile.observe(this, {
//            addFragmentInStack<Any>(AppFragmentState.F_UPLOAD_DOCUMENT)
//        })

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
        val list = Constants.USER?.test as ArrayList<TestInfo>
        Collections.sort(list, Comparator { obj1, obj2 ->
            val d1= DateUtils.getDate(  obj1!!.date!!,
                DateUtils.API_DATE_FORMAT_VACCINE)
            val d2= DateUtils.getDate(  obj2!!.date!!,
                DateUtils.API_DATE_FORMAT_VACCINE)
            return@Comparator d2.compareTo(d1)
        })
        adapter.setHealthList(list)
        updateNoData()
    }

    private fun updateNoData() {
        if (!Constants.USER?.test.isNullOrEmpty()) {
            val list = Constants.USER?.test as ArrayList<TestInfo>

            Collections.sort(list, Comparator { obj1, obj2 ->
                val d1= DateUtils.getDate(  obj1!!.date!!,
                    DateUtils.API_DATE_FORMAT_VACCINE)
                val d2= DateUtils.getDate(  obj2!!.date!!,
                    DateUtils.API_DATE_FORMAT_VACCINE)
                return@Comparator d2.compareTo(d1)
            })
            adapter.setHealthList(list)
            layoutNoData.visibility = View.GONE
        } else {
            layoutNoData.visibility = View.VISIBLE
        }
    }
}