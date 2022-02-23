package com.global.vtg.appview.home.event

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.vtg.R
import kotlinx.android.synthetic.main.include_image.view.*

import retrofit2.http.Url


@SuppressLint("ViewConstructor")
class EventImageView(context: Context?, name: Bitmap?, url: String?, count:Int, isnew :Boolean, completedTripDetailActivity: OnDeleteImageClick, v: LinearLayout) : RelativeLayout(context) {
   
    init {
      var view=  View.inflate(context, R.layout.include_image, this)
       var upload= view.findViewById<ImageView>(R.id.upload)
       var actual= view.findViewById<ImageView>(R.id.actual)
       var ic_delete= view.findViewById<ImageView>(R.id.ic_delete)
       var fl_actual= view.findViewById<FrameLayout>(R.id.fl_actual)
        upload.visibility = View.GONE

        if(isnew)
            upload.visibility = View.VISIBLE

        fl_actual.visibility = View.VISIBLE
       // listOfImages.add(obj.attributes.signed_url)
        if(name!=null) {
            ic_delete.visibility=View.VISIBLE

            actual.setImageBitmap(name)


        } else if(!TextUtils.isEmpty(url!!.toString())){
            ic_delete.visibility=View.GONE
            Glide.with(context!!)
                .load(url)
                .apply(
                    RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))

                .into(actual)
        }else
            fl_actual.visibility = View.GONE

        actual.setOnClickListener {
            completedTripDetailActivity.onViewClick()
        }
        upload.setOnClickListener {
            completedTripDetailActivity.onImageClick()
        }
        v.tag=count
        ic_delete.tag=count
        ic_delete.setOnClickListener {
            completedTripDetailActivity.onDeleteClick(ic_delete.tag as Int,v)
        }

    }

    public fun loadImage(url: String, actualimg : ImageView,context: Context?){

    }



    interface OnDeleteImageClick{
        fun onDeleteClick(item: Int, ll:LinearLayout)
        fun onImageClick()
        fun onViewClick()
    }


  public  fun hideAdd(v :View){
        v.visibility=View.INVISIBLE
    }
   public fun showAdd(v :View){
        v.visibility=View.VISIBLE
    }


}
