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
NetworkDetector networkDetector = new NetworkDetector(context, /*time delay*/ 3000L, /*OnNetworkStatusChangeListener*/ this);
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

## Download
In the project Gradle:
```Gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

In the app Gradle:
```Gradle
dependencies {
    compile 'com.github.tingyik90:networkdetector:1.2'
}
```

## License
Copyright 2017 Saw Ting Yik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
