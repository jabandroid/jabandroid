package com.global.vtg.appview.home.parentchild


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.ViewDataBinding
import com.global.vtg.base.AppFragment
import com.global.vtg.base.AppFragmentState
import com.global.vtg.base.fragment.addFragmentInStack
import com.global.vtg.utils.AppAlertDialog
import com.global.vtg.utils.Constants
import com.vtg.R
import com.vtg.databinding.FragmentBirthdayVerificationBinding
import kotlinx.android.synthetic.main.fragment_birthday_verification.*
import kotlinx.android.synthetic.main.fragment_child_list.ivBack
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class BirthdayVerificationFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentBirthdayVerificationBinding
    private val viewModel by viewModel<ChildRegistrationModel>()
    lateinit var adapter: ChildListAdapter
    lateinit  var arrayDate : Array<String>
    lateinit  var arrayMonth : Array<String>
    lateinit  var arrayYear: Array<String>
    var day:String=""
    var month:String=""
    var year:String=""
    override fun getLayoutId(): Int {
        return R.layout.fragment_birthday_verification
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentBirthdayVerificationBinding
        mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    @SuppressLint("SetTextI18n")
    override fun initializeComponent(view: View?) {
        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        btnNext.setOnClickListener {
            if( getAge()!! <18) {
                val bundle = Bundle()
                bundle.putBoolean(Constants.BUNDLE_CHILD_ACCOUNT, true)
                bundle.putString(Constants.BUNDLE_DATE,  dob.text.toString())
                addFragmentInStack<Any>(AppFragmentState.F_REG_STEP1, bundle)
            }else{
                AppAlertDialog().showAlert(
                    activity!!,
                    object : AppAlertDialog.GetClick {
                        override fun response(type: String) {
                        }
                    }, getString(R.string.child_valid_date), "Ok", ""
                )
            }


        }


        val thisYear = Calendar.getInstance()[Calendar.YEAR]
        val thisMonth = Calendar.getInstance()[Calendar.MONTH]+1
        val thisDate = Calendar.getInstance()[Calendar.DAY_OF_MONTH]

        year=thisYear.toString()
        month=thisMonth.toString()
        day=thisDate.toString()


        dob.text="$month/$day/$year"

        val numDays: Int = Calendar.getInstance().getActualMaximum(Calendar.DATE)

        arrayDate = Array(numDays) { i -> (i + 1).toString() }
        numberPickerDate.minValue =1
        numberPickerDate.maxValue = arrayDate.size
        numberPickerDate.displayedValues = arrayDate
        numberPickerDate.wrapSelectorWheel = true
        numberPickerDate.value=thisDate
       // numberPickerDate.wheelItemCount=3
        numberPickerDate.setOnValueChangedListener { picker, oldVal, newVal ->
            day= newVal.toString()
            dob.text="$month/$day/$year"

        }



        arrayMonth = Array(12) { i -> (i + 1).toString() }
        numberPickerMonth.minValue =1
        numberPickerMonth.maxValue = arrayMonth.size
        numberPickerMonth.displayedValues = arrayMonth
        numberPickerMonth.wrapSelectorWheel = true
        numberPickerMonth.value=thisMonth
        // numberPickerDate.wheelItemCount=3
        numberPickerMonth.setOnValueChangedListener { picker, oldVal, newVal ->
            month= newVal.toString()
            dob.text="$month/$day/$year"
        }

        val years = ArrayList<String>()
        val differ=thisYear-1900
        for (i in 1900..thisYear) {
            years.add(i.toString())
        }

        years.add(thisYear.toString())
        arrayYear = arrayOf(arrayOfNulls<Int>(years.size).toString())
        arrayYear = years.toArray(arrayYear)
        numberPickerYear.minValue =1
        numberPickerYear.maxValue = arrayYear.size-1
        numberPickerYear.value=arrayYear.size-1
        numberPickerYear.displayedValues = arrayYear
        numberPickerYear.wrapSelectorWheel = false
        // numberPickerDate.wheelItemCount=3
        numberPickerYear.setOnValueChangedListener { picker, oldVal, newVal ->
            year= arrayYear.get(newVal-1)
            Log.e("year",""+year)
            dob.text="$month/$day/$year"
//            dob.text= DateUtils.formatDateUTCToLocal(
//                "$month/$day/$year",
//                DateUtils.MMDDYYY,
//                false
//            )
        }


    }

    public fun getAge(): Int? {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob.set(Calendar.YEAR, year.toInt())
        dob.set(Calendar.MONTH, month.toInt())
        dob.set(Calendar.DAY_OF_YEAR, day.toInt())

        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        val ageInt = age
        return ageInt
    }

    override fun pageVisible() {

    }

}