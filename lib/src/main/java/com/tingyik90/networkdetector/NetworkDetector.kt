package com.tingyik90.networkdetector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Handler

class NetworkDetector(private val context: Context, private val timeDelay: Long = 0L, private val onNetworkStatusChangeListener: OnNetworkStatusChangeListener?) {

    /* parameters */
    private val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val handler = Handler()
    private val runnable = Runnable { updateNetworkStatus() }
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateNetworkStatus()
        }
    }
    private var isNetworkConnected = true

    /* interface */
    interface OnNetworkStatusChangeListener {
        fun onNetworkStatusChange(isNetworkConnected: Boolean)
    }

    /* get current network status */
    fun isNetworkConnected(): Boolean {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    /* start */
    fun start() {
        // register receiver
        context.registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    /* stop */
    fun stop() {
        // reset the condition so it will start fresh onResume
        isNetworkConnected = true
        handler.removeCallbacks(runnable)
        context.unregisterReceiver(broadcastReceiver)
    }

    /* update network status */
    private fun updateNetworkStatus() {
        // stop previous runnable
        handler.removeCallbacks(runnable)
        // record previous status
        val oldIsNetworkConnected = isNetworkConnected
        // apparent status with delay
        var apparentNetworkConnected = false
        // get current status
        isNetworkConnected = isNetworkConnected()
        if (isNetworkConnected) {
            if (timeDelay == 0L) {
                // show as connected if time delay is not required
                apparentNetworkConnected = true
            } else {
                if (oldIsNetworkConnected) {
                    // show as connected if network is still connected after time delay
                    apparentNetworkConnected = true
                } else {
                    // if disconnected network just recovered, check the connection again after time delay
                    handler.postDelayed(runnable, timeDelay)
                }
            }
        } else {
            // show as disconnected immediately if no network
            handler.removeCallbacks(runnable)
        }
        // trigger listener
        onNetworkStatusChangeListener?.onNetworkStatusChange(apparentNetworkConnected)
    }
}