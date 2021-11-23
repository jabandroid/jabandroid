package com.global.vtg.appview.config


import android.app.Activity
import android.graphics.Color
import android.os.Build

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity


/**
 * Purpose  - Handle status bar and some bottom area.
 * @author  - amit.prajapati
 * Created  - 10/10/17
 * Modified - 26/12/17
 */
object ImmersiveUI {
    private const val TAG = "ImmersiveUI"
    private fun hideActionBar(activity: Activity) {
        if (activity is AppCompatActivity) {
            val actionBar = activity.supportActionBar
            if (actionBar == null) {
                Log.d(TAG, "action bar is null.")
            } else {
                actionBar.hide()
            }
        } else {
            Log.d(TAG, "not an AppCompatActivity")
        }
    }

    private fun showActionBar(activity: Activity) {
        if (activity is AppCompatActivity) {
            val actionBar = activity.supportActionBar
            if (actionBar == null) {
                Log.d(TAG, "action bar is null.")
            } else {
                actionBar.show()
            }
        } else {
            Log.d(TAG, "not an AppCompatActivity")
        }
    }

    fun hideHead(activity: Activity) {
        setFlag(activity, View.SYSTEM_UI_FLAG_FULLSCREEN)
        hideActionBar(activity)
    }

    private fun setFlag(activity: Activity, flag: Int) {
        activity.window.decorView.systemUiVisibility = flag
    }


    fun setHead(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            setFlag(activity, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            activity.window.statusBarColor = Color.TRANSPARENT
        }
        hideActionBar(activity)
    }

    fun immersive(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            setFlag(activity, View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            activity.window.statusBarColor = Color.TRANSPARENT
            activity.window.navigationBarColor = Color.TRANSPARENT
        }
        hideActionBar(activity)
    }

    fun setFoot(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            setFlag(activity, View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
            activity.window.navigationBarColor = Color.TRANSPARENT
        }
        hideActionBar(activity)
    }

    fun quit(activity: Activity) {
        setFlag(activity, View.SYSTEM_UI_FLAG_VISIBLE)
        showActionBar(activity)
    }

    fun fullScreen(activity: Activity) {
        setFlag(activity, View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
}