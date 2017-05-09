package com.tingyik90.networkdetector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

public class NetworkDetector {

    /* variables */
    private boolean isNetworkConnected = true;
    private long timeDelay = 0L;
    private BroadcastReceiver broadcastReceiver;
    private ConnectivityManager connectivityManager;
    private Context context;
    private Handler handler;
    private Runnable runnable;
    private OnNetworkStatusChangeListener onNetworkStatusChangeListener;

    /* builder */
    public static class Builder {
        private long timeDelay = 0L;
        private Context context;
        private OnNetworkStatusChangeListener onNetworkStatusChangeListener;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder timeDelay(long timeDelay) {
            this.timeDelay = timeDelay;
            return this;
        }

        public Builder addOnNetworkStatusChangeListener(OnNetworkStatusChangeListener onNetworkStatusChangeListener) {
            this.onNetworkStatusChangeListener = onNetworkStatusChangeListener;
            return this;
        }

        public NetworkDetector build() {
            return new NetworkDetector(this);
        }
    }

    /* constructors */
    private NetworkDetector(Builder builder) {
        context = builder.context;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        setTimeDelay(builder.timeDelay);
        if (builder.onNetworkStatusChangeListener != null) {
            setOnNetworkStatusChangeListener(builder.onNetworkStatusChangeListener);
            // update network status when ConnectivityManager broadcast is received on status change
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    updateNetworkStatus();
                }
            };
            // handler and runnable for apparent connection status
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    updateNetworkStatus();
                }
            };
        }
    }

    /* interface */
    public interface OnNetworkStatusChangeListener {
        void onNetworkStatusChange(boolean isNetworkConnected);
    }

    /* set listener */
    public void setOnNetworkStatusChangeListener(OnNetworkStatusChangeListener onNetworkStatusChangeListener) {
        this.onNetworkStatusChangeListener = onNetworkStatusChangeListener;
    }

    /* set time delay for the apparent connection status, default = 0L (no delay) */
    public void setTimeDelay(long millis) {
        this.timeDelay = millis;
    }

    /* start */
    public void start() {
        // register receiver
        context.registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /* stop */
    public void stop() {
        // reset the condition so it will start fresh onResume
        isNetworkConnected = true;
        handler.removeCallbacks(runnable);
        context.unregisterReceiver(broadcastReceiver);
    }

    /* update network status */
    private void updateNetworkStatus() {
        // stop previous runnable
        handler.removeCallbacks(runnable);
        // record previous status
        boolean oldIsNetworkConnected = isNetworkConnected;
        // apparent status with delay
        boolean apparentNetworkConnected = false;
        // get current status
        isNetworkConnected = isNetworkConnected();
        if (isNetworkConnected) {
            if (timeDelay == 0L) {
                // show as connected if time delay is not required
                apparentNetworkConnected = true;
            } else {
                if (oldIsNetworkConnected) {
                    // show as connected if network is still connected after time delay
                    apparentNetworkConnected = true;
                } else {
                    // if disconnected network just recovered, check the connection again after time delay
                    handler.postDelayed(runnable, timeDelay);
                }
            }
        } else {
            // show as disconnected immediately if no network
            handler.removeCallbacks(runnable);
        }
        // trigger listener
        if (onNetworkStatusChangeListener != null) {
            onNetworkStatusChangeListener.onNetworkStatusChange(apparentNetworkConnected);
        }
    }

    /* get current network status */
    public boolean isNetworkConnected() {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
