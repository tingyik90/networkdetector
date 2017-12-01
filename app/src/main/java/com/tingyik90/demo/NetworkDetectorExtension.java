package com.tingyik90.demo;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.tingyik90.networkdetector.NetworkDetector;

/**
 * custom demo wrapper class extending the function of NetworkDetector
 */
public class NetworkDetectorExtension implements NetworkDetector.OnNetworkStatusChangeListener {

    private NetworkDetector networkDetector;
    private View networkBarLayout;

    public NetworkDetectorExtension(Context context, View networkBarLayout) {
        this.networkBarLayout = networkBarLayout;
        networkDetector = new NetworkDetector(context, 3000, this);
    }

    public void start() {
        networkDetector.start();
    }

    public void stop() {
        networkDetector.stop();
    }

    @Override
    public void onNetworkStatusChange(boolean isNetworkConnected) {
        Log.d("DEBUG", "isNetworkConnected: " + isNetworkConnected);
        // show or hide the layout
        if (networkBarLayout != null) {
            if (isNetworkConnected) {
                networkBarLayout.setVisibility(View.GONE);
            } else {
                networkBarLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}
