package com.example.suduku.window;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import com.example.suduku.R;
import com.example.suduku.window.map.Map;

public class Welcome extends Activity {
    //LOG
    private static final String TAG = "Welcome";

    public static MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        mp = MediaPlayer.create(Welcome.this, R.raw.bgm);
        mp.setOnCompletionListener(mp -> {
            try {
                mp.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        });

        //1秒后跳转到map界面
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(Welcome.this, Map.class));
                            finish();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}