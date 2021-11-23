package com.global.vtg.utils.broadcasts

import android.content.Context
import android.net.ConnectivityManager


/**
 * Purpose  - Check internet connectivity.
 * @author  - amit.prajapati
 * Created  - 13/11/17
 * Modified - 26/12/17
 */
//object ConnectivityUtils {

fun isNetworkAvailable(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    val activeNetwork = cm?.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
}
//}