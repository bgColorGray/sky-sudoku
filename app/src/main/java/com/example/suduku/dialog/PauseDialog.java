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
import com.example.suduku.window.puzzle.Puzzle;
import com.example.suduku.window.Welcome;

public class PauseDialog extends Dialog {

    public PauseDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void show() {
        super.show();
    }

    public Puzzle mPuzzle;//游戏界面

    private void restartPause(){
        Button htplay = findViewById(R.id.restartPause);
        htplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空mNotes和mSign
                for(int i=0;i<9;i++){
                    for(int j=0;j<9;j++){
                        if (mPuzzle.puzzleV.game.mSubject[i][j] == 0){
                            mPuzzle.puzzleV.game.mNotes[i][j] = 0;
                        }
                        for(int k=0;k<9;k++){
                            mPuzzle.puzzleV.game.mSign[i][j][k] = false;
                        }
                    }
                }
                mPuzzle.mTimer.reset();
                mPuzzle.mTimer.start();
                mPuzzle.puzzleV.invalidate();//刷新界面
                dismiss();//dialog使用该方法关闭
            }
        });
    }

    private void starrySky(){
        Button htplay = findViewById(R.id.starrySky);
        htplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭Activity mPuzzle
                mPuzzle.finish();
            }
        });
    }

    private void close(){
        ImageView ib = findViewById(R.id.closePause);
        ib.setOnClickListener(new View.OnClickListener() {//按键监听
            @Override
            public void onClick(View v) {
                mPuzzle.mTimer.resume();//继续倒计时
                dismiss();//dialog使用该方法关闭
            }
        });
    }

    private void soundEffect(){
        ImageView ib = findViewById(R.id.soundEffectPause);
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
        ImageView ib = findViewById(R.id.backGroundMusicPause);
        ib.setOnClickListener(new View.OnClickListener() {//按键监听
            private boolean flag = true;
            @Override
            public void onClick(View v) {
                if(flag){
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
        setCanceledOnTouchOutside(false);//点击外部不关闭dialog
        setContentView(R.layout.pause_dialog);

        restartPause();     //继续游戏
        starrySky();        //返回主界面
        close();            //关闭游戏
        soundEffect();      //音效开关
        backGroundMusic();  //BGM开关
    }

}
