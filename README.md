# NetworkDetector
Simple network checker for Android. Start by calling
```Java
NetworkDetector networkDetector = new NetworkDetector.Builder(context).build();
```

You can check the connectivity manually by
```Java
boolean isNetworkConnected = networkDetector.isNetworkConnected();
```

`isNetworkConnected` is checked via Android `ConnectivityManager` and the method `getActiveNetworkInfo().isConnected()`.

If you would like to monitor the network status change, make your class implements `NetworkDetector.OnNetworkStatusChangeListener`. Build `networkDetector` as below.
```Java
NetworkDetector networkDetector = new NetworkDetector.Builder(context)
            .addOnNetworkStatusChangeListener(this)
            .timeDelay(3000)        // optional, default = 0
            .build();
```

This will attach a `BroadcastReceiver` on `ConnectivityManager.CONNECTIVITY_ACTION`. Override your action in
```Java
@Override
public void onNetworkStatusChange(boolean isNetworkConnected) {
    if (isNetworkConnected) {
        // do something
    }
}
```

With time delay set, `isNetworkConnected` returns `false` when the device first recovers its connection.
It will check for connectivity again after the time delay and only return `true` if it is still connected.
This is useful if you are using Firebase and other services as it takes some time for them to reconnect after losing connection. 

Make sure to call the following in your Activity.
```Java
@Override
protected void onResume() {
    super.onResume();
    networkDetector.start();
}

@Override
protected void onPause() {
    super.onPause();
    networkDetector.stop();    // make sure to call this to detach listener
}
```

