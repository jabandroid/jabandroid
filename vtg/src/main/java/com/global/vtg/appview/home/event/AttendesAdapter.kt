package com.global.vtg.appview.home.event

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.global.vtg.imageview.setGlideNormalImage
import com.global.vtg.utils.Constants
import com.vtg.R
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.adapter_attendees.view.*


class AttendesAdapter(
    private val context: Context?,
    private var appReqList: ArrayList<EventUser>,
  private val name : String ,
    private val   pic :String

) : RecyclerView.Adapter<AttendesAdapter.MyViewHolder>() {
    class MyViewHolder(val layout: RelativeLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_attendees,
            parent,
            false
        ) as RelativeLayout
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return appReqList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        holder.layout.tv_name.text = appReqList[position].getName()
        if (!TextUtils.isEmpty(appReqList[position].firstName)) {
            holder.layout.tv_name.text =
                appReqList[position].firstName + " " + appReqList[position].lastName
            holder.layout.tv_number.visibility=View.VISIBLE
            holder.layout.tv_number.text = appReqList[position].userName
        } else {
            holder.layout.tv_number.visibility=View.GONE
            holder.layout.tv_name.text = appReqList[position].userName

        }


        if (appReqList[position].userId!="0")
            holder.layout.invite.visibility = View.GONE
        else
            holder.layout.invite.visibility = View.VISIBLE

        if (!TextUtils.isEmpty(appReqList[position].profileUrl)) {
            holder.layout.ivProfilePic.setGlideNormalImage(Constants.USER!!.profileUrl)
        }
        holder.layout.tvEdit.visibility=View.VISIBLE
        if(TextUtils.isEmpty(appReqList[position].interested)){
            holder.layout.tvEdit.visibility=View.GONE
        }else if(appReqList[position].interested.equals("1")){
            holder.layout.tvEdit.setImageResource(R.drawable.ic_drawable_tick)
        }else if(appReqList[position].interested.equals("0")){
            holder.layout.tvEdit.setImageResource(R.drawable.ic_drawable_cross)
        }
        holder.layout.invite.setOnClickListener{
            val branchUniversalObject: BranchUniversalObject =
                BranchUniversalObject()
                    .setCanonicalIdentifier("item/12345")
                    .setTitle("You are invited to join event :$name")
                    .setContentDescription("")
                    .setContentImageUrl(pic) //.setContentImageUrl(Uri.parse("file://"+downloadedImagePath).toString())
                    .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)

            val linkProperties: LinkProperties = LinkProperties()
                .addControlParameter("event_id", appReqList[position].eventId)
                .setFeature("sharing")

                .setStage("1")

            val bundle = Bundle()

            bundle.putString("event_id", appReqList[position].eventId)
            branchUniversalObject.generateShortUrl(
                context!!,
                linkProperties
            ) { url, error ->
                if (error == null) {
                    val uri: Uri = Uri.parse("smsto:$"+appReqList[position].userName)
                    val sendIntent = Intent(Intent.ACTION_SEND,uri)

                    sendIntent.putExtra(Intent.EXTRA_TEXT, url)
                    sendIntent.type = "text/plain"
                    context!!.startActivity(sendIntent)
                }
            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public  fun remove(index:Int){
        appReqList.removeAt(index)
        notifyDataSetChanged()
    }
}