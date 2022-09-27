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

public class LostDialog extends Dialog {

    public LostDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void show() {
        super.show();
    }

    public Puzzle mPuzzle;//游戏界面

    private void close(){
        ImageView ib = findViewById(R.id.closeWin);
        ib.setOnClickListener(new View.OnClickListener() {//按键监听
            @Override
            public void onClick(View v) {
                mPuzzle.finish();//
            }
        });
    }

    private void playAgain(){
        Button ib = findViewById(R.id.playAgain);
        ib.setOnClickListener(new View.OnClickListener() {//按键监听
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置无title
        setCanceledOnTouchOutside(false);//点击外部不关闭dialog
        setContentView(R.layout.lost_dialog);

        close();
        playAgain();

    }

}
