package com.example.suduku;

import android.app.Application;
import android.util.Log;

import com.example.suduku.window.Welcome;

public class MyApp extends Application {
    private static final String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();
        AppFrontBackHelper helper = new AppFrontBackHelper();
        helper.register(MyApp.this, new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront() {
                Log.d(TAG, "onFront: ");
                if (Welcome.mp != null) {
                    Welcome.mp.start();
                }
            }

            @Override
            public void onBack() {
                Log.d(TAG, "onBack: ");
                //暂停播放音乐
                if (Welcome.mp != null) {
                    Welcome.mp.pause();
                }
            }
        });
    }
}