package com.global.vtg.appview.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.vtg.R
import kotlinx.android.synthetic.main.activity_image_view.*


import java.util.ArrayList

class ImageViewActivity :AppCompatActivity() {

    private var listOfImages: ArrayList<String>? = null
    private lateinit var mViewPagerAdapter: ImageSliderAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        window.apply {
//            setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = Color.TRANSPARENT
        }
        menu_icon.setOnClickListener{
            finish()
        }
        if(intent.hasExtra("IM")){
            listOfImages = intent.getStringArrayListExtra("IM")
        }


        mViewPagerAdapter = ImageSliderAdapter(
            this@ImageViewActivity,
            listOfImages
        )

        viewPagerMain.adapter = mViewPagerAdapter
    }
}