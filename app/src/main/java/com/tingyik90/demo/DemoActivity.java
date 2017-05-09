package com.tingyik90.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class DemoActivity extends AppCompatActivity {

    private NetworkDetectorExtension networkDetectorExtension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        View networkBarLayout = findViewById(R.id.overlay_networkBar);
        networkDetectorExtension = new NetworkDetectorExtension(this, networkBarLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkDetectorExtension.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        networkDetectorExtension.stop();
    }
}
