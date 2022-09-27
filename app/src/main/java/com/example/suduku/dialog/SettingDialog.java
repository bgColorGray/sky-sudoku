package com.example.suduku.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.suduku.R;
import com.example.suduku.window.Welcome;

public class SettingDialog extends Dialog {

    public SettingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void show() {
        super.show();
    }

    private void howToPlay(){
        Button htplay = findViewById(R.id.howToPlay);
        htplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });
    }

    private void close(){
        ImageView ib = findViewById(R.id.closeWin);
        ib.setOnClickListener(new View.OnClickListener() {//按键监听
            @Override
            public void onClick(View v) {
                dismiss();//dialog使用该方法关闭
            }
        });
    }

    private void soundEffect(){
        ImageView ib = findViewById(R.id.soundEffect);
        ib.setOnClickListener(new View.OnClickListener() {//按键监听
            private boolean flag = true;
            @Override
            public void onClick(View v) {
                //TODO 音效切换
                //修改图片,在ic_pop_audio_off和ic_pop_audio_on之间切换
                if(flag){
                    ib.setImageResource(R.drawable.ic_pop_audio_off);
                }else{
                    ib.setImageResource(R.drawable.ic_pop_audio_on);
                }
                flag = !flag;
            }
        });
    }

    private void backGroundMusic(){
        ImageView ib = findViewById(R.id.backGroundMusic);
        ib.setOnClickListener(new View.OnClickListener() {//按键监听
            private boolean flag = true;
            @Override
            public void onClick(View v) {
                //TODO BGM切换
                if(flag){
                    //暂停音乐
                    ib.setImageResource(R.drawable.ic_pop_music_off);
                    Welcome.mp.pause();
                }else{
                    ib.setImageResource(R.drawable.ic_pop_music_on);
                    Welcome.mp.start();
                }
                flag = !flag;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置无title
        setContentView(R.layout.setting_dialog);

        howToPlay();
        close();
        soundEffect();
        backGroundMusic();
    }

}
