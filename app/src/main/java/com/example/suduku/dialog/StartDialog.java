package com.example.suduku.dialog;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.suduku.window.map.Map;
import com.example.suduku.window.puzzle.Puzzle;
import com.example.suduku.R;

public class StartDialog extends Dialog {
//    private Puzzle PuzzleActivity = new Puzzle();
    TextView textView = findViewById(R.id.levelWin);

    public StartDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void show() {
        super.show();
    }

    private void close() {
        ImageView ib = findViewById(R.id.closeWin);
        ib.setOnClickListener(new View.OnClickListener() {//按键监听
            @Override
            public void onClick(View v) {

                dismiss();//dialog使用该方法关闭
            }
        });
    }

    private void play() {
        Button ib = findViewById(R.id.nextWin);
        ib.setOnClickListener(new View.OnClickListener() {//按键监听
            @Override
            public void onClick(View v) {
                //创建Puzzle，跳转到Puzzle界面
                Intent intent = new Intent(getContext(), Puzzle.class);
                intent.putExtra("level", textView.getText().toString());
                //将map传递到Puzzle，跳转到Puzzle界面

                getContext().startActivity(intent);
                dismiss();//dialog使用该方法关闭
            }
        });
    }

    //传入显示文本内容
    public void setLevel(int level) {
        textView = findViewById(R.id.levelWin);
        //设置文本内容
        textView.setText(String.format("STAR - %d", level));
    }

    //传入图片显示
    public void setImage() {
        ImageView ib = findViewById(R.id.star_start);
        ib.setImageResource(R.drawable.ic_pop_star);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置无title // TODO API22不可用
        setContentView(R.layout.start_dialog);//设置布局文件

        close();//调用X按键监听方法
        play();//调用play按键监听方法

    }
}
