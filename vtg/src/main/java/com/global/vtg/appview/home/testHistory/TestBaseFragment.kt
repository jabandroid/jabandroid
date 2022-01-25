package com.global.vtg.appview.home.testHistory

import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.global.vtg.base.AppFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.vtg.R
import com.vtg.databinding.FragmentTestFragmentBinding
import kotlinx.android.synthetic.main.fragment_test_fragment.*
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager2.widget.ViewPager2
import com.global.vtg.utils.Constants


class TestBaseFragment : AppFragment() {
    private lateinit var mFragmentBinding: FragmentTestFragmentBinding

    private var countView: Int = 2

    override fun getLayoutId(): Int {
        return R.layout.fragment_test_fragment
    }

    override fun preDataBinding(arguments: Bundle?) {

    }

    override fun postDataBinding(binding: ViewDataBinding): ViewDataBinding {
        mFragmentBinding = binding as FragmentTestFragmentBinding
       // mFragmentBinding.viewModel = viewModel
        mFragmentBinding.lifecycleOwner = this
        return mFragmentBinding
    }

    override fun initializeComponent(view: View?) {
        ivBackScan.setOnClickListener {
            activity?.onBackPressed()
        }


        val pagerAdapter = ViewPagerAdapter(activity)
        vpPager.adapter = pagerAdapter

        TabLayoutMediator(vpDots, vpPager) { _, _ ->
            //Some implementation
        }.attach()

        vpPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        tvTitle.text=getString(R.string.label_covid_test_status)
                    }

                    else -> {
                        tvTitle.text=getString(R.string.label_test_history)

                    }
                }
            }
        })
    }

    override fun pageVisible() {

    }

    class ViewPagerAdapter(@NonNull fragmentActivity: FragmentActivity?) :
        FragmentStateAdapter(fragmentActivity!!) {
        @NonNull
        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> {
                    var f = TestHistoryDetailFragment()

                    return f
                }

                else -> {
                    var f = TestHistoryFragment()

                    return f

                }
            }
        }

        override fun getItemCount(): Int {
            return CARD_ITEM_SIZE
        }

        companion object {
            private const val CARD_ITEM_SIZE = 2
        }
    }

//    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) :
//        FragmentStatePagerAdapter(fm) {
//        override fun getCount(): Int = countView
//
//        override fun getItem(position: Int): Fragment {
//
//            when (position) {
//                0 -> {
//                    var f = TestHistoryDetailFragment()
//
//                    return f
//                }
//
//                else -> {
//                    var f = TestHistoryFragment()
//
//                    return f
//
//                }
//            }
//
//
//        }
//    }

}