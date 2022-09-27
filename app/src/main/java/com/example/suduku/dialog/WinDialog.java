package com.example.suduku.dialog;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.suduku.R;
import com.example.suduku.window.map.Map;
import com.example.suduku.window.puzzle.Puzzle;

public class WinDialog extends Dialog {

    public WinDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void show() {
        super.show();
    }

    public Puzzle mPuzzle;//游戏界面

    private void nextPause() {
        Button htplay = findViewById(R.id.nextWin);
        htplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入下一关
                Intent intent = new Intent(getContext(), Puzzle.class);
                String str = mPuzzle.puzzle_top_text.getText().toString();
                int level = Integer.parseInt(str.substring(str.length() - (str.length() - 7)));
                level++;
                intent.putExtra("level", String.format("STAR - %d", level));
                getContext().startActivity(intent);
                mPuzzle.finish();//关闭游戏界面
                dismiss();//dialog使用该方法关闭
            }
        });
    }

    private void close() {
        ImageView ib = findViewById(R.id.closeWin);
        ib.setOnClickListener(new View.OnClickListener() {//按键监听
            @Override
            public void onClick(View v) {
                //跳转到map
                Intent intent = new Intent(getContext(), Map.class);
                getContext().startActivity(intent);
                mPuzzle.finish();
                dismiss();//dialog使用该方法关闭
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置无title
        setCanceledOnTouchOutside(false);//点击外部不关闭dialog
        setContentView(R.layout.win_dialog);


        SharedPreferences sp = getContext().getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //游戏进度从sp中读取
        int levelProgress = sp.getInt("levelProgress", 1);

        String str = mPuzzle.puzzle_top_text.getText().toString();
        int level = Integer.parseInt(str.substring(str.length() - (str.length() - 7)));

        //log level and levelProgress
        Log.d("level", "level:" + String.valueOf(level) + " levelProgress:" + String.valueOf(levelProgress));

        if (level == levelProgress) {
            levelProgress++;//设置通过状态
            editor.putInt("levelProgress", levelProgress);
            editor.apply();
        }

        nextPause();     //继续游戏
        close();         //关闭游戏
    }

}
